package cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "管理后台 - AI 敏感词测试 Response VO")
@Data
public class AiSensitiveWordTestRespVO {

    @Schema(description = "是否包含敏感词", example = "true")
    private Boolean hasSensitive;

    @Schema(description = "匹配到的敏感词列表", example = "[\"敏感词1\", \"敏感词2\"]")
    private List<String> matchedWords;

    @Schema(description = "过滤后的内容", example = "这是一段包含***的测试文本")
    private String filteredContent;

}
