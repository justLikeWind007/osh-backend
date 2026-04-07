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
        validateCourseExists(courseId);

        OshCourseCollection collection = oshCourseCollectionMapper.selectByUserIdAndCourseId(userId, courseId);
        if (collection == null) {
            OshCourseCollection newCollection = new OshCourseCollection();
            Date now = new Date();
            newCollection.setUserId(userId);
            newCollection.setCourseId(courseId);
            newCollection.setDeleteFlag(0);
            newCollection.setCreateBy(operator);
            newCollection.setCreateTime(now);
            newCollection.setUpdateBy(operator);
            newCollection.setUpdateTime(now);
            if (oshCourseCollectionMapper.insertCourseCollection(newCollection) <= 0) {
                throw new ServiceException("收藏课程失败");
            }
            oshCourseMapper.increaseCollectionCount(courseId);
            return;
        }

        if (collection.getDeleteFlag() != null && collection.getDeleteFlag() == 0) {
            return;
        }

        if (oshCourseCollectionMapper.updateCollectionDeleteFlag(collection.getId(), 0, operator) <= 0) {
            throw new ServiceException("收藏课程失败");
        }
        oshCourseMapper.increaseCollectionCount(courseId);
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
