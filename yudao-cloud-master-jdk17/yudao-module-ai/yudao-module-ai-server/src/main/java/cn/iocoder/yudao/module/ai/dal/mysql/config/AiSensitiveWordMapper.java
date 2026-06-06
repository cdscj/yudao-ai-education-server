package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.AiSensitiveWordPageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSensitiveWordDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 敏感词 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiSensitiveWordMapper extends BaseMapperX<AiSensitiveWordDO> {

    default PageResult<AiSensitiveWordDO> selectPage(AiSensitiveWordPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiSensitiveWordDO>()
                .likeIfPresent(AiSensitiveWordDO::getWord, reqVO.getWord())
                .eqIfPresent(AiSensitiveWordDO::getLevel, reqVO.getLevel())
                .eqIfPresent(AiSensitiveWordDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiSensitiveWordDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiSensitiveWordDO::getId));
    }

    default List<AiSensitiveWordDO> selectListByStatus(Integer status) {
        return selectList(AiSensitiveWordDO::getStatus, status);
    }

    default List<AiSensitiveWordDO> selectAllEnabled() {
        return selectList(new LambdaQueryWrapperX<AiSensitiveWordDO>()
                .eq(AiSensitiveWordDO::getStatus, 0));
    }

    default AiSensitiveWordDO selectByWord(String word) {
        return selectOne(AiSensitiveWordDO::getWord, word);
    }

}
