package cn.iocoder.yudao.module.ai.service.school;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.app.school.vo.AiStudentSchoolSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiStudentSchoolDO;
import cn.iocoder.yudao.module.ai.dal.mysql.school.AiSchoolMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.school.AiStudentSchoolMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

/**
 * AI 学生学校关联 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiStudentSchoolServiceImpl implements AiStudentSchoolService {

    @Resource
    private AiStudentSchoolMapper studentSchoolMapper;
    @Resource
    private AiSchoolMapper schoolMapper;

    @Override
    public AiStudentSchoolDO getMySchool(Long userId) {
        return studentSchoolMapper.selectByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindSchool(Long userId, AiStudentSchoolSaveReqVO reqVO) {
        // 校验学校是否存在
        AiSchoolDO school = schoolMapper.selectById(reqVO.getSchoolId());
        if (school == null) {
            throw exception(SCHOOL_NOT_EXISTS);
        }

        // 查询已有的绑定记录
        AiStudentSchoolDO existing = studentSchoolMapper.selectByUserId(userId);
        if (existing != null) {
            // 已存在则更新
            AiStudentSchoolDO updateObj = BeanUtils.toBean(reqVO, AiStudentSchoolDO.class);
            updateObj.setId(existing.getId());
            updateObj.setUserId(userId);
            studentSchoolMapper.updateById(updateObj);
        } else {
            // 不存在则插入
            AiStudentSchoolDO insertObj = BeanUtils.toBean(reqVO, AiStudentSchoolDO.class);
            insertObj.setUserId(userId);
            studentSchoolMapper.insert(insertObj);
        }
    }

    @Override
    public List<AiStudentSchoolDO> getClassmates(Long userId) {
        AiStudentSchoolDO mySchool = studentSchoolMapper.selectByUserId(userId);
        if (mySchool == null) {
            return Collections.emptyList();
        }
        // 查找同学校同年级的同学
        return studentSchoolMapper.selectListBySchoolIdAndGrade(mySchool.getSchoolId(), mySchool.getGrade());
    }

}
