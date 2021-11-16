import request from '@/utils/request'

// dashborad

export function chartInfo () {
  return request({
    url: '/api/dashboard',
    method: 'get'
  })
}
