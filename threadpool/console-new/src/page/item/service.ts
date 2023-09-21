import request from '@/utils';

const fetchItemList = async (
  pageProps: { current: number; pageSize: number },
  formData: { tencent: string | number }
): Promise<{ total: number; list: Array<any> }> => {
  const res: any = await request('/hippo4j/v1/cs/item/query/page', {
    method: 'POST',
    headers: {
      Authorization:
        'Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3LGJhb3hpbnlpX2FkbWluIiwiaXNzIjoiYWRtaW4iLCJleHAiOjE2OTUzOTg4NDksImlhdCI6MTY5NDc5NDA0OSwicm9sIjoiUk9MRV9BRE1JTiJ9.syRDshKpd-xETsSdeMPRtk956f4BJkPt4utVsUl4smgH71Woj8SUq4w2RX1YtGTC4aTZRJYdKOfkTqwK0g_dHQ',
      cookie:
        'Admin-Token=Bearer%20eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI3LGJhb3hpbnlpX2FkbWluIiwiaXNzIjoiYWRtaW4iLCJleHAiOjE2OTUzOTg4NDksImlhdCI6MTY5NDc5NDA0OSwicm9sIjoiUk9MRV9BRE1JTiJ9.syRDshKpd-xETsSdeMPRtk956f4BJkPt4utVsUl4smgH71Woj8SUq4w2RX1YtGTC4aTZRJYdKOfkTqwK0g_dHQ; userName=baoxinyi_admin',
    },
    body: {
      ...formData,
      current: pageProps.current,
      size: pageProps.pageSize,
      desc: true,
    },
  });
  if (res && res.success) {
    return {
      total: res.data.total,
      list: res.data.records.map((item: any, index: number) => ({ index: index + 1, ...item })),
    };
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchAddTenant = async (params: {
  itemDesc: string; // 项目简介
  itemId: string; // 项目
  itemName: string; // 项目名称
  owner: string; // 负责人
  tenantId: string; // 租户
  tenantDesc?: string;
  tenantName?: string;
}) => {
  const res = await request('/hippo4j/v1/cs/tenant/save', {
    method: 'POST',
    body: { ...params },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchDeleteItem = async (id: string) => {
  const url = 'hippo4j/v1/cs/item/delete/prescription/' + id;
  const res = await request(url, {
    method: 'DELETE',
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchUpdateItem = async (id: string) => {
  const res = await request('/hippo4j/v1/cs/item/update', {
    method: 'POST',
    params: { id },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

export { fetchItemList, fetchAddTenant, fetchDeleteItem, fetchUpdateItem };
