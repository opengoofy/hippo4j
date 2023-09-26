import request from '@/utils';

const fetchTenantOptions = async (tencent: string) => {
  const res: any = await request('/hippo4j/v1/cs/tenant/query/page', {
    method: 'POST',
    body: {
      tencent,
      current: 1,
      size: 10,
      desc: true,
    },
  });
  if (res && res.success) {
    return res.data && res.data.records.map((item: any) => ({ value: item.tenantId, label: item.tenantId }));
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchTenantList = async (
  pageProps: { current: number; pageSize: number },
  formData: { tencent: string | number }
): Promise<{ total: number; list: Array<any> }> => {
  const res: any = await request('/hippo4j/v1/cs/tenant/query/page', {
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

const fetchAddTenant = async (params: {
  tenantDesc: string; // 项目简介
  tenantId: string; // 项目
  tenantName: string; // 项目名称
  owner: string; // 负责人
}) => {
  const res = await request('/hippo4j/v1/cs/tenant/save', {
    method: 'POST',
    body: { ...params },
  });
  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchUpdateTenant = async (params: {
  tenantDesc: string; // 项目简介
  tenantId: string; // 项目
  tenantName: string; // 项目名称
  owner: string; // 负责人
}) => {
  const res = await request('hippo4j/v1/cs/tenant/update', {
    method: 'POST',
    body: { ...params },
  });
  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchDeleteTenant = async (id: string) => {
  const url = 'hippo4j/v1/cs/item/delete/prescription/' + id;
  const res = await request(url, {
    method: 'DELETE',
  });
  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

export { fetchTenantOptions, fetchTenantList, fetchAddTenant, fetchDeleteTenant, fetchUpdateTenant };
