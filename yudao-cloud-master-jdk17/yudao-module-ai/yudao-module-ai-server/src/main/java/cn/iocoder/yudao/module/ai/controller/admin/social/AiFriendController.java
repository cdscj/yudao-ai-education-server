package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.friend.AiFriendPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.friend.AiFriendRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiFriendDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiFriendMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 好友管理")
@RestController
@RequestMapping("/ai/social/friend")
@Validated
public class AiFriendController {

    @Resource
    private AiFriendMapper friendMapper;

    @GetMapping("/page")
    @Operation(summary = "获得好友分页")
    @PreAuthorize("@ss.hasPermission('ai:social:friend-query')")
    public CommonResult<PageResult<AiFriendRespVO>> getFriendPage(@Valid AiFriendPageReqVO pageReqVO) {
        PageResult<AiFriendDO> pageResult = friendMapper.selectPage(
                pageReqVO.getUserId(), pageReqVO.getFriendUserId(),
                pageReqVO.getStatus(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiFriendRespVO.class));
    }

    @DeleteMapping("/delete")
    @Operation(summary = "强制删除好友关系")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ai:social:friend-delete')")
    public CommonResult<Boolean> deleteFriend(@RequestParam("id") Long id) {
        friendMapper.deleteByIdForce(id);
        return success(true);
    }

}
