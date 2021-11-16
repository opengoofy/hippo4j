import request from '@/utils/request'

export function getList(data) {
  return request({
    url: '/api/auth/users/page',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/api/auth/users/update',
    method: 'put',
    data
  })
}

export function createUser(data) {
  return request({
    url: '/api/auth/users/add',
    method: 'post',
    data
  })
}

export function deleteUser(name) {
  return request({
    url: '/api/user/remove/' + name,
    method: 'delete'
  })
}
