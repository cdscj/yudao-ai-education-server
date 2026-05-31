package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningPathPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningPathNodeDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningPathMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningPathNodeMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiLearningPathServiceImpl implements AiLearningPathService {

    @Resource
    private AiLearningPathMapper learningPathMapper;
    @Resource
    private AiLearningPathNodeMapper learningPathNodeMapper;
    @Resource
    private AiModelService modalService;

    private static final String PATH_SYSTEM_PROMPT = """
            你是一位学习路径规划专家。根据用户的学习目标、课程和时间周期，制定科学、有序的个性化学习路径。
            
            请按以下JSON格式输出：
            `json
            {
              "title": "学习路径标题",
              "description": "路径总体描述",
              "nodes": [
                {
                  "title": "阶段1标题",
                  "description": "阶段描述",
                  "content": "学习内容说明",
                  "sortOrder": 1
                }
              ]
            }
            `
            每个节点代表一个学习阶段，建议按由浅入深排列。节点数量根据时间周期合理分配。
            """;

    @Override
    public Flux<CommonResult<String>> generatePath(LearningPathGenerateReqVO reqVO, Long userId) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        AiLearningPathDO path = new AiLearningPathDO();
        path.setUserId(userId).setGoal(reqVO.getGoal()).setStatus("GENERATING");
        learningPathMapper.insert(path);

        Long pathId = path.getId();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(PATH_SYSTEM_PROMPT));
        String userMsg = StrUtil.format("学习目标：{}\n课程领域：{}\n时间周期：{}天",
                reqVO.getGoal(), StrUtil.nullToDefault(reqVO.getCourseName(), ""),
                reqVO.getDurationDays() != null ? reqVO.getDurationDays() : 30);
        messages.add(new UserMessage(userMsg));

        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);
        Flux<ChatResponse> streamResponse = chatModel.stream(prompt);

        StringBuffer contentBuffer = new StringBuffer();
        return streamResponse.map(chunk -> {
            String newContent = chunk.getResult() != null ? chunk.getResult().getOutput().getText() : "";
            contentBuffer.append(newContent);
            return success(newContent);
        }).doOnComplete(() -> {
            TenantUtils.executeIgnore(() -> {
                String fullContent = contentBuffer.toString();
                learningPathMapper.updateById(new AiLearningPathDO()
                        .setId(pathId).setDescription(fullContent).setStatus("COMPLETED"));
            });
        }).doOnError(throwable -> {
            log.error("[generatePath][reqVO({}) 异常]", reqVO, throwable);
            TenantUtils.executeIgnore(() -> {
                learningPathMapper.updateById(new AiLearningPathDO()
                        .setId(pathId).setStatus("FAILED"));
            });
        }).onErrorResume(error -> Flux.just(error(EDUCATION_STREAM_ERROR)));
    }

    @Override
    public AiLearningPathDO getPath(Long id) {
        return learningPathMapper.selectById(id);
    }

    @Override
    public PageResult<AiLearningPathDO> getPathPage(LearningPathPageReqVO reqVO) {
        return learningPathMapper.selectPage(reqVO);
    }

    @Override
    public void updateNodeStatus(Long nodeId, String status) {
        AiLearningPathNodeDO node = learningPathNodeMapper.selectById(nodeId);
        if (node == null) {
            throw exception(LEARNING_PATH_NODE_NOT_EXISTS);
        }
        learningPathNodeMapper.updateById(new AiLearningPathNodeDO()
                .setId(nodeId).setStatus(status)
                .setCompletedTime("COMPLETED".equals(status) ? LocalDateTime.now() : null));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePath(Long id) {
        if (learningPathMapper.selectById(id) == null) {
            throw exception(LEARNING_PATH_NOT_EXISTS);
        }
        learningPathNodeMapper.deleteByPathId(id);
        learningPathMapper.deleteById(id);
    }
}
