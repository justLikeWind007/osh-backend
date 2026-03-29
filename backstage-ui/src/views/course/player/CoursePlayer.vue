<template>
  <div class="course-player-container">
    <!-- ==================== 左侧课程目录 ==================== -->
    <div class="cp-sidebar" :class="{ collapsed: sidebarCollapsed }">
      <!-- 课程信息头部 -->
      <div class="cp-sidebar-header">
        <div class="header-top">
          <h3 class="course-title" :title="courseDetail.title">{{ courseDetail.title }}</h3>
          <el-button
            type="text"
            class="collapse-btn"
            :icon="sidebarCollapsed ? 'el-icon-s-unfold' : 'el-icon-s-fold'"
            @click="sidebarCollapsed = !sidebarCollapsed"
          />
        </div>
        <div class="course-meta" v-if="!sidebarCollapsed">
          共 {{ chapterList.length }} 章 &middot; {{ totalSections }} 课时 &middot; 总时长 {{ totalDurationText }}
        </div>
        <!-- 整体学习进度 -->
        <div class="progress-wrapper" v-if="!sidebarCollapsed">
          <el-progress
            :percentage="overallProgress"
            :stroke-width="6"
            :show-text="false"
            color="#409EFF"
          />
          <div class="progress-text">
            <span>已学 {{ learnedCount }}/{{ totalSections }} 课时</span>
            <span>{{ overallProgress }}%</span>
          </div>
        </div>
      </div>

      <!-- 树形课程目录 -->
      <div class="cp-tree" v-if="!sidebarCollapsed">
        <div
          v-for="(chapter, cIdx) in chapterList"
          :key="chapter.id || cIdx"
          class="chapter-group"
        >
          <!-- 章节标题 -->
          <div
            class="chapter-title"
            @click="toggleChapter(cIdx)"
          >
            <i
              class="el-icon-caret-right chapter-arrow"
              :class="{ open: chapter.expanded }"
            />
            <span class="chapter-name">{{ chapter.title }}</span>
            <span class="chapter-count">
              {{ getChapterLearnedCount(chapter) }}/{{ chapter.sections.length }}
            </span>
          </div>
          <!-- 课时列表 -->
          <transition name="slide-sections">
            <div class="sections-list" v-show="chapter.expanded">
              <div
                v-for="(section, sIdx) in chapter.sections"
                :key="section.id"
                class="section-item"
                :class="{
                  active: currentSection && currentSection.id === section.id,
                  completed: section.isLearned,
                  locked: section.locked
                }"
                @click="handleSectionClick(section)"
              >
                <!-- 状态图标 -->
                <span class="section-icon">
                  <i v-if="section.locked" class="el-icon-lock" />
                  <i
                    v-else-if="currentSection && currentSection.id === section.id && isPlaying"
                    class="el-icon-video-play playing-icon"
                  />
                  <i v-else-if="section.isLearned" class="el-icon-success" />
                  <i v-else class="el-icon-video-camera" />
                </span>
                <!-- 课时标题 -->
                <span class="section-title" :title="section.title">
                  {{ section.title }}
                </span>
                <!-- 标签 -->
                <span class="section-badges">
                  <el-tag v-if="section.isFree" size="mini" type="success" effect="plain">
                    免费
                  </el-tag>
                  <el-tag
                    v-if="section.hasQuestion && section.questionCount > 0"
                    size="mini"
                    type="primary"
                    effect="plain"
                  >
                    {{ section.questionCount }}问
                  </el-tag>
                  <el-tag v-if="section.locked" size="mini" type="info" effect="plain">
                    需购买
                  </el-tag>
                </span>
                <!-- 时长 -->
                <span class="section-duration">
                  {{ formatDuration(section.duration) }}
                </span>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>

    <!-- ==================== 右侧主内容区 ==================== -->
    <div class="cp-main">
      <!-- 视频播放区域 -->
      <div class="cp-player-wrapper">
        <div class="player-area" ref="playerArea">
          <!-- HTML5 视频播放器 -->
          <video
            ref="videoPlayer"
            class="video-element"
            :src="videoInfo.mediaUrl"
            :poster="videoInfo.cover"
            @timeupdate="onTimeUpdate"
            @loadedmetadata="onMetadataLoaded"
            @play="onPlay"
            @pause="onPause"
            @ended="onVideoEnded"
            @waiting="videoBuffering = true"
            @canplay="videoBuffering = false"
            @error="onVideoError"
            @progress="onBufferProgress"
            preload="auto"
            crossorigin="anonymous"
          >
            <track
              v-if="videoInfo.subtitleUrl"
              kind="subtitles"
              :src="videoInfo.subtitleUrl"
              srclang="zh"
              label="中文"
              default
            />
          </video>

          <!-- 加载遮罩 -->
          <div class="player-overlay" v-if="videoBuffering || videoLoading">
            <i class="el-icon-loading" style="font-size: 40px; color: #fff;" />
            <span style="color: #fff; margin-top: 8px; font-size: 13px;">
              {{ videoLoading ? '加载中...' : '缓冲中...' }}
            </span>
          </div>

          <!-- 初始播放遮罩（无视频时显示） -->
          <div
            class="player-placeholder"
            v-if="!videoInfo.mediaUrl && !videoLoading"
            @click="handlePlayPlaceholder"
          >
            <div class="play-circle">
              <i class="el-icon-video-play" />
            </div>
            <p class="placeholder-title">
              {{ currentSection ? currentSection.title : '请从左侧选择课时开始学习' }}
            </p>
            <p class="placeholder-sub" v-if="currentSection">
              时长 {{ formatDuration(currentSection.duration) }}
            </p>
          </div>

          <!-- 全屏切换提示 -->
          <transition name="el-fade-in">
            <div class="fullscreen-tip" v-if="showFullscreenTip">
              {{ isFullscreen ? '已进入全屏模式' : '已退出全屏模式' }}
            </div>
          </transition>
        </div>

        <!-- ========== 视频控制栏 ========== -->
        <div class="cp-controls">
          <!-- 进度条 -->
          <div
            class="progress-bar-wrapper"
            ref="progressBar"
            @mousedown="startProgressDrag"
            @mousemove="onProgressHover"
            @mouseleave="progressHoverTime = null"
          >
            <div class="progress-buffered" :style="{ width: bufferedPercent + '%' }" />
            <div class="progress-played" :style="{ width: playedPercent + '%' }" />
            <div
              class="progress-thumb"
              :style="{ left: playedPercent + '%' }"
            />
            <!-- 悬浮时间提示 -->
            <div
              class="progress-tooltip"
              v-if="progressHoverTime !== null"
              :style="{ left: progressHoverPercent + '%' }"
            >
              {{ formatTime(progressHoverTime) }}
            </div>
          </div>

          <!-- 控制按钮行 -->
          <div class="controls-row">
            <div class="controls-left">
              <!-- 播放/暂停 -->
              <el-button
                type="text"
                class="ctrl-btn"
                @click="togglePlay"
              >
                <i :class="isPlaying ? 'el-icon-video-pause' : 'el-icon-video-play'" />
              </el-button>
              <!-- 上一课 -->
              <el-button
                type="text"
                class="ctrl-btn"
                @click="playPrevSection"
                :disabled="!hasPrevSection"
              >
                <i class="el-icon-d-arrow-left" />
              </el-button>
              <!-- 下一课 -->
              <el-button
                type="text"
                class="ctrl-btn"
                @click="playNextSection"
                :disabled="!hasNextSection"
              >
                <i class="el-icon-d-arrow-right" />
              </el-button>
              <!-- 时间显示 -->
              <span class="time-display">
                {{ formatTime(currentTime) }} / {{ formatTime(videoDuration) }}
              </span>
            </div>

            <div class="controls-right">
              <!-- 音量控制 -->
              <div class="volume-control">
                <el-button
                  type="text"
                  class="ctrl-btn"
                  @click="toggleMute"
                >
                  <i :class="volumeIcon" />
                </el-button>
                <el-slider
                  v-model="volume"
                  :max="100"
                  :show-tooltip="false"
                  class="volume-slider"
                  @input="onVolumeChange"
                />
              </div>
              <!-- 倍速选择 -->
              <el-dropdown trigger="click" @command="onSpeedChange">
                <span class="ctrl-dropdown">{{ playbackRate }}x</span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="speed in speedOptions"
                    :key="speed"
                    :command="speed"
                    :class="{ 'is-active': playbackRate === speed }"
                  >
                    {{ speed }}x
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- 清晰度选择 -->
              <el-dropdown trigger="click" @command="onQualityChange" v-if="qualityOptions.length > 0">
                <span class="ctrl-dropdown">{{ currentQuality }}</span>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    v-for="q in qualityOptions"
                    :key="q.value"
                    :command="q.value"
                    :class="{ 'is-active': currentQuality === q.value }"
                  >
                    {{ q.label }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- 全屏 -->
              <el-button
                type="text"
                class="ctrl-btn"
                @click="toggleFullscreen"
              >
                <i :class="isFullscreen ? 'el-icon-copy-document' : 'el-icon-full-screen'" />
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 当前课时信息栏 -->
      <div class="cp-info-bar">
        <div class="info-left">
          <h4 class="current-section-title">
            {{ currentSection ? currentSection.title : '请选择课时' }}
          </h4>
          <el-tag v-if="videoInfo.isCompleted" size="mini" type="success">已完成</el-tag>
        </div>
        <div class="info-right">
          <span class="auto-play-label">自动连播</span>
          <el-switch
            v-model="autoPlayNext"
            active-color="#409EFF"
            inactive-color="#DCDFE6"
            :width="40"
          />
        </div>
      </div>

      <!-- ========== 底部功能标签区 ========== -->
      <div class="cp-tabs-area">
        <el-tabs v-model="activeTab" @tab-click="onTabChange">
          <!-- 学习统计 -->
          <el-tab-pane label="学习统计" name="stats">
            <div class="stats-grid">
              <div class="stat-card">
                <div class="stat-value">{{ overallProgress }}%</div>
                <div class="stat-label">完成进度</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ learnedCount }}</div>
                <div class="stat-label">已学课时</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ totalSections - learnedCount }}</div>
                <div class="stat-label">剩余课时</div>
              </div>
              <div class="stat-card">
                <div class="stat-value">{{ totalLearnTimeText }}</div>
                <div class="stat-label">累计学习</div>
              </div>
            </div>
            <h5 class="section-subtitle">各章节完成度</h5>
            <div
              v-for="(chapter, cIdx) in chapterList"
              :key="'stat-' + cIdx"
              class="chapter-progress-item"
            >
              <div class="chapter-progress-header">
                <span>{{ chapter.title }}</span>
                <span :class="getChapterProgressPercent(chapter) > 0 ? 'accent-text' : 'muted-text'">
                  {{ getChapterProgressPercent(chapter) }}%
                </span>
              </div>
              <el-progress
                :percentage="getChapterProgressPercent(chapter)"
                :stroke-width="6"
                :show-text="false"
                color="#409EFF"
              />
            </div>
          </el-tab-pane>

          <!-- 课时笔记 -->
          <el-tab-pane label="课时笔记" name="notes">
            <div v-if="noteList.length === 0" class="empty-state">
              <i class="el-icon-notebook-2" />
              <p>暂无笔记，学习时随时记录要点吧</p>
            </div>
            <div
              v-for="(note, nIdx) in noteList"
              :key="'note-' + nIdx"
              class="note-card"
            >
              <div class="note-header">
                <span class="note-time" @click="seekToTime(note.videoTime)">
                  <i class="el-icon-time" /> {{ formatTime(note.videoTime) }}
                </span>
                <span class="note-date">{{ note.createTime }}</span>
                <el-button
                  type="text"
                  size="mini"
                  icon="el-icon-delete"
                  class="note-delete"
                  @click="deleteNote(nIdx)"
                />
              </div>
              <div class="note-content">{{ note.content }}</div>
            </div>
            <!-- 笔记输入区 -->
            <div class="note-input-area">
              <el-input
                v-model="newNoteContent"
                type="textarea"
                :rows="3"
                :placeholder="'在此输入笔记内容' + (currentTime > 0 ? '（关联时间点 ' + formatTime(currentTime) + '）' : '')"
                resize="none"
              />
              <el-button
                type="primary"
                size="small"
                @click="saveNote"
                :disabled="!newNoteContent.trim()"
              >
                保存笔记
              </el-button>
            </div>
          </el-tab-pane>

          <!-- 课程问答 -->
          <el-tab-pane label="课程问答" name="questions">
            <div v-if="questionList.length === 0" class="empty-state">
              <i class="el-icon-chat-dot-round" />
              <p>暂无问答，有疑问可以提问</p>
            </div>
            <div
              v-for="(q, qIdx) in questionList"
              :key="'q-' + qIdx"
              class="question-card"
            >
              <div class="question-avatar">{{ q.askerName ? q.askerName.charAt(0) : '匿' }}</div>
              <div class="question-body">
                <div class="question-header">
                  <span class="question-asker">{{ q.askerName || '匿名用户' }}</span>
                  <span class="question-time">{{ q.createTime }}</span>
                </div>
                <div class="question-title-text" v-if="q.questionTitle">{{ q.questionTitle }}</div>
                <div class="question-content-text">{{ q.questionContent }}</div>
                <!-- 回答 -->
                <div class="answer-block" v-if="q.answerContent">
                  <div class="answer-label">讲师回答</div>
                  <div class="answer-text">{{ q.answerContent }}</div>
                </div>
                <div class="question-status" v-else>
                  <el-tag size="mini" type="warning" effect="plain">待回答</el-tag>
                </div>
              </div>
            </div>
            <!-- 提问输入区 -->
            <div class="question-input-area" v-if="currentSection">
              <el-input
                v-model="newQuestionTitle"
                placeholder="问题标题"
                size="small"
                style="margin-bottom: 8px;"
              />
              <el-input
                v-model="newQuestionContent"
                type="textarea"
                :rows="3"
                placeholder="详细描述你的问题..."
                resize="none"
              />
              <el-button
                type="primary"
                size="small"
                @click="submitQuestion"
                :disabled="!newQuestionTitle.trim() || !newQuestionContent.trim()"
                style="margin-top: 8px;"
              >
                提交问题
              </el-button>
            </div>
          </el-tab-pane>

          <!-- 课程资料 -->
          <el-tab-pane label="课程资料" name="materials">
            <div v-if="materialList.length === 0" class="empty-state">
              <i class="el-icon-folder" />
              <p>暂无课程资料</p>
            </div>
            <div
              v-for="(mat, mIdx) in materialList"
              :key="'mat-' + mIdx"
              class="material-card"
            >
              <i class="material-icon" :class="getMaterialIcon(mat.materialName)" />
              <div class="material-info">
                <div class="material-name">{{ mat.materialName }}</div>
                <div class="material-meta">
                  {{ formatFileSize(mat.fileSize) }}
                  <span v-if="mat.downloadCount"> &middot; 下载 {{ mat.downloadCount }} 次</span>
                </div>
              </div>
              <el-button
                v-if="mat.isDownloadable"
                type="primary"
                size="mini"
                plain
                @click="downloadMaterial(mat)"
              >
                下载
              </el-button>
              <el-tag v-else size="mini" type="info" effect="plain">需购买</el-tag>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
