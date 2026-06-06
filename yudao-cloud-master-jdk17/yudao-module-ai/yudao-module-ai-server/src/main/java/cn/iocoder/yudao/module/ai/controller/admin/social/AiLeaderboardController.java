package cn.iocoder.yudao.module.ai.controller.admin.social;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.leaderboard.AiLeaderboardPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.social.vo.leaderboard.AiLeaderboardRespVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.social.AiLeaderboardRecordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.social.AiLeaderboardRecordMapper;
import cn.iocoder.yudao.module.ai.dal.redis.AiLeaderboardRedisDAO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - AI 排行榜")
@RestController
@RequestMapping("/ai/social/leaderboard")
@Validated
@Slf4j
public class AiLeaderboardController {

    @Resource
    private AiLeaderboardRedisDAO leaderboardRedisDAO;

    @Resource
    private AiLeaderboardRecordMapper leaderboardRecordMapper;

    @GetMapping("/page")
    @Operation(summary = "获得排行榜分页")
    @PreAuthorize("@ss.hasPermission('ai:social:leaderboard-query')")
    public CommonResult<PageResult<AiLeaderboardRespVO>> getLeaderboardPage(
            @Valid AiLeaderboardPageReqVO pageReqVO) {
        PageResult<AiLeaderboardRecordDO> pageResult = leaderboardRecordMapper.selectPage(
                pageReqVO.getUserId(), pageReqVO.getPeriodType(),
                pageReqVO.getSnapshotDate(), pageReqVO);
        return success(BeanUtils.toBean(pageResult, AiLeaderboardRespVO.class));
    }

    @PostMapping("/manual-archive")
    @Operation(summary = "手动触发排行榜归档")
    @PreAuthorize("@ss.hasPermission('ai:social:leaderboard-archive')")
    public CommonResult<Boolean> manualArchive() {
        log.info("[manualArchive][手动触发排行榜数据归档开始]");
        LocalDate today = LocalDate.now();
        String[] periodTypes = {"WEEKLY", "MONTHLY"};

        for (String periodType : periodTypes) {
            try {
                Set<ZSetOperations.TypedTuple<Object>> scores = leaderboardRedisDAO.getAllWithScores(periodType);
                if (scores == null || scores.isEmpty()) {
                    log.info("[manualArchive][周期 {} 暂无数据，跳过归档]", periodType);
                    continue;
                }

                int rank = 1;
                for (ZSetOperations.TypedTuple<Object> tuple : scores) {
                    if (tuple.getValue() == null) {
                        rank++;
                        continue;
                    }
                    Long userId = Long.valueOf(tuple.getValue().toString());
                    Integer score = tuple.getScore() != null ? tuple.getScore().intValue() : 0;

                    AiLeaderboardRecordDO existing = leaderboardRecordMapper.selectOne(
                            AiLeaderboardRecordDO::getUserId, userId,
                            AiLeaderboardRecordDO::getPeriodType, periodType,
                            AiLeaderboardRecordDO::getSnapshotDate, today);

                    if (existing != null) {
                        existing.setScore(score);
                        existing.setRank(rank);
                        leaderboardRecordMapper.updateById(existing);
                    } else {
                        AiLeaderboardRecordDO record = new AiLeaderboardRecordDO();
                        record.setUserId(userId);
                        record.setPeriodType(periodType);
                        record.setScore(score);
                        record.setRank(rank);
                        record.setSnapshotDate(today);
                        leaderboardRecordMapper.insert(record);
                    }
                    rank++;
                }
                log.info("[manualArchive][周期 {} 归档完成，共 {} 条]", periodType, scores.size());
            } catch (Exception e) {
                log.error("[manualArchive][周期 {} 归档异常]", periodType, e);
            }
        }
        log.info("[manualArchive][手动触发排行榜数据归档完成]");
        return success(true);
    }

}
