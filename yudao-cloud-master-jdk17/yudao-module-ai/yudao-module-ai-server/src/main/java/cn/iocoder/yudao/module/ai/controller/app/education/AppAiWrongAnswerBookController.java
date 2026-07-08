package cn.iocoder.yudao.module.ai.controller.app.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.AiWrongAnswerBookRespVO;
import cn.iocoder.yudao.module.ai.controller.app.education.vo.AppWrongAnswerRecordReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiPracticeQuestionDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiQuestionBankDO;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiWrongAnswerBookDO;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiPracticeQuestionMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiQuestionBankMapper;
import cn.iocoder.yudao.module.ai.service.education.AiWrongAnswerBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;
import static cn.iocoder.yudao.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 错题本")
@RestController
@RequestMapping("/ai/education/wrong-answer")
@Validated
public class AppAiWrongAnswerBookController {

    @Resource private AiWrongAnswerBookService wrongAnswerBookService;
    @Resource private AiQuestionBankMapper questionBankMapper;
    @Resource private AiPracticeQuestionMapper practiceQuestionMapper;

    @GetMapping("/page")
    @Operation(summary = "获得我的错题本分页")
    public CommonResult<PageResult<AiWrongAnswerBookRespVO>> page(
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestParam(value = "masteryLevel", required = false) Integer masteryLevel,
            @RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        PageResult<AiWrongAnswerBookDO> pageResult = wrongAnswerBookService.getWrongAnswerPage(
                getLoginUserId(), subjectId, masteryLevel, pageNo, pageSize);
        PageResult<AiWrongAnswerBookRespVO> result = BeanUtils.toBean(pageResult, AiWrongAnswerBookRespVO.class);
        // 批量查询题目内容
        enrichWithQuestionTitles(result.getList());
        return success(result);
    }

    @GetMapping("/get")
    @Operation(summary = "获得错题详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1")
    public CommonResult<AiWrongAnswerBookRespVO> get(@RequestParam("id") Long id) {
        AiWrongAnswerBookDO record = wrongAnswerBookService.getWrongAnswer(id);
        AiWrongAnswerBookRespVO vo = BeanUtils.toBean(record, AiWrongAnswerBookRespVO.class);
        if (record != null) enrichSingleQuestionTitle(vo, record.getQuestionId());
        return success(vo);
    }

    /** 批量填充题目内容 */
    private void enrichWithQuestionTitles(List<AiWrongAnswerBookRespVO> list) {
        if (list == null || list.isEmpty()) return;
        for (AiWrongAnswerBookRespVO vo : list) {
            if (vo.getQuestionId() != null) enrichSingleQuestionTitle(vo, vo.getQuestionId());
        }
    }

    private void enrichSingleQuestionTitle(AiWrongAnswerBookRespVO vo, Long questionId) {
        // 先查题库
        AiQuestionBankDO q = questionBankMapper.selectById(questionId);
        if (q != null) {
            vo.setQuestionTitle(q.getTitle());
            vo.setQuestionOptions(q.getOptions());
            return;
        }
        // 再查练习题
        AiPracticeQuestionDO pq = practiceQuestionMapper.selectById(questionId);
        if (pq != null) {
            vo.setQuestionTitle(pq.getTitle());
            vo.setQuestionOptions(pq.getOptions());
        }
    }

    @PostMapping("/review")
    @Operation(summary = "复习错题")
    @Parameter(name = "id", description = "错题编号", required = true, example = "1")
    public CommonResult<Boolean> review(@RequestParam("id") Long id) {
        wrongAnswerBookService.reviewQuestion(id);
        return success(true);
    }

    @GetMapping("/stats")
    @Operation(summary = "获得我的错题统计")
    public CommonResult<Map<String, Object>> stats() {
        return success(wrongAnswerBookService.getStats(getLoginUserId()));
    }

    @GetMapping("/weak-points")
    @Operation(summary = "获得薄弱知识点分析")
    public CommonResult<List<AiWrongAnswerBookService.WeakPointVO>> weakPoints(
            @RequestParam(value = "subjectId", required = false) Long subjectId) {
        return success(wrongAnswerBookService.getWeakPointAnalysis(getLoginUserId(), subjectId));
    }

    @PostMapping("/record")
    @Operation(summary = "记录答题（自动归集错题）")
    public CommonResult<Boolean> record(@RequestBody @Valid AppWrongAnswerRecordReqVO reqVO) {
        AiWrongAnswerBookDO record = new AiWrongAnswerBookDO();
        record.setUserId(getLoginUserId()).setQuestionId(reqVO.getQuestionId())
              .setSubjectId(reqVO.getSubjectId()).setUserAnswer(reqVO.getUserAnswer())
              .setCorrectAnswer(reqVO.getCorrectAnswer()).setIsCorrect(reqVO.getIsCorrect())
              .setKnowledgeTagIds(reqVO.getKnowledgeTagIds())
              .setSourceType(reqVO.getSourceType()).setSourceId(reqVO.getSourceId());
        wrongAnswerBookService.recordAnswer(record);
        return success(true);
    }
}
