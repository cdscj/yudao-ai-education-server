package cn.iocoder.yudao.module.ai.controller.admin.school.vo;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Schema(description = "管理后台 - AI 学校分页请求 VO")
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSchoolPageReqVO extends PageParam {

    @Schema(description = "学校名称", example = "清华大学")
    private String name;

    @Schema(description = "学校类型", example = "UNIVERSITY")
    private String type;

    @Schema(description = "城市", example = "北京")
    private String city;

}
