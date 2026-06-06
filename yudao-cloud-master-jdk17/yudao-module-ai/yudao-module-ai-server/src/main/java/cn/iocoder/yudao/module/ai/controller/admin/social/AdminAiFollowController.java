package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.follow.AiFollowPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.follow.AiFollowRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFollowDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFollowMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 关注管理")
@RestController
@RequestMapping("/ai/social/follow")
@Validated
public class AdminAiFollowController {

    @Resource
    private AiFollowMapper followMapper;

    @GetMapping("/page")
    @Operation(summary = "获得关注分页")
    @PreAuthorize("@ss.hasPermission('ai:follow:query')")
    public CommonResult<PageResult<AiFollowRespVO>> getFollowPage(@Valid AiFollowPageReqVO pageReqVO) {
        PageResult<AiFollowDO> pageResult = followMapper.selectPage(pageReqVO,
                new LambdaQueryWrapperX<AiFollowDO>()
                        .eqIfPresent(AiFollowDO::getUserId, pageReqVO.getUserId())
                        .eqIfPresent(AiFollowDO::getFollowUserId, pageReqVO.getFollowUserId())
                        .orderByDesc(AiFollowDO::getId));
        return success(BeanUtils.toBean(pageResult, AiFollowRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除关注关系")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('ai:follow:delete')")
    public CommonResult<Boolean> deleteFollow(@RequestParam("id") Long id) {
        followMapper.deleteById(id);
        return success(true);
    }

}
