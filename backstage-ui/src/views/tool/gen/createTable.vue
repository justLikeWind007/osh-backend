<template>
  <el-dialog
    title="可视化创建表"
    :visible.sync="visible"
    width="900px"
    top="5vh"
    append-to-body
    @closed="resetForm"
  >
    <el-form ref="form" :model="form" :rules="rules" label-width="120px">
      <el-row>
        <el-col :span="12">
          <el-form-item label="表名" prop="tableName">
            <el-input v-model="form.tableName" placeholder="请输入表名" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="表注释" prop="tableComment">
            <el-input v-model="form.tableComment" placeholder="请输入表注释" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider content-position="left">字段定义</el-divider>

      <el-table :data="form.columns" border style="width: 100%">
        <el-table-column prop="columnName" label="列名" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.columnName" placeholder="列名" />
          </template>
        </el-table-column>
        <el-table-column prop="columnType" label="类型" width="120">
          <template slot-scope="scope">
            <el-select v-model="scope.row.columnType" placeholder="选择类型" @change="handleTypeChange(scope.row)">
              <el-option-group
                v-for="group in dataTypes"
                :key="group.label"
                :label="group.label"
              >
                <el-option
                  v-for="item in group.options"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-option-group>
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="length" label="长度/值" width="100">
          <template slot-scope="scope">
            <el-input
              v-model="scope.row.length"
              placeholder="长度"
              :disabled="!typeHasLength(scope.row.columnType)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="isNullable" label="允许NULL" width="100">
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.isNullable"
              :disabled="scope.row.isPrimaryKey"
            />
          </template>
        </el-table-column>
        <el-table-column prop="defaultValue" label="默认值" width="120">
          <template slot-scope="scope">
            <el-input v-model="scope.row.defaultValue" placeholder="默认值" />
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="注释" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.comment" placeholder="列注释" />
          </template>
        </el-table-column>
        <el-table-column prop="isPrimaryKey" label="主键" width="80">
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.isPrimaryKey"
              @change="handlePrimaryKeyChange(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column
          v-if="hasAutoIncrementColumn"
          prop="isAutoIncrement"
          label="自增"
          width="80"
        >
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.isAutoIncrement"
              :disabled="!canAutoIncrement(scope.row)"
            />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="handleRemoveColumn(scope.$index)"
            />
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 10px; text-align: center;">
        <el-button type="primary" icon="el-icon-plus" @click="handleAddColumn">添加字段</el-button>
      </div>

      <el-divider content-position="left">索引定义</el-divider>

      <el-table :data="form.indexes" border style="width: 100%">
        <el-table-column prop="indexName" label="索引名" width="150">
          <template slot-scope="scope">
            <el-input v-model="scope.row.indexName" placeholder="索引名" />
          </template>
        </el-table-column>
        <el-table-column prop="columns" label="包含列" width="200">
          <template slot-scope="scope">
            <el-select
              v-model="scope.row.columns"
              multiple
              placeholder="选择列"
              style="width: 100%"
            >
              <el-option
                v-for="col in form.columns"
                :key="col.columnName"
                :label="col.columnName"
                :value="col.columnName"
              />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="indexType" label="类型" width="120">
          <template slot-scope="scope">
            <el-select v-model="scope.row.indexType" placeholder="选择类型">
              <el-option label="普通索引" value="INDEX" />
              <el-option label="唯一索引" value="UNIQUE" />
              <el-option label="全文索引" value="FULLTEXT" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="danger"
              icon="el-icon-delete"
              @click="handleRemoveIndex(scope.$index)"
            />
          </template>
        </el-table-column>
      </el-table>

      <div style="margin-top: 10px; text-align: center;">
        <el-button type="primary" icon="el-icon-plus" @click="handleAddIndex">添加索引</el-button>
      </div>

      <el-divider content-position="left">SQL预览</el-divider>
      <el-input type="textarea" :rows="5" v-model="sqlPreview" readonly />
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleCreateTable">确 定</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { createTable } from "@/api/tool/gen"

