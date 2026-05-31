package cn.iocoder.yudao.module.ai.controller.admin.knowledge.vo.document;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - AI 知识库文档上传 Request VO")
@Data
public class AiKnowledgeDocumentUploadReqVO {

    @Schema(description = "知识库编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1204")
    @NotNull(message = "知识库编号不能为空")
    private Long knowledgeId;

    @Schema(description = "文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    @Schema(description = "文档名称", example = "课程资料.pdf")
    private String name;

    @Schema(description = "分段的最大 Token 数", requiredMode = Schema.RequiredMode.REQUIRED, example = "800")
    @NotNull(message = "分段的最大 Token 数不能为空")
    private Integer segmentMaxTokens;

}
