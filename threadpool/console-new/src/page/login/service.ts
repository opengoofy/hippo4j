import request from '@/utils';

const fetchLogin = async (body: any) => {
  const { data } = await request<{ data: string; roles: string[] }>('/hippo4j/v1/cs/auth/login', {
    method: 'POST',
    body,
  });
  return data;
};

export default {
  fetchLogin,
};
