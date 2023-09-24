import request from '@/utils';

const fetchItemList = async (
  pageProps: { current: number; pageSize: number },
  formData: { tencent: string | number }
): Promise<{ total: number; list: Array<any> }> => {
  const res: any = await request('/hippo4j/v1/cs/item/query/page', {
    method: 'POST',
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
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchAddItem = async (params: {
  itemDesc: string; // 项目简介
  itemId: string; // 项目
  itemName: string; // 项目名称
  owner: string; // 负责人
  tenantId: string; // 租户
  tenantDesc?: string;
  tenantName?: string;
}) => {
  const res = await request('/hippo4j/v1/cs/item/save', {
    method: 'POST',
    body: { ...params },
  });
  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchUpdateItem = async (params: {
  id: string;
  itemDesc: string; // 项目简介
  itemId: string; // 项目
  itemName: string; // 项目名称
  owner: string; // 负责人
  tenantId: string; // 租户
  tenantDesc?: string;
  tenantName?: string;
}) => {
  const res = await request('/hippo4j/v1/cs/item/update', {
    method: 'POST',
    body: { ...params },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchDeleteItem = async (id: string) => {
  const url = 'hippo4j/v1/cs/item/delete/prescription/' + id;
  const res = await request(url, {
    method: 'DELETE',
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

export { fetchItemList, fetchAddItem, fetchDeleteItem, fetchUpdateItem };
