<template>
  <div class="course-detail-page">
    <!-- ===== 页面头部 ===== -->
    <div class="detail-header">
      <div class="detail-header-inner">
        <el-button type="text" icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回课程列表</el-button>
      </div>
    </div>

    <!-- ===== 课程信息 Hero ===== -->
    <div class="course-hero" v-if="course" v-loading="loading">
      <div class="hero-inner">
        <div class="hero-cover">
          <img :src="course.cover" :alt="course.title" />
        </div>
        <div class="hero-info">
          <h1 class="hero-title">{{ course.title }}</h1>
          <div class="hero-tags">
            <el-tag
              v-for="tag in course.tags"
              :key="tag.id"
              size="small"
              :style="{ color: tag.color, borderColor: tag.color + '40', background: tag.color + '15' }"
              effect="plain"
            >{{ tag.name }}</el-tag>
          </div>
          <p class="hero-desc">{{ course.description }}</p>
          <div class="hero-stats">
            <span class="stat good"><i class="el-icon-thumb"></i> 好评 {{ course.goodCount }}</span>
            <span class="stat medium"><i class="el-icon-minus"></i> 中评 {{ course.mediumCount }}</span>
            <span class="stat bad"><i class="el-icon-thumb rotate-icon"></i> 差评 {{ course.badCount }}</span>
            <span class="divider">|</span>
            <span class="stat fav" :class="{ active: course.isFavorite }" @click="handleFavorite">
              <i :class="course.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"></i>
              收藏 {{ course.favoriteCount }}
            </span>
            <span class="divider">|</span>
            <span class="stat"><i class="el-icon-oshUser-solid"></i> {{ course.studentCount }}人学习</span>
          </div>
          <div class="hero-meta">
            <div class="meta-item">
              <span class="meta-label">服务周期</span>
              <span class="meta-value">{{ course.serviceCycle }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">服务内容</span>
              <span class="meta-value">{{ course.serviceContent }}</span>
            </div>
            <div class="meta-item">
              <span class="meta-label">讲师</span>
              <span class="meta-value">{{ course.instructor }}</span>
            </div>
          </div>
          <div class="hero-actions">
            <div class="price-area" v-if="!course.isBuy">
              <span class="current-price">¥{{ course.price }}</span>
              <span class="original-price" v-if="course.originalPrice">¥{{ course.originalPrice }}</span>
            </div>
            <el-button v-if="course.isBuy" type="success" icon="el-icon-video-play" @click="playFirstSection">
              继续学习
            </el-button>
            <el-button v-else type="primary" icon="el-icon-shopping-cart-2">
              立即购买
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== Tab 切换 ===== -->
    <div class="detail-tabs-wrapper">
      <div class="detail-tabs-inner">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <el-tab-pane label="课程介绍" name="intro" />
          <el-tab-pane label="课程章节" name="chapters" />
          <el-tab-pane label="课程资料" name="materials" />
        </el-tabs>
      </div>
    </div>

    <!-- ===== Tab 内容 ===== -->
    <div class="detail-content">
      <div class="detail-content-inner">
        <!-- 课程介绍 -->
        <div v-show="activeTab === 'intro'" class="tab-panel">
          <div class="intro-card" v-if="course">
            <h3>课程简介</h3>
            <p>{{ course.description }}</p>
            <div class="intro-stats">
              <div class="intro-stat-item">
                <span class="stat-num">{{ course.chapterCount }}</span>
                <span class="stat-label">章节</span>
              </div>
              <div class="intro-stat-item">
                <span class="stat-num">{{ course.sectionCount }}</span>
                <span class="stat-label">课时</span>
              </div>
              <div class="intro-stat-item">
                <span class="stat-num">{{ formatTotalDur(course.totalDuration) }}</span>
                <span class="stat-label">总时长</span>
              </div>
              <div class="intro-stat-item">
                <span class="stat-num">{{ course.studentCount }}</span>
                <span class="stat-label">学员</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 课程章节 -->
        <div v-show="activeTab === 'chapters'" class="tab-panel">
          <div
            v-for="(chapter, cIdx) in chapters"
            :key="chapter.id"
            class="chapter-card"
          >
            <div class="chapter-card-header" @click="toggleChapter(cIdx)">
              <i class="el-icon-caret-right chapter-arrow" :class="{ open: chapter._expanded }" />
              <span class="chapter-card-title">{{ chapter.title }}</span>
              <span class="chapter-card-count">{{ chapter.sections.length }}课时</span>
            </div>
            <transition name="slide">
              <div class="chapter-sections" v-show="chapter._expanded">
                <div
                  v-for="section in chapter.sections"
                  :key="section.id"
                  class="section-card-row"
                >
                  <div class="section-info">
                    <i class="el-icon-video-camera-solid section-play-icon"></i>
                    <span class="section-card-title">{{ section.title }}</span>
                    <el-tag v-if="section.isFree" size="mini" type="success" effect="plain">免费</el-tag>
                    <span class="section-card-duration">{{ formatDur(section.duration) }}</span>
                  </div>
                  <div class="section-actions">
                    <el-button
                      v-if="course && (course.isBuy || section.isFree)"
                      type="primary"
                      size="mini"
                      icon="el-icon-video-play"
                      @click="playSection(section.id)"
                    >播放</el-button>
                    <el-button
                      v-else
                      size="mini"
                      disabled
                    >
                      <i class="el-icon-lock"></i> 需购买
                    </el-button>
                    <!-- 资料下载 -->
                    <el-button
                      v-if="section.materialName && course && (course.isBuy || section.isFree)"
                      type="warning"
                      size="mini"
                      icon="el-icon-download"
                      plain
                      @click="downloadMaterial(section)"
                    >资料</el-button>
                  </div>
                </div>
              </div>
            </transition>
          </div>
        </div>

        <!-- 课程资料 -->
        <div v-show="activeTab === 'materials'" class="tab-panel">
          <div class="materials-card" v-if="materials.length > 0">
            <div
              v-for="m in materials"
              :key="m.id"
              class="material-row"
            >
              <div class="material-icon" :class="'type-' + m.fileType">
                <i :class="getFileIcon(m.fileType)"></i>
              </div>
              <div class="material-info">
                <span class="material-name">{{ m.materialName }}</span>
                <span class="material-meta">{{ formatSize(m.fileSize) }} · 已下载 {{ m.downloadCount }} 次</span>
              </div>
              <el-button
                v-if="course && course.isBuy"
                type="primary"
                size="small"
                icon="el-icon-download"
                plain
                @click="downloadResource(m)"
              >下载</el-button>
              <el-button v-else size="small" disabled>
                <i class="el-icon-lock"></i> 购买后可下载
              </el-button>
            </div>
          </div>
          <div class="empty-materials" v-else>
            <i class="el-icon-folder-opened" style="font-size: 40px; color: #c0c4cc;"></i>
            <p>暂无课程资料</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getCourseDetailById,
  getCourseChapters,
  getCourseResourceList,
  toggleFavorite,
  formatFileSize,
  formatDuration,
  formatTotalDuration
} from '@/api/course/courseListMock'

