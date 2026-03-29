import request from '@/utils/request'

/**
 * 课程播放器相关 API
 */

// 获取课程详情
export function getCourseDetail(courseId) {
  return request({
    url: '/system/course/' + courseId,
    method: 'get'
  })
}

// 获取课程大纲（章节列表）
export function getCourseSections(courseId) {
  return request({
    url: '/system/course/' + courseId + '/sections',
    method: 'get'
  })
}

// 获取章节视频信息
export function getSectionVideo(sectionId) {
  return request({
    url: '/system/course/section/' + sectionId + '/video',
    method: 'get'
  })
}

// 立即学习（试看/学习章节）
export function learnSection(courseId, sectionId) {
  return request({
    url: '/system/course/learn',
    method: 'post',
    data: { courseId, sectionId }
  })
}

// 更新播放进度
export function updatePlayProgress(sectionId, data) {
  return request({
    url: '/system/course/section/' + sectionId + '/progress',
    method: 'put',
    data: data
  })
}

// 获取播放历史（断点续播）
export function getPlayProgress(sectionId) {
  return request({
    url: '/system/course/section/' + sectionId + '/progress',
    method: 'get'
  })
}

// 标记章节学习完成
export function markSectionComplete(sectionId) {
  return request({
    url: '/system/course/section/' + sectionId + '/complete',
    method: 'post'
  })
}

// 检查章节访问权限
export function checkSectionAccess(sectionId) {
  return request({
    url: '/system/course/section/' + sectionId + '/access',
    method: 'get'
  })
}

// 获取课程问答列表
export function getCourseQuestions(courseId, sectionId, status) {
  return request({
    url: '/system/course/' + courseId + '/questions',
    method: 'get',
    params: { sectionId, status }
  })
}

// 提交问题
export function askQuestion(data) {
  return request({
    url: '/system/course/question',
    method: 'post',
    data: data
  })
}

// 获取课程资料列表
export function getCourseMaterials(courseId) {
  return request({
    url: '/system/course/' + courseId + '/materials',
    method: 'get'
  })
}
