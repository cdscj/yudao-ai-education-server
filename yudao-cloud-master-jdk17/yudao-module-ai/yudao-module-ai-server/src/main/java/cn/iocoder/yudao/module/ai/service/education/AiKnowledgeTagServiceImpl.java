package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiKnowledgeTagDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiKnowledgeTagMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.KNOWLEDGE_TAG_NOT_EXISTS;

@Service
@Validated
public class AiKnowledgeTagServiceImpl implements AiKnowledgeTagService {

    @Resource
    private AiKnowledgeTagMapper mapper;

    @Override
    public Long createKnowledgeTag(AiKnowledgeTagDO tag) {
        mapper.insert(tag);
        return tag.getId();
    }

    @Override
    public void updateKnowledgeTag(AiKnowledgeTagDO tag) {
        validateExists(tag.getId());
        mapper.updateById(tag);
    }

    @Override
    public void deleteKnowledgeTag(Long id) {
        validateExists(id);
        mapper.deleteById(id);
    }

    @Override
    public AiKnowledgeTagDO getKnowledgeTag(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<AiKnowledgeTagDO> getKnowledgeTagPage(Long subjectId, String name, Integer status,
                                                             Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectId, name, status);
    }

    @Override
    public List<AiKnowledgeTagDO> getKnowledgeTagListBySubjectId(Long subjectId) {
        return mapper.selectListBySubjectId(subjectId);
    }

    private void validateExists(Long id) {
        if (mapper.selectById(id) == null) {
            throw exception(KNOWLEDGE_TAG_NOT_EXISTS);
        }
    }
}
