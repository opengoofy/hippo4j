import request from '@/utils';
import { useContext, useEffect } from 'react';
import { MyContext } from '@/context';

const getTenantList = async () => {
  return await request('/tenant/query/page', {
    method: 'POST',
  })
    .then(response => {
      console.log('租户列表返回', response);
      const { setTenantInfo } = useContext<any>(MyContext);
      useEffect(() => {
        setTenantInfo(response.data);
      });
    })
    .catch(err => {
      console.log('租户列表获取出错：', err);
    });
};

const updataTenant = async (formData: { size: number }) => {
  return await request('/tenant/update', {
    method: 'POST',
    params: {
      ...formData,
    },
  }).catch(err => {
    console.log('租户列表获取出错：', err);
  });
};

export { getTenantList, updataTenant };
