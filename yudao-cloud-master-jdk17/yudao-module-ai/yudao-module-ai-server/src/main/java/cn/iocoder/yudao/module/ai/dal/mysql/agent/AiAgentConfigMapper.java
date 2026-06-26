package cn.iocoder.yudao.module.ai.dal.mysql.agent;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.agent.vo.AiAgentConfigPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.agent.AiAgentConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI Agent 配置 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AiAgentConfigMapper extends BaseMapperX<AiAgentConfigDO> {

    /**
     * 根据状态和类型获取启用的 Agent 列表
     *
     * @param status 状态
     * @param type   类型（可为 null）
     * @return Agent 配置列表，按 sort 升序
     */
    default List<AiAgentConfigDO> selectListByStatusAndType(Integer status, Integer type) {
        return selectList(new LambdaQueryWrapperX<AiAgentConfigDO>()
                .eq(AiAgentConfigDO::getStatus, status)
                .eqIfPresent(AiAgentConfigDO::getType, type)
                .orderByAsc(AiAgentConfigDO::getSort));
    }

    /**
     * 分页查询 Agent 配置
     */
    default PageResult<AiAgentConfigDO> selectPage(AiAgentConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiAgentConfigDO>()
                .likeIfPresent(AiAgentConfigDO::getName, reqVO.getName())
                .eqIfPresent(AiAgentConfigDO::getType, reqVO.getType())
                .eqIfPresent(AiAgentConfigDO::getStatus, reqVO.getStatus())
                .orderByAsc(AiAgentConfigDO::getSort));
    }

    /**
     * 根据名字查询 Agent 配置
     *
     * @param name 名称
     * @return Agent 配置，不存在返回 null
     */
    default AiAgentConfigDO selectByName(String name) {
        return selectOne(new LambdaQueryWrapperX<AiAgentConfigDO>()
                .eq(AiAgentConfigDO::getName, name));
    }
}
