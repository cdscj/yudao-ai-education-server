package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.AiSensitiveWordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.AiSensitiveWordSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSensitiveWordDO;
import jakarta.validation.Valid;

import java.util.List;

/**
 * AI 敏感词 Service 接口
 *
 * @author 芋道源码
 */
public interface AiSensitiveWordService {

    /**
     * 创建敏感词
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSensitiveWord(@Valid AiSensitiveWordSaveReqVO createReqVO);

    /**
     * 更新敏感词
     *
     * @param updateReqVO 更新信息
     */
    void updateSensitiveWord(@Valid AiSensitiveWordSaveReqVO updateReqVO);

    /**
     * 删除敏感词
     *
     * @param id 编号
     */
    void deleteSensitiveWord(Long id);

    /**
     * 获得敏感词
     *
     * @param id 编号
     * @return 敏感词
     */
    AiSensitiveWordDO getSensitiveWord(Long id);

    /**
     * 获得敏感词分页
     *
     * @param pageReqVO 分页查询
     * @return 敏感词分页
     */
    PageResult<AiSensitiveWordDO> getSensitiveWordPage(AiSensitiveWordPageReqVO pageReqVO);

    /**
     * 检查内容是否包含敏感词
     *
     * @param content 待检查内容
     * @return 第一个匹配的敏感词，若无匹配返回 null
     */
    String checkSensitiveWord(String content);

    /**
     * 查找内容中所有匹配的敏感词
     *
     * @param content 待检查内容
     * @return 所有匹配的敏感词列表
     */
    List<String> findAllSensitiveWords(String content);

    /**
     * 批量创建敏感词
     *
     * @param createReqVOs 批量创建信息
     */
    void batchCreateSensitiveWord(@Valid List<AiSensitiveWordSaveReqVO> createReqVOs);

}
