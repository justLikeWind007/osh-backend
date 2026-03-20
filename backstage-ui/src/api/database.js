import request from '@/utils/request'

// 获取数据库对象（保持原样）
export function getDatabaseObjects() {
  return request({
    url: '/database/objects',
    method: 'get'
  })
}

// 获取表数据（增加分页参数）
export function getTableData(tableName, params = {}) {
  return request({
    url: `/database/table/${tableName}`,
    method: 'get',
    params: {
      pageNum: params.pageNum || 1,    // 兼容page/pageNum
      pageSize: params.pageSize || 10   // 兼容size/pageSize
    }
  })
}

// 执行SQL查询（保持原样）
export function executeQuery(data) {
  return request({
    url: '/database/query',
    method: 'post',
    data: {
      sql: data.sql,
      page: data.page || 1,
      pageSize: data.pageSize || 10
    }
  })
}

// 保存表数据（保持原样）
export function saveTableData(tableName, data) {
  return request({
    url: `/database/table/${tableName}`,
    method: 'post',
    data
  })
}

// 新增：删除单条数据
export function deleteTableRow(tableName, id) {
  return request({
    url: `/database/table/${tableName}/${id}`,
    method: 'delete'
  })
}

// 新增：批量删除（简单实现）
export function batchDeleteRows(tableName, ids) {
  return request({
    url: `/database/table/${tableName}/batch`,
    method: 'delete',
    data: JSON.stringify(ids)
  })
}
