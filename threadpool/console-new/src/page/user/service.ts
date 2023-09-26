import request from '@/utils';

const fetchUserList = async (
  pageProps: { current: number; pageSize: number },
  formData: { tencent: string | number }
): Promise<{ total: number; list: Array<any> }> => {
  const res: any = await request('/hippo4j/v1/cs/auth/users/page', {
    method: 'POST',
    body: {
      ...formData,
      current: pageProps.current,
      size: pageProps.pageSize,
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

const fetchAddUser = async (params: {
  password?: string;
  permission?: string;
  role: string;
  userName: string;
  tempResources: Array<string>;
  tenant: Array<string>;
  resources?: Array<{ resource: string; action: 'rw' }>;
}) => {
  const { tempResources = [] } = params;
  const res = await request('/hippo4j/v1/cs/auth/users/add', {
    method: 'POST',
    body: {
      ...params,
      tempResources: tempResources.length > 0 ? true : false,
      resources: tempResources.map(item => ({ resource: item, action: 'rw' })),
    },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchUpdateUser = async (params: {
  password?: string;
  permission?: string;
  role: string;
  userName: string;
  tempResources: Array<any>;
  resources?: Array<{ resource: string; action: 'rw' }>;
}) => {
  const { tempResources = [] } = params;
  if (tempResources.length > 0) {
    params.resources = tempResources.map(item => ({ resource: item, action: 'rw' }));
  }
  const res = await request('/hippo4j/v1/cs/auth/users/update', {
    method: 'PUT',
    body: { ...params },
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

const fetchDeleteUser = async (id: string) => {
  const url = '/hippo4j/v1/cs/auth/users/remove/' + id;
  const res = await request(url, {
    method: 'DELETE',
  });

  if (res && res.success) {
    return res;
  }
  throw new Error(res.message || '服务器开小差啦~');
};

export { fetchUserList, fetchAddUser, fetchDeleteUser, fetchUpdateUser };
