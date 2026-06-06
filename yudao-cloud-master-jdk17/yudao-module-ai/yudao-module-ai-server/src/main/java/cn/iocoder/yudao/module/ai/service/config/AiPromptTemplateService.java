package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplateSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * AI 提示词模板 Service 接口
 *
 * @author 芋道源码
 */
public interface AiPromptTemplateService {

    /**
     * 创建提示词模板
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPromptTemplate(@Valid AiPromptTemplateSaveReqVO createReqVO);

    /**
     * 更新提示词模板
     *
     * @param updateReqVO 更新信息
     */
    void updatePromptTemplate(@Valid AiPromptTemplateSaveReqVO updateReqVO);

    /**
     * 删除提示词模板
     *
     * @param id 编号
     */
    void deletePromptTemplate(Long id);

    /**
     * 获得提示词模板
     *
     * @param id 编号
     * @return 提示词模板
     */
    AiPromptTemplateDO getPromptTemplate(Long id);

    /**
     * 获得提示词模板分页
     *
     * @param pageReqVO 分页查询
     * @return 提示词模板分页
     */
    PageResult<AiPromptTemplateDO> getPromptTemplatePage(AiPromptTemplatePageReqVO pageReqVO);

    /**
     * 获得指定分类的提示词模板列表
     *
     * @param category 分类
     * @return 提示词模板列表
     */
    List<AiPromptTemplateDO> getPromptTemplateListByCategory(String category);

    /**
     * 获得指定类型的已启用提示词模板列表
     *
     * @param type 类型
     * @return 提示词模板列表
     */
    List<AiPromptTemplateDO> getEnabledPromptTemplateList(Integer type);

}
