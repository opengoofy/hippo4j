import request from '@/utils/request';

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/log/query/page',
    method: 'post',
    data,
  });
}

export function active(data) {
  return request({
    url: '/hippo4j/v1/cs/monitor/info',
    method: 'post',
    data,
  });
}

export function lastTaskCountFun(data) {
  return request({
    url: '/hippo4j/v1/cs/monitor/last/task/count',
    method: 'post',
    data,
  });
}
