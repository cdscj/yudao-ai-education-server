package cn.iocoder.yudao.module.ai.service.agent;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConfigDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.model.AiModelDO;
import cn.iocoder.yudao.module.ai.dal.mysql.agent.AiAgentConfigMapper;
import cn.iocoder.yudao.module.ai.enums.agent.AiAgentTypeEnum;
import cn.iocoder.yudao.module.ai.service.model.AiModelService;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI Agent 配置 Service 实现类
 *
 * @author yudao
 */
@Service
@Validated
public class AiAgentConfigServiceImpl implements AiAgentConfigService {

    @Resource
    private AiAgentConfigMapper agentConfigMapper;

    @Resource
    private AiModelService modelService;

    @Override
    public Long createAgentConfig(AiAgentConfigSaveReqVO createReqVO) {
        // 1. 校验：类型合法
        AiAgentTypeEnum agentType = AiAgentTypeEnum.getByType(createReqVO.getType());
        if (agentType == null) {
            throw exception(AGENT_TYPE_NOT_SUPPORTED, createReqVO.getType());
        }
        // 2. 校验：模型可用
        AiModelDO model = modelService.validateModel(createReqVO.getModelId());

        // 3. 校验：名称唯一
        AiAgentConfigDO existing = agentConfigMapper.selectByName(createReqVO.getName());
        if (existing != null) {
            throw exception(AGENT_CONFIG_NAME_DUPLICATE);
        }

        // 4. 插入
        AiAgentConfigDO config = BeanUtils.toBean(createReqVO, AiAgentConfigDO.class);
        agentConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    @CacheEvict(cacheNames = "ai:agent:config", allEntries = true)
    public void updateAgentConfig(AiAgentConfigSaveReqVO updateReqVO) {
        // 1. 校验存在
        validateAgentConfigExists(updateReqVO.getId());
        // 2. 校验类型
        AiAgentTypeEnum agentType = AiAgentTypeEnum.getByType(updateReqVO.getType());
        if (agentType == null) {
            throw exception(AGENT_TYPE_NOT_SUPPORTED, updateReqVO.getType());
        }
        // 3. 校验模型
        modelService.validateModel(updateReqVO.getModelId());

        // 4. 更新
        AiAgentConfigDO updateObj = BeanUtils.toBean(updateReqVO, AiAgentConfigDO.class);
        agentConfigMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = "ai:agent:config", allEntries = true)
    public void deleteAgentConfig(Long id) {
        validateAgentConfigExists(id);
        agentConfigMapper.deleteById(id);
    }

    private AiAgentConfigDO validateAgentConfigExists(Long id) {
        AiAgentConfigDO config = agentConfigMapper.selectById(id);
        if (config == null) {
            throw exception(AGENT_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    @Cacheable(cacheNames = "ai:agent:config", key = "#id", unless = "#result == null")
    public AiAgentConfigDO getAgentConfig(Long id) {
        return agentConfigMapper.selectById(id);
    }

    @Override
    public PageResult<AiAgentConfigDO> getAgentConfigPage(AiAgentConfigPageReqVO pageReqVO) {
        return agentConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public AiAgentConfigDO validateAgentConfig(Long id) {
        AiAgentConfigDO config = validateAgentConfigExists(id);
        if (CommonStatusEnum.isDisable(config.getStatus())) {
            throw exception(AGENT_CONFIG_DISABLE, config.getName());
        }
        // 同时校验关联模型是否可用
        modelService.validateModel(config.getModelId());
        return config;
    }

    @Override
    public List<AiAgentConfigDO> getAgentConfigListByStatusAndType(Integer status, Integer type) {
        return agentConfigMapper.selectListByStatusAndType(status, type);
    }
}
