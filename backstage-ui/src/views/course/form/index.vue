<template>
  <div class="course-form-page">
    <!-- ===== 页面头部 ===== -->
    <div class="form-header">
      <div class="form-header-inner">
        <el-button type="text" icon="el-icon-arrow-left" @click="goBack" class="back-btn">返回课程列表</el-button>
        <h2 class="page-title">新增课程</h2>
        <div class="header-spacer"></div>
      </div>
    </div>

    <!-- ===== 权限不足提示 ===== -->
    <div class="no-permission" v-if="!canCreate">
      <i class="el-icon-lock" style="font-size: 48px; color: #c0c4cc;"></i>
      <h3>暂无权限</h3>
      <p>您当前身份为「普通用户」，没有新增课程的权限。</p>
      <p>请切换为「管理员」身份后再操作。</p>
      <el-button type="primary" @click="goBack">返回课程列表</el-button>
    </div>

    <!-- ===== 课程表单 ===== -->
    <div class="form-container" v-if="canCreate">
      <el-form ref="courseForm" :model="form" :rules="rules" label-width="120px" class="course-form">
        <!-- 基本信息 -->
        <div class="form-section">
          <div class="section-title"><i class="el-icon-document"></i> 基本信息</div>
          <el-form-item label="课程名称" prop="title">
            <el-input v-model="form.title" placeholder="请输入课程名称" maxlength="80" show-word-limit />
          </el-form-item>
          <el-form-item label="课程介绍" prop="description">
            <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入课程介绍" maxlength="500" show-word-limit />
          </el-form-item>
          <el-form-item label="课程标签" prop="tagIds">
            <el-select v-model="form.tagIds" multiple placeholder="请选择课程标签" style="width: 100%;">
              <el-option v-for="tag in tagList" :key="tag.id" :label="tag.name" :value="tag.id">
                <span class="tag-option-form">
                  <span class="tag-dot" :style="{ background: tag.color }"></span>
                  {{ tag.name }}
                </span>
              </el-option>
            </el-select>
          </el-form-item>
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="课程价格" prop="price">
                <el-input v-model="form.price" placeholder="0.00">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="原始价格">
                <el-input v-model="form.originalPrice" placeholder="0.00（选填）">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="服务周期" prop="serviceCycle">
            <el-select v-model="form.serviceCycle" placeholder="请选择服务周期" style="width: 100%;">
              <el-option label="90天" value="90天" />
              <el-option label="180天" value="180天" />
              <el-option label="365天" value="365天" />
              <el-option label="永久" value="永久" />
            </el-select>
          </el-form-item>
          <el-form-item label="服务内容" prop="serviceContent">
            <el-input v-model="form.serviceContent" type="textarea" :rows="2" placeholder="如：答疑服务 + 课程资料下载 + 考试辅导" maxlength="200" show-word-limit />
          </el-form-item>
        </div>

        <!-- 章节管理 -->
        <div class="form-section">
          <div class="section-title">
            <span><i class="el-icon-video-camera-solid"></i> 章节管理</span>
            <el-button type="primary" size="small" icon="el-icon-plus" @click="addChapter">添加章节</el-button>
          </div>

          <div class="chapters-container" v-if="form.chapters.length > 0">
            <div
              v-for="(chapter, cIdx) in form.chapters"
              :key="chapter._key"
              class="chapter-block"
            >
              <!-- 章节头部 -->
              <div class="chapter-header">
                <div class="chapter-header-left">
                  <span class="chapter-badge">第{{ toChineseNum(cIdx + 1) }}章</span>
                  <el-input
                    v-model="chapter.title"
                    placeholder="请输入章节标题"
                    size="medium"
                    class="chapter-title-input"
                  />
                </div>
                <el-button type="text" class="delete-btn" @click="removeChapter(cIdx)">
                  <i class="el-icon-delete"></i> 删除章节
                </el-button>
              </div>

              <!-- 课时列表 -->
              <div class="sections-list">
                <div
                  v-for="(section, sIdx) in chapter.sections"
                  :key="section._key"
                  class="section-row"
                >
                  <span class="section-index">{{ cIdx + 1 }}.{{ sIdx + 1 }}</span>
                  <el-input
                    v-model="section.title"
                    placeholder="课时标题"
                    size="small"
                    class="section-title-input"
                  />
                  <!-- 视频上传 -->
                  <div class="upload-cell">
                    <template v-if="section.videoFile">
                      <el-tag size="small" closable @close="section.videoFile = null" type="success">
                        <i class="el-icon-video-camera"></i> {{ section.videoFile.name }}
                      </el-tag>
                    </template>
                    <el-upload
                      v-else
                      :auto-upload="false"
                      :show-file-list="false"
                      accept="video/*"
                      :on-change="(file) => handleVideoUpload(file, cIdx, sIdx)"
                      action=""
                    >
                      <el-button size="mini" type="primary" plain icon="el-icon-upload2">上传视频</el-button>
                    </el-upload>
                  </div>
                  <!-- 资料上传 -->
                  <div class="upload-cell">
                    <template v-if="section.materialFile">
                      <el-tag size="small" closable @close="section.materialFile = null" type="warning">
                        <i class="el-icon-folder"></i> {{ section.materialFile.name }}
                      </el-tag>
                    </template>
                    <el-upload
                      v-else
                      :auto-upload="false"
                      :show-file-list="false"
                      accept=".zip,.rar,.7z,.tar,.gz"
                      :before-upload="beforeMaterialUpload"
                      :on-change="(file) => handleMaterialUpload(file, cIdx, sIdx)"
                      action=""
                    >
                      <el-button size="mini" type="warning" plain icon="el-icon-upload2">上传资料</el-button>
                    </el-upload>
                  </div>
                  <!-- 免费标记 -->
                  <el-checkbox v-model="section.isFree" class="free-check">免费</el-checkbox>
                  <!-- 删除课时 -->
                  <el-button type="text" size="small" class="delete-section-btn" @click="removeSection(cIdx, sIdx)">
                    <i class="el-icon-close"></i>
                  </el-button>
                </div>
                <!-- 添加课时按钮 -->
                <div class="add-section-row">
                  <el-button type="text" icon="el-icon-plus" @click="addSection(cIdx)">添加课时</el-button>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="chapters-empty" v-else>
            <i class="el-icon-folder-opened" style="font-size: 40px; color: #c0c4cc;"></i>
            <p>暂无章节，请点击「添加章节」开始构建课程内容</p>
          </div>
        </div>

        <!-- 提交按钮 -->
        <div class="form-actions">
          <el-button @click="goBack">取 消</el-button>
          <el-button type="primary" icon="el-icon-check" :loading="submitting" @click="handleSubmit">
            保存课程
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
import { getTagList, createCourse, hasPermission } from '@/api/course/courseListMock'

