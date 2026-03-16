package com.backstage.system.service.studyprogress;

import com.backstage.system.domain.vo.studyprogress.UserStudyProgressVo;
import java.util.List;

public interface IStudyProgressService {
    public List<UserStudyProgressVo> selectStudyProgressList(Long userId);

    boolean updateProgress(UserStudyProgressVo vo, Long userId);
}