// ===== 演示模式：使用本地 Mock 数据，无需后端 =====
// 切换回真实 API 时，将此行改为: from '@/api/course/player'
import {
  getCourseDetail,
  getCourseSections,
  getSectionVideo,
  updatePlayProgress,
  markSectionComplete,
  checkSectionAccess,
  getCourseQuestions,
  askQuestion,
  getCourseMaterials
} from '@/api/course/mockData'

export default {
  name: 'CoursePlayer',

  data() {
    return {
      // ===== 课程数据 =====
      courseId: null,
      courseDetail: {},
      chapterList: [],           // 章节列表（含 sections 子数组）
      currentSection: null,      // 当前选中的课时
      videoInfo: {},             // 当前视频信息 (SectionVideoVO)

      // ===== 播放器状态 =====
      isPlaying: false,
      currentTime: 0,
      videoDuration: 0,
      bufferedPercent: 0,
      volume: 75,
      previousVolume: 75,
      playbackRate: 1.0,
      speedOptions: [0.5, 0.75, 1.0, 1.25, 1.5, 2.0],
      currentQuality: '1080p',
      qualityOptions: [],
      isFullscreen: false,
      showFullscreenTip: false,
      videoBuffering: false,
      videoLoading: false,
      autoPlayNext: true,

      // ===== 进度条交互 =====
      isDragging: false,
      progressHoverTime: null,
      progressHoverPercent: 0,

      // ===== UI 状态 =====
      sidebarCollapsed: false,
      activeTab: 'stats',

      // ===== 笔记数据 =====
      noteList: [],
      newNoteContent: '',

      // ===== 问答数据 =====
      questionList: [],
      newQuestionTitle: '',
      newQuestionContent: '',

      // ===== 资料数据 =====
      materialList: [],

      // ===== 学习统计 =====
      totalLearnTime: 0,         // 累计学习时间（秒）
      sessionStartTime: null,    // 本次会话开始时间

      // ===== 进度上报定时器 =====
      progressTimer: null,
      progressSaveInterval: 15000  // 每 15 秒保存一次进度
    }
  },

  computed: {
    /** 所有课时的扁平化列表 */
    allSections() {
      const list = []
      this.chapterList.forEach(ch => {
        if (ch.sections) {
          ch.sections.forEach(s => list.push(s))
        }
      })
      return list
    },

    /** 总课时数 */
    totalSections() {
      return this.allSections.length
    },

    /** 已学课时数 */
    learnedCount() {
      return this.allSections.filter(s => s.isLearned).length
    },

    /** 整体学习进度百分比 */
    overallProgress() {
      if (this.totalSections === 0) return 0
      return Math.round((this.learnedCount / this.totalSections) * 100)
    },

    /** 总时长文本 */
    totalDurationText() {
      const totalSeconds = this.allSections.reduce((sum, s) => sum + (s.duration || 0), 0)
      return this.formatDurationLong(totalSeconds)
    },

    /** 累计学习时间文本 */
    totalLearnTimeText() {
      return this.formatDurationShort(this.totalLearnTime)
    },

    /** 播放进度百分比 */
    playedPercent() {
      if (this.videoDuration === 0) return 0
      return Math.min((this.currentTime / this.videoDuration) * 100, 100)
    },

    /** 音量图标（使用铃铛图标替代，Element UI 无扬声器图标） */
    volumeIcon() {
      if (this.volume === 0) return 'el-icon-close-notification'
      return 'el-icon-bell'
    },

    /** 当前课时在扁平列表中的索引 */
    currentSectionIndex() {
      if (!this.currentSection) return -1
      return this.allSections.findIndex(s => s.id === this.currentSection.id)
    },

    /** 是否有上一课 */
    hasPrevSection() {
      return this.currentSectionIndex > 0
    },

    /** 是否有下一课 */
    hasNextSection() {
      return this.currentSectionIndex < this.allSections.length - 1
    }
  },

  watch: {
    /** 路由参数变化时重新加载课程 */
    '$route.params.courseId': {
      handler(newId) {
        if (newId) {
          this.courseId = Number(newId)
          this.initCourse()
        }
      },
      immediate: true
    }
  },

  mounted() {
    // 如果 watch(immediate) 已经触发了初始化，不再重复调用
    if (!this.courseId) {
      // 尝试从 query 参数获取 courseId（watch 只监听 params）
      const queryId = this.$route.query.courseId
      if (queryId) {
        this.courseId = Number(queryId)
      } else {
        // 演示模式：未指定 courseId 时使用默认值
        this.courseId = 1
      }
      this.initCourse()
    }

    // 监听全屏变化事件
    document.addEventListener('fullscreenchange', this.onFullscreenChange)
    document.addEventListener('webkitfullscreenchange', this.onFullscreenChange)

    // 监听键盘快捷键
    document.addEventListener('keydown', this.onKeydown)

    // 页面关闭前保存进度
    window.addEventListener('beforeunload', this.saveProgressBeforeLeave)
  },

  beforeDestroy() {
    // 清理定时器
    this.stopProgressTimer()

    // 保存当前进度
    this.saveCurrentProgress()

    // 移除事件监听
    document.removeEventListener('fullscreenchange', this.onFullscreenChange)
    document.removeEventListener('webkitfullscreenchange', this.onFullscreenChange)
    document.removeEventListener('keydown', this.onKeydown)
    window.removeEventListener('beforeunload', this.saveProgressBeforeLeave)
  },

  methods: {
    // ==================== 初始化 ====================

    /** 初始化课程数据 */
    async initCourse() {
      try {
        // 并行获取课程详情和大纲
        const [detailRes, sectionsRes] = await Promise.all([
          getCourseDetail(this.courseId),
          getCourseSections(this.courseId)
        ])

        this.courseDetail = detailRes.data || detailRes || {}
        this.buildChapterTree(sectionsRes.data || sectionsRes || [])

        // 加载课程资料
        this.loadMaterials()

        // 如果路由指定了章节 ID，则自动定位
        const sectionId = this.$route.query.sectionId
        if (sectionId) {
          const target = this.allSections.find(s => s.id === Number(sectionId))
          if (target) {
            this.handleSectionClick(target)
            return
          }
        }

        // 否则自动定位到第一个未完成的课时，或最后观看的课时
        this.autoLocateSection()
      } catch (error) {
        console.error('初始化课程失败:', error)
        this.$message.error('课程数据加载失败，请刷新重试')
      }
    },

    /**
     * 构建章节树结构
     * 后端返回的扁平列表 -> 按章分组
     * 如果后端已经返回分组结构，直接使用
     */
    buildChapterTree(sections) {
      // 如果数据已经是分组格式 (有 children / sections 属性)
      if (sections.length > 0 && (sections[0].children || sections[0].sections)) {
        this.chapterList = sections.map((ch, idx) => ({
          id: ch.id || idx,
          title: ch.title || '第' + (idx + 1) + '章',
          expanded: idx === 0,
          sections: (ch.children || ch.sections || []).map(s => ({
            ...s,
            isLearned: s.isLearned || false,
            isFree: s.isFree || false,
            hasQuestion: s.hasQuestion || false,
            questionCount: s.questionCount || 0,
            locked: s.locked || false
          }))
        }))
        return
      }

      // 扁平列表按固定数量分组（回退方案）
      const chunkSize = Math.ceil(sections.length / Math.max(Math.ceil(sections.length / 5), 1))
      const chapters = []
      for (let i = 0; i < sections.length; i += chunkSize) {
        const chunk = sections.slice(i, i + chunkSize)
        chapters.push({
          id: i,
          title: '第' + (chapters.length + 1) + '章',
          expanded: chapters.length === 0,
          sections: chunk.map(s => ({
            ...s,
            isLearned: s.isLearned || false,
            isFree: s.isFree || false,
            hasQuestion: s.hasQuestion || false,
            questionCount: s.questionCount || 0,
            locked: s.locked || false
          }))
        })
      }
      this.chapterList = chapters
    },

    /** 自动定位到上次学习位置或第一个未完成的课时 */
    autoLocateSection() {
      // 优先找正在学习中的（有进度但未完成）
      const inProgress = this.allSections.find(s => !s.isLearned && !s.locked)
      if (inProgress) {
        this.selectSection(inProgress, false)
        return
      }

      // 全部已完成，选第一个
      if (this.allSections.length > 0) {
        this.selectSection(this.allSections[0], false)
      }
    },

    // ==================== 目录交互 ====================

    /** 展开/折叠章节 */
    toggleChapter(index) {
      this.$set(this.chapterList[index], 'expanded', !this.chapterList[index].expanded)
    },

    /** 获取章节已学课时数 */
    getChapterLearnedCount(chapter) {
      if (!chapter.sections) return 0
      return chapter.sections.filter(s => s.isLearned).length
    },

    /** 获取章节完成百分比 */
    getChapterProgressPercent(chapter) {
      if (!chapter.sections || chapter.sections.length === 0) return 0
      const learned = chapter.sections.filter(s => s.isLearned).length
      return Math.round((learned / chapter.sections.length) * 100)
    },

    /** 点击课时节点 */
    async handleSectionClick(section) {
      // 锁定状态不允许播放
      if (section.locked) {
        this.$message.warning('该课时需要购买课程后才能观看')
        return
      }

      // 先保存当前进度
      if (this.currentSection && this.currentSection.id !== section.id) {
        await this.saveCurrentProgress()
      }

      this.selectSection(section, true)
    },

    /** 选中课时并加载视频 */
    async selectSection(section, autoPlay = true) {
      this.currentSection = section
      this.videoLoading = true
      this.videoInfo = {}

      // 确保当前章节展开
      this.expandChapterOfSection(section)

      try {
        // 检查权限
        const accessRes = await checkSectionAccess(section.id)
        const access = accessRes.data || accessRes
        if (!access.hasAccess && !section.isFree) {
          this.videoLoading = false
          if (access.needPurchase) {
            this.$message.warning('需要购买课程才能观看此课时，课程价格: ' + (access.price || ''))
          } else {
            this.$message.warning(access.reason || '暂无观看权限')
          }
          return
        }

        // 获取视频信息
        const videoRes = await getSectionVideo(section.id)
        this.videoInfo = videoRes.data || videoRes || {}

        // 设置清晰度选项
        this.setupQualityOptions()

        // 等待 DOM 更新后操作视频元素
        this.$nextTick(() => {
          const video = this.$refs.videoPlayer
          if (video && this.videoInfo.mediaUrl) {
            video.load()

            // 断点续播：恢复上次播放位置
            if (this.videoInfo.lastPosition && this.videoInfo.lastPosition > 0) {
              video.currentTime = this.videoInfo.lastPosition
              this.$message.info('已恢复到上次观看位置 ' + this.formatTime(this.videoInfo.lastPosition))
            }

            // 应用播放速度
            video.playbackRate = this.playbackRate

            // 自动播放
            if (autoPlay) {
              video.play().catch(() => {})
            }
          }
          this.videoLoading = false
        })

        // 加载当前课时的问答
        this.loadQuestions()

        // 启动进度上报定时器
        this.startProgressTimer()

      } catch (error) {
        console.error('加载视频失败:', error)
        this.videoLoading = false
        this.$message.error('视频加载失败，请重试')
      }
    },

    /** 展开包含指定课时的章节 */
    expandChapterOfSection(section) {
      this.chapterList.forEach(ch => {
        if (ch.sections && ch.sections.some(s => s.id === section.id)) {
          this.$set(ch, 'expanded', true)
        }
      })
    },

    // ==================== 视频播放控制 ====================

    /** 播放/暂停切换 */
    togglePlay() {
      const video = this.$refs.videoPlayer
      if (!video || !this.videoInfo.mediaUrl) {
        if (this.currentSection) {
          this.handleSectionClick(this.currentSection)
        }
        return
      }
      if (this.isPlaying) {
        video.pause()
      } else {
        video.play().catch(() => {})
      }
    },

    /** 占位区域点击播放 */
    handlePlayPlaceholder() {
      if (this.currentSection) {
        this.handleSectionClick(this.currentSection)
      }
    },

    /** 播放事件 */
    onPlay() {
      this.isPlaying = true
      this.sessionStartTime = Date.now()
    },

    /** 暂停事件 */
    onPause() {
      this.isPlaying = false
      this.accumulateLearnTime()
    },

    /** 时间更新事件 */
    onTimeUpdate() {
      const video = this.$refs.videoPlayer
      if (video && !this.isDragging) {
        this.currentTime = video.currentTime
      }
    },

    /** 元数据加载完成 */
    onMetadataLoaded() {
      const video = this.$refs.videoPlayer
      if (video) {
        this.videoDuration = video.duration
      }
    },

    /** 缓冲进度 */
    onBufferProgress() {
      const video = this.$refs.videoPlayer
      if (video && video.buffered.length > 0) {
        const bufferedEnd = video.buffered.end(video.buffered.length - 1)
        this.bufferedPercent = (bufferedEnd / video.duration) * 100
      }
    },

    /** 视频播放结束 - 核心联动逻辑 */
    async onVideoEnded() {
      this.isPlaying = false
      this.accumulateLearnTime()

      // 1. 标记当前课时为已完成
      try {
        const res = await markSectionComplete(this.currentSection.id)
        // 更新本地状态
        this.markSectionLearned(this.currentSection.id)
        this.videoInfo.isCompleted = true

        // 检查是否需要考试
        const examId = res.data
        if (examId) {
          this.$confirm('该章节学习完成，请参加考试以检验学习效果', '提示', {
            confirmButtonText: '前往考试',
            cancelButtonText: '稍后再说',
            type: 'info'
          }).then(() => {
            // 跳转到考试页面
            this.$router.push({ path: '/exam/' + examId })
          }).catch(() => {})
          return
        }
      } catch (error) {
        console.error('标记完成失败:', error)
      }

      // 2. 自动连播下一课时
      if (this.autoPlayNext && this.hasNextSection) {
        const nextSection = this.allSections[this.currentSectionIndex + 1]
        if (!nextSection.locked) {
          this.$message.success('即将播放下一课时：' + nextSection.title)
          setTimeout(() => {
            this.selectSection(nextSection, true)
          }, 1500)
        }
      } else if (!this.hasNextSection) {
        this.$message.success('恭喜你，课程全部学习完成！')
      }
    },

    /** 视频错误 */
    onVideoError() {
      this.videoBuffering = false
      this.videoLoading = false
      if (this.videoInfo.mediaUrl) {
        this.$message.error('视频加载失败，请检查网络后重试')
      }
    },

    /** 在本地数据中标记课时为已完成 */
    markSectionLearned(sectionId) {
      this.chapterList.forEach(ch => {
        if (ch.sections) {
          ch.sections.forEach(s => {
            if (s.id === sectionId) {
              this.$set(s, 'isLearned', true)
            }
          })
        }
      })
    },

    /** 上一课 */
    playPrevSection() {
      if (!this.hasPrevSection) return
      const prevSection = this.allSections[this.currentSectionIndex - 1]
      this.handleSectionClick(prevSection)
    },

    /** 下一课 */
    playNextSection() {
      if (!this.hasNextSection) return
      const nextSection = this.allSections[this.currentSectionIndex + 1]
      this.handleSectionClick(nextSection)
    },

    // ==================== 进度条交互 ====================

    /** 开始拖拽进度条 */
    startProgressDrag(e) {
      this.isDragging = true
      this.updateProgressFromMouse(e)
      document.addEventListener('mousemove', this.onProgressDrag)
      document.addEventListener('mouseup', this.stopProgressDrag)
    },

    /** 拖拽中 */
    onProgressDrag(e) {
      if (this.isDragging) {
        this.updateProgressFromMouse(e)
      }
    },

    /** 停止拖拽 */
    stopProgressDrag() {
      this.isDragging = false
      document.removeEventListener('mousemove', this.onProgressDrag)
      document.removeEventListener('mouseup', this.stopProgressDrag)
    },

    /** 根据鼠标位置更新进度 */
    updateProgressFromMouse(e) {
      const bar = this.$refs.progressBar
      if (!bar) return
      const rect = bar.getBoundingClientRect()
      let percent = (e.clientX - rect.left) / rect.width
      percent = Math.max(0, Math.min(1, percent))
      const newTime = percent * this.videoDuration
      this.currentTime = newTime
      const video = this.$refs.videoPlayer
      if (video) {
        video.currentTime = newTime
      }
    },

    /** 进度条悬浮提示 */
    onProgressHover(e) {
      if (this.isDragging) return
      const bar = this.$refs.progressBar
      if (!bar) return
      const rect = bar.getBoundingClientRect()
      let percent = (e.clientX - rect.left) / rect.width
      percent = Math.max(0, Math.min(1, percent))
      this.progressHoverPercent = percent * 100
      this.progressHoverTime = percent * this.videoDuration
    },

    // ==================== 音量控制 ====================

    /** 音量变化 */
    onVolumeChange(val) {
      const video = this.$refs.videoPlayer
      if (video) {
        video.volume = val / 100
      }
    },

    /** 静音切换 */
    toggleMute() {
      if (this.volume > 0) {
        this.previousVolume = this.volume
        this.volume = 0
      } else {
        this.volume = this.previousVolume || 75
      }
      this.onVolumeChange(this.volume)
    },

    // ==================== 倍速 / 清晰度 ====================

    /** 倍速变化 */
    onSpeedChange(speed) {
      this.playbackRate = speed
      const video = this.$refs.videoPlayer
      if (video) {
        video.playbackRate = speed
      }
    },

    /** 设置清晰度选项 */
    setupQualityOptions() {
      const resolution = this.videoInfo.videoResolution || '1080p'
      // 根据视频信息生成可用清晰度
      const qualityMap = {
        '4k': [
          { label: '4K', value: '4k' },
          { label: '1080p', value: '1080p' },
          { label: '720p', value: '720p' }
        ],
        '1080p': [
          { label: '1080p', value: '1080p' },
          { label: '720p', value: '720p' }
        ],
        '720p': [
          { label: '720p', value: '720p' }
        ]
      }
      this.qualityOptions = qualityMap[resolution] || qualityMap['1080p']
      this.currentQuality = resolution
    },

    /** 清晰度切换 */
    onQualityChange(quality) {
      this.currentQuality = quality
      this.$message.info('已切换到 ' + quality)
      // 实际项目中这里会请求不同清晰度的视频URL
    },

    // ==================== 全屏控制 ====================

    /** 全屏切换 */
    toggleFullscreen() {
      const playerArea = this.$refs.playerArea
      if (!playerArea) return

      if (!this.isFullscreen) {
        if (playerArea.requestFullscreen) {
          playerArea.requestFullscreen()
        } else if (playerArea.webkitRequestFullscreen) {
          playerArea.webkitRequestFullscreen()
        }
      } else {
        if (document.exitFullscreen) {
          document.exitFullscreen()
        } else if (document.webkitExitFullscreen) {
          document.webkitExitFullscreen()
        }
      }
    },

    /** 全屏变化监听 */
    onFullscreenChange() {
      this.isFullscreen = !!(document.fullscreenElement || document.webkitFullscreenElement)
      this.showFullscreenTip = true
      setTimeout(() => { this.showFullscreenTip = false }, 1500)
    },

    // ==================== 键盘快捷键 ====================

    onKeydown(e) {
      // 如果焦点在输入框，不拦截
      if (['INPUT', 'TEXTAREA'].includes(e.target.tagName)) return

      switch (e.code) {
        case 'Space':
          e.preventDefault()
          this.togglePlay()
          break
        case 'ArrowLeft':
          e.preventDefault()
          this.seekRelative(-5)
          break
        case 'ArrowRight':
          e.preventDefault()
          this.seekRelative(5)
          break
        case 'ArrowUp':
          e.preventDefault()
          this.volume = Math.min(100, this.volume + 5)
          this.onVolumeChange(this.volume)
          break
        case 'ArrowDown':
          e.preventDefault()
          this.volume = Math.max(0, this.volume - 5)
          this.onVolumeChange(this.volume)
          break
        case 'KeyF':
          e.preventDefault()
          this.toggleFullscreen()
          break
        case 'KeyM':
          e.preventDefault()
          this.toggleMute()
          break
      }
    },

    /** 相对跳转 */
    seekRelative(seconds) {
      const video = this.$refs.videoPlayer
      if (video) {
        video.currentTime = Math.max(0, Math.min(video.duration, video.currentTime + seconds))
      }
    },

    /** 跳转到指定时间 */
    seekToTime(time) {
      const video = this.$refs.videoPlayer
      if (video && this.videoInfo.mediaUrl) {
        video.currentTime = time
        if (!this.isPlaying) {
          video.play().catch(() => {})
        }
      }
    },

    // ==================== 进度上报 ====================

    /** 启动进度上报定时器 */
    startProgressTimer() {
      this.stopProgressTimer()
      this.progressTimer = setInterval(() => {
        this.saveCurrentProgress()
      }, this.progressSaveInterval)
    },

    /** 停止进度上报定时器 */
    stopProgressTimer() {
      if (this.progressTimer) {
        clearInterval(this.progressTimer)
        this.progressTimer = null
      }
    },

    /** 保存当前播放进度到服务器 */
    async saveCurrentProgress() {
      if (!this.currentSection || !this.videoInfo.mediaUrl) return
      if (this.currentTime <= 0) return

      try {
        const progress = this.videoDuration > 0
          ? Math.round((this.currentTime / this.videoDuration) * 100)
          : 0

        await updatePlayProgress(this.currentSection.id, {
          progress: progress,
          lastPosition: Math.floor(this.currentTime),
          learnTime: this.getSessionLearnTime(),
          status: progress >= 95 ? 3 : 1
        })
      } catch (error) {
        // 静默失败，不打扰用户
        console.warn('进度保存失败:', error)
      }
    },

    /** 页面关闭前保存进度（使用 sendBeacon 确保可靠发送） */
    saveProgressBeforeLeave() {
      if (!this.currentSection || !this.videoInfo.mediaUrl || this.currentTime <= 0) return

      const progress = this.videoDuration > 0
        ? Math.round((this.currentTime / this.videoDuration) * 100)
        : 0
      const data = {
        progress: progress,
        lastPosition: Math.floor(this.currentTime),
        learnTime: this.getSessionLearnTime(),
        status: progress >= 95 ? 3 : 1
      }

      // 优先使用 sendBeacon，它在页面卸载时也能可靠发送
      if (navigator.sendBeacon) {
        const url = process.env.VUE_APP_BASE_API + '/system/course/section/' + this.currentSection.id + '/progress'
        const blob = new Blob([JSON.stringify(data)], { type: 'application/json' })
        navigator.sendBeacon(url, blob)
      } else {
        // 降级方案：同步 XMLHttpRequest（不推荐但兼容老浏览器）
        this.saveCurrentProgress()
      }
    },

    /** 获取本次会话学习时长（秒） */
    getSessionLearnTime() {
      if (!this.sessionStartTime) return 0
      return Math.floor((Date.now() - this.sessionStartTime) / 1000)
    },

    /** 累加学习时间 */
    accumulateLearnTime() {
      if (this.sessionStartTime) {
        this.totalLearnTime += this.getSessionLearnTime()
        this.sessionStartTime = null
      }
    },

    // ==================== 笔记功能 ====================

    /** 保存笔记 */
    saveNote() {
      if (!this.newNoteContent.trim()) return

      const note = {
        videoTime: Math.floor(this.currentTime),
        content: this.newNoteContent.trim(),
        createTime: this.formatDate(new Date()),
        sectionId: this.currentSection ? this.currentSection.id : null
      }

      this.noteList.push(note)
      this.newNoteContent = ''
      this.$message.success('笔记已保存')

      // 实际项目中这里会调用后端 API 保存笔记
    },

    /** 删除笔记 */
    deleteNote(index) {
      this.$confirm('确定删除此笔记吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.noteList.splice(index, 1)
        this.$message.success('笔记已删除')
      }).catch(() => {})
    },

    // ==================== 问答功能 ====================

    /** 加载课程问答 */
    async loadQuestions() {
      if (!this.courseId) return
      try {
        const sectionId = this.currentSection ? this.currentSection.id : null
        const res = await getCourseQuestions(this.courseId, sectionId)
        this.questionList = res.data || res || []
      } catch (error) {
        console.warn('加载问答失败:', error)
      }
    },

    /** 提交问题 */
    async submitQuestion() {
      if (!this.newQuestionTitle.trim() || !this.newQuestionContent.trim()) return

      try {
        await askQuestion({
          courseId: this.courseId,
          sectionId: this.currentSection ? this.currentSection.id : null,
          questionTitle: this.newQuestionTitle.trim(),
          questionContent: this.newQuestionContent.trim()
        })

        this.newQuestionTitle = ''
        this.newQuestionContent = ''
        this.$message.success('问题提交成功，等待讲师回答')
        this.loadQuestions()
      } catch (error) {
        this.$message.error('提交失败，请重试')
      }
    },

    // ==================== 课程资料 ====================

    /** 加载课程资料 */
    async loadMaterials() {
      if (!this.courseId) return
      try {
        const res = await getCourseMaterials(this.courseId)
        this.materialList = res.data || res || []
      } catch (error) {
        console.warn('加载资料失败:', error)
      }
    },

    /** 下载资料 */
    downloadMaterial(material) {
      if (material.fileUrl) {
        window.open(material.fileUrl, '_blank')
      }
    },

    /** 获取资料图标 */
    getMaterialIcon(fileName) {
      if (!fileName) return 'el-icon-document'
      const ext = fileName.split('.').pop().toLowerCase()
      const iconMap = {
        pdf: 'el-icon-document',
        doc: 'el-icon-document',
        docx: 'el-icon-document',
        xls: 'el-icon-s-grid',
        xlsx: 'el-icon-s-grid',
        ppt: 'el-icon-s-marketing',
        pptx: 'el-icon-s-marketing',
        zip: 'el-icon-files',
        rar: 'el-icon-files',
        mp4: 'el-icon-video-camera',
        mp3: 'el-icon-headset',
        jpg: 'el-icon-picture',
        png: 'el-icon-picture'
      }
      return iconMap[ext] || 'el-icon-document'
    },

    // ==================== Tab 切换 ====================

    onTabChange(tab) {
      if (tab.name === 'questions') {
        this.loadQuestions()
      } else if (tab.name === 'materials') {
        this.loadMaterials()
      }
    },

    // ==================== 工具方法 ====================

    /** 格式化时长 mm:ss */
    formatTime(seconds) {
      if (!seconds || isNaN(seconds)) return '00:00'
      seconds = Math.floor(seconds)
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
    },

    /** 格式化课时时长 mm:ss */
    formatDuration(seconds) {
      if (!seconds) return '--:--'
      const m = Math.floor(seconds / 60)
      const s = seconds % 60
      return String(m).padStart(2, '0') + ':' + String(s).padStart(2, '0')
    },

    /** 格式化长时长 Xh Xmin */
    formatDurationLong(seconds) {
      if (!seconds) return '0min'
      const h = Math.floor(seconds / 3600)
      const m = Math.floor((seconds % 3600) / 60)
      if (h > 0) return h + 'h ' + m + 'min'
      return m + 'min'
    },

    /** 格式化短时长 XhXm */
    formatDurationShort(seconds) {
      if (!seconds) return '0m'
      const h = Math.floor(seconds / 3600)
      const m = Math.floor((seconds % 3600) / 60)
      if (h > 0) return h + 'h' + m + 'm'
      return m + 'm'
    },

    /** 格式化日期 */
    formatDate(date) {
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      return y + '-' + m + '-' + d
    },

    /** 格式化文件大小 */
    formatFileSize(bytes) {
      if (!bytes) return '0 B'
      const units = ['B', 'KB', 'MB', 'GB']
      let i = 0
      let size = bytes
      while (size >= 1024 && i < units.length - 1) {
        size /= 1024
        i++
      }
      return size.toFixed(1) + ' ' + units[i]
    }
  }
}
</script>