let _uid = 0
function uid() { return ++_uid }

export default {
  name: 'CourseForm',
  data() {
    return {
      tagList: [],
      submitting: false,
      form: {
        title: '',
        description: '',
        tagIds: [],
        price: '',
        originalPrice: '',
        serviceCycle: '365天',
        serviceContent: '',
        chapters: []
      },
      rules: {
        title: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
        description: [{ required: true, message: '请输入课程介绍', trigger: 'blur' }],
        tagIds: [{ required: true, type: 'array', message: '请选择至少一个标签', trigger: 'change' }],
        price: [{ required: true, message: '请输入课程价格', trigger: 'blur' }],
        serviceCycle: [{ required: true, message: '请选择服务周期', trigger: 'change' }],
        serviceContent: [{ required: true, message: '请输入服务内容', trigger: 'blur' }]
      }
    }
  },
  computed: {
    canCreate() {
      return hasPermission('course:create')
    }
  },
  created() {
    this.loadTags()
  },
  methods: {
    async loadTags() {
      const res = await getTagList()
      this.tagList = res.data || []
    },
    toChineseNum(num) {
      const map = ['一', '二', '三', '四', '五', '六', '七', '八', '九', '十']
      return map[num - 1] || String(num)
    },
    addChapter() {
      this.form.chapters.push({
        _key: uid(),
        title: '',
        sections: [
          { _key: uid(), title: '', videoFile: null, materialFile: null, isFree: false }
        ]
      })
    },
    removeChapter(cIdx) {
      this.$confirm('确认删除此章节及其所有课时？', '提示', { type: 'warning' }).then(() => {
        this.form.chapters.splice(cIdx, 1)
      }).catch(() => {})
    },
    addSection(cIdx) {
      this.form.chapters[cIdx].sections.push({
        _key: uid(),
        title: '',
        videoFile: null,
        materialFile: null,
        isFree: false
      })
    },
    removeSection(cIdx, sIdx) {
      if (this.form.chapters[cIdx].sections.length <= 1) {
        this.$message.warning('每个章节至少保留一个课时')
        return
      }
      this.form.chapters[cIdx].sections.splice(sIdx, 1)
    },
    handleVideoUpload(file, cIdx, sIdx) {
      const raw = file.raw || file
      if (!raw.type.startsWith('video/')) {
        this.$message.error('请上传视频文件')
        return
      }
      this.form.chapters[cIdx].sections[sIdx].videoFile = raw
    },
    beforeMaterialUpload(file) {
      const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
      const allowedExts = ['.zip', '.rar', '.7z', '.tar', '.gz']
      if (!allowedExts.includes(ext)) {
        this.$message.error('课程资料必须为压缩包格式（.zip、.rar、.7z、.tar、.gz）')
        return false
      }
      return true
    },
    handleMaterialUpload(file, cIdx, sIdx) {
      const raw = file.raw || file
      const ext = raw.name.substring(raw.name.lastIndexOf('.')).toLowerCase()
      const allowedExts = ['.zip', '.rar', '.7z', '.tar', '.gz']
      if (!allowedExts.includes(ext)) {
        this.$message.error('课程资料必须为压缩包格式（.zip、.rar、.7z、.tar、.gz）')
        return
      }
      this.form.chapters[cIdx].sections[sIdx].materialFile = raw
    },
    async handleSubmit() {
      try {
        await this.$refs.courseForm.validate()
      } catch {
        this.$message.warning('请完善必填信息')
        return
      }
      if (this.form.chapters.length === 0) {
        this.$message.warning('请至少添加一个章节')
        return
      }
      for (const ch of this.form.chapters) {
        if (!ch.title.trim()) {
          this.$message.warning('请填写所有章节标题')
          return
        }
        for (const sec of ch.sections) {
          if (!sec.title.trim()) {
            this.$message.warning('请填写所有课时标题')
            return
          }
        }
      }
      this.submitting = true
      try {
        await createCourse(this.form)
        this.$message.success('课程创建成功！')
        this.$router.push('/demo/courses')
      } catch (e) {
        this.$message.error(e.message || '创建失败')
      } finally {
        this.submitting = false
      }
    },
    goBack() {
      this.$router.push('/demo/courses')
    }
  }
}
</script>

