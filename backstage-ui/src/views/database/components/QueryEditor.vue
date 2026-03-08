<template>
  <div class="query-editor">
    <div class="editor-toolbar">
      <el-button-group>
        <el-button
          size="small"
          @click="executeQuery"
          icon="el-icon-caret-right"
          :loading="executing"
        >执行(F5)</el-button>
      </el-button-group>
    </div>

    <div class="editor-container">
      <el-input
        type="textarea"
        :rows="10"
        v-model="sql"
        placeholder="输入SQL查询语句"
      ></el-input>
    </div>

    <div class="result-container">
      <el-table
        v-if="resultData.length > 0"
        :data="resultData"
        border
        style="width: 100%"
        height="300px"
      >
        <el-table-column
          v-for="column in resultColumns"
          :key="column"
          :prop="column"
          :label="column"
        ></el-table-column>
      </el-table>

      <el-pagination
        v-if="total > 0"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="pageSize"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination"
      ></el-pagination>
    </div>
  </div>
</template>

<script>
import { executeQuery } from '@/api/database'

export default {
  name: 'QueryEditor',
  data() {
    return {
      sql: '',
      executing: false,
      resultData: [],
      resultColumns: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    }
  },
  methods: {
    async executeQuery() {
      if (!this.sql.trim()) {
        this.$message.warning('请输入SQL语句')
        return
      }

      this.executing = true
      this.currentPage = 1 // 重置到第一页

      try {
        await this.fetchData()
      } catch (error) {
        this.$message.error('执行失败: ' + error.message)
      } finally {
        this.executing = false
      }
    },

    async fetchData() {
      const res = await executeQuery({
        sql: this.sql,
        page: this.currentPage,
        pageSize: this.pageSize
      })

      if (res.code === 200) {
        this.resultData = res.rows || []
        this.total = res.total || 0

        // 自动提取列名（如果后端没有返回）
        if (this.resultData.length > 0 && this.resultColumns.length === 0) {
          this.resultColumns = Object.keys(this.resultData[0])
        }
      }
    },

    handleSizeChange(val) {
      this.pageSize = val
      this.fetchData()
    },

    handleCurrentChange(val) {
      this.currentPage = val
      this.fetchData()
    }
  }
}
</script>

<style scoped>
.query-editor {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.editor-toolbar {
  margin-bottom: 10px;
}

.editor-container {
  margin-bottom: 10px;
}

.result-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.pagination {
  margin-top: 10px;
  flex-shrink: 0;
}
</style>
