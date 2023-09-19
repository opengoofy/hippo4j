import request from '@/utils';

const getLogin = async (formData: { username: string; password: string | number; rememberMe: number }) => {
  return await request('/auth/login', {
    method: 'GET',
    params: {
      ...formData,
    },
  }).catch(err => {
    console.log('登陆出错：', err);
  });
};

// export function getInfo() {
//   return request('get', '/hippo4j/v1/cs/user/info');
// }

// export function logout() {
//   return request('post', '/hippo4j/v1/cs/user/logout');
// }
// eslint-disable-next-line import/no-anonymous-default-export

export { getLogin };