<style lang="scss" scoped>
/* ==================== 布局容器 ==================== */
.course-player-container {
  display: flex;
  /* 有 Layout 包裹时减去头部高度，独立页面时占满全屏 */
  height: calc(100vh - 84px);
  background: #f5f7fa;
  overflow: hidden;
}

/* 独立演示模式（路由不包裹 Layout 时，html/body 100%） */
:root .course-player-container:only-child {
  height: 100vh;
}

/* ==================== 左侧课程目录 ==================== */
.cp-sidebar {
  width: 320px;
  min-width: 320px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;

  &.collapsed {
    width: 48px;
    min-width: 48px;

    .cp-sidebar-header {
      padding: 12px 8px;

      .header-top {
        justify-content: center;

        .course-title { display: none; }
      }
    }
  }
}

.cp-sidebar-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;

  .header-top {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  .course-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    margin: 0;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
    margin-right: 8px;
  }

  .collapse-btn {
    padding: 4px;
    font-size: 16px;
    color: #909399;
    flex-shrink: 0;
  }

  .course-meta {
    font-size: 12px;
    color: #909399;
    margin-top: 6px;
  }
}

.progress-wrapper {
  margin-top: 12px;

  .progress-text {
    display: flex;
    justify-content: space-between;
    font-size: 11px;
    color: #909399;
    margin-top: 4px;
  }
}

