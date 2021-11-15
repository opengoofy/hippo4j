import request from '@/utils/request'

export function list(data) {
  return request({
    url: '/api/thread/pool/query/page',
    method: 'post',
    data
  })
}

export function tenantList(data) {
  return request({
    url: '/api/item/query/page',
    method: 'post',
    data
  })
}

export function updated(data) {
  return request({
    url: '/api/thread/pool/save_or_update',
    method: 'post',
    data
  })
}

export function created(data) {
  return request({
    url: '/api/thread/pool/save_or_update',
    method: 'post',
    data
  })
}

export function deleted(data) {
  return request({
    url: '/api/thread/pool/delete',
    method: 'delete',
    data
  })
}