export default {
  name: 'CourseDetail',
  data() {
    return {
      courseId: null,
      course: null,
      chapters: [],
      materials: [],
      activeTab: 'chapters',
      loading: true
    }
  },
  created() {
    this.courseId = Number(this.$route.params.id || this.$route.query.id || 1)
    this.loadData()
  },
  methods: {
    async loadData() {
      this.loading = true
      try {
        const [detailRes, chaptersRes, materialsRes] = await Promise.all([
          getCourseDetailById(this.courseId),
          getCourseChapters(this.courseId),
          getCourseResourceList(this.courseId)
        ])
        this.course = detailRes.data
        this.chapters = (chaptersRes.data || []).map((ch, idx) => ({
          ...ch,
          _expanded: idx === 0
        }))
        this.materials = materialsRes.data || []
      } finally {
        this.loading = false
      }
    },
    toggleChapter(idx) {
      this.$set(this.chapters[idx], '_expanded', !this.chapters[idx]._expanded)
    },
    formatDur(seconds) {
      return formatDuration(seconds)
    },
    formatTotalDur(seconds) {
      return formatTotalDuration(seconds)
    },
    formatSize(bytes) {
      return formatFileSize(bytes)
    },
    getFileIcon(type) {
      const map = {
        pdf: 'el-icon-document',
        xlsx: 'el-icon-tickets',
        zip: 'el-icon-folder',
        png: 'el-icon-picture-outline',
        jpg: 'el-icon-picture-outline'
      }
      return map[type] || 'el-icon-document'
    },
    playSection(sectionId) {
      this.$router.push(`/demo/player?courseId=${this.courseId}&sectionId=${sectionId}`)
    },
    playFirstSection() {
      if (this.chapters.length > 0 && this.chapters[0].sections.length > 0) {
        this.playSection(this.chapters[0].sections[0].id)
      }
    },
    async handleFavorite() {
      const res = await toggleFavorite(this.courseId)
      this.course.isFavorite = res.data.isFavorite
      this.course.favoriteCount += this.course.isFavorite ? 1 : -1
      this.$message.success(this.course.isFavorite ? '已收藏' : '已取消收藏')
    },
    downloadMaterial(section) {
      this.$message.success(`模拟下载资料：${section.materialName}`)
    },
    downloadResource(material) {
      this.$message.success(`模拟下载：${material.materialName}`)
    },
    goBack() {
      this.$router.push('/demo/courses')
    }
  }
}
</script>

<style lang="scss" scoped>
.course-detail-page {
  min-height: 100vh;
  background: #f0f2f5;
}

/* ===== 头部 ===== */
.detail-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
}
.detail-header-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 12px 24px;
}
.back-btn {
  font-size: 14px;
  color: #606266;
}

