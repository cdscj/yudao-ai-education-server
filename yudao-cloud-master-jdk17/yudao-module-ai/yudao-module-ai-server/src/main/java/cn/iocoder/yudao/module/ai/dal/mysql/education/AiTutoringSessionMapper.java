package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiTutoringSessionDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiTutoringSessionMapper extends BaseMapperX<AiTutoringSessionDO> {

    default List<AiTutoringSessionDO> selectListByUserId(Long userId) {
        return selectList(new LambdaQueryWrapperX<AiTutoringSessionDO>()
                .eq(AiTutoringSessionDO::getUserId, userId)
                .orderByDesc(AiTutoringSessionDO::getId));
    }
}
