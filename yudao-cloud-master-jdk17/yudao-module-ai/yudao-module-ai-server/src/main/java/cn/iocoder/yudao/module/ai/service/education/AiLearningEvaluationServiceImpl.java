package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.EvaluationPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningEvaluationDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningEvaluationMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningResourceMapper;
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
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiLearningEvaluationServiceImpl implements AiLearningEvaluationService {

    @Resource
    private AiLearningEvaluationMapper evaluationMapper;
    @Resource
    private AiLearningResourceMapper learningResourceMapper;
    @Resource
    private AiStudentProfileServiceImpl studentProfileService;
    @Resource
    private AiModelService modalService;

    private static final String EVALUATION_SYSTEM_PROMPT = """
            你是一位学习效果评估专家。根据学生的学习数据（画像、已完成资源、练习记录），进行多维度量化评估。
            
            评估维度包括：
            1. 知识掌握度 (0-100)
            2. 学习效率 (0-100)
            3. 薄弱环节分析
            4. 进步趋势
            5. 学习建议
            
            输出JSON格式：
            `json
            {
              "evaluations": [
                {"dimension": "知识掌握度", "score": 85, "evaluation": "评估描述", "suggestion": "改进建议"},
                {"dimension": "学习效率", "score": 70, "evaluation": "评估描述", "suggestion": "改进建议"}
              ],
              "overallSuggestion": "综合建议"
            }
            `
            """;

    @Override
    public Flux<CommonResult<String>> generateEvaluation(Long userId) {
        AiModelDO model = modalService.getRequiredDefaultModel(AiModelTypeEnum.CHAT.getType());
        ChatModel chatModel = modalService.getChatModel(model.getId());

        AiStudentProfileDO profile = studentProfileService.getProfileByUserId(userId);
        List<AiLearningResourceDO> resources = learningResourceMapper.selectListByUserId(userId);

        StringBuilder sb = new StringBuilder();
        sb.append("学生画像：").append(profile != null ? profile.getProfileJson() : "暂无").append("\n");
        sb.append("已完成学习资源数：").append(resources.size()).append("\n");
        resources.forEach(r -> sb.append("- ").append(r.getTitle()).append(" [").append(r.getResourceType()).append("] 状态：").append(r.getStatus()).append("\n"));

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(EVALUATION_SYSTEM_PROMPT));
        messages.add(new UserMessage(sb.toString()));

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
                AiLearningEvaluationDO eval = new AiLearningEvaluationDO();
                eval.setUserId(userId).setEvaluationType("COMPREHENSIVE")
                        .setProfileId(profile != null ? profile.getId() : null)
                        .setEvaluation(contentBuffer.toString());
                evaluationMapper.insert(eval);
            });
        }).doOnError(throwable -> {
            log.error("[generateEvaluation][userId({}) 异常]", userId, throwable);
        }).onErrorResume(error -> Flux.just(error(EDUCATION_STREAM_ERROR)));
    }

    @Override
    public PageResult<AiLearningEvaluationDO> getEvaluationPage(EvaluationPageReqVO reqVO) {
        return evaluationMapper.selectPage(reqVO);
    }
}
