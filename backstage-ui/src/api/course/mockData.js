/**
 * 课程播放器 Mock API
 * 完全模拟后端接口，使用静态硬编码数据
 * 所有函数返回 Promise 以保持与真实 API 相同的调用方式
 */

// 使用公共免费视频作为演示（Big Buck Bunny - 开源短片）
const DEMO_VIDEOS = {
  101: 'https://www.w3schools.com/html/mov_bbb.mp4',
  102: 'https://www.w3schools.com/html/movie.mp4',
  103: 'https://www.w3schools.com/html/mov_bbb.mp4',
  104: 'https://www.w3schools.com/html/movie.mp4',
  105: 'https://www.w3schools.com/html/mov_bbb.mp4',
  201: 'https://www.w3schools.com/html/movie.mp4',
  202: 'https://www.w3schools.com/html/mov_bbb.mp4',
  203: 'https://www.w3schools.com/html/movie.mp4',
  204: '',
  301: 'https://www.w3schools.com/html/mov_bbb.mp4',
  302: 'https://www.w3schools.com/html/movie.mp4',
  303: 'https://www.w3schools.com/html/mov_bbb.mp4'
}

// ==================== 课程详情 ====================
const COURSE_DETAIL = {
  id: 1,
  title: '安全生产法律法规精讲',
  cover: '',
  tryContent: '本课程系统讲解安全生产法律法规体系，涵盖安全生产法、行政法规、技术标准三大模块，帮助学员全面掌握安全生产法律知识。',
  price: '299.00',
  tPrice: '599.00',
  type: 'media',
  serviceCycle: '365天',
  serviceContent: '答疑服务 + 课程资料下载 + 考试辅导',
  goodCount: 328,
  mediumCount: 15,
  badCount: 3,
  subCount: 12,
  isBuy: true,
  isfava: false,
  sections: []
}

// ==================== 课程大纲（章节树） ====================
const COURSE_SECTIONS = [
  {
    id: 10,
    title: '第一章 安全生产法基础',
    sections: [
      { id: 101, title: '1.1 安全生产法概述与立法目的', sectionType: 'video', duration: 1935, isFree: true, isLearned: true, hasQuestion: false, questionCount: 0, locked: false },
      { id: 102, title: '1.2 安全生产法律体系框架', sectionType: 'video', duration: 1720, isFree: false, isLearned: true, hasQuestion: false, questionCount: 0, locked: false },
      { id: 103, title: '1.3 生产经营单位的安全生产保障', sectionType: 'video', duration: 2720, isFree: false, isLearned: true, hasQuestion: true, questionCount: 3, locked: false },
      { id: 104, title: '1.4 从业人员的安全权利与义务', sectionType: 'video', duration: 2290, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: false },
      { id: 105, title: '1.5 安全生产监督管理体制', sectionType: 'video', duration: 2100, isFree: true, isLearned: false, hasQuestion: false, questionCount: 0, locked: false }
    ]
  },
  {
    id: 20,
    title: '第二章 安全生产行政法规',
    sections: [
      { id: 201, title: '2.1 安全生产许可证条例解读', sectionType: 'video', duration: 2415, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: false },
      { id: 202, title: '2.2 生产安全事故应急条例', sectionType: 'video', duration: 2210, isFree: false, isLearned: false, hasQuestion: true, questionCount: 1, locked: false },
      { id: 203, title: '2.3 危险化学品安全管理条例', sectionType: 'video', duration: 2550, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: false },
      { id: 204, title: '2.4 特种设备安全法', sectionType: 'video', duration: 2280, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: true }
    ]
  },
  {
    id: 30,
    title: '第三章 安全技术标准规范',
    sections: [
      { id: 301, title: '3.1 国家安全标准体系概述', sectionType: 'video', duration: 1845, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: false },
      { id: 302, title: '3.2 行业安全标准深度解读', sectionType: 'video', duration: 2060, isFree: false, isLearned: false, hasQuestion: false, questionCount: 0, locked: false },
      { id: 303, title: '3.3 安全评价技术规范', sectionType: 'video', duration: 1755, isFree: true, isLearned: false, hasQuestion: false, questionCount: 0, locked: false }
    ]
  }
]

// ==================== 视频详情映射 ====================
function buildVideoInfo(sectionId) {
  // 在章节树中查找对应课时
  let section = null
  for (const ch of COURSE_SECTIONS) {
    const found = ch.sections.find(s => s.id === sectionId)
    if (found) { section = found; break }
  }
  if (!section) return null

  return {
    sectionId: section.id,
    title: section.title,
    courseId: 1,
    courseName: COURSE_DETAIL.title,
    mediaUrl: DEMO_VIDEOS[section.id] || '',
    cover: '',
    duration: section.duration,
    durationText: formatDur(section.duration),
    videoCodec: 'h264',
    videoResolution: '1080p',
    fileSize: Math.floor(section.duration * 520),
    subtitleUrl: '',
    type: section.sectionType,
    isFree: section.isFree,
    hasAccess: !section.locked,
    currentProgress: section.isLearned ? 100 : 0,
    lastPosition: section.isLearned ? 0 : (section.id === 104 ? 185 : 0),
    status: section.isLearned ? 3 : (section.id === 104 ? 1 : 0),
    isCompleted: section.isLearned,
    examId: null
  }
}

