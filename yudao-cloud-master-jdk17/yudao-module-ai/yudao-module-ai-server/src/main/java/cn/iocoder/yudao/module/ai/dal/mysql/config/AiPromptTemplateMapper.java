package cn.iocoder.yudao.module.ai.dal.mysql.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.prompttemplate.AiPromptTemplatePageReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiPromptTemplateDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * AI 提示词模板 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AiPromptTemplateMapper extends BaseMapperX<AiPromptTemplateDO> {

    default PageResult<AiPromptTemplateDO> selectPage(AiPromptTemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AiPromptTemplateDO>()
                .likeIfPresent(AiPromptTemplateDO::getName, reqVO.getName())
                .eqIfPresent(AiPromptTemplateDO::getCategory, reqVO.getCategory())
                .eqIfPresent(AiPromptTemplateDO::getType, reqVO.getType())
                .eqIfPresent(AiPromptTemplateDO::getStatus, reqVO.getStatus())
                .orderByDesc(AiPromptTemplateDO::getId));
    }

    default List<AiPromptTemplateDO> selectListByCategory(String category, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiPromptTemplateDO>()
                .eq(AiPromptTemplateDO::getCategory, category)
                .eqIfPresent(AiPromptTemplateDO::getStatus, status)
                .orderByAsc(AiPromptTemplateDO::getSort));
    }

    default List<AiPromptTemplateDO> selectListByTypeAndStatus(Integer type, Integer status) {
        return selectList(new LambdaQueryWrapperX<AiPromptTemplateDO>()
                .eq(AiPromptTemplateDO::getType, type)
                .eq(AiPromptTemplateDO::getStatus, status)
                .orderByAsc(AiPromptTemplateDO::getSort));
    }

    default AiPromptTemplateDO selectByName(String name) {
        return selectOne(AiPromptTemplateDO::getName, name);
    }

}
