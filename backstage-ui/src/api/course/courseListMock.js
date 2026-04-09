/**
 * 课程列表 / 详情 / 新增 Mock API
 * 完全静态数据，无需后端
 */

// ==================== 工具函数 ====================
function delay(ms) {
  return new Promise(resolve => setTimeout(resolve, ms || 150))
}
function deepClone(obj) {
  return JSON.parse(JSON.stringify(obj))
}

// ==================== RBAC 权限模拟 ====================
let _currentRole = 'admin' // 'admin' | 'user'

const ROLE_PERMISSIONS = {
  admin: ['course:list', 'course:create', 'course:edit', 'course:delete'],
  user: ['course:list']
}

/** 获取当前角色 */
export function getCurrentRole() {
  return _currentRole
}

/** 切换角色 */
export function switchRole(role) {
  _currentRole = role
}

/** 检查是否有指定权限 */
export function hasPermission(permission) {
  const perms = ROLE_PERMISSIONS[_currentRole] || []
  return perms.includes(permission)
}

/** 获取角色列表 */
export function getRoleOptions() {
  return [
    { value: 'admin', label: '管理员（可新增/编辑/删除课程）' },
    { value: 'user', label: '普通用户（仅浏览）' }
  ]
}

// ==================== 标签数据（按使用数量降序） ====================
const TAGS = [
  { id: 1, name: '安全生产', color: '#1890ff', count: 8 },
  { id: 2, name: '法律法规', color: '#722ed1', count: 6 },
  { id: 3, name: '应急管理', color: '#eb2f96', count: 5 },
  { id: 4, name: '消防安全', color: '#fa541c', count: 4 },
  { id: 5, name: '职业健康', color: '#13c2c2', count: 4 },
  { id: 6, name: '危化品管理', color: '#fa8c16', count: 3 },
  { id: 7, name: '特种设备', color: '#2f54eb', count: 3 },
  { id: 8, name: '建筑安全', color: '#52c41a', count: 2 },
  { id: 9, name: '矿山安全', color: '#a0d911', count: 2 },
  { id: 10, name: '电气安全', color: '#faad14', count: 1 }
]