/* ===== 树形目录 ===== */
.cp-tree {
  flex: 1;
  overflow-y: auto;
  padding: 4px 0;

  &::-webkit-scrollbar {
    width: 4px;
  }
  &::-webkit-scrollbar-thumb {
    background: #dcdfe6;
    border-radius: 2px;
  }
}

.chapter-group {
  margin-bottom: 2px;
}

.chapter-title {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  cursor: pointer;
  transition: background 0.15s;
  user-select: none;

  &:hover {
    background: #f5f7fa;
  }

  .chapter-arrow {
    margin-right: 6px;
    font-size: 12px;
    color: #909399;
    transition: transform 0.2s;

    &.open {
      transform: rotate(90deg);
    }
  }

  .chapter-name {
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .chapter-count {
    margin-left: 8px;
    font-size: 11px;
    color: #909399;
    font-weight: 400;
    flex-shrink: 0;
  }
}

.slide-sections-enter-active,
.slide-sections-leave-active {
  transition: all 0.25s ease;
  max-height: 1000px;
  overflow: hidden;
}
.slide-sections-enter,
.slide-sections-leave-to {
  max-height: 0;
  opacity: 0;
}

.section-item {
  display: flex;
  align-items: center;
  padding: 8px 16px 8px 44px;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.15s;
  gap: 6px;

  &:hover {
    background: #f5f7fa;
  }

  &.active {
    background: #ecf5ff;
    color: #409EFF;

    .section-icon {
      color: #409EFF;
    }
  }

  &.completed {
    .section-icon {
      color: #67C23A;
    }
  }

  &.locked {
    color: #c0c4cc;
    cursor: not-allowed;

    .section-icon {
      color: #c0c4cc;
    }
  }
}

.section-icon {
  width: 20px;
  text-align: center;
  flex-shrink: 0;
  font-size: 14px;
  color: #909399;

  .playing-icon {
    animation: pulse 1.5s infinite;
  }
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}

.section-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  min-width: 0;
}

