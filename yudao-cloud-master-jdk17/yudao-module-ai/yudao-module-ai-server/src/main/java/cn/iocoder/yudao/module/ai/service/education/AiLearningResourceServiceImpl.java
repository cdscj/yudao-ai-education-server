package cn.iocoder.yudao.module.ai.service.education;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourceGenerateReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.LearningResourcePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiLearningResourceMapper;
import cn.iocoder.yudao.module.ai.enums.model.AiModelTypeEnum;
import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.config.AiSchedulerConfig;
import cn.iocoder.yudao.module.ai.framework.ai.config.YudaoAiProperties;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.xinghuo.api.XunFeiPptApi;
import cn.iocoder.yudao.module.ai.service.config.AiSystemConfigService;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import cn.iocoder.yudao.module.ai.framework.ai.core.gateway.AiModelGateway;
import cn.iocoder.yudao.module.ai.util.AiUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.error;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Slf4j
public class AiLearningResourceServiceImpl implements AiLearningResourceService {

    @Resource
    private AiLearningResourceMapper learningResourceMapper;
    @Resource
    private AiModelService modelService;
    @Resource
    private AiModelGateway modelGateway;
    @Resource
    private AiSystemConfigService configService;

    /** 讯飞 PPT API（可选注入 — 未启用 PPT 功能时为 null） */
    @Autowired(required = false)
    private XunFeiPptApi xunFeiPptApi;

    /** 讯飞 PPT 属性配置 */
    @Resource
    private YudaoAiProperties yudaoAiProperties;

    /** PPT 进度轮询间隔（秒） */
    private static final int PPT_POLL_INTERVAL_SECONDS = 3;
    /** PPT 生成最大等待时间（秒） */
    private static final int PPT_MAX_WAIT_SECONDS = 300;

    private String getSystemPrompt(String resourceType) {
        // 优先使用管理端配置的全局 Prompt
        String customPrompt = configService.getConfigValue("edu.resource.prompt", null);
        if (StrUtil.isNotBlank(customPrompt)) {
            return customPrompt + "\n\n资源类型：" + resourceType;
        }
        // 回退到各类型的默认 Prompt
        return switch (resourceType.toUpperCase()) {
            case "DOCUMENT" -> """
                    你是一位课程讲解文档撰写专家。根据用户提供的课程主题和难度级别，生成详细、结构化的课程讲解文档。
                    输出格式为 Markdown，包含：1. 课程概述 2. 核心概念讲解 3. 详细知识点 4. 示例说明 5. 重点总结。
                    需要根据难度级别调整内容深度：BEGINNER-入门级, INTERMEDIATE-进阶级, ADVANCED-高级。
                    """;
            case "MIND_MAP" -> """
                    你是一位思维导图生成专家。将用户提供的课程主题整理成清晰的思维导图结构。
                    输出格式为 Markdown 标题层级（# ## ### ####），展示知识体系结构。
                    """;
            case "EXERCISE" -> """
                    你是一位习题生成专家。根据课程主题和难度，生成分层练习题。
                    输出格式：包含选择题、填空题、简答题等类型，每题附带答案和解析。
                    按难度分级：基础题(60%)、提高题(30%)、挑战题(10%)。
                    """;
            case "READING" -> """
                    你是一位学术阅读推荐专家。根据课程主题，推荐拓展阅读材料。
                    输出格式：推荐5-8篇相关文献/资料，包含标题、作者、核心内容摘要和推荐理由。
                    """;
            case "CODE_EXAMPLE" -> """
                    你是一位编程教学专家。根据课程主题生成代码实操案例。
                    输出格式：1. 案例描述 2. 学习目标 3. 完整代码实现 4. 代码解析 5. 运行结果 6. 扩展练习。
                    代码需要带注释说明。
                    """;
            case "PPT" -> """
                    你是一位教学课件设计专家。根据课程主题，生成结构化的 PPT 大纲内容。
                    输出格式为 JSON，包含以下结构：
                    {
                      "title": "课件标题",
                      "subTitle": "副标题",
                      "chapters": [
                        {"chapterTitle": "章节标题", "chapterContents": [{"chapterTitle": "小节标题"}]}
                      ]
                    }
                    确保内容逻辑清晰、层次分明，适合课堂教学使用。
                    """;
            default -> """
                    你是一位教育资源生成专家。根据用户提供的主题和要求，生成优质的学习资源内容。
                    输出格式为 Markdown，内容需结构清晰、准确详实。
                    """;
        };
    }

