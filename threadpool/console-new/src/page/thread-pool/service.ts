import request from '@/utils';

const fetchThreadPoolTable = async (body: ThreadPoolTableBody): Promise<Result> => {
  const { data } = await request<ThreadPoolTableRes>('/hippo4j/v1/cs/thread/pool/query/page', {
    method: 'POST',
    body,
  });
  return {
    total: data?.total,
    list: data?.records,
  };
};

export default {
  fetchThreadPoolTable,
};