.section-badges {
  display: flex;
  gap: 4px;
  flex-shrink: 0;

  .el-tag {
    padding: 0 4px;
    height: 18px;
    line-height: 16px;
    font-size: 10px;
  }
}

.section-duration {
  font-size: 11px;
  color: #909399;
  flex-shrink: 0;
  font-family: 'Courier New', monospace;
}

/* ==================== 右侧主内容区 ==================== */
.cp-main {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  overflow: hidden;
}

/* ===== 视频播放区域 ===== */
.cp-player-wrapper {
  flex-shrink: 0;
  background: #000;
}

.player-area {
  position: relative;
  width: 100%;
  max-height: 480px;
  aspect-ratio: 16 / 9;
  background: #000;
  overflow: hidden;
}

.video-element {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.player-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.5);
  z-index: 5;
}

.player-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #1a1a2e;
  cursor: pointer;
  z-index: 3;

  .play-circle {
    width: 68px;
    height: 68px;
    border-radius: 50%;
    background: rgba(255, 255, 255, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 16px;
    transition: background 0.2s;

    i {
      font-size: 32px;
      color: rgba(255, 255, 255, 0.8);
      margin-left: 4px;
    }
  }

  &:hover .play-circle {
    background: rgba(255, 255, 255, 0.2);
  }

  .placeholder-title {
    color: rgba(255, 255, 255, 0.85);
    font-size: 15px;
    margin: 0;
  }

  .placeholder-sub {
    color: rgba(255, 255, 255, 0.5);
    font-size: 12px;
    margin: 4px 0 0;
  }
}

