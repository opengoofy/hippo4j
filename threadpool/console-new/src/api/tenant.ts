import request from '@/utils';
import { useContext, useEffect } from 'react';
import { MyContext } from '@/context';

const getTenantList = async (formData: { current: number; desc: boolean; size: number; tenantId: string }) => {
  return await request('/tenant/query/page', {
    method: 'POST',
  })
    .then(response => {
      console.log('租户列表返回', response);
    })
    .catch(err => {
      console.log('租户列表获取出错：', err);
    });
};

const updataTenant = async (formData: {
  id: number;
  tenantId: string;
  tenantName: string;
  tenantDesc: string;
  owner: string;
  // gmtCreate: Data;
  // gmtModified: Data;
}) => {
  return await request('/tenant/update', {
    method: 'POST',
    params: {
      ...formData,
    },
  })
    .then((response: any) => {
      useEffect(() => {
        const { setTenantInfo } = useContext<any>(MyContext);
        setTenantInfo(response.data.records);
      });
    })
    .catch(err => {
      console.log('租户列表获取出错：', err);
    });
};

export { getTenantList, updataTenant };
