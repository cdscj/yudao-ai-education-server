package cn.iocoder.yudao.module.ai.controller.admin.config;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSensitiveWordDO;
import cn.iocoder.yudao.module.ai.service.config.AiSensitiveWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 敏感词")
@RestController
@RequestMapping("/ai/sensitive-word")
@Validated
public class AiSensitiveWordController {

    @Resource
    private AiSensitiveWordService sensitiveWordService;

    @PostMapping("/create")
    @Operation(summary = "创建敏感词")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:create')")
    public CommonResult<Long> createSensitiveWord(@Valid @RequestBody AiSensitiveWordSaveReqVO createReqVO) {
        return success(sensitiveWordService.createSensitiveWord(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新敏感词")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:update')")
    public CommonResult<Boolean> updateSensitiveWord(@Valid @RequestBody AiSensitiveWordSaveReqVO updateReqVO) {
        sensitiveWordService.updateSensitiveWord(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除敏感词")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:delete')")
    public CommonResult<Boolean> deleteSensitiveWord(@RequestParam("id") Long id) {
        sensitiveWordService.deleteSensitiveWord(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得敏感词")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:query')")
    public CommonResult<AiSensitiveWordRespVO> getSensitiveWord(@RequestParam("id") Long id) {
        AiSensitiveWordDO sensitiveWord = sensitiveWordService.getSensitiveWord(id);
        return success(BeanUtils.toBean(sensitiveWord, AiSensitiveWordRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得敏感词分页")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:query')")
    public CommonResult<PageResult<AiSensitiveWordRespVO>> getSensitiveWordPage(@Valid AiSensitiveWordPageReqVO pageReqVO) {
        PageResult<AiSensitiveWordDO> pageResult = sensitiveWordService.getSensitiveWordPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiSensitiveWordRespVO.class));
    }

    @PostMapping("/test")
    @Operation(summary = "测试文本敏感词")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:query')")
    public CommonResult<AiSensitiveWordTestRespVO> testContent(@Valid @RequestBody AiSensitiveWordTestReqVO testReqVO) {
        List<String> matchedWords = sensitiveWordService.findAllSensitiveWords(testReqVO.getContent());
        AiSensitiveWordTestRespVO testResult = new AiSensitiveWordTestRespVO();
        testResult.setHasSensitive(!matchedWords.isEmpty());
        testResult.setMatchedWords(matchedWords);
        return success(testResult);
    }

    @PostMapping("/batch-create")
    @Operation(summary = "批量创建敏感词")
    @PreAuthorize("@ss.hasPermission('ai:sensitive-word:create')")
    public CommonResult<Boolean> batchCreate(@Valid @RequestBody List<AiSensitiveWordSaveReqVO> createReqVOs) {
        sensitiveWordService.batchCreateSensitiveWord(createReqVOs);
        return success(true);
    }

}
