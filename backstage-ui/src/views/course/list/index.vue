<template>
  <div class="course-list-page">
    <!-- ===== 顶部搜索栏 ===== -->
    <div class="search-header">
      <div class="search-header-inner">
        <div class="search-area">
          <!-- 标签多选下拉 -->
          <el-select
            v-model="selectedTags"
            multiple
            collapse-tags
            clearable
            placeholder="按标签筛选"
            class="tag-select"
            @change="handleSearch"
          >
            <el-option
              v-for="tag in tagList"
              :key="tag.id"
              :label="`${tag.name}（${tag.count}）`"
              :value="tag.id"
            >
              <span class="tag-option">
                <span class="tag-dot" :style="{ background: tag.color }"></span>
                <span>{{ tag.name }}</span>
                <span class="tag-count">{{ tag.count }}门课程</span>
              </span>
            </el-option>
          </el-select>
          <!-- 关键字搜索 -->
          <el-input
            v-model="keyword"
            placeholder="搜索课程名称、描述或讲师"
            prefix-icon="el-icon-search"
            clearable
            class="keyword-input"
            @keyup.enter.native="handleSearch"
            @clear="handleSearch"
          />
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
        </div>
        <div class="header-actions">
          <!-- RBAC 角色切换（演示用） -->
          <div class="role-switch">
            <span class="role-label">当前身份：</span>
            <el-select v-model="currentRole" size="small" @change="handleRoleChange" class="role-select">
              <el-option
                v-for="r in roleOptions"
                :key="r.value"
                :label="r.label"
                :value="r.value"
              />
            </el-select>
          </div>
          <!-- 新增课程按钮（仅管理员可见） -->
          <el-button
            v-if="canCreate"
            type="primary"
            icon="el-icon-plus"
            @click="handleCreate"
            class="create-btn"
          >新增课程</el-button>
        </div>
      </div>
    </div>

    <!-- ===== 活动标签筛选展示 ===== -->
    <div class="active-filters" v-if="selectedTags.length > 0">
      <span class="filter-label">已选标签：</span>
      <el-tag
        v-for="tagId in selectedTags"
        :key="tagId"
        closable
        size="medium"
        :color="getTagById(tagId).color"
        effect="dark"
        class="filter-tag"
        @close="removeTag(tagId)"
      >
        {{ getTagById(tagId).name }}
      </el-tag>
      <el-button type="text" size="small" @click="clearAllTags" class="clear-all">清空全部</el-button>
    </div>

    <!-- ===== 课程计数 ===== -->
    <div class="result-summary">
      <span>共找到 <b>{{ total }}</b> 门课程</span>
    </div>

    <!-- ===== 课程卡片网格 ===== -->
    <div class="course-grid" v-loading="loading">
      <div
        v-for="course in courseList"
        :key="course.id"
        class="course-card"
        @click="goDetail(course.id)"
      >
        <!-- 封面 -->
        <div class="card-cover">
          <img :src="course.cover" :alt="course.title" />
          <div class="cover-overlay">
            <span class="course-price" v-if="course.price">
              <template v-if="course.isBuy">
                <el-tag size="mini" type="success" effect="dark">已购买</el-tag>
              </template>
              <template v-else>
                ¥{{ course.price }}
              </template>
            </span>
            <span class="course-sections-count">{{ course.sectionCount }}课时</span>
          </div>
        </div>
        <!-- 卡片内容 -->
        <div class="card-body">
          <h3 class="card-title" :title="course.title">{{ course.title }}</h3>
          <!-- 标签 -->
          <div class="card-tags">
            <el-tag
              v-for="tag in course.tags"
              :key="tag.id"
              size="mini"
              :style="{ color: tag.color, borderColor: tag.color + '40', background: tag.color + '10' }"
              effect="plain"
              class="card-tag"
            >{{ tag.name }}</el-tag>
          </div>
          <!-- 讲师 & 学员 -->
          <div class="card-meta">
            <span><i class="el-icon-oshUser"></i> {{ course.instructor }}</span>
            <span><i class="el-icon-oshUser-solid"></i> {{ course.studentCount }}人学习</span>
          </div>
          <!-- 评价 & 收藏 -->
          <div class="card-stats">
            <span class="stat good" title="好评">
              <i class="el-icon-thumb"></i>{{ course.goodCount }}
            </span>
            <span class="stat medium" title="中评">
              <i class="el-icon-minus"></i>{{ course.mediumCount }}
            </span>
            <span class="stat bad" title="差评">
              <i class="el-icon-thumb rotate-icon"></i>{{ course.badCount }}
            </span>
            <span class="stat favorite" :class="{ active: course.isFavorite }" title="收藏" @click.stop="handleFavorite(course)">
              <i :class="course.isFavorite ? 'el-icon-star-on' : 'el-icon-star-off'"></i>{{ course.favoriteCount }}
            </span>
          </div>
        </div>
        <!-- 管理操作（仅管理员） -->
        <div class="card-admin-actions" v-if="canDelete" @click.stop>
          <el-dropdown trigger="click" @command="handleCardAction($event, course)">
            <span class="admin-dot"><i class="el-icon-more"></i></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="delete" icon="el-icon-delete">
                <span style="color: #f56c6c;">删除课程</span>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </div>
      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && courseList.length === 0">
        <i class="el-icon-box" style="font-size: 48px; color: #c0c4cc;"></i>
        <p>没有找到匹配的课程</p>
        <el-button type="text" @click="resetSearch">重置搜索条件</el-button>
      </div>
    </div>

    <!-- ===== 分页 ===== -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        background
        layout="prev, pager, next, jumper"
        :current-page.sync="page"
        :page-size="pageSize"
        :total="total"
        @current-change="loadCourses"
      />
    </div>
  </div>
