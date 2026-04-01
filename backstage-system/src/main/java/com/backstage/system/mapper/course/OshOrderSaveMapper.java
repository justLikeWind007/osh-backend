package com.backstage.system.mapper.course;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 订单信息 Mapper 接口
 * 用于查询用户课程购买记录
 *
 * @author ruoyi
 * @date 2026-03-31
 */
@Mapper
public interface OshOrderSaveMapper {

    /**
     * 查询用户的课程订单
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 订单信息（仅返回已支付的订单）
     */
    Map<String, Object> selectOrderByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 查询用户的课程订单（包含所有状态，用于管理）
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 订单信息
     */
    Map<String, Object> selectOrderByUserIdAndCourseIdAllStatus(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 查询用户的有效订单（未过期的）
     *
     * @param userId   用户ID
     * @param courseId 课程ID
     * @return 有效订单信息
     */
    Map<String, Object> selectValidOrderByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);

    /**
     * 统计用户购买课程数量
     *
     * @param userId 用户ID
     * @return 购买课程数量
     */
    int countUserPurchasedCourses(@Param("userId") Long userId);
}