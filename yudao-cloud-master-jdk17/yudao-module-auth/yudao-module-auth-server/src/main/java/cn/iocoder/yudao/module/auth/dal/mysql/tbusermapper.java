package cn.iocoder.yudao.module.auth.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbUserMapper extends BaseMapperX<TbUserDO> {

    default TbUserDO selectByUsername(String username) {
        return selectOne(TbUserDO::getUsername, username);
    }

    default TbUserDO selectByEmail(String email) {
        return selectOne(TbUserDO::getEmail, email);
    }

    default TbUserDO selectByPhone(String phone) {
        return selectOne(TbUserDO::getPhone, phone);
    }

    default Long selectCountByUsername(String username) {
        return selectCount(TbUserDO::getUsername, username);
    }

    default Long selectCountByEmail(String email) {
        return selectCount(TbUserDO::getEmail, email);
    }

    default Long selectCountByPhone(String phone) {
        return selectCount(TbUserDO::getPhone, phone);
    }
}