.fullscreen-tip {
  position: absolute;
  top: 16px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 6px 16px;
  border-radius: 4px;
  font-size: 13px;
  z-index: 10;
}

/* ===== 视频控制栏 ===== */
.cp-controls {
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 16px;
}

.progress-bar-wrapper {
  position: relative;
  height: 6px;
  background: #e4e7ed;
  cursor: pointer;
  margin: 0 -16px;
  padding: 0 16px;
  transition: height 0.15s;

  &:hover {
    height: 8px;

    .progress-thumb {
      opacity: 1;
    }
  }
}

.progress-buffered {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: rgba(64, 158, 255, 0.2);
  pointer-events: none;
}

.progress-played {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: #409EFF;
  pointer-events: none;
  z-index: 1;
}

.progress-thumb {
  position: absolute;
  width: 14px;
  height: 14px;
  background: #409EFF;
  border: 2px solid #fff;
  border-radius: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  z-index: 2;
  opacity: 0;
  transition: opacity 0.15s;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.progress-tooltip {
  position: absolute;
  bottom: calc(100% + 8px);
  transform: translateX(-50%);
  background: #303133;
  color: #fff;
  padding: 2px 8px;
  border-radius: 3px;
  font-size: 11px;
  white-space: nowrap;
  z-index: 10;
  font-family: 'Courier New', monospace;
}

.controls-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
}

