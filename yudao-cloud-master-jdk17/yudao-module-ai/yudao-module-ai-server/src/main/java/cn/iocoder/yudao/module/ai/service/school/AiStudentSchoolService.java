package cn.iocoder.yudao.module.ai.service.school;

import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiStudentSchoolSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiStudentSchoolDO;

import java.util.List;

/**
 * AI 学生学校关联 Service 接口
 *
 * @author 芋道源码
 */
public interface AiStudentSchoolService {

    /**
     * 获得我的学校信息
     *
     * @param userId 用户编号
     * @return 学生学校关联
     */
    AiStudentSchoolDO getMySchool(Long userId);

    /**
     * 绑定/更新学校信息
     *
     * @param userId 用户编号
     * @param reqVO 绑定信息
     */
    void bindSchool(Long userId, AiStudentSchoolSaveReqVO reqVO);

    /**
     * 获得同校同学列表
     *
     * @param userId 用户编号
     * @return 同学列表
     */
    List<AiStudentSchoolDO> getClassmates(Long userId);

}