</template>

<script>
import {
  getTagList,
  getCourseList,
  toggleFavorite,
  deleteCourse,
  getCurrentRole,
  switchRole,
  getRoleOptions,
  hasPermission
} from '@/api/course/courseListMock'

export default {
  name: 'CourseList',
  data() {
    return {
      // 搜索
      keyword: '',
      selectedTags: [],
      tagList: [],
      // 数据
      courseList: [],
      total: 0,
      page: 1,
      pageSize: 8,
      loading: false,
      // RBAC
      currentRole: getCurrentRole(),
      roleOptions: getRoleOptions()
    }
  },
  computed: {
    canCreate() {
      return hasPermission('course:create')
    },
    canDelete() {
      return hasPermission('course:delete')
    }
  },
  created() {
    this.loadTags()
    this.loadCourses()
  },
  methods: {
    async loadTags() {
      const res = await getTagList()
      this.tagList = res.data || []
    },
    async loadCourses() {
      this.loading = true
      try {
        const res = await getCourseList({
          keyword: this.keyword,
          tagIds: this.selectedTags,
          page: this.page,
          pageSize: this.pageSize
        })
        this.courseList = res.data.list || []
        this.total = res.data.total || 0
      } finally {
        this.loading = false
      }
    },
    handleSearch() {
      this.page = 1
      this.loadCourses()
    },
    resetSearch() {
      this.keyword = ''
      this.selectedTags = []
      this.page = 1
      this.loadCourses()
    },
    getTagById(id) {
      return this.tagList.find(t => t.id === id) || { name: '', color: '#999' }
    },
    removeTag(tagId) {
      this.selectedTags = this.selectedTags.filter(id => id !== tagId)
      this.handleSearch()
    },
    clearAllTags() {
      this.selectedTags = []
      this.handleSearch()
    },
    handleRoleChange(role) {
      switchRole(role)
      this.$message.success(`已切换为：${role === 'admin' ? '管理员' : '普通用户'}`)
    },
    handleCreate() {
      this.$router.push('/demo/course/create')
    },
    goDetail(courseId) {
      this.$router.push(`/demo/course/detail/${courseId}`)
    },
    async handleFavorite(course) {
      const res = await toggleFavorite(course.id)
      course.isFavorite = res.data.isFavorite
      course.favoriteCount += course.isFavorite ? 1 : -1
      this.$message.success(course.isFavorite ? '已收藏' : '已取消收藏')
    },
    handleCardAction(command, course) {
      if (command === 'delete') {
        this.$confirm(`确认删除课程「${course.title}」？此操作不可恢复。`, '删除确认', {
          type: 'warning',
          confirmButtonText: '确认删除',
          cancelButtonText: '取消',
          confirmButtonClass: 'el-button--danger'
        }).then(async () => {
          await deleteCourse(course.id)
          this.$message.success('课程已删除')
          this.loadCourses()
        }).catch(() => {})
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.course-list-page {
  min-height: 100vh;
  background: #f0f2f5;
  padding: 0 0 40px;
}

/* ===== 搜索头部 ===== */
.search-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  padding: 20px 0;
  position: sticky;
  top: 0;
  z-index: 100;
}
.search-header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  flex-wrap: wrap;
}
.search-area {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  min-width: 0;
}
.tag-select {
  width: 240px;
  flex-shrink: 0;
}
.keyword-input {
  width: 300px;
  flex-shrink: 0;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-shrink: 0;
}
.role-switch {
  display: flex;
  align-items: center;
  gap: 6px;
  .role-label {
    font-size: 13px;
    color: #909399;
    white-space: nowrap;
  }
  .role-select {
    width: 200px;
  }
}
.create-btn {
  font-weight: 500;
}

/* 标签下拉选项 */
.tag-option {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
  .tag-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
    flex-shrink: 0;
  }
  .tag-count {
    margin-left: auto;
    color: #909399;
    font-size: 12px;
  }
}

/* ===== 活动筛选 ===== */
.active-filters {
  max-width: 1200px;
  margin: 16px auto 0;
  padding: 0 24px;
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  .filter-label {
    font-size: 13px;
    color: #606266;
  }
  .filter-tag {
    border: none;
  }
  .clear-all {
    font-size: 13px;
  }
}

/* ===== 结果统计 ===== */
.result-summary {
  max-width: 1200px;
  margin: 16px auto 0;
  padding: 0 24px;
  font-size: 14px;
  color: #909399;
  b {
    color: #303133;
  }
}

/* ===== 课程卡片网格 ===== */
.course-grid {
  max-width: 1200px;
  margin: 20px auto 0;
  padding: 0 24px;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  min-height: 200px;
}
@media (max-width: 1100px) {
  .course-grid { grid-template-columns: repeat(3, 1fr); }
}
@media (max-width: 820px) {
  .course-grid { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 540px) {
  .course-grid { grid-template-columns: 1fr; }
}

.course-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  transition: transform 0.25s cubic-bezier(0.4, 0, 0.2, 1), box-shadow 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
  }
}

