import request from '@/utils/request'

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/configs/verify/query/application/page',
    method: 'post',
    data
  })
}

export function verify(data) {
  return request({
    url: '/hippo4j/v1/cs/configs/verify',
    method: 'post',
    data
  })
}

export function applicationDetail(data){
  return request({
    url: '/hippo4j/v1/cs/configs/verify/query/application/detail?id='+data,
    method: 'get',
    data
  })  
}


