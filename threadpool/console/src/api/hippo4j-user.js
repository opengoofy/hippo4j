import request from '@/utils/request'

export function getList(data) {
  return request({
    url: '/hippo4j/v1/cs/auth/users/page',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/hippo4j/v1/cs/auth/users/update',
    method: 'put',
    data
  })
}

export function createUser(data) {
  return request({
    url: '/hippo4j/v1/cs/auth/users/add',
    method: 'post',
    data
  })
}

export function deleteUser(name) {
  return request({
    url: '/hippo4j/v1/cs/auth/users/remove/' + name,
    method: 'delete'
  })
}

export function getCurrentUser(name) {
  return request({
    url: '/hippo4j/v1/cs/auth/users/info/' + name,
    method: 'get'

  })
}
