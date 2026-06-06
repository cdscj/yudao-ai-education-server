package cn.iocoder.yudao.module.ai.service.model;

import cn.iocoder.yudao.module.ai.enums.model.AiPlatformEnum;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.AiModelFactory;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.midjourney.api.MidjourneyApi;
import cn.iocoder.yudao.module.ai.framework.ai.core.model.suno.api.SunoApi;
import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.model.vo.model.AiModelSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiApiKeyDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.model.AiChatMapper;
import com.agentsflex.llm.ollama.OllamaLlm;
import com.agentsflex.llm.ollama.OllamaLlmConfig;
import com.agentsflex.llm.qwen.QwenLlm;
import com.agentsflex.llm.qwen.QwenLlmConfig;
import dev.tinyflow.core.Tinyflow;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.ai.vectorstore.qdrant.QdrantVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;

import cn.iocoder.yudao.module.ai.framework.ai.config.YudaoAiProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 模型 Service 实现类
 *
 * @author fansili
 */
@Service
@Validated
public class AiModelServiceImpl implements AiModelService {

    @Resource
    private AiApiKeyService apiKeyService;

    @Resource
    private AiChatMapper modelMapper;

    @Resource
    private AiModelFactory modelFactory;

    @Resource
    private YudaoAiProperties yudaoAiProperties;

    @Override
    public Long createModel(AiModelSaveReqVO createReqVO) {
        // 1. 校验
        AiPlatformEnum.validatePlatform(createReqVO.getPlatform());
        apiKeyService.validateApiKey(createReqVO.getKeyId());

        // 2. 插入
        AiModelDO model = BeanUtils.toBean(createReqVO, AiModelDO.class);
        modelMapper.insert(model);
        return model.getId();
    }

    @Override
    @CacheEvict(cacheNames = {"ai:model", "ai:model:enabled"}, allEntries = true)
    public void updateModel(AiModelSaveReqVO updateReqVO) {
        // 1. 校验
        validateModelExists(updateReqVO.getId());
        AiPlatformEnum.validatePlatform(updateReqVO.getPlatform());
        apiKeyService.validateApiKey(updateReqVO.getKeyId());

        // 2. 更新
        AiModelDO updateObj = BeanUtils.toBean(updateReqVO, AiModelDO.class);
        modelMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = {"ai:model", "ai:model:enabled"}, allEntries = true)
    public void deleteModel(Long id) {
        // 校验存在
        validateModelExists(id);
        // 删除
        modelMapper.deleteById(id);
    }

    private AiModelDO validateModelExists(Long id) {
        AiModelDO model = modelMapper.selectById(id);
        if (modelMapper.selectById(id) == null) {
            throw exception(MODEL_NOT_EXISTS);
        }
        return model;
    }

    @Override
    @Cacheable(cacheNames = "ai:model", key = "#id", unless = "#result == null")
    public AiModelDO getModel(Long id) {
        return modelMapper.selectById(id);
    }

    @Override
    public AiModelDO getRequiredDefaultModel(Integer type) {
        AiModelDO model = modelMapper.selectFirstByStatus(type, CommonStatusEnum.ENABLE.getStatus());
        if (model == null) {
            throw exception(MODEL_DEFAULT_NOT_EXISTS);
        }
        return model;
    }

    @Override
    public PageResult<AiModelDO> getModelPage(AiModelPageReqVO pageReqVO) {
        return modelMapper.selectPage(pageReqVO);
    }

    @Override
    public AiModelDO validateModel(Long id) {
        AiModelDO model = validateModelExists(id);
        if (CommonStatusEnum.isDisable(model.getStatus())) {
            throw exception(MODEL_DISABLE);
        }
        return model;
    }

    @Override
    public List<AiModelDO> getModelListByStatusAndType(Integer status, Integer type, String platform) {
        return modelMapper.selectListByStatusAndType(status, type, platform);
    }

    // ========== 与 Spring AI 集成 ==========

