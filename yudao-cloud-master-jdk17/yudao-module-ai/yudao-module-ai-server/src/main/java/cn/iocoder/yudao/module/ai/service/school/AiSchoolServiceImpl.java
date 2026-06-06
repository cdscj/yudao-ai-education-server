package cn.iocoder.yudao.module.ai.service.school;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;
import cn.iocoder.yudao.module.ai.dal.mysql.school.AiSchoolMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SCHOOL_NAME_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SCHOOL_NOT_EXISTS;

/**
 * AI 学校信息 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AiSchoolServiceImpl implements AiSchoolService {

    @Resource
    private AiSchoolMapper schoolMapper;

    @Override
    public PageResult<AiSchoolDO> getSchoolList(String name, String type, String city, Integer pageNo, Integer pageSize) {
        PageParam pageParam = new PageParam().setPageNo(pageNo).setPageSize(pageSize);
        return schoolMapper.selectPage(pageParam, name, type, city);
    }

    @Override
    public AiSchoolDO getSchool(Long id) {
        return schoolMapper.selectById(id);
    }

    @Override
    public Long createSchool(AiSchoolDO school) {
        // 校验名称是否存在
        validateSchoolNameExists(school.getName());

        schoolMapper.insert(school);
        return school.getId();
    }

    @Override
    public void updateSchool(AiSchoolDO school) {
        // 校验存在
        validateSchoolExists(school.getId());
        // 校验名称是否存在（排除自身）
        AiSchoolDO existingName = schoolMapper.selectByName(school.getName());
        if (existingName != null && !existingName.getId().equals(school.getId())) {
            throw exception(SCHOOL_NAME_EXISTS);
        }

        schoolMapper.updateById(school);
    }

    @Override
    public void deleteSchool(Long id) {
        // 校验存在
        validateSchoolExists(id);
        schoolMapper.deleteById(id);
    }

    private void validateSchoolExists(Long id) {
        if (schoolMapper.selectById(id) == null) {
            throw exception(SCHOOL_NOT_EXISTS);
        }
    }

    private void validateSchoolNameExists(String name) {
        if (schoolMapper.selectByName(name) != null) {
            throw exception(SCHOOL_NAME_EXISTS);
        }
    }

}
