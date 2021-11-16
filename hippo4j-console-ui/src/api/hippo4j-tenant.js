import request from '@/utils/request'

// project

export function list (data) {
  return request({
    url: '/api/tenant/query/page',
    method: 'post',
    data
  })
}

export function updated (data) {
  return request({
    url: '/api/tenant/update',
    method: 'post',
    data
  })
}

export function created (data) {
  return request({
    url: '/api/tenant/save',
    method: 'post',
    data
  })
}

export function deleted (data) {
  return request({
    url: '/api/tenant/delete/' + data,
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

