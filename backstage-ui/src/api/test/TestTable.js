import request from '@/utils/request'

// 查询测试代码生成列表
export function listTable(query) {
  return request({
    url: '/test/table/list',
    method: 'get',
    params: query
  })
}

// 查询测试代码生成详细
export function getTable(id) {
  return request({
    url: '/test/table/' + id,
    method: 'get'
  })
}

// 新增测试代码生成
export function addTable(data) {
  return request({
    url: '/test/table',
    method: 'post',
    data: data
  })
}

// 修改测试代码生成
export function updateTable(data) {
  return request({
    url: '/test/table',
    method: 'put',
    data: data
  })
}

// 删除测试代码生成
export function delTable(id) {
  return request({
    url: '/test/table/' + id,
    method: 'delete'
  })
}
