import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/hippo4j/v1/cs/auth/login',
    method: 'post',
    data
  })
}

export function getInfo() {
  return request({
    url: '/hippo4j/v1/cs/user/info',
    method: 'get'
  })
}

export function logout() {
  return request({
    url: '/hippo4j/v1/cs/user/logout',
    method: 'post'
  })
}
