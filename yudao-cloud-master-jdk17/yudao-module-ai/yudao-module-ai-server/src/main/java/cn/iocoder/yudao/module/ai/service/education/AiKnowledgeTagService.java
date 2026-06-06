package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;

import java.util.List;

public interface AiKnowledgeTagService {

    Long createKnowledgeTag(AiKnowledgeTagDO tag);

    void updateKnowledgeTag(AiKnowledgeTagDO tag);

    void deleteKnowledgeTag(Long id);

    AiKnowledgeTagDO getKnowledgeTag(Long id);

    PageResult<AiKnowledgeTagDO> getKnowledgeTagPage(Long subjectId, String name, Integer status, Integer pageNo, Integer pageSize);

    List<AiKnowledgeTagDO> getKnowledgeTagListBySubjectId(Long subjectId);
}
