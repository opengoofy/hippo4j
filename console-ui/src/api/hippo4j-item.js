import request from '@/utils/request'

// project

export function list (data) {
  return request({
    url: '/api/item/query/page',
    method: 'post',
    data
  })
}

export function tenantList (data) {
  return request({
    url: '/api/item/query/page',
    method: 'post',
    data
  })
}

export function updated (data) {
  return request({
    url: '/api/item/update',
    method: 'post',
    data
  })
}

export function created (data) {
  return request({
    url: '/api/item/save',
    method: 'post',
    data
  })
}

export function deleted (data) {
  return request({
    url: '/api/item/delete/' + data[0] + '/' + data[1],
    method: 'delete'
  })
}

export function getJobProjectList (params) {
  return request({
    url: 'api/jobProject/list',
    method: 'get',
    params
  })

}