<style lang="scss" scoped>
.course-form-page {
  min-height: 100vh;
  background: #f0f2f5;
  padding-bottom: 40px;
}

/* ===== 页面头部 ===== */
.form-header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}
.form-header-inner {
  max-width: 960px;
  margin: 0 auto;
  padding: 16px 24px;
  display: flex;
  align-items: center;
}
.back-btn {
  font-size: 14px;
  color: #606266;
}
.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 0 12px;
}
.header-spacer {
  flex: 1;
}

/* ===== 无权限 ===== */
.no-permission {
  max-width: 500px;
  margin: 80px auto 0;
  text-align: center;
  background: #fff;
  border-radius: 8px;
  padding: 48px 40px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
  h3 {
    margin: 16px 0 8px;
    color: #303133;
  }
  p {
    color: #909399;
    font-size: 14px;
    margin: 4px 0;
    line-height: 1.6;
  }
  .el-button {
    margin-top: 24px;
  }
}

/* ===== 表单容器 ===== */
.form-container {
  max-width: 960px;
  margin: 24px auto 0;
  padding: 0 24px;
}

/* ===== 表单分区 ===== */
.form-section {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.06);
}
.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 24px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
  i {
    margin-right: 6px;
    color: #1890ff;
  }
}

/* 标签选项样式 */
.tag-option-form {
  display: flex;
  align-items: center;
  gap: 8px;
  .tag-dot {
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }
}

/* ===== 章节管理 ===== */
.chapters-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.chapter-block {
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
}
.chapter-header {
  background: #f5f7fa;
  padding: 12px 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.chapter-header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}
.chapter-badge {
  background: #1890ff;
  color: #fff;
  padding: 2px 10px;
  border-radius: 3px;
  font-size: 13px;
  font-weight: 500;
  white-space: nowrap;
}
.chapter-title-input {
  max-width: 400px;
}
.delete-btn {
  color: #f56c6c;
  &:hover {
    color: #f78989;
  }
}

/* 课时行 */
.sections-list {
  padding: 12px 16px;
}
.section-row {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  flex-wrap: wrap;
  &:last-of-type {
    border-bottom: none;
  }
}
.section-index {
  font-size: 13px;
  color: #909399;
  font-weight: 500;
  width: 32px;
  text-align: center;
  flex-shrink: 0;
}
.section-title-input {
  flex: 1;
  min-width: 160px;
}
.upload-cell {
  flex-shrink: 0;
  .el-tag {
    max-width: 160px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
.free-check {
  flex-shrink: 0;
}
.delete-section-btn {
  color: #c0c4cc;
  padding: 4px;
  &:hover {
    color: #f56c6c;
  }
}
.add-section-row {
  padding-top: 8px;
  .el-button {
    font-size: 13px;
  }
}

/* 空状态 */
.chapters-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  color: #909399;
  p {
    margin-top: 12px;
    font-size: 14px;
  }
}

/* ===== 提交 ===== */
.form-actions {
  text-align: center;
  padding: 24px 0 0;
  .el-button {
    min-width: 120px;
    & + .el-button {
      margin-left: 16px;
    }
  }
}
</style>
