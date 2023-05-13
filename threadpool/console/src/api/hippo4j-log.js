import request from '@/utils/request';

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/log/query/page',
    method: 'post',
    data,
  });
}
