package com.backstage.system.service.impl.studyprogress;

import com.backstage.system.domain.vo.studyprogress.UserStudyProgressVo;
import com.backstage.system.mapper.studyprogress.StudyProgressMapper;
import com.backstage.system.service.studyprogress.IStudyProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class StudyProgressServiceImpl implements IStudyProgressService {
    
    @Autowired
    private StudyProgressMapper studyProgressMapper;

    @Override
    public List<UserStudyProgressVo> selectStudyProgressList(Long userId) {
        return studyProgressMapper.selectStudyProgressList(userId);
    }

    @Override
    public boolean updateProgress(UserStudyProgressVo vo, Long userId) {
        // 这里调用 Mapper 执行保存或更新
        return studyProgressMapper.saveOrUpdateProgress(vo, userId) > 0;
    }
}