    @Override
    public Flux<CommonResult<String>> generateResource(LearningResourceGenerateReqVO reqVO, Long userId) {
        // PPT 类型走专门的生成流程
        if ("PPT".equalsIgnoreCase(reqVO.getResourceType())) {
            return generatePptResource(reqVO, userId);
        }

        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            log.error("[generateResource][userId({}) 无可用模型]", userId);
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        AiLearningResourceDO resource = BeanUtils.toBean(reqVO, AiLearningResourceDO.class);
        resource.setUserId(userId).setStatus("GENERATING").setTitle(reqVO.getTopic());
        learningResourceMapper.insert(resource);

        Long resourceId = resource.getId();
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(getSystemPrompt(reqVO.getResourceType())));
        String userPrompt = StrUtil.format("请生成关于「{}」的{}资源。{}",
                reqVO.getTopic(), reqVO.getResourceType(),
                StrUtil.blankToDefault(reqVO.getRequirements(), ""));
        if (StrUtil.isNotBlank(reqVO.getDifficulty())) {
            userPrompt += "\n难度级别：" + reqVO.getDifficulty();
        }
        messages.add(new UserMessage(userPrompt));

        AiModelDO model = models.get(models.size() - 1);
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(model.getPlatform());
        ChatOptions options = AiUtils.buildChatOptions(platform, model.getModel(),
                model.getTemperature(), model.getMaxTokens());
        Prompt prompt = new Prompt(messages, options);