/* ===== Hero 区 ===== */
.course-hero {
  background: #fff;
  padding-bottom: 32px;
}
.hero-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 24px 24px 0;
  display: flex;
  gap: 32px;
}
.hero-cover {
  width: 360px;
  height: 240px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  background: #e8ecf1;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
}
.hero-info {
  flex: 1;
  min-width: 0;
}
.hero-title {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
  margin: 0 0 12px;
  line-height: 1.3;
}
.hero-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.hero-desc {
  color: #606266;
  font-size: 14px;
  line-height: 1.6;
  margin: 0 0 16px;
}
.hero-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 13px;
  margin-bottom: 16px;
  .stat {
    display: inline-flex;
    align-items: center;
    gap: 4px;
    &.good { color: #67c23a; }
    &.medium { color: #e6a23c; }
    &.bad { color: #f56c6c; }
    &.fav {
      cursor: pointer;
      color: #909399;
      transition: color 0.2s;
      &:hover, &.active { color: #f7ba2a; }
    }
  }
  .divider {
    color: #dcdfe6;
  }
}
.rotate-icon {
  transform: rotate(180deg);
}
.hero-meta {
  display: flex;
  gap: 32px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}
.meta-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  .meta-label {
    font-size: 12px;
    color: #909399;
  }
  .meta-value {
    font-size: 14px;
    color: #303133;
    font-weight: 500;
  }
}
.hero-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}
.price-area {
  .current-price {
    font-size: 24px;
    font-weight: 700;
    color: #f56c6c;
  }
  .original-price {
    font-size: 14px;
    color: #c0c4cc;
    text-decoration: line-through;
    margin-left: 8px;
  }
}

/* ===== Tabs ===== */
.detail-tabs-wrapper {
  background: #fff;
  border-bottom: 1px solid #ebeef5;
  position: sticky;
  top: 0;
  z-index: 50;
}
.detail-tabs-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}
.detail-tabs {
  ::v-deep .el-tabs__header {
    margin: 0;
  }
  ::v-deep .el-tabs__nav-wrap::after {
    display: none;
  }
}

/* ===== Tab 内容 ===== */
.detail-content {
  padding: 24px 0 40px;
}
.detail-content-inner {
  max-width: 1100px;
  margin: 0 auto;
  padding: 0 24px;
}
.tab-panel {
  min-height: 300px;
}

/* 课程介绍 */
.intro-card {
  background: #fff;
  border-radius: 8px;
  padding: 24px 32px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 0 0 16px;
  }
  p {
    color: #606266;
    font-size: 14px;
    line-height: 1.8;
    margin: 0;
  }
}
.intro-stats {
  display: flex;
  gap: 40px;
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #ebeef5;
}
.intro-stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  .stat-num {
    font-size: 22px;
    font-weight: 700;
    color: #1890ff;
  }
  .stat-label {
    font-size: 13px;
    color: #909399;
  }
}

/* 章节列表 */
.chapter-card {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}
.chapter-card-header {
  padding: 16px 20px;
  display: flex;
  align-items: center;
  cursor: pointer;
  transition: background 0.2s;
  &:hover {
    background: #fafafa;
  }
}
.chapter-arrow {
  transition: transform 0.2s;
  color: #909399;
  margin-right: 8px;
  &.open {
    transform: rotate(90deg);
  }
}
.chapter-card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}
.chapter-card-count {
  font-size: 13px;
  color: #909399;
}
.chapter-sections {
  border-top: 1px solid #ebeef5;
}
.section-card-row {
  padding: 14px 20px 14px 44px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.15s;
  &:last-child {
    border-bottom: none;
  }
  &:hover {
    background: #f9fbff;
  }
}
.section-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}
.section-play-icon {
  color: #1890ff;
  font-size: 16px;
  flex-shrink: 0;
}
.section-card-title {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.section-card-duration {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
.section-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
  margin-left: 16px;
}

/* slide transition */
.slide-enter-active, .slide-leave-active {
  transition: all 0.25s ease;
  overflow: hidden;
}
.slide-enter, .slide-leave-to {
  max-height: 0;
  opacity: 0;
}

/* 课程资料 */
.materials-card {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}
.material-row {
  padding: 16px 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.15s;
  &:last-child {
    border-bottom: none;
  }
  &:hover {
    background: #fafafa;
  }
}
.material-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  i { font-size: 20px; color: #fff; }
  &.type-pdf { background: #f56c6c; }
  &.type-xlsx { background: #67c23a; }
  &.type-zip { background: #e6a23c; }
  &.type-png, &.type-jpg { background: #409eff; }
}
.material-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.material-name {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}
.material-meta {
  font-size: 12px;
  color: #909399;
}
.empty-materials {
  text-align: center;
  padding: 60px 20px;
  color: #909399;
  p {
    margin-top: 12px;
    font-size: 14px;
  }
}
</style>
