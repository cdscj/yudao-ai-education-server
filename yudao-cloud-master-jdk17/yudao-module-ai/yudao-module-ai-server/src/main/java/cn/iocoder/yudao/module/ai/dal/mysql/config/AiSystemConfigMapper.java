package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sysconfig.AiSystemConfigPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSystemConfigDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 系统配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiSystemConfigMapper extends BaseMapperX<AiSystemConfigDO> {

    default AiSystemConfigDO selectByKey(String configKey) {
        return selectOne(AiSystemConfigDO::getConfigKey, configKey);
    }

    default List<AiSystemConfigDO> selectListByCategory(String category) {
        return selectList(new LambdaQueryWrapperX<AiSystemConfigDO>()
                .eq(AiSystemConfigDO::getCategory, category)
                .orderByAsc(AiSystemConfigDO::getSort));
    }

    default PageResult<AiSystemConfigDO> selectPage(AiSystemConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiSystemConfigDO>()
                .likeIfPresent(AiSystemConfigDO::getConfigKey, reqVO.getConfigKey())
                .eqIfPresent(AiSystemConfigDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiSystemConfigDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiSystemConfigDO::getId));
    }

}
