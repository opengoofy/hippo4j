import request from '@/utils/request';

export function list(data) {
  return request({
    url: '/hippo4j/v1/cs/notify/query/page',
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
    url: '/hippo4j/v1/cs/notify/update',
    method: 'post',
    data,
  });
}

export function created(data) {
  return request({
    url: '/hippo4j/v1/cs/notify/save',
    method: 'post',
    data,
  });
}

export function deleted(data) {
  return request({
    url: '/hippo4j/v1/cs/notify/remove',
    method: 'delete',
    data,
  });
}

export function enable(data) {
  return request({
    url: '/hippo4j/v1/cs/notify/enable/' + data.id + '/' + data.enable,
    method: 'post',
  });
}
