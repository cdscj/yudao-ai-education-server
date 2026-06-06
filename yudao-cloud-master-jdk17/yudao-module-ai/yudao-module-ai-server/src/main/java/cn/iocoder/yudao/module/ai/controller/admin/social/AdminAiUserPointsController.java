package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.points.AiUserPointsAddReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.points.AiUserPointsPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.points.AiUserPointsRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiUserPointsDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiUserPointsMapper;
import cn.iocoder.yudao.module.ai.service.social.AiUserPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 用户积分")
@RestController
@RequestMapping("/ai/social/user-points")
@Validated
public class AdminAiUserPointsController {

    @Resource
    private AiUserPointsService userPointsService;

    @Resource
    private AiUserPointsMapper userPointsMapper;

    @GetMapping("/page")
    @Operation(summary = "获得用户积分分页")
    @PreAuthorize("@ss.hasPermission('ai:user-points:query')")
    public CommonResult<PageResult<AiUserPointsRespVO>> getUserPointsPage(
            @Valid AiUserPointsPageReqVO pageReqVO) {
        PageResult<AiUserPointsDO> pageResult = userPointsMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<AiUserPointsDO>()
                        .eqIfPresent(AiUserPointsDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(AiUserPointsDO::getLevel, pageReqVO.getLevel())
                        .orderByDesc(AiUserPointsDO::getId));
        return success(BeanUtils.toBean(pageResult, AiUserPointsRespVO.class));
    }

    @PostMapping("/add-points")
    @Operation(summary = "手动增加用户积分")
    @PreAuthorize("@ss.hasPermission('ai:user-points:query')")
    public CommonResult<AiUserPointsRespVO> addPoints(@Valid @RequestBody AiUserPointsAddReqVO reqVO) {
        AiUserPointsDO userPoints = userPointsService.addPoints(reqVO.getUserId(), reqVO.getPoints(), 0, 0L);
        return success(BeanUtils.toBean(userPoints, AiUserPointsRespVO.class));
    }

    @GetMapping("/get")
    @Operation(summary = "获得用户积分")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:user-points:query')")
    public CommonResult<AiUserPointsRespVO> getUserPoints(@RequestParam("id") Long id) {
        AiUserPointsDO userPoints = userPointsMapper.selectById(id);
        return success(BeanUtils.toBean(userPoints, AiUserPointsRespVO.class));
    }

}
