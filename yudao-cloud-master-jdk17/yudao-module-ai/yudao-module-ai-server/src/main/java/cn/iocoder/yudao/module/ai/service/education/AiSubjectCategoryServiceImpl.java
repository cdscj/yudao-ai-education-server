package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiSubjectCategoryDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiSubjectCategoryMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.*;

@Service
@Validated
@Slf4j
public class AiSubjectCategoryServiceImpl implements AiSubjectCategoryService {

    @Resource
    private AiSubjectCategoryMapper mapper;

    @Override
    @CacheEvict(cacheNames = "ai:subject-category:tree", allEntries = true)
    public Long createSubjectCategory(AiSubjectCategoryDO category) {
        validateCodeUnique(category.getCode(), null);
        try {
            mapper.insert(category);
        } catch (DataIntegrityViolationException e) {
            log.warn("[createSubjectCategory][编码({})唯一键冲突]", category.getCode(), e);
            throw exception(SUBJECT_CATEGORY_CODE_EXISTS);
        }
        return category.getId();
    }

    @Override
    @CacheEvict(cacheNames = "ai:subject-category:tree", allEntries = true)
    public void updateSubjectCategory(AiSubjectCategoryDO category) {
        validateExists(category.getId());
        validateCodeUnique(category.getCode(), category.getId());
        mapper.updateById(category);
    }

    @Override
    @CacheEvict(cacheNames = "ai:subject-category:tree", allEntries = true)
    public void deleteSubjectCategory(Long id) {
        validateExists(id);
        if (!mapper.selectListByParentId(id).isEmpty()) {
            throw exception(SUBJECT_CATEGORY_HAS_CHILDREN);
        }
        mapper.deleteById(id);
    }

    @Override
    public AiSubjectCategoryDO getSubjectCategory(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<AiSubjectCategoryDO> getSubjectCategoryPage(String name, Integer status, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), name, status);
    }

    @Override
    @Cacheable(cacheNames = "ai:subject-category:tree", unless = "#result == null || #result.isEmpty()")
    public List<AiSubjectCategoryDO> getSubjectCategoryTree() {
        return mapper.selectListByStatus(0);
    }

    @Override
    public List<AiSubjectCategoryDO> getEnabledList() {
        return mapper.selectListByStatus(0);
    }

    private void validateExists(Long id) {
        if (mapper.selectById(id) == null) {
            throw exception(SUBJECT_CATEGORY_NOT_EXISTS);
        }
    }

    private void validateCodeUnique(String code, Long excludeId) {
        AiSubjectCategoryDO existing = mapper.selectByCode(code);
        if (existing != null && !existing.getId().equals(excludeId)) {
            throw exception(SUBJECT_CATEGORY_CODE_EXISTS);
        }
    }
}
