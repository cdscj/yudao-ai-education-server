package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.education.vo.*;
import cn.iocoder.yudao.module.ai.dal.dataobject.education.AiLearningResourceDO;
import reactor.core.publisher.Flux;

/**
 * 学习资源服务接口
 *
 * <p>支持多种资源类型的 AI 生成：
 * <ul>
 *   <li>DOCUMENT - 课程讲解文档（流式 Markdown）</li>
 *   <li>MIND_MAP - 知识点思维导图（流式 Markdown）</li>
 *   <li>EXERCISE - 练习题（流式文本）</li>
 *   <li>READING - 拓展阅读材料（流式文本）</li>
 *   <li>CODE_EXAMPLE - 代码实操案例（流式文本）</li>
 *   <li>PPT - 课件 PPT（异步生成，含进度追踪）</li>
 * </ul>
 * </p>
 *
 * @author yudao
 */
public interface AiLearningResourceService {

    /**
     * 通用资源生成（流式 SSE）
     *
     * <p>支持类型：DOCUMENT, MIND_MAP, EXERCISE, READING, CODE_EXAMPLE</p>
     */
    Flux<CommonResult<String>> generateResource(LearningResourceGenerateReqVO reqVO, Long userId);

    /**
     * PPT 资源生成（异步，含进度追踪事件）
     *
     * <p>使用讯飞智能 PPT API 生成课件，返回 SSE 事件流：</p>
     * <pre>
     *   event: progress  → {"stage":"OUTLINE","percent":30}
     *   event: progress  → {"stage":"GENERATING","percent":60}
     *   event: complete  → {"pptUrl":"https://...", "coverImgSrc":"https://..."}
     *   event: error     → {"message":"错误信息"}
     * </pre>
     */
    Flux<CommonResult<String>> generatePptResource(LearningResourceGenerateReqVO reqVO, Long userId);

    AiLearningResourceDO getResource(Long id);

    PageResult<AiLearningResourceDO> getResourcePage(LearningResourcePageReqVO reqVO);

    void deleteResource(Long id);

}
