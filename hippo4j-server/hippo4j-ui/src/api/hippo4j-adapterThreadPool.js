import request from '@/utils/request'

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/adapter/thread-pool/query?mark=' + data.mark + '&tenant=' + data.tenantId + '&item=' + data.itemId + '&threadPoolKey=' + data.threadPoolKey,
    method: 'get'
  })
}

export function listKey(data) {
  return request({
    url: '/hippo4j/v1/cs/adapter/thread-pool/query/key?mark=' + data.mark + '&tenant=' + data.tenantId + '&item=' + data.itemId,
    method: 'get'
  })
}

export function updatePool(data) {
  return request({
    url: '/hippo4j/v1/cs/adapter/thread-pool/update',
    method: 'post',
    data
  })
}