// ==================== 课程列表数据 ====================
const COURSES = [
  {
    id: 1,
    title: '安全生产法律法规精讲',
    description: '本课程系统讲解安全生产法律法规体系，涵盖安全生产法、行政法规、技术标准三大模块，帮助学员全面掌握安全生产法律知识。',
    cover: '/static/img/course/cover_law.png',
    tagIds: [1, 2],
    goodCount: 328,
    mediumCount: 15,
    badCount: 3,
    favoriteCount: 156,
    price: '299.00',
    originalPrice: '599.00',
    chapterCount: 3,
    sectionCount: 12,
    totalDuration: 26880,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 课程资料下载 + 考试辅导',
    isBuy: true,
    isFavorite: false,
    instructor: '李讲师',
    studentCount: 1286,
    createTime: '2025-01-15',
    status: 'published'
  },
  {
    id: 2,
    title: '消防安全实务全解',
    description: '深入讲解消防安全管理实务，涵盖建筑消防设施、灭火器材使用、疏散逃生方案制定等核心内容，助力企业消防安全管理水平提升。',
    cover: '/static/img/course/cover_fire.png',
    tagIds: [1, 4],
    goodCount: 256,
    mediumCount: 22,
    badCount: 5,
    favoriteCount: 132,
    price: '399.00',
    originalPrice: '699.00',
    chapterCount: 4,
    sectionCount: 16,
    totalDuration: 34560,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 配套习题 + 消防模拟演练视频',
    isBuy: false,
    isFavorite: true,
    instructor: '王讲师',
    studentCount: 982,
    createTime: '2025-02-20',
    status: 'published'
  },
  {
    id: 3,
    title: '生产安全事故应急管理',
    description: '系统学习安全事故应急预案编制、应急演练组织实施、事故现场处置方案等关键知识，提高突发事件应急响应与处置能力。',
    cover: '/static/img/course/cover_industry.png',
    tagIds: [1, 3],
    goodCount: 198,
    mediumCount: 8,
    badCount: 2,
    favoriteCount: 87,
    price: '259.00',
    originalPrice: '459.00',
    chapterCount: 3,
    sectionCount: 10,
    totalDuration: 21600,
    serviceCycle: '180天',
    serviceContent: '答疑服务 + 应急预案模板下载',
    isBuy: true,
    isFavorite: false,
    instructor: '赵讲师',
    studentCount: 756,
    createTime: '2025-03-10',
    status: 'published'
  },
  {
    id: 4,
    title: '危险化学品安全管理实务',
    description: '全面解读危险化学品安全管理条例，涵盖储存、运输、使用全流程安全管理要求，结合典型事故案例进行深度分析。',
    cover: '/static/img/course/cover_health.png',
    tagIds: [1, 6],
    goodCount: 145,
    mediumCount: 12,
    badCount: 1,
    favoriteCount: 68,
    price: '349.00',
    originalPrice: '549.00',
    chapterCount: 5,
    sectionCount: 18,
    totalDuration: 38880,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 法规文件汇编 + 案例分析资料',
    isBuy: false,
    isFavorite: false,
    instructor: '孙讲师',
    studentCount: 523,
    createTime: '2025-04-05',
    status: 'published'
  },
  {
    id: 5,
    title: '职业健康管理体系构建',
    description: '讲解职业病防治法、职业健康监护、职业危害因素识别与评价等内容，帮助企业建立健全职业健康管理体系。',
    cover: '/static/img/course/cover_health.png',
    tagIds: [5, 2],
    goodCount: 112,
    mediumCount: 6,
    badCount: 0,
    favoriteCount: 54,
    price: '199.00',
    originalPrice: '399.00',
    chapterCount: 3,
    sectionCount: 9,
    totalDuration: 19440,
    serviceCycle: '180天',
    serviceContent: '答疑服务 + 体检管理模板',
    isBuy: false,
    isFavorite: true,
    instructor: '周讲师',
    studentCount: 412,
    createTime: '2025-05-18',
    status: 'published'
  },
  {
    id: 6,
    title: '特种设备安全操作规范',
    description: '涵盖锅炉、压力容器、起重机械、电梯等特种设备的安全操作规范、日常维护保养要点及常见故障排查方法。',
    cover: '/static/img/course/cover_industry.png',
    tagIds: [7, 1],
    goodCount: 89,
    mediumCount: 4,
    badCount: 1,
    favoriteCount: 43,
    price: '279.00',
    originalPrice: '479.00',
    chapterCount: 4,
    sectionCount: 14,
    totalDuration: 30240,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 操作规程模板 + 维保检查表',
    isBuy: true,
    isFavorite: false,
    instructor: '吴讲师',
    studentCount: 345,
    createTime: '2025-06-22',
    status: 'published'
  },
  {
    id: 7,
    title: '建筑施工安全管理精要',
    description: '聚焦建筑施工领域安全管理重点，包括脚手架搭设、高处作业、临时用电、起重吊装等高风险作业的安全管控措施。',
    cover: '/static/img/course/cover_fire.png',
    tagIds: [8, 1, 3],
    goodCount: 76,
    mediumCount: 5,
    badCount: 2,
    favoriteCount: 38,
    price: '329.00',
    originalPrice: '529.00',
    chapterCount: 5,
    sectionCount: 20,
    totalDuration: 43200,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 安全技术交底模板 + 检查表',
    isBuy: false,
    isFavorite: false,
    instructor: '郑讲师',
    studentCount: 278,
    createTime: '2025-08-01',
    status: 'published'
  },
  {
    id: 8,
    title: '矿山安全技术与管理',
    description: '系统讲解矿山开采安全技术、通风防尘、瓦斯防治、顶板管理等核心知识，结合矿山典型事故案例深入分析。',
    cover: '/static/img/course/cover_law.png',
    tagIds: [9, 1],
    goodCount: 65,
    mediumCount: 3,
    badCount: 0,
    favoriteCount: 31,
    price: '359.00',
    originalPrice: '559.00',
    chapterCount: 4,
    sectionCount: 15,
    totalDuration: 32400,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 安全技术规范文件',
    isBuy: false,
    isFavorite: false,
    instructor: '钱讲师',
    studentCount: 198,
    createTime: '2025-09-15',
    status: 'published'
  },
  {
    id: 9,
    title: '电气安全与防爆技术',
    description: '讲解电气设备安全使用规范、防爆电气设备选型安装、接地与防雷技术、电气火灾预防等专业知识。',
    cover: '/static/img/course/cover_industry.png',
    tagIds: [10, 1, 7],
    goodCount: 52,
    mediumCount: 2,
    badCount: 1,
    favoriteCount: 24,
    price: '249.00',
    originalPrice: '449.00',
    chapterCount: 3,
    sectionCount: 11,
    totalDuration: 23760,
    serviceCycle: '180天',
    serviceContent: '答疑服务 + 电气安全检查清单',
    isBuy: false,
    isFavorite: false,
    instructor: '陈讲师',
    studentCount: 156,
    createTime: '2025-10-20',
    status: 'published'
  },
  {
    id: 10,
    title: '安全生产应急预案编制实战',
    description: '从实战角度出发，详解综合应急预案、专项应急预案、现场处置方案的编制方法，包含大量实际案例和模板。',
    cover: '/static/img/course/cover_fire.png',
    tagIds: [3, 1, 2],
    goodCount: 183,
    mediumCount: 9,
    badCount: 1,
    favoriteCount: 95,
    price: '319.00',
    originalPrice: '519.00',
    chapterCount: 4,
    sectionCount: 13,
    totalDuration: 28080,
    serviceCycle: '365天',
    serviceContent: '答疑服务 + 预案模板全套 + 案例库',
    isBuy: true,
    isFavorite: true,
    instructor: '赵讲师',
    studentCount: 687,
    createTime: '2025-11-08',
    status: 'published'
  }
]

