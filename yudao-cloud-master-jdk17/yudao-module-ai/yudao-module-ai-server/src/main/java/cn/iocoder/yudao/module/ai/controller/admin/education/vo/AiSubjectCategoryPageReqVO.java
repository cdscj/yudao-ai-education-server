package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - 学科分类分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSubjectCategoryPageReqVO extends PageParam {
    private String name;
    private Integer status;
}
