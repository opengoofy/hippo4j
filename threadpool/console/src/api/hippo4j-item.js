import request from '@/utils/request';

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/item/query/page',
    method: 'post',
    data,
  });
}

export function tenantList(data) {
  return request({
    url: '/hippo4j/v1/cs/item/query/page',
    method: 'post',
    data,
  });
}

export function updated(data) {
  return request({
    url: '/hippo4j/v1/cs/item/update',
    method: 'post',
    data,
  });
}

export function created(data) {
  return request({
    url: '/hippo4j/v1/cs/item/save',
    method: 'post',
    data,
  });
}

export function deleted(data) {
  return request({
    url: '/hippo4j/v1/cs/item/delete/' + data[0] + '/' + data[1],
    method: 'delete',
  });
}

export function getJobProjectList(params) {
  return request({
    url: 'v1/cs/jobProject/list',
    method: 'get',
    params,
  });
}