// ==================== 课程章节详情（用于课程详情页和新增课程演示） ====================
const COURSE_CHAPTERS = {
  1: [
    {
      id: 10, title: '第一章 安全生产法基础',
      sections: [
        { id: 101, title: '1.1 安全生产法概述与立法目的', duration: 1935, isFree: true, videoName: '安全生产法概述.mp4', materialName: '第一节配套资料.zip', materialSize: 3145728 },
        { id: 102, title: '1.2 安全生产法律体系框架', duration: 1720, isFree: false, videoName: '法律体系框架.mp4', materialName: '', materialSize: 0 },
        { id: 103, title: '1.3 生产经营单位的安全生产保障', duration: 2720, isFree: false, videoName: '安全生产保障.mp4', materialName: '安全保障案例.zip', materialSize: 5242880 },
        { id: 104, title: '1.4 从业人员的安全权利与义务', duration: 2290, isFree: false, videoName: '权利与义务.mp4', materialName: '', materialSize: 0 },
        { id: 105, title: '1.5 安全生产监督管理体制', duration: 2100, isFree: true, videoName: '监督管理体制.mp4', materialName: '监管体制图表.zip', materialSize: 2097152 }
      ]
    },
    {
      id: 20, title: '第二章 安全生产行政法规',
      sections: [
        { id: 201, title: '2.1 安全生产许可证条例解读', duration: 2415, isFree: false, videoName: '许可证条例.mp4', materialName: '', materialSize: 0 },
        { id: 202, title: '2.2 生产安全事故应急条例', duration: 2210, isFree: false, videoName: '应急条例.mp4', materialName: '应急条例全文.zip', materialSize: 1572864 },
        { id: 203, title: '2.3 危险化学品安全管理条例', duration: 2550, isFree: false, videoName: '危化品管理.mp4', materialName: '', materialSize: 0 },
        { id: 204, title: '2.4 特种设备安全法', duration: 2280, isFree: false, videoName: '特种设备法.mp4', materialName: '特种设备法解读.zip', materialSize: 4194304 }
      ]
    },
    {
      id: 30, title: '第三章 安全技术标准规范',
      sections: [
        { id: 301, title: '3.1 国家安全标准体系概述', duration: 1845, isFree: false, videoName: '标准体系概述.mp4', materialName: '', materialSize: 0 },
        { id: 302, title: '3.2 行业安全标准深度解读', duration: 2060, isFree: false, videoName: '行业标准解读.mp4', materialName: '标准汇编.zip', materialSize: 8388608 },
        { id: 303, title: '3.3 安全评价技术规范', duration: 1755, isFree: true, videoName: '评价技术规范.mp4', materialName: '评价规范模板.zip', materialSize: 3670016 }
      ]
    }
  ],
  2: [
    {
      id: 40, title: '第一章 消防安全基础理论',
      sections: [
        { id: 401, title: '1.1 燃烧基础知识', duration: 1800, isFree: true, videoName: '燃烧基础.mp4', materialName: '', materialSize: 0 },
        { id: 402, title: '1.2 火灾分类与危险性', duration: 2100, isFree: false, videoName: '火灾分类.mp4', materialName: '火灾分类图表.zip', materialSize: 2621440 },
        { id: 403, title: '1.3 建筑耐火等级', duration: 1950, isFree: false, videoName: '耐火等级.mp4', materialName: '', materialSize: 0 },
        { id: 404, title: '1.4 防火分区设计', duration: 2300, isFree: false, videoName: '防火分区.mp4', materialName: '防火分区案例.zip', materialSize: 4718592 }
      ]
    },
    {
      id: 50, title: '第二章 消防设施与器材',
      sections: [
        { id: 501, title: '2.1 自动喷水灭火系统', duration: 2400, isFree: false, videoName: '喷水灭火.mp4', materialName: '', materialSize: 0 },
        { id: 502, title: '2.2 火灾自动报警系统', duration: 2150, isFree: false, videoName: '自动报警.mp4', materialName: '报警系统规范.zip', materialSize: 3145728 },
        { id: 503, title: '2.3 灭火器配置与使用', duration: 1680, isFree: true, videoName: '灭火器使用.mp4', materialName: '', materialSize: 0 },
        { id: 504, title: '2.4 消防栓系统', duration: 1920, isFree: false, videoName: '消防栓系统.mp4', materialName: '', materialSize: 0 }
      ]
    },
    {
      id: 60, title: '第三章 消防安全管理',
      sections: [
        { id: 601, title: '3.1 消防安全责任制', duration: 2050, isFree: false, videoName: '安全责任制.mp4', materialName: '责任制模板.zip', materialSize: 1048576 },
        { id: 602, title: '3.2 消防安全检查', duration: 2200, isFree: false, videoName: '安全检查.mp4', materialName: '检查表模板.zip', materialSize: 2097152 },
        { id: 603, title: '3.3 灭火与应急疏散预案', duration: 2350, isFree: false, videoName: '疏散预案.mp4', materialName: '', materialSize: 0 },
        { id: 604, title: '3.4 消防安全教育培训', duration: 1780, isFree: false, videoName: '安全培训.mp4', materialName: '培训计划模板.zip', materialSize: 1572864 }
      ]
    },
    {
      id: 70, title: '第四章 典型火灾案例分析',
      sections: [
        { id: 701, title: '4.1 工业厂房火灾案例', duration: 2500, isFree: false, videoName: '厂房火灾案例.mp4', materialName: '', materialSize: 0 },
        { id: 702, title: '4.2 高层建筑火灾案例', duration: 2300, isFree: false, videoName: '高层火灾案例.mp4', materialName: '案例分析报告.zip', materialSize: 6291456 },
        { id: 703, title: '4.3 公共场所火灾案例', duration: 2100, isFree: true, videoName: '公共场所案例.mp4', materialName: '', materialSize: 0 },
        { id: 704, title: '4.4 石油化工火灾案例', duration: 2450, isFree: false, videoName: '石化火灾案例.mp4', materialName: '石化安全规范.zip', materialSize: 4194304 }
      ]
    }
  ]
}

