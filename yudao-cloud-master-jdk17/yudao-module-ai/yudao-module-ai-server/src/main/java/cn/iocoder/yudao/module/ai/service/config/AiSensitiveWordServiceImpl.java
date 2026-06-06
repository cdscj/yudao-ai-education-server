package cn.iocoder.yudao.module.ai.service.config;

import cn.iocoder.yudao.framework.common.enums.CommonStatusEnum;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.AiSensitiveWordPageReqVO;
import cn.iocoder.yudao.module.ai.controller.admin.config.vo.sensitiveword.AiSensitiveWordSaveReqVO;
import cn.iocoder.yudao.module.ai.dal.dataobject.config.AiSensitiveWordDO;
import cn.iocoder.yudao.module.ai.dal.mysql.config.AiSensitiveWordMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SENSITIVE_WORD_DETECTED;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SENSITIVE_WORD_EXISTS;
import static cn.iocoder.yudao.module.ai.enums.ErrorCodeConstants.SENSITIVE_WORD_NOT_EXISTS;

/**
 * AI 敏感词 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class AiSensitiveWordServiceImpl implements AiSensitiveWordService {

    @Resource
    private AiSensitiveWordMapper sensitiveWordMapper;

    @Override
    @CacheEvict(cacheNames = "ai:sensitive-word", allEntries = true)
    public Long createSensitiveWord(AiSensitiveWordSaveReqVO createReqVO) {
        cachedWords = null;
        // 校验敏感词是否已存在
        validateSensitiveWordUnique(createReqVO.getWord());

        // 插入
        AiSensitiveWordDO sensitiveWord = BeanUtils.toBean(createReqVO, AiSensitiveWordDO.class);
        sensitiveWordMapper.insert(sensitiveWord);
        return sensitiveWord.getId();
    }

    @Override
    @CacheEvict(cacheNames = "ai:sensitive-word", allEntries = true)
    public void updateSensitiveWord(AiSensitiveWordSaveReqVO updateReqVO) {
        cachedWords = null;
        // 校验存在
        validateSensitiveWordExists(updateReqVO.getId());
        // 校验敏感词是否已存在
        validateSensitiveWordUnique(updateReqVO.getWord());

        // 更新
        AiSensitiveWordDO updateObj = BeanUtils.toBean(updateReqVO, AiSensitiveWordDO.class);
        sensitiveWordMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = "ai:sensitive-word", allEntries = true)
    public void deleteSensitiveWord(Long id) {
        cachedWords = null;
        // 校验存在
        validateSensitiveWordExists(id);
        // 删除
        sensitiveWordMapper.deleteById(id);
    }

    private AiSensitiveWordDO validateSensitiveWordExists(Long id) {
        AiSensitiveWordDO sensitiveWord = sensitiveWordMapper.selectById(id);
        if (sensitiveWord == null) {
            throw exception(SENSITIVE_WORD_NOT_EXISTS);
        }
        return sensitiveWord;
    }

    private void validateSensitiveWordUnique(String word) {
        AiSensitiveWordDO sensitiveWord = sensitiveWordMapper.selectByWord(word);
        if (sensitiveWord != null) {
            throw exception(SENSITIVE_WORD_EXISTS);
        }
    }

    @Override
    public AiSensitiveWordDO getSensitiveWord(Long id) {
        return sensitiveWordMapper.selectById(id);
    }

    @Override
    public PageResult<AiSensitiveWordDO> getSensitiveWordPage(AiSensitiveWordPageReqVO pageReqVO) {
        return sensitiveWordMapper.selectPage(pageReqVO);
    }

    @Override
    public String checkSensitiveWord(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        // 加载所有启用的敏感词（带缓存）
        List<AiSensitiveWordDO> wordList = getAllEnabledWords();
        for (AiSensitiveWordDO word : wordList) {
            if (content.contains(word.getWord())) {
                log.warn("检测到敏感词: {}", word.getWord());
                throw exception(SENSITIVE_WORD_DETECTED);
            }
        }
        return null;
    }

    @Override
    public List<String> findAllSensitiveWords(String content) {
        if (content == null || content.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> matchedWords = new ArrayList<>();
        List<AiSensitiveWordDO> wordList = getAllEnabledWords();
        for (AiSensitiveWordDO word : wordList) {
            if (content.contains(word.getWord())) {
                matchedWords.add(word.getWord());
            }
        }
        return matchedWords;
    }

    private volatile List<AiSensitiveWordDO> cachedWords;

    private List<AiSensitiveWordDO> getAllEnabledWords() {
        if (cachedWords == null) {
            cachedWords = sensitiveWordMapper.selectAllEnabled();
        }
        return cachedWords;
    }

    @Override
    @CacheEvict(cacheNames = "ai:sensitive-word", allEntries = true)
    public void batchCreateSensitiveWord(List<AiSensitiveWordSaveReqVO> createReqVOs) {
        for (AiSensitiveWordSaveReqVO createReqVO : createReqVOs) {
            createSensitiveWord(createReqVO);
        }
    }

}
