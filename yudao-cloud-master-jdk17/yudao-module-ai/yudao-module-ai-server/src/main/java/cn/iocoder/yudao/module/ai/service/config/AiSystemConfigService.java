package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSystemConfigDO;
import jakarta.validation.Valid;

/**
 * AI 系统配置 Service 接口
 *
 * @author 芋道源码
 */
public interface AiSystemConfigService {

    /**
     * 创建系统配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSystemConfig(@Valid AiSystemConfigSaveReqVO createReqVO);

    /**
     * 更新系统配置
     *
     * @param updateReqVO 更新信息
     */
    void updateSystemConfig(@Valid AiSystemConfigSaveReqVO updateReqVO);

    /**
     * 删除系统配置
     *
     * @param id 编号
     */
    void deleteSystemConfig(Long id);

    /**
     * 获得系统配置
     *
     * @param id 编号
     * @return 系统配置
     */
    AiSystemConfigDO getSystemConfig(Long id);

    /**
     * 根据配置键获得系统配置
     *
     * @param configKey 配置键
     * @return 系统配置
     */
    AiSystemConfigDO getSystemConfigByKey(String configKey);

    /**
     * 获得系统配置分页
     *
     * @param pageReqVO 分页查询
     * @return 系统配置分页
     */
    PageResult<AiSystemConfigDO> getSystemConfigPage(AiSystemConfigPageReqVO pageReqVO);

    /**
     * 获取配置值（字符串）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    String getConfigValue(String key, String defaultValue);

    /**
     * 获取配置值（布尔）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Boolean getConfigBoolean(String key, Boolean defaultValue);

    /**
     * 获取配置值（整数）
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    Integer getConfigInteger(String key, Integer defaultValue);

    /**
     * 刷新配置缓存
     */
    void refreshConfigCache();

}
