package cn.iocoder.yudao.module.ai.controller.admin.education.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - 学科分类 Response VO")
@Data
public class AiSubjectCategoryRespVO {
    private Long id;
    private String name;
    private String code;
    private Long parentId;
    private String icon;
    private String description;
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
}
