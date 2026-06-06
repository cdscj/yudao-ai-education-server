package cn.iocoder.yudao.module.ai.controller.admin.model.vo.apikey;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(description = "管理后台 - AI API 密钥 Response VO")
@Data
public class AiApiKeyRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "23538")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "文心一言")
    private String name;

    @Schema(description = "密钥（脱敏，仅显示前4后4位）", requiredMode = Schema.RequiredMode.REQUIRED, example = "sk-a***90Df")
    private String apiKey;

    @Schema(description = "平台", requiredMode = Schema.RequiredMode.REQUIRED, example = "OpenAI")
    private String platform;

    @Schema(description = "自定义 API 地址", example = "https://aip.baidubce.com")
    private String url;

    @Schema(description = "状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status;

    public String getApiKey() {
        if (apiKey == null || apiKey.length() <= 8) {
            return apiKey;
        }
        return apiKey.substring(0, 4) + "****" + apiKey.substring(apiKey.length() - 4);
    }

}