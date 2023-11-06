import request from '@/utils';

const fetchLogList = async (
  pageProps: { current: number; pageSize: number },
  formData: { category: string | number; bizNo: string; operator: string }
): Promise<{ total: number; list: Array<any> }> => {
  const res: any = await request('/hippo4j/v1/cs/log/query/page', {
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
      list: res.data.records.map((item: any, index: number) => ({ id: index + 1, ...item })),
    };
  }
  throw new Error(res.msg || '服务器开小差啦~');
};

export { fetchLogList };
