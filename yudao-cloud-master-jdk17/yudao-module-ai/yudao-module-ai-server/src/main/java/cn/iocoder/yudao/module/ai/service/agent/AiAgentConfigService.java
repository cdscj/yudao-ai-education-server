package cn.iocoder.yudao.module.ai.service.agent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConfigDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * AI Agent 配置 Service 接口
 *
 * @author yudao
 */
public interface AiAgentConfigService {

    /**
     * 创建 Agent 配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAgentConfig(@Valid AiAgentConfigSaveReqVO createReqVO);

    /**
     * 更新 Agent 配置
     *
     * @param updateReqVO 更新信息
     */
    void updateAgentConfig(@Valid AiAgentConfigSaveReqVO updateReqVO);

    /**
     * 删除 Agent 配置
     *
     * @param id 编号
     */
    void deleteAgentConfig(Long id);

    /**
     * 获得 Agent 配置
     *
     * @param id 编号
     * @return Agent 配置
     */
    AiAgentConfigDO getAgentConfig(Long id);

    /**
     * 获得 Agent 配置分页
     *
     * @param pageReqVO 分页查询
     * @return Agent 配置分页
     */
    PageResult<AiAgentConfigDO> getAgentConfigPage(AiAgentConfigPageReqVO pageReqVO);

    /**
     * 校验 Agent 配置是否可用
     *
     * @param id 编号
     * @return Agent 配置
     */
    AiAgentConfigDO validateAgentConfig(Long id);

    /**
     * 获得指定类型和状态的 Agent 配置列表
     *
     * @param status 状态
     * @param type   类型（可为 null）
     * @return Agent 配置列表
     */
    List<AiAgentConfigDO> getAgentConfigListByStatusAndType(Integer status, Integer type);
}