// ==================== 课程资料数据 ====================
const COURSE_MATERIALS = {
  1: [
    { id: 501, materialName: '安全生产法条文逐条解读.pdf', fileUrl: '#', fileSize: 2516582, downloadCount: 328, fileType: 'pdf' },
    { id: 502, materialName: '课程配套模拟练习题.xlsx', fileUrl: '#', fileSize: 1153434, downloadCount: 186, fileType: 'xlsx' },
    { id: 503, materialName: '安全生产法律法规汇编.zip', fileUrl: '#', fileSize: 8945621, downloadCount: 92, fileType: 'zip' },
    { id: 504, materialName: '课程思维导图.png', fileUrl: '#', fileSize: 524288, downloadCount: 215, fileType: 'png' },
    { id: 505, materialName: '历年真题精选与解析.pdf', fileUrl: '#', fileSize: 3670016, downloadCount: 145, fileType: 'pdf' }
  ],
  2: [
    { id: 601, materialName: '消防安全管理制度模板.zip', fileUrl: '#', fileSize: 3145728, downloadCount: 256, fileType: 'zip' },
    { id: 602, materialName: '灭火器检查记录表.xlsx', fileUrl: '#', fileSize: 524288, downloadCount: 198, fileType: 'xlsx' },
    { id: 603, materialName: '消防设施平面布置示例.pdf', fileUrl: '#', fileSize: 6291456, downloadCount: 167, fileType: 'pdf' }
  ]
}