.controls-left {
  display: flex;
  align-items: center;
  gap: 4px;
}

.controls-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.ctrl-btn {
  padding: 4px 6px;
  font-size: 18px;
  color: #606266;

  &:hover {
    color: #409EFF;
  }

  &.is-disabled {
    color: #c0c4cc;
  }
}

.time-display {
  font-size: 12px;
  color: #909399;
  font-family: 'Courier New', monospace;
  margin-left: 8px;
}

.volume-control {
  display: flex;
  align-items: center;
  gap: 4px;

  .volume-slider {
    width: 70px;

    ::v-deep .el-slider__runway {
      height: 3px;
      margin: 0;
      background: #e4e7ed;
    }

    ::v-deep .el-slider__bar {
      height: 3px;
      background: #409EFF;
    }

    ::v-deep .el-slider__button-wrapper {
      top: -16px;
    }

    ::v-deep .el-slider__button {
      width: 10px;
      height: 10px;
      border: 2px solid #409EFF;
    }
  }
}

.ctrl-dropdown {
  font-size: 12px;
  color: #606266;
  padding: 3px 8px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  user-select: none;
  background: #fff;
  transition: all 0.15s;

  &:hover {
    border-color: #409EFF;
    color: #409EFF;
  }
}

/* ===== 当前课时信息栏 ===== */
.cp-info-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  flex-shrink: 0;

  .info-left {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .current-section-title {
    font-size: 14px;
    font-weight: 600;
    color: #303133;
    margin: 0;
  }

  .info-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .auto-play-label {
    font-size: 12px;
    color: #909399;
  }
}