export default {
  data() {
    return {
      visible: false,
      form: {
        tableName: '',
        tableComment: '',
        columns: [
          {
            columnName: 'id',
            columnType: 'bigint',
            length: '',
            isNullable: false,
            defaultValue: '',
            comment: '主键ID',
            isPrimaryKey: true,
            isAutoIncrement: true
          }
        ],
        indexes: []
      },
      rules: {
        tableName: [
          { required: true, message: '请输入表名', trigger: 'blur' },
          { pattern: /^[a-zA-Z][a-zA-Z0-9_]*$/, message: '表名只能包含字母、数字和下划线，且以字母开头' }
        ]
      },
      dataTypes: [
        {
          label: '整数类型',
          options: [
            { label: 'TINYINT', value: 'tinyint' },
            { label: 'SMALLINT', value: 'smallint' },
            { label: 'MEDIUMINT', value: 'mediumint' },
            { label: 'INT', value: 'int' },
            { label: 'BIGINT', value: 'bigint' }
          ]
        },
        {
          label: '小数类型',
          options: [
            { label: 'FLOAT', value: 'float' },
            { label: 'DOUBLE', value: 'double' },
            { label: 'DECIMAL', value: 'decimal' }
          ]
        },
        {
          label: '字符串类型',
          options: [
            { label: 'CHAR', value: 'char' },
            { label: 'VARCHAR', value: 'varchar' },
            { label: 'TEXT', value: 'text' },
            { label: 'LONGTEXT', value: 'longtext' }
          ]
        },
        {
          label: '日期时间',
          options: [
            { label: 'DATE', value: 'date' },
            { label: 'TIME', value: 'time' },
            { label: 'DATETIME', value: 'datetime' },
            { label: 'TIMESTAMP', value: 'timestamp' }
          ]
        }
      ]
    }
  },
  computed: {
    sqlPreview() {
      if (!this.form.tableName) return '请输入表名'

      let sql = `CREATE TABLE ${this.form.tableName} (\n`

      // 处理列定义
      const columns = this.form.columns.map(col => {
        let columnDef = `  ${col.columnName} ${col.columnType.toUpperCase()}`

        if (col.length && this.typeHasLength(col.columnType)) {
          columnDef += `(${col.length})`
        }

        if (!col.isNullable) {
          columnDef += ' NOT NULL'
        }

        if (col.defaultValue !== '') {
          columnDef += ` DEFAULT ${this.formatDefaultValue(col.defaultValue, col.columnType)}`
        }

        if (col.isPrimaryKey && col.isAutoIncrement && this.isNumericType(col.columnType)) {
          columnDef += ' AUTO_INCREMENT'
        }

        if (col.comment) {
          columnDef += ` COMMENT '${col.comment}'`
        }

        return columnDef
      })

      sql += columns.join(',\n')

      // 处理主键
      const primaryKeys = this.form.columns.filter(col => col.isPrimaryKey).map(col => col.columnName)
      if (primaryKeys.length > 0) {
        sql += `,\n  PRIMARY KEY (${primaryKeys.join(', ')})`
      }

      // 处理索引
      this.form.indexes.forEach(index => {
        if (index.columns && index.columns.length > 0) {
          sql += `,\n  ${index.indexType} ${index.indexName ? index.indexName : ''} (${index.columns.join(', ')})`
        }
      })

      sql += `\n)`

      // 表注释
      if (this.form.tableComment) {
        sql += ` COMMENT='${this.form.tableComment}'`
      }

      sql += ';'

      return sql
    },
    hasAutoIncrementColumn() {
      return this.form.columns.some(col => this.canAutoIncrement(col))
    }
  },
  methods: {
    show() {
      this.visible = true
    },
    resetForm() {
      this.form = {
        tableName: '',
        tableComment: '',
        columns: [
          {
            columnName: 'id',
            columnType: 'bigint',
            length: '',
            isNullable: false,
            defaultValue: '',
            comment: '主键ID',
            isPrimaryKey: true,
            isAutoIncrement: true
          }
        ],
        indexes: []
      }
    },
    handleAddColumn() {
      this.form.columns.push({
        columnName: '',
        columnType: 'varchar',
        length: '255',
        isNullable: true,
        defaultValue: '',
        comment: '',
        isPrimaryKey: false,
        isAutoIncrement: false
      })
    },
    handleRemoveColumn(index) {
      this.form.columns.splice(index, 1)
    },
    handlePrimaryKeyChange(row) {
      if (row.isPrimaryKey) {
        row.isNullable = false
        // 如果不是数值类型，不能设置自增
        if (!this.isNumericType(row.columnType)) {
          row.isAutoIncrement = false
        }
      } else {
        row.isAutoIncrement = false
      }
    },
    handleTypeChange(row) {
      // 类型变化时重置相关属性
      if (!this.isNumericType(row.columnType)) {
        row.isAutoIncrement = false
      }
      // 清除不适用类型的长度值
      if (!this.typeHasLength(row.columnType)) {
        row.length = ''
      }
    },
    handleAddIndex() {
      this.form.indexes.push({
        indexName: '',
        columns: [],
        indexType: 'INDEX'
      })
    },
    handleRemoveIndex(index) {
      this.form.indexes.splice(index, 1)
    },
    formatDefaultValue(value, type) {
      if (['varchar', 'char', 'text', 'longtext'].includes(type.toLowerCase())) {
        return `'${value}'`
      }
      return value
    },
    isNumericType(type) {
      const numericTypes = ['tinyint', 'smallint', 'mediumint', 'int', 'bigint']
      return numericTypes.includes(type.toLowerCase())
    },
    typeHasLength(type) {
      const typesWithLength = ['varchar', 'char', 'decimal']
      return typesWithLength.includes(type.toLowerCase())
    },
    canAutoIncrement(row) {
      return row.isPrimaryKey && this.isNumericType(row.columnType)
    },
    handleCreateTable() {
      this.$refs.form.validate(valid => {
        if (!valid) return

        if (this.form.columns.length === 0) {
          this.$modal.msgError('请至少添加一个字段')
          return
        }

        // 检查是否有主键
        const hasPrimaryKey = this.form.columns.some(col => col.isPrimaryKey)
        if (!hasPrimaryKey) {
          this.$modal.msgError('请至少设置一个主键字段')
          return
        }

        // 检查字段名是否重复
        const columnNames = this.form.columns.map(col => col.columnName)
        const uniqueNames = new Set(columnNames)
        if (uniqueNames.size !== columnNames.length) {
          this.$modal.msgError('字段名不能重复')
          return
        }

        // 检查索引是否有效
        for (const index of this.form.indexes) {
          if (!index.columns || index.columns.length === 0) {
            this.$modal.msgError('请为每个索引选择至少一个字段')
            return
          }
        }

        createTable({ sql: this.sqlPreview }).then(res => {
          this.$modal.msgSuccess(res.msg)
          if (res.code === 200) {
            this.visible = false
            this.$emit("ok")
          }
        })
      })
    }
  }
}
</script>

<style scoped>
.el-divider {
  margin: 15px 0;
}

.el-table >>> .el-table__body .disabled-row {
  opacity: 0.6;
  pointer-events: none;
}
</style>
