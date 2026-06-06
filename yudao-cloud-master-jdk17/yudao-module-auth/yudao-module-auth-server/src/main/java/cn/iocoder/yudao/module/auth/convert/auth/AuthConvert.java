package cn.iocoder.yudao.module.auth.convert.auth;

import cn.iocoder.yudao.framework.common.biz.system.oauth2.dto.OAuth2AccessTokenRespDTO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.auth.controller.app.auth.vo.*;
import cn.iocoder.yudao.module.auth.controller.app.user.vo.AppUserResetPasswordReqVO;
import cn.iocoder.yudao.module.auth.controller.app.user.vo.AppUserRespVO;
import cn.iocoder.yudao.module.auth.dal.dataobject.user.UserDO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeSendReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeUseReqDTO;
import cn.iocoder.yudao.module.system.api.sms.dto.code.SmsCodeValidateReqDTO;
import cn.iocoder.yudao.module.system.api.social.dto.SocialUserBindReqDTO;
import cn.iocoder.yudao.module.system.enums.sms.SmsSceneEnum;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AuthConvert {

    AuthConvert INSTANCE = Mappers.getMapper(AuthConvert.class);

    // 社交登录绑定
    SocialUserBindReqDTO convert(Long userId, Integer userType, AppAuthSocialLoginReqVO reqVO);

    // 短信验证码
    SmsCodeSendReqDTO convert(AppAuthSmsSendReqVO reqVO);
    SmsCodeUseReqDTO convert(AppUserResetPasswordReqVO reqVO, SmsSceneEnum scene, String usedIp);
    SmsCodeUseReqDTO convert(AppAuthSmsLoginReqVO reqVO, Integer scene, String usedIp);
    SmsCodeValidateReqDTO convert(AppAuthSmsValidateReqVO bean);

    // 登录返回
    AppAuthLoginRespVO convert(OAuth2AccessTokenRespDTO bean, String openid);

    // 用户分页
    PageResult<AppUserRespVO> convertPage(PageResult<UserDO> page);
    AppUserRespVO convert(UserDO bean);

}