/* ==================== 底部标签功能区 ==================== */
.cp-tabs-area {
  flex: 1;
  background: #fff;
  overflow: hidden;
  display: flex;
  flex-direction: column;

  ::v-deep .el-tabs {
    display: flex;
    flex-direction: column;
    height: 100%;

    .el-tabs__header {
      margin-bottom: 0;
      padding: 0 16px;
      flex-shrink: 0;
    }

    .el-tabs__content {
      flex: 1;
      overflow-y: auto;
      padding: 16px 20px;

      &::-webkit-scrollbar {
        width: 4px;
      }
      &::-webkit-scrollbar-thumb {
        background: #dcdfe6;
        border-radius: 2px;
      }
    }

    .el-tab-pane {
      height: 100%;
    }
  }
}

/* ===== 学习统计 ===== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 14px 8px;
  background: #f5f7fa;
  border-radius: 8px;
  border: 1px solid #e4e7ed;

  .stat-value {
    font-size: 22px;
    font-weight: 600;
    color: #409EFF;
  }

  .stat-label {
    font-size: 11px;
    color: #909399;
    margin-top: 4px;
  }
}

.section-subtitle {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
  margin-bottom: 12px;
}

.chapter-progress-item {
  margin-bottom: 14px;

  .chapter-progress-header {
    display: flex;
    justify-content: space-between;
    font-size: 12px;
    margin-bottom: 4px;
  }

  .accent-text {
    color: #409EFF;
  }

  .muted-text {
    color: #c0c4cc;
  }
}

/* ===== 课时笔记 ===== */
.note-card {
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 8px;
  background: #fafbfc;

  .note-header {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
    gap: 8px;
  }

  .note-time {
    font-size: 12px;
    color: #409EFF;
    cursor: pointer;
    font-family: 'Courier New', monospace;

    &:hover {
      text-decoration: underline;
    }
  }

  .note-date {
    font-size: 11px;
    color: #c0c4cc;
  }

  .note-delete {
    margin-left: auto;
    color: #c0c4cc;
    padding: 2px;

    &:hover {
      color: #F56C6C;
    }
  }

  .note-content {
    font-size: 13px;
    line-height: 1.6;
    color: #606266;
  }
}

.note-input-area {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;

  .el-button {
    align-self: flex-end;
  }
}

/* ===== 课程问答 ===== */
.question-card {
  display: flex;
  gap: 12px;
  padding: 14px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 10px;
  background: #fafbfc;
}

.question-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409EFF;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  flex-shrink: 0;
}

.question-body {
  flex: 1;
  min-width: 0;

  .question-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 4px;
  }

  .question-asker {
    font-size: 13px;
    font-weight: 500;
    color: #303133;
  }

  .question-time {
    font-size: 11px;
    color: #c0c4cc;
  }

  .question-title-text {
    font-size: 13px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .question-content-text {
    font-size: 13px;
    line-height: 1.6;
    color: #606266;
    margin-bottom: 8px;
  }
}

.answer-block {
  padding: 10px 12px;
  background: #fff;
  border-radius: 4px;
  border-left: 3px solid #409EFF;

  .answer-label {
    font-size: 11px;
    color: #409EFF;
    font-weight: 500;
    margin-bottom: 4px;
  }

  .answer-text {
    font-size: 12px;
    line-height: 1.6;
    color: #606266;
  }
}

.question-status {
  margin-top: 4px;
}

.question-input-area {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

/* ===== 课程资料 ===== */
.material-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 8px;
  background: #fafbfc;
  transition: background 0.15s;

  &:hover {
    background: #f0f2f5;
  }

  .material-icon {
    font-size: 24px;
    color: #909399;
    flex-shrink: 0;
  }

  .material-info {
    flex: 1;
    min-width: 0;
  }

  .material-name {
    font-size: 13px;
    font-weight: 500;
    color: #303133;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .material-meta {
    font-size: 11px;
    color: #909399;
    margin-top: 2px;
  }
}

/* ===== 空状态 ===== */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #c0c4cc;

  i {
    font-size: 40px;
    margin-bottom: 12px;
  }

  p {
    font-size: 13px;
    margin: 0;
  }
}

/* ==================== 响应式适配 ==================== */
@media screen and (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media screen and (max-width: 960px) {
  .cp-sidebar {
    width: 260px;
    min-width: 260px;
  }

  .section-item {
    padding-left: 32px;
  }
}

@media screen and (max-width: 768px) {
  .course-player-container {
    flex-direction: column;
    height: auto;
  }

  .cp-sidebar {
    width: 100% !important;
    min-width: 100% !important;
    max-height: 300px;
    border-right: none;
    border-bottom: 1px solid #e4e7ed;

    &.collapsed {
      max-height: 48px;
    }
  }

  .player-area {
    max-height: none;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .controls-right {
    .volume-control {
      display: none;
    }
  }
}
</style>
