package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiPromptTemplateMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.PROMPT_TEMPLATE_NAME_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.PROMPT_TEMPLATE_NOT_EXISTS;

/**
 * AI 提示词模板 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiPromptTemplateServiceImpl implements AiPromptTemplateService {

    @Resource
    private AiPromptTemplateMapper promptTemplateMapper;

    @Override
    public Long createPromptTemplate(AiPromptTemplateSaveReqVO createReqVO) {
        // 1. 校验名称唯一
        validatePromptTemplateNameUnique(null, createReqVO.getName());

        // 2. 插入
        AiPromptTemplateDO template = BeanUtils.toBean(createReqVO, AiPromptTemplateDO.class);
        promptTemplateMapper.insert(template);
        return template.getId();
    }

    @Override
    public void updatePromptTemplate(AiPromptTemplateSaveReqVO updateReqVO) {
        // 1. 校验存在
        validatePromptTemplateExists(updateReqVO.getId());
        // 1.2 校验名称唯一
        validatePromptTemplateNameUnique(updateReqVO.getId(), updateReqVO.getName());

        // 2. 更新
        AiPromptTemplateDO updateObj = BeanUtils.toBean(updateReqVO, AiPromptTemplateDO.class);
        promptTemplateMapper.updateById(updateObj);
    }

    @Override
    public void deletePromptTemplate(Long id) {
        // 校验存在
        validatePromptTemplateExists(id);
        // 删除
        promptTemplateMapper.deleteById(id);
    }

    private AiPromptTemplateDO validatePromptTemplateExists(Long id) {
        AiPromptTemplateDO template = promptTemplateMapper.selectById(id);
        if (template == null) {
            throw exception(PROMPT_TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    private void validatePromptTemplateNameUnique(Long id, String name) {
        AiPromptTemplateDO template = promptTemplateMapper.selectByName(name);
        if (template == null) {
            return;
        }
        // 如果 id 为空，说明是新增，存在即冲突
        if (id == null) {
            throw exception(PROMPT_TEMPLATE_NAME_EXISTS);
        }
        // 如果 id 不为空，说明是更新，排除自身
        if (!template.getId().equals(id)) {
            throw exception(PROMPT_TEMPLATE_NAME_EXISTS);
        }
    }

    @Override
    public AiPromptTemplateDO getPromptTemplate(Long id) {
        return promptTemplateMapper.selectById(id);
    }

    @Override
    public PageResult<AiPromptTemplateDO> getPromptTemplatePage(AiPromptTemplatePageReqVO pageReqVO) {
        return promptTemplateMapper.selectPage(pageReqVO);
    }

    @Override
    public List<AiPromptTemplateDO> getPromptTemplateListByCategory(String category) {
        return promptTemplateMapper.selectListByCategory(category, null);
    }

    @Override
    public List<AiPromptTemplateDO> getEnabledPromptTemplateList(Integer type) {
        return promptTemplateMapper.selectListByTypeAndStatus(type, CommonStatusEnum.ENABLE.getStatus());
    }

}