        StringBuffer contentBuffer = new StringBuffer();
        return modelGateway.chatStream(model.getId(), prompt)
                .map(text -> {
                    String newContent = text != null && !"null".equals(text) ? text : "";
                    contentBuffer.append(newContent);
                    return success(newContent);
                }).doOnComplete(() -> {
                    TenantUtils.executeIgnore(() -> {
                        learningResourceMapper.updateById(new AiLearningResourceDO()
                                .setId(resourceId).setContent(contentBuffer.toString()).setStatus("COMPLETED"));
                    });
                }).doOnError(throwable -> {
                    log.error("[generateResource][reqVO({}) 异常]", reqVO, throwable);
                    TenantUtils.executeIgnore(() -> {
                        learningResourceMapper.updateById(new AiLearningResourceDO()
                                .setId(resourceId).setErrorMessage(throwable.getMessage()).setStatus("FAILED"));
                    });
                });
    }

    // ========== PPT 资源生成（异步 + 进度追踪） ==========

    @Override
    public Flux<CommonResult<String>> generatePptResource(LearningResourceGenerateReqVO reqVO, Long userId) {
        if (xunFeiPptApi == null) {
            return Flux.just(error(500, "PPT 生成功能未启用，请配置 yudao.ai.xinghuo.ppt.enable=true"));
        }

        // 1. 创建资源记录
        AiLearningResourceDO resource = BeanUtils.toBean(reqVO, AiLearningResourceDO.class);
        resource.setUserId(userId).setStatus("GENERATING").setTitle(reqVO.getTopic())
                .setResourceType("PPT").setProgress(0);
        learningResourceMapper.insert(resource);
        Long resourceId = resource.getId();

        // 2. 先通过 LLM 生成 PPT 大纲内容
        List<AiModelDO> models = modelService.getEnabledModels(AiModelTypeEnum.CHAT.getType());
        if (CollUtil.isEmpty(models)) {
            return Flux.just(error(MODEL_DEFAULT_NOT_EXISTS));
        }

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(getSystemPrompt("PPT")));
        String userPrompt = StrUtil.format("请为「{}」课程生成 PPT 大纲。{}",
                reqVO.getTopic(), StrUtil.blankToDefault(reqVO.getRequirements(), ""));
        if (StrUtil.isNotBlank(reqVO.getDifficulty())) {
            userPrompt += "\n难度级别：" + reqVO.getDifficulty();
        }
        messages.add(new UserMessage(userPrompt));

        AiModelDO primaryModel = models.get(0);

        return Flux.concat(
                // 阶段1: 大纲生成
                Flux.just(success("[阶段1/3] 正在生成 PPT 大纲...\n")),
                generatePptOutline(primaryModel.getId(), messages, resourceId),
                // 阶段2: 提交讯飞 PPT 生成
                Flux.just(success("\n[阶段2/3] 正在提交讯飞 PPT 生成引擎...\n")),
                submitAndPollPptGeneration(resourceId, reqVO),
                // 阶段3: 完成
                Flux.just(success("\n[阶段3/3] PPT 生成完毕！\n"))
        ).doOnError(throwable -> {
            log.error("[generatePptResource][reqVO({}) 异常]", reqVO, throwable);
            TenantUtils.executeIgnore(() -> {
                learningResourceMapper.updateById(new AiLearningResourceDO()
                        .setId(resourceId).setErrorMessage(throwable.getMessage()).setStatus("FAILED"));
            });
        });
    }

    /**
     * 通过 LLM 生成 PPT 大纲，缓存到资源记录中
     */
    private Flux<CommonResult<String>> generatePptOutline(Long modelId,
                                                          List<Message> messages, Long resourceId) {
        StringBuffer outlineBuffer = new StringBuffer();
        return modelGateway.chatStream(modelId, new Prompt(messages))
                .map(text -> {
                    if (text != null && !"null".equals(text)) {
                        outlineBuffer.append(text);
                    }
                    return success(text);
                }).doOnComplete(() -> {
            // 将大纲缓存到资源记录中
            TenantUtils.executeIgnore(() -> {
                learningResourceMapper.updateById(new AiLearningResourceDO()
                        .setId(resourceId)
                        .setContent(outlineBuffer.toString())
                        .setProgress(30));
            });
        });
    }

    /**
     * 提交讯飞 PPT 生成任务并轮询进度
     */
    private Flux<CommonResult<String>> submitAndPollPptGeneration(Long resourceId,
                                                                   LearningResourceGenerateReqVO reqVO) {
        return Flux.defer(() -> {
            try {
                // 获取之前生成的大纲内容
                AiLearningResourceDO resource = learningResourceMapper.selectById(resourceId);
                String outlineContent = resource != null ? resource.getContent() : reqVO.getTopic();

                // 尝试解析 LLM 生成的 JSON 大纲，失败则使用纯文本
                XunFeiPptApi.OutlineData outlineData;
                try {
                    outlineData = JsonUtils.parseObject(outlineContent, XunFeiPptApi.OutlineData.class);
                } catch (Exception e) {
                    log.warn("[submitAndPollPptGeneration] 大纲 JSON 解析失败，使用纯文本创建: {}", e.getMessage());
                    outlineData = null;
                }

                // 获取 PPT 配置
                YudaoAiProperties.XingHuo.Ppt pptConfig = getPptConfig();

                // 创建 PPT 生成任务
                XunFeiPptApi.CreateResponse createResp;
                if (outlineData != null && outlineData.chapters() != null
                        && !outlineData.chapters().isEmpty()) {
                    // 通过大纲创建
                    XunFeiPptApi.CreatePptByOutlineRequest pptReq = XunFeiPptApi.CreatePptByOutlineRequest.builder()
                            .outline(outlineData)
                            .query(reqVO.getTopic())
                            .templateId(pptConfig.getDefaultTemplateId())
                            .author(pptConfig.getDefaultAuthor())
                            .isFigure(pptConfig.getIsFigure())
                            .aiImage(pptConfig.getAiImage())
                            .build();
                    createResp = xunFeiPptApi.createPptByOutline(pptReq);
                } else {
                    // 直接通过文本创建
                    XunFeiPptApi.CreatePptRequest pptReq = XunFeiPptApi.CreatePptRequest.builder()
                            .query(reqVO.getTopic() + "\n" + StrUtil.blankToDefault(reqVO.getRequirements(), ""))
                            .templateId(pptConfig.getDefaultTemplateId())
                            .author(pptConfig.getDefaultAuthor())
                            .isFigure(pptConfig.getIsFigure())
                            .aiImage(pptConfig.getAiImage())
                            .build();
                    createResp = xunFeiPptApi.create(pptReq);
                }

                if (!createResp.flag()) {
                    return Flux.just(error(500, "PPT 创建失败: " + createResp.desc()));
                }

                String sid = createResp.data().sid();
                log.info("[submitAndPollPptGeneration] PPT 任务已创建, sid={}, resourceId={}", sid, resourceId);

                // 轮询进度
                return pollPptProgress(sid, resourceId, 0);
            } catch (Exception e) {
                log.error("[submitAndPollPptGeneration] PPT 提交失败", e);
                return Flux.just(error(500, "PPT 提交失败: " + e.getMessage()));
            }
        }).subscribeOn(AiSchedulerConfig.AI);
    }

    /**
     * 轮询 PPT 生成进度
     */
    private Flux<CommonResult<String>> pollPptProgress(String sid, Long resourceId, int elapsedSeconds) {
        if (elapsedSeconds >= PPT_MAX_WAIT_SECONDS) {
            return Flux.just(error(500, "PPT 生成超时，请稍后重试"));
        }

        return Mono.delay(Duration.ofSeconds(PPT_POLL_INTERVAL_SECONDS))
                .flatMapMany(tick -> {
                    try {
                        XunFeiPptApi.ProgressResponse progress = xunFeiPptApi.checkProgress(sid);
                        XunFeiPptApi.ProgressResponseData data = progress.data();

                        if (data == null) {
                            return Flux.just(error(500, "PPT 进度查询失败: " + progress.desc()));
                        }

                        if (data.isFailed()) {
                            String errMsg = data.errMsg() != null ? data.errMsg() : "未知错误";
                            TenantUtils.executeIgnore(() -> {
                                learningResourceMapper.updateById(new AiLearningResourceDO()
                                        .setId(resourceId).setStatus("FAILED").setErrorMessage(errMsg));
                            });
                            return Flux.just(error(500, "PPT 生成失败: " + errMsg));
                        }

                        if (data.isAllDone()) {
                            // 生成完成，保存 PPT URL
                            String pptUrl = data.pptUrl();
                            TenantUtils.executeIgnore(() -> {
                                learningResourceMapper.updateById(new AiLearningResourceDO()
                                        .setId(resourceId)
                                        .setContent(pptUrl)
                                        .setStatus("COMPLETED")
                                        .setProgress(100));
                            });
                            return Flux.just(success("PPT 生成完成！下载地址: " + pptUrl));
                        }

                        // 更新进度
                        int percent = data.getProgressPercent();
                        TenantUtils.executeIgnore(() -> {
                            learningResourceMapper.updateById(new AiLearningResourceDO()
                                    .setId(resourceId).setProgress(percent));
                        });

                        String progressMsg = StrUtil.format("PPT 生成进度: {}% (已完成 {}/{} 页)",
                                percent,
                                data.donePages() != null ? data.donePages() : "?",
                                data.totalPages() != null ? data.totalPages() : "?");

                        // 递归轮询
                        return Flux.concat(
                                Flux.just(success(progressMsg + "\n")),
                                pollPptProgress(sid, resourceId, elapsedSeconds + PPT_POLL_INTERVAL_SECONDS)
                        );
                    } catch (Exception e) {
                        log.error("[pollPptProgress] 进度查询异常, sid={}", sid, e);
                        // 出错后继续重试
                        return pollPptProgress(sid, resourceId,
                                elapsedSeconds + PPT_POLL_INTERVAL_SECONDS);
                    }
                });
    }

    /**
     * 获取 PPT 配置（处理配置为 null 的情况）
     */
    private YudaoAiProperties.XingHuo.Ppt getPptConfig() {
        YudaoAiProperties.XingHuo xingHuo = yudaoAiProperties.getXinghuo();
        if (xingHuo != null && xingHuo.getPpt() != null) {
            return xingHuo.getPpt();
        }
        // 返回默认配置
        YudaoAiProperties.XingHuo.Ppt defaultPpt = new YudaoAiProperties.XingHuo.Ppt();
        defaultPpt.setDefaultAuthor("AI 教育助手");
        defaultPpt.setIsFigure(true);
        defaultPpt.setAiImage("normal");
        return defaultPpt;
    }

    // ========== CRUD 操作 ==========

    @Override
    public AiLearningResourceDO getResource(Long id) {
        return learningResourceMapper.selectById(id);
    }

    @Override
    public PageResult<AiLearningResourceDO> getResourcePage(LearningResourcePageReqVO reqVO) {
        return learningResourceMapper.selectPage(reqVO);
    }

    @Override
    public void deleteResource(Long id) {
        if (learningResourceMapper.selectById(id) == null) {
            throw exception(LEARNING_RESOURCE_NOT_EXISTS);
        }
        learningResourceMapper.deleteById(id);
    }
}
