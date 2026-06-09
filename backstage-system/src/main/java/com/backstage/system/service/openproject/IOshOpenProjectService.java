package com.backstage.system.service.openproject;

import com.backstage.system.domain.openproject.OshOpenProjectTag;
import com.backstage.system.domain.openproject.dto.OpenProjectAuditDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectQueryDTO;
import com.backstage.system.domain.openproject.dto.OpenProjectSubmitDTO;
import com.backstage.system.domain.openproject.vo.OpenProjectVO;

import java.util.List;
import java.util.Map;

public interface IOshOpenProjectService {

    /** 分页查询已通过的开源项目列表 */
    Map<String, Object> listPage(OpenProjectQueryDTO queryDTO);

    /** 查询待审核列表 */
    Map<String, Object> listPending(OpenProjectQueryDTO queryDTO);

    /** 用户提交开源项目 */
    void submit(OpenProjectSubmitDTO dto);

    /** 审核开源项目 */
    void audit(OpenProjectAuditDTO dto);

    /** 增加点击次数 */
    void incrementClickCount(Long id);

    /** 查询项目详情 */
    OpenProjectVO getDetail(Long id);

    /** 查询所有标签 */
    List<OshOpenProjectTag> listTags();
}