/* 封面 */
.card-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  background: #e8ecf1;
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }
  .course-card:hover & img {
    transform: scale(1.05);
  }
}
.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 8px 12px;
  background: linear-gradient(transparent, rgba(0, 0, 0, 0.6));
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.course-price {
  color: #fff;
  font-weight: 600;
  font-size: 16px;
}
.course-sections-count {
  color: rgba(255, 255, 255, 0.85);
  font-size: 12px;
}

/* 卡片内容 */
.card-body {
  padding: 14px 16px 16px;
}
.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 10px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}
.card-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 10px;
  min-height: 24px;
}
.card-tag {
  border-radius: 3px;
  font-size: 11px;
}
.card-meta {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #909399;
  margin-bottom: 12px;
  i {
    margin-right: 3px;
  }
}

/* 评价 & 收藏统计 */
.card-stats {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
}
.stat {
  display: inline-flex;
  align-items: center;
  gap: 3px;
  font-size: 12px;
  color: #909399;
  i {
    font-size: 14px;
  }
  &.good {
    color: #67c23a;
  }
  &.medium {
    color: #e6a23c;
  }
  &.bad {
    color: #f56c6c;
  }
  &.favorite {
    margin-left: auto;
    cursor: pointer;
    transition: color 0.2s;
    &:hover, &.active {
      color: #f7ba2a;
    }
  }
}
.rotate-icon {
  transform: rotate(180deg);
}

/* 管理操作 */
.card-admin-actions {
  position: absolute;
  top: 8px;
  right: 8px;
  z-index: 2;
}
.admin-dot {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.9);
  cursor: pointer;
  transition: background 0.2s;
  i {
    color: #606266;
    font-size: 16px;
  }
  &:hover {
    background: #fff;
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.12);
  }
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #909399;
  p {
    margin: 16px 0 8px;
    font-size: 14px;
  }
}

/* 分页 */
.pagination-wrapper {
  max-width: 1200px;
  margin: 32px auto 0;
  padding: 0 24px;
  display: flex;
  justify-content: center;
}
</style>
