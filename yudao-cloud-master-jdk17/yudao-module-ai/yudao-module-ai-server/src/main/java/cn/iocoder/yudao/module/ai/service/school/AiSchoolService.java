package cn.iocoder.yudao.module.ai.service.school;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.ai.dal.dataobject.school.AiSchoolDO;

/**
 * AI 学校信息 Service 接口
 *
 * @author 芋道源码
 */
public interface AiSchoolService {

    /**
     * 获得学校分页
     *
     * @param name 学校名称
     * @param type 学校类型
     * @param city 城市
     * @param pageNo 页码
     * @param pageSize 每页条数
     * @return 学校分页
     */
    PageResult<AiSchoolDO> getSchoolList(String name, String type, String city, Integer pageNo, Integer pageSize);

    /**
     * 获得学校
     *
     * @param id 学校编号
     * @return 学校
     */
    AiSchoolDO getSchool(Long id);

    /**
     * 创建学校
     *
     * @param school 学校
     * @return 学校编号
     */
    Long createSchool(AiSchoolDO school);

    /**
     * 更新学校
     *
     * @param school 学校
     */
    void updateSchool(AiSchoolDO school);

    /**
     * 删除学校
     *
     * @param id 学校编号
     */
    void deleteSchool(Long id);

}