// ==================== Mock API 函数 ====================

/** 获取标签列表（按使用数量排序） */
export function getTagList() {
  return delay(100).then(() => ({
    data: deepClone(TAGS)
  }))
}

/** 获取课程列表（支持标签筛选 + 关键字搜索） */
export function getCourseList({ keyword, tagIds, page, pageSize } = {}) {
  let list = deepClone(COURSES)

  // 关键字搜索
  if (keyword && keyword.trim()) {
    const kw = keyword.trim().toLowerCase()
    list = list.filter(c =>
      c.title.toLowerCase().includes(kw) ||
      c.description.toLowerCase().includes(kw) ||
      c.instructor.toLowerCase().includes(kw)
    )
  }

  // 标签筛选（多选：课程包含任一选中标签即匹配）
  if (tagIds && tagIds.length > 0) {
    list = list.filter(c =>
      c.tagIds.some(tid => tagIds.includes(tid))
    )
  }

  // 给每个课程附上标签名称
  list = list.map(c => ({
    ...c,
    tags: c.tagIds.map(tid => TAGS.find(t => t.id === tid)).filter(Boolean)
  }))

  const total = list.length
  // 分页
  const p = page || 1
  const ps = pageSize || 12
  const start = (p - 1) * ps
  list = list.slice(start, start + ps)

  return delay(250).then(() => ({
    data: { list, total, page: p, pageSize: ps }
  }))
}

/** 获取课程详情 */
export function getCourseDetailById(courseId) {
  const course = COURSES.find(c => c.id === courseId)
  if (!course) {
    return delay(100).then(() => ({ data: null }))
  }
  const result = deepClone(course)
  result.tags = result.tagIds.map(tid => TAGS.find(t => t.id === tid)).filter(Boolean)
  return delay(200).then(() => ({ data: result }))
}

