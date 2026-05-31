package cn.iocoder.yudao.module.ai.dal.mysql.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.StudentProfilePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiStudentProfileDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AiStudentProfileMapper extends BaseMapperX<AiStudentProfileDO> {

    default AiStudentProfileDO selectByUserId(Long userId) {
        return selectOne(AiStudentProfileDO::getUserId, userId);
    }

    default PageResult<AiStudentProfileDO> selectPage(StudentProfilePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiStudentProfileDO>()
                .eqIfPresent(AiStudentProfileDO::getUserId, reqVO.getUserId())
                .eqIfPresent(AiStudentProfileDO::getMajor, reqVO.getMajor())
                .eqIfPresent(AiStudentProfileDO::getGrade, reqVO.getGrade())
                .orderByDesc(AiStudentProfileDO::getId));
    }
}
