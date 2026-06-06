package cn.iocoder.yudao.module.auth.convert.user;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.auth.controller.app.user.vo.AppUserRespVO;
import cn.iocoder.yudao.module.auth.dal.dataobject.user.UserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    // APP 用户信息
    AppUserRespVO convert(UserDO bean);

    // 分页转换
    PageResult<AppUserRespVO> convertPage(PageResult<UserDO> page);

}