/** 获取课程章节列表 */
export function getCourseChapters(courseId) {
  const chapters = COURSE_CHAPTERS[courseId]
  if (!chapters) {
    // 对于没有详细章节数据的课程，生成模拟章节
    const course = COURSES.find(c => c.id === courseId)
    if (!course) return delay(100).then(() => ({ data: [] }))
    const mockChapters = []
    let sectionId = courseId * 1000
    for (let i = 0; i < course.chapterCount; i++) {
      const secCount = Math.ceil(course.sectionCount / course.chapterCount)
      const sections = []
      for (let j = 0; j < secCount; j++) {
        sectionId++
        sections.push({
          id: sectionId,
          title: `${i + 1}.${j + 1} 课时内容${j + 1}`,
          duration: 1800 + Math.floor(Math.random() * 1200),
          isFree: j === 0 && i === 0,
          videoName: `课时${j + 1}.mp4`,
          materialName: j % 3 === 0 ? `资料${j + 1}.zip` : '',
          materialSize: j % 3 === 0 ? 2097152 + Math.floor(Math.random() * 5242880) : 0
        })
      }
      mockChapters.push({
        id: courseId * 100 + i,
        title: `第${['一', '二', '三', '四', '五'][i] || (i + 1)}章 章节内容${i + 1}`,
        sections
      })
    }
    return delay(200).then(() => ({ data: deepClone(mockChapters) }))
  }
  return delay(200).then(() => ({ data: deepClone(chapters) }))
}

/** 获取课程资料列表 */
export function getCourseResourceList(courseId) {
  const materials = COURSE_MATERIALS[courseId]
  if (!materials) {
    return delay(100).then(() => ({ data: [] }))
  }
  return delay(150).then(() => ({ data: deepClone(materials) }))
}

/** 新增课程（模拟） */
export function createCourse(courseData) {
  if (!hasPermission('course:create')) {
    return delay(100).then(() => {
      throw new Error('您没有新增课程的权限')
    })
  }
  console.log('[Mock] 新增课程:', courseData)
  const newId = COURSES.length + 1
  return delay(500).then(() => ({ data: { id: newId, ...courseData } }))
}

/** 删除课程（模拟） */
export function deleteCourse(courseId) {
  if (!hasPermission('course:delete')) {
    return delay(100).then(() => {
      throw new Error('您没有删除课程的权限')
    })
  }
  console.log('[Mock] 删除课程:', courseId)
  return delay(300).then(() => ({ data: null }))
}

/** 收藏/取消收藏 */
export function toggleFavorite(courseId) {
  const course = COURSES.find(c => c.id === courseId)
  if (course) {
    course.isFavorite = !course.isFavorite
    course.favoriteCount += course.isFavorite ? 1 : -1
  }
  return delay(100).then(() => ({
    data: { isFavorite: course ? course.isFavorite : false }
  }))
}

/** 模拟文件上传 */
export function uploadFile(file) {
  console.log('[Mock] 上传文件:', file.name, file.size)
  return delay(800).then(() => ({
    data: {
      fileName: file.name,
      fileUrl: URL.createObjectURL(file),
      fileSize: file.size
    }
  }))
}

/** 格式化文件大小 */
export function formatFileSize(bytes) {
  if (!bytes || bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(i > 1 ? 1 : 0) + ' ' + units[i]
}

/** 格式化时长 */
export function formatDuration(seconds) {
  if (!seconds) return '00:00'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  const s = seconds % 60
  if (h > 0) {
    return `${h}小时${m}分`
  }
  return `${String(m).padStart(2, '0')}:${String(s).padStart(2, '0')}`
}

/** 格式化总时长 */
export function formatTotalDuration(seconds) {
  if (!seconds) return '0分钟'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) {
    return m > 0 ? `${h}小时${m}分钟` : `${h}小时`
  }
  return `${m}分钟`
}
