package cn.iocoder.yudao.module.ai.service.education;

import cn.iocoder.yudao.module.ai.dal.mysql.education.AiWrongAnswerBookMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiHomeworkSubmissionMapper;
import cn.iocoder.yudao.module.ai.dal.mysql.education.AiExamRecordMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AiStudyDataServiceImpl implements AiStudyDataService {

    @Resource private AiWrongAnswerBookMapper wrongBookMapper;
    @Resource private AiHomeworkSubmissionMapper homeworkMapper;
    @Resource private AiExamRecordMapper examMapper;

    @Override
    public Map<String, Object> getDashboardData(Long userId) {
        Map<String, Object> data = new HashMap<>();
        data.put("wrongCount", wrongBookMapper.selectCountByUserId(userId));
        data.put("homeworkCount", homeworkMapper.selectCountByUserId(userId));
        data.put("examCount", examMapper.selectCountByUserId(userId));
        data.put("totalQuestions", wrongBookMapper.selectCountByUserId(userId) +
                homeworkMapper.selectCountByUserId(userId) * 5 +
                examMapper.selectCountByUserId(userId) * 10);
        return data;
    }
}
