package com.backstage.system.service.impl;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.course.OshCourseCollection;
import com.backstage.system.mapper.course.OshCourseCollectionMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.service.IOshCourseCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OshCourseCollectionServiceImpl implements IOshCourseCollectionService {

    @Autowired
    private OshCourseCollectionMapper oshCourseCollectionMapper;

    @Autowired
    private OshCourseMapper oshCourseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void collectCourse(Long userId, String operator, Long courseId) {
        // 1. 基础校验
        validateCourseExists(courseId);

        // 2. 尝试找回历史记录（包含已逻辑删除的记录）
        OshCourseCollection collection = oshCourseCollectionMapper.selectByUserIdAndCourseId(userId, courseId);

        if (collection == null) {
            // 情况 A: 数据库里没这人这课，直接插入
            OshCourseCollection newCollection = new OshCourseCollection();
            newCollection.setUserId(userId);
            newCollection.setCourseId(courseId);
            newCollection.setDeleteFlag(0);
            newCollection.setCreateBy(operator);
            newCollection.setUpdateBy(operator);

            if (oshCourseCollectionMapper.insertCourseCollection(newCollection) <= 0) {
                throw new ServiceException("收藏失败");
            }
            // 更新课程总表计数
            oshCourseMapper.increaseCollectionCount(courseId);

        } else if (collection.getDeleteFlag() == 1) {
            // 情况 B: 以前收藏过又取消了，现在“激活”它
            if (oshCourseCollectionMapper.updateCollectionDeleteFlag(collection.getId(), 0, operator) <= 0) {
                throw new ServiceException("恢复收藏失败");
            }
            // 更新课程总表计数
            oshCourseMapper.increaseCollectionCount(courseId);
        }

        // 情况 C: 如果 delete_flag 已经是 0，说明已经收藏过了，静默处理（不报错，也不重复计分）
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeCourseCollection(Long userId, String operator, Long courseId) {
        OshCourseCollection collection = oshCourseCollectionMapper.selectByUserIdAndCourseId(userId, courseId);
        if (collection == null || collection.getDeleteFlag() == null || collection.getDeleteFlag() != 0) {
            return;
        }

        if (oshCourseCollectionMapper.updateCollectionDeleteFlag(collection.getId(), 1, operator) <= 0) {
            throw new ServiceException("取消收藏课程失败");
        }
        oshCourseMapper.decreaseCollectionCount(courseId);
    }

    private void validateCourseExists(Long courseId) {
        OshCourse course = oshCourseMapper.selectCourseById(courseId);
        if (course == null) {
            throw new ServiceException("课程不存在");
        }
    }
}
