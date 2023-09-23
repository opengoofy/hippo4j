import request from '@/utils';

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
      list: res.data.records,
    };
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchAddTenant = async (id: string) => {
  const res = await request('/hippo4j/v1/cs/tenant/save', {
    method: 'POST',
    params: { id },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchDeleteTenant = async (id: string) => {
  const res = await request('/tenants', {
    method: 'POST',
    params: { id },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchUpdateTenant = async (id: string) => {
  const res = await request('hippo4j/v1/cs/tenant/update', {
    method: 'POST',
    params: { id },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

const fetchTenantDetail = async (id: string) => {
  const res = await request('/tenants', {
    method: 'POST',
    params: { id },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

export { fetchTenantList, fetchAddTenant, fetchDeleteTenant, fetchUpdateTenant, fetchTenantDetail };
