import request from '@/utils';

const fetchTenantList = async (
  pageProps: { current: number; pageSize: number },
  formData: { tencent: string | number }
) => {
  // mock request
  return {
    total: 3,
    list: [
      {
        num: '1',
        tenant: 'admin',
        tenantName: 'admin',
        creator: 'admin',
        data: 'admin',
      },
    ],
  };
  const res = await request('/tenants', {
    method: 'POST',
    params: {
      ...formData,
      pageNum: pageProps.current,
      pageSize: pageProps.pageSize,
    },
  });

  if (res && res.success) {
    return {
      total: 3,
      list: [
        {
          num: '1',
          tenant: 'admin',
          tenantName: 'admin',
          creator: 'admin',
          data: 'admin',
        },
      ],
    };
  }
  throw new Error(res.msg || '接口异常');
};

export { fetchTenantList };
