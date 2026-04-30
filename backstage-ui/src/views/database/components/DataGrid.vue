<template>
  <div class="data-grid">
    <div class="toolbar">
      <el-button-group>
        <el-button size="small" @click="refresh" icon="el-icon-refresh">刷新</el-button>
        <el-button size="small" @click="addRow" icon="el-icon-plus">新增</el-button>
        <el-button
          size="small"
          @click="saveChanges"
          icon="el-icon-check"
          :disabled="!hasChanges"
        >保存</el-button>
        <el-button
          size="small"
          @click="batchDelete"
          icon="el-icon-delete"
          :disabled="!selectedRows.length"
        >批量删除</el-button>
      </el-button-group>
    </div>

    <el-table
      :data="tableData"
      border
      style="width: 100%"
      height="calc(100% - 90px)"
      highlight-current-row
      v-loading="loading"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column
        v-for="column in columns"
        :key="column.name"
        :prop="column.name"
        :label="column.label"
      >
        <template slot-scope="scope">
          <el-input
            v-model="scope.row[column.name]"
            @change="handleCellChange(scope.$index, column.name, scope.row[column.name])"
          ></el-input>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="danger"
            @click="deleteRow(scope.$index, scope.row)"
            icon="el-icon-delete"
          ></el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="pagination.current"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="pagination.size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="pagination.total"
      class="pagination"
    ></el-pagination>
  </div>
</template>

<script>
import {getTableData, saveTableData, batchDeleteRows, deleteTableRow} from '@/api/database'

export default {
  name: 'DataGrid',
  props: {
    tableName: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      columns: [],
      tableData: [],
      originalData: [],
      selectedRows: [],
      hasChanges: false,
      loading: false,
      pagination: {
        current: 1,
        size: 10,
        total: 0
      }
    }
  },
  created() {
    this.loadTableData()
  },
  methods: {
    async loadTableData() {
      this.loading = true
      try {
        const res = await getTableData(this.tableName, {
          pageNum: this.pagination.current,
          pageSize: this.pagination.size
        })

        this.tableData = res.rows || res.data || []
        this.originalData = JSON.parse(JSON.stringify(this.tableData))
        this.pagination.total = res.total || (Array.isArray(res) ? res.length : 0)

        if (this.tableData.length > 0) {
          this.columns = Object.keys(this.tableData[0])
            .filter(key => key !== 'selection') // 排除selection列
            .map(key => ({
              name: key,
              label: key
            }))
        }
      } catch (error) {
        this.$message.error('加载数据失败: ' + (error.message || '未知错误'))
        console.error('加载数据错误详情:', error)
      } finally {
        this.loading = false
      }
    },

    refresh() {
      this.pagination.current = 1
      this.loadTableData()
    },

    addRow() {
      const newRow = {}
      this.columns.forEach(column => {
        newRow[column.name] = ''
      })
      this.tableData.unshift(newRow)
      this.hasChanges = true
    },

    async saveChanges() {
      try {
        const changes = {
          inserted: this.tableData.filter(row =>
            !this.originalData.some(original => original.id === row.id)
          ),
          updated: this.tableData.filter(row =>
            this.originalData.some(original =>
              original.id === row.id &&
              JSON.stringify(original) !== JSON.stringify(row)
            )
          ),
          deleted: this.originalData.filter(original =>
            !this.tableData.some(row => row.id === original.id)
          )
        }

        await saveTableData(this.tableName, changes)
        this.$message.success('保存成功')
        this.loadTableData()
      } catch (error) {
        this.$message.error('保存失败: ' + error.message)
      }
    },

    async deleteRow(index, row) {
      this.$confirm('确定要删除该行数据吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 调用删除接口
          await deleteTableRow(this.tableName, row.id)

          // 前端移除数据
          this.tableData.splice(index, 1)
          this.hasChanges = true
          this.$message.success('删除成功')
          this.originalData = JSON.parse(JSON.stringify(this.tableData))
          // 如果当前页数据为空且不是第一页，返回上一页
          if (this.tableData.length === 0 && this.pagination.current > 1) {
            this.pagination.current--
            this.loadTableData()
          }
        } catch (error) {
          this.$message.error('删除失败: ' + error.message)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message.warning('请至少选择一行数据')
        return
      }

      this.$confirm(`确定要删除选中的 ${this.selectedRows.length} 行数据吗?`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const selectedIds = this.selectedRows.map(row => row.id)

          // 调用批量删除接口
          await batchDeleteRows(this.tableName, selectedIds)

          // 前端移除数据
          this.tableData = this.tableData.filter(row => !selectedIds.includes(row.id))
          this.selectedRows = []
          this.hasChanges = true
          this.$message.success(`成功删除 ${selectedIds.length} 条数据`)
          this.originalData = JSON.parse(JSON.stringify(this.tableData))
          // 如果当前页数据为空且不是第一页，返回上一页
          if (this.tableData.length === 0 && this.pagination.current > 1) {
            this.pagination.current--
            this.loadTableData()
          }
        } catch (error) {
          this.$message.error('删除失败: ' + error.message)
        }
      }).catch(() => {
        this.$message.info('已取消删除')
      })
    },

    handleSelectionChange(rows) {
      this.selectedRows = rows
    },

    handleCellChange(rowIndex, columnName, newValue) {
      this.hasChanges = true
    },

    handleSizeChange(size) {
      this.pagination.size = size
      this.pagination.current = 1
      this.loadTableData()
    },

    handleCurrentChange(current) {
      this.pagination.current = current
      this.loadTableData()
    }
  }
}
</script>

<style scoped>
.data-grid {
  height: 100%;
  min-height: 500px;
  display: flex;
  flex-direction: column;
  padding: 10px;
  box-sizing: border-box;
}

.toolbar {
  margin-bottom: 10px;
}

.el-table {
  flex: 1;
  overflow: auto;
  margin-bottom: 10px;
}

.pagination {
  flex-shrink: 0;
}
</style>
