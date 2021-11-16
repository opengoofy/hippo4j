import request from '@/utils/request'

export function list(listArray) {
  return request({
    url: '/api/thread/pool/list/instance/' + listArray[0] + '/' + listArray[1],
    method: 'get'
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
    url: '/api/configs?identify=' + data.identify,
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

