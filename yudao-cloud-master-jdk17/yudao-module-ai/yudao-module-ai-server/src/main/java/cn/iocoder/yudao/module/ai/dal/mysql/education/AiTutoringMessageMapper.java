package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiTutoringMessageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AiTutoringMessageMapper extends BaseMapperX<AiTutoringMessageDO> {

    default List<AiTutoringMessageDO> selectListBySessionId(Long sessionId) {
        return selectList(new LambdaQueryWrapperX<AiTutoringMessageDO>()
                .eq(AiTutoringMessageDO::getSessionId, sessionId)
                .orderByAsc(AiTutoringMessageDO::getId));
    }
}
