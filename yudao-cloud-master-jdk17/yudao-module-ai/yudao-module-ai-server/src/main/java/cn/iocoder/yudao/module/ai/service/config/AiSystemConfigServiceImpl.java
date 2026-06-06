package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSystemConfigDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiSystemConfigMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SYSTEM_CONFIG_KEY_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SYSTEM_CONFIG_NOT_EXISTS;

/**
 * AI 系统配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiSystemConfigServiceImpl implements AiSystemConfigService {

    @Resource
    private AiSystemConfigMapper systemConfigMapper;

    @Override
    @CacheEvict(cacheNames = "ai:system_config", allEntries = true)
    public Long createSystemConfig(AiSystemConfigSaveReqVO createReqVO) {
        // 校验配置键唯一
        validateSystemConfigKeyUnique(null, createReqVO.getConfigKey());

        // 插入
        AiSystemConfigDO config = BeanUtils.toBean(createReqVO, AiSystemConfigDO.class);
        systemConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    @CacheEvict(cacheNames = "ai:system_config", allEntries = true)
    public void updateSystemConfig(AiSystemConfigSaveReqVO updateReqVO) {
        // 校验存在
        validateSystemConfigExists(updateReqVO.getId());
        // 校验配置键唯一
        validateSystemConfigKeyUnique(updateReqVO.getId(), updateReqVO.getConfigKey());

        // 更新
        AiSystemConfigDO updateObj = BeanUtils.toBean(updateReqVO, AiSystemConfigDO.class);
        systemConfigMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = "ai:system_config", allEntries = true)
    public void deleteSystemConfig(Long id) {
        // 校验存在
        validateSystemConfigExists(id);
        // 删除
        systemConfigMapper.deleteById(id);
    }

    private AiSystemConfigDO validateSystemConfigExists(Long id) {
        AiSystemConfigDO config = systemConfigMapper.selectById(id);
        if (config == null) {
            throw exception(SYSTEM_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    private void validateSystemConfigKeyUnique(Long id, String configKey) {
        AiSystemConfigDO config = systemConfigMapper.selectByKey(configKey);
        if (config == null) {
            return;
        }
        // 如果 id 为空，说明是新增，存在即冲突
        if (id == null) {
            throw exception(SYSTEM_CONFIG_KEY_EXISTS);
        }
        // 如果 id 不为空，说明是更新，排除自身
        if (!config.getId().equals(id)) {
            throw exception(SYSTEM_CONFIG_KEY_EXISTS);
        }
    }

    @Override
    public AiSystemConfigDO getSystemConfig(Long id) {
        return systemConfigMapper.selectById(id);
    }

    @Override
    @Cacheable(cacheNames = "ai:system_config", key = "#configKey", unless = "#result == null")
    public AiSystemConfigDO getSystemConfigByKey(String configKey) {
        return systemConfigMapper.selectByKey(configKey);
    }

    @Override
    public PageResult<AiSystemConfigDO> getSystemConfigPage(AiSystemConfigPageReqVO pageReqVO) {
        return systemConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public String getConfigValue(String key, String defaultValue) {
        AiSystemConfigDO config = getSystemConfigByKey(key);
        if (config == null) {
            return defaultValue;
        }
        return config.getConfigValue();
    }

    @Override
    public Boolean getConfigBoolean(String key, Boolean defaultValue) {
        AiSystemConfigDO config = getSystemConfigByKey(key);
        if (config == null) {
            return defaultValue;
        }
        String value = config.getConfigValue();
        if ("boolean".equals(config.getValueType())) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    @Override
    public Integer getConfigInteger(String key, Integer defaultValue) {
        AiSystemConfigDO config = getSystemConfigByKey(key);
        if (config == null) {
            return defaultValue;
        }
        String value = config.getConfigValue();
        if ("integer".equals(config.getValueType())) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.error("[getConfigInteger][配置键({}) 的值({}) 无法转换为整数]", key, value, e);
                return defaultValue;
            }
        }
        return defaultValue;
    }

    @Override
    @CacheEvict(cacheNames = "ai:system_config", allEntries = true)
    public void refreshConfigCache() {
        log.info("[refreshConfigCache][成功刷新系统配置缓存]");
    }

}
