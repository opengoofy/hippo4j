import request from '@/utils/request'

// dashborad

export function chartInfo() {
  return request({
    url: '/hippo4j/v1/cs/dashboard',
    method: 'get'
  })
}

export function lineChart() {
  return request({
    url: '/hippo4j/v1/cs/dashboard/line/chart',
    method: 'get'
  })
}

export function pieChart() {
  return request({
    url: '/hippo4j/v1/cs/dashboard/pie/chart',
    method: 'get'
  })
}

export function tenantChart() {
  return request({
    url: '/hippo4j/v1/cs/dashboard/tenant/chart',
    method: 'get'
  })
}

export function ranking() {
  return request({
    url: '/hippo4j/v1/cs/dashboard/ranking',
    method: 'get'
  })
}