// ==================== 问答数据 ====================
const QUESTIONS_DATA = [
  {
    id: 1001,
    sectionTitle: '1.3 生产经营单位的安全生产保障',
    questionTitle: '安全生产法中的"三同时"制度具体指什么？',
    questionContent: '老师好，课程中提到了"三同时"制度，能否详细解释一下具体包含哪些内容？在实际工作中如何落实？',
    askerName: '张同学',
    answerContent: '"三同时"是指建设项目的安全设施必须与主体工程同时设计、同时施工、同时投入生产和使用。这是安全生产法第三十三条的核心规定。在实际工作中，企业新建、改建、扩建项目都必须严格执行，安全设施投资应纳入项目预算，且不得削减。',
    answererName: '李讲师',
    answerTime: '2026-03-25',
    status: 'resolved',
    likeCount: 12,
    isTop: true,
    createTime: '2026-03-24'
  },
  {
    id: 1002,
    sectionTitle: '1.3 生产经营单位的安全生产保障',
    questionTitle: '安全生产责任制中主要负责人的职责有哪些？',
    questionContent: '课程讲到生产经营单位主要负责人的安全生产职责，请问除了课件中列举的，还有哪些容易被忽略的职责？',
    askerName: '王同学',
    answerContent: '主要负责人的七项职责包括：建立健全安全生产责任制、组织制定安全规章制度和操作规程、组织制定并实施安全生产教育培训计划、保证安全生产投入的有效实施、组织建立应急救援组织和体系、及时如实报告事故等。特别容易忽视的是"保证安全投入"和"定期研究安全问题"这两项。',
    answererName: '李讲师',
    answerTime: '2026-03-26',
    status: 'resolved',
    likeCount: 8,
    isTop: false,
    createTime: '2026-03-25'
  },
  {
    id: 1003,
    sectionTitle: '2.2 生产安全事故应急条例',
    questionTitle: '应急预案的编制层级和评审要求是怎样的？',
    questionContent: '请问综合应急预案、专项应急预案和现场处置方案三个层级的评审要求有什么不同？',
    askerName: '刘同学',
    answerContent: '',
    answererName: '',
    answerTime: null,
    status: 'pending',
    likeCount: 3,
    isTop: false,
    createTime: '2026-03-27'
  }
]

// ==================== 课程资料 ====================
const MATERIALS_DATA = [
  { id: 501, materialName: '安全生产法条文逐条解读.pdf', fileUrl: '#', fileSize: 2516582, downloadCount: 328, isDownloadable: true },
  { id: 502, materialName: '课程配套模拟练习题.xlsx', fileUrl: '#', fileSize: 1153434, downloadCount: 186, isDownloadable: true },
  { id: 503, materialName: '安全生产法律法规汇编.zip', fileUrl: '#', fileSize: 8945621, downloadCount: 92, isDownloadable: true },
  { id: 504, materialName: '课程思维导图.png', fileUrl: '#', fileSize: 524288, downloadCount: 215, isDownloadable: true },
  { id: 505, materialName: '历年真题精选与解析.pdf', fileUrl: '#', fileSize: 3670016, downloadCount: 145, isDownloadable: false }
]

// ==================== 工具函数 ====================
function formatDur(seconds) {
  const m = Math.floor(seconds / 60)
  const s = seconds % 60
  return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
}

function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms || 200))
}

// ==================== Mock API 函数 ====================

export function getCourseDetail(courseId) {
  return delay(300).then(() => ({ data: { ...COURSE_DETAIL } }))
}

export function getCourseSections(courseId) {
  // 返回深拷贝防止直接修改原数据
  return delay(200).then(() => ({
    data: JSON.parse(JSON.stringify(COURSE_SECTIONS))
  }))
}

export function getSectionVideo(sectionId) {
  const info = buildVideoInfo(sectionId)
  return delay(400).then(() => ({ data: info }))
}

export function checkSectionAccess(sectionId) {
  let section = null
  for (const ch of COURSE_SECTIONS) {
    const found = ch.sections.find(s => s.id === sectionId)
    if (found) { section = found; break }
  }
  const locked = section ? section.locked : true
  return delay(100).then(() => ({
    data: {
      sectionId: sectionId,
      hasAccess: !locked,
      isFree: section ? section.isFree : false,
      needPurchase: locked,
      isPurchased: !locked,
      reason: locked ? '需要购买课程才能观看此课时' : '',
      price: locked ? '299.00' : ''
    }
  }))
}

export function updatePlayProgress(sectionId, data) {
  console.log('[Mock] 保存进度:', sectionId, data)
  return delay(50).then(() => ({ data: null }))
}

export function markSectionComplete(sectionId) {
  console.log('[Mock] 标记完成:', sectionId)
  return delay(200).then(() => ({ data: null }))
}

export function getCourseQuestions(courseId, sectionId) {
  let list = [...QUESTIONS_DATA]
  if (sectionId) {
    // 简单过滤：根据 sectionId 范围匹配
    if (sectionId >= 100 && sectionId < 200) {
      list = list.filter(q => q.sectionTitle.startsWith('1.'))
    } else if (sectionId >= 200 && sectionId < 300) {
      list = list.filter(q => q.sectionTitle.startsWith('2.'))
    } else {
      list = list.filter(q => q.sectionTitle.startsWith('3.'))
    }
  }
  return delay(150).then(() => ({ data: list }))
}

export function askQuestion(data) {
  console.log('[Mock] 提交问题:', data)
  return delay(300).then(() => ({ data: Date.now() }))
}

export function getCourseMaterials(courseId) {
  return delay(200).then(() => ({ data: [...MATERIALS_DATA] }))
}
