package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiQuestionBankMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.QUESTION_NOT_EXISTS;

@Service
@Validated
public class AiQuestionBankServiceImpl implements AiQuestionBankService {

    @Resource
    private AiQuestionBankMapper mapper;

    @Override
    public Long createQuestion(AiQuestionBankDO question) {
        mapper.insert(question);
        return question.getId();
    }

    @Override
    public void updateQuestion(AiQuestionBankDO question) {
        validateExists(question.getId());
        mapper.updateById(question);
    }

    @Override
    public void deleteQuestion(Long id) {
        validateExists(id);
        mapper.deleteById(id);
    }

    @Override
    public AiQuestionBankDO getQuestion(Long id) {
        return mapper.selectById(id);
    }

    @Override
    public PageResult<AiQuestionBankDO> getQuestionPage(Long subjectId, String questionType, Integer difficulty,
                                                         String keyword, Integer status, Integer pageNo, Integer pageSize) {
        return mapper.selectPage(new PageParam().setPageNo(pageNo).setPageSize(pageSize), subjectId, questionType, difficulty, keyword, status);
    }

    @Override
    public List<AiQuestionBankDO> getQuestionListByIds(List<Long> ids) {
        return mapper.selectListByIds(ids);
    }

    private void validateExists(Long id) {
        if (mapper.selectById(id) == null) {
            throw exception(QUESTION_NOT_EXISTS);
        }
    }
}
