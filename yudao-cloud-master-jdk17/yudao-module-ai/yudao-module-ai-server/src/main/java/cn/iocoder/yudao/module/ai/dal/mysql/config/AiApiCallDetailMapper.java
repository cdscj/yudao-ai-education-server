package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.statistics.AiApiCallDetailPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiApiCallDetailDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI API 调用详情 Mapper
 *
 * @author yudao
 */
@Mapper
public interface AiApiCallDetailMapper extends BaseMapperX<AiApiCallDetailDO> {

    default PageResult<AiApiCallDetailDO> selectPage(AiApiCallDetailPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiApiCallDetailDO>()
                .eqIfPresent(AiApiCallDetailDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiApiCallDetailDO::getModelId, reqVO.getModelId())
                .eqIfPresent(AiApiCallDetailDO::getPlatform, reqVO.getPlatform())
                .eqIfPresent(AiApiCallDetailDO::getApiType, reqVO.getApiType())
                .eqIfPresent(AiApiCallDetailDO::getSuccess, reqVO.getSuccess())
                .betweenIfPresent(AiApiCallDetailDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(AiApiCallDetailDO::getId));
    }
}
