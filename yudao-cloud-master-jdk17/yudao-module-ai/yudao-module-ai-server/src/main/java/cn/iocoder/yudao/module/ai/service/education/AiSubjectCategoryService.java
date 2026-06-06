package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiSubjectCategoryDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;

import java.util.List;

public interface AiSubjectCategoryService {

    Long createSubjectCategory(AiSubjectCategoryDO category);

    void updateSubjectCategory(AiSubjectCategoryDO category);

    void deleteSubjectCategory(Long id);

    AiSubjectCategoryDO getSubjectCategory(Long id);

    PageResult<AiSubjectCategoryDO> getSubjectCategoryPage(String name, Integer status, Integer pageNo, Integer pageSize);

    List<AiSubjectCategoryDO> getSubjectCategoryTree();

    List<AiSubjectCategoryDO> getEnabledList();
}
