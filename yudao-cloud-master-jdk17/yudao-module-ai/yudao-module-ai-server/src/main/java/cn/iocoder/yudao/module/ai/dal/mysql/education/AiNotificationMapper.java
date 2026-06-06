package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiNotificationDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiNotificationMapper extends BaseMapperX<AiNotificationDO> {

    default PageResult<AiNotificationDO> selectPage(PageParam pp, Long userId, Boolean isRead) {
        return selectPage(pp, new LambdaQueryWrapperX<AiNotificationDO>()
                .eqIfPresent(AiNotificationDO::getUserId, userId)
                .eqIfPresent(AiNotificationDO::getIsRead, isRead)
                .orderByDesc(AiNotificationDO::getId));
    }

    default long selectUnreadCount(Long userId) {
        return selectCount(new LambdaQueryWrapperX<AiNotificationDO>()
                .eq(AiNotificationDO::getUserId, userId)
                .eq(AiNotificationDO::getIsRead, false));
    }
}