    @Override
    @Cacheable(cacheNames = "ai:model:enabled", key = "#type", unless = "#result == null || #result.isEmpty()")
    public List<AiModelDO> getEnabledModels(Integer type) {
        return modelMapper.selectListByStatusAndType(CommonStatusEnum.ENABLE.getStatus(), type, null);
    }

    @Override
    public ChatModel getChatModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateChatModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public ImageModel getImageModel(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        return modelFactory.getOrCreateImageModel(platform, apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public MidjourneyApi getMidjourneyApi(Long id) {
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        return modelFactory.getOrCreateMidjourneyApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public SunoApi getSunoApi() {
        AiApiKeyDO apiKey = apiKeyService.getRequiredDefaultApiKey(
                AiPlatformEnum.SUNO.getPlatform(), CommonStatusEnum.ENABLE.getStatus());
        return modelFactory.getOrCreateSunoApi(apiKey.getApiKey(), apiKey.getUrl());
    }

    @Override
    public VectorStore getOrCreateVectorStore(Long id, Map<String, Class<?>> metadataFields) {
        // 获取模型 + 密钥
        AiModelDO model = validateModel(id);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());

        // 创建或获取 EmbeddingModel 对象
        EmbeddingModel embeddingModel = modelFactory.getOrCreateEmbeddingModel(
                platform, apiKey.getApiKey(), apiKey.getUrl(), model.getModel());

        // 创建或获取 VectorStore 对象（根据配置动态选择类型）
        Class<? extends VectorStore> vectorStoreType = resolveVectorStoreType();
        return modelFactory.getOrCreateVectorStore(vectorStoreType, embeddingModel, metadataFields);
    }

    /**
     * 根据配置解析向量存储类型
     *
     * 显式配置了 yudao.ai.vector-store → 直接返回对应 Class
     * 未配置 → 默认使用 Simple（本地文件存储，零外部依赖）
     */
    private Class<? extends VectorStore> resolveVectorStoreType() {
        String configured = yudaoAiProperties.getVectorStore();
        if ("milvus".equalsIgnoreCase(configured)) {
            return MilvusVectorStore.class;
        }
        if ("qdrant".equalsIgnoreCase(configured)) {
            return QdrantVectorStore.class;
        }
        if ("redis".equalsIgnoreCase(configured)) {
            return RedisVectorStore.class;
        }
        if ("simple".equalsIgnoreCase(configured)) {
            return SimpleVectorStore.class;
        }
        // 未配置，默认使用 Simple 本地文件存储（零外部依赖，开箱即用）
        return SimpleVectorStore.class;
    }

    // TODO @lesan：是不是返回 Llm 对象会好点哈？
    @Override
    public void getLLmProvider4Tinyflow(Tinyflow tinyflow, Long modelId) {
        AiModelDO model = validateModel(modelId);
        AiApiKeyDO apiKey = apiKeyService.validateApiKey(model.getKeyId());
        AiPlatformEnum platform = AiPlatformEnum.validatePlatform(apiKey.getPlatform());
        switch (platform) {
            // TODO @lesan 考虑到未来不需要使用agents-flex 现在仅测试通义千问
            // TODO @lesan：【重要】是不是可以实现一个 SpringAiLlm，这样的话，内部全部用它就好了。只实现 chat 部分；这样，就把 flex 作为一个 agent 框架，内部调用，还是 spring ai 相关的。成本可能低一点？！
            case TONG_YI:
                QwenLlmConfig qwenLlmConfig = new QwenLlmConfig();
                qwenLlmConfig.setApiKey(apiKey.getApiKey());
                qwenLlmConfig.setModel(model.getModel());
                // TODO @lesan：这个有点奇怪。。。如果一个链式里，有多个模型，咋整呀。。。
                tinyflow.setLlmProvider(id -> new QwenLlm(qwenLlmConfig));
                break;
            case OLLAMA:
                OllamaLlmConfig ollamaLlmConfig = new OllamaLlmConfig();
                ollamaLlmConfig.setEndpoint(apiKey.getUrl());
                ollamaLlmConfig.setModel(model.getModel());
                tinyflow.setLlmProvider(id -> new OllamaLlm(ollamaLlmConfig));
                break;
        }
    }

}