import { Button } from 'antd';
import request from '@/utils';

const Home = () => {
  const fetchdata = (body: { duid: string }) => {
    return request<{ phone: string }>('https://mock.xiaojukeji.com/mock/16635/am/marketing/mis/member/archive/phone', {
      method: 'post',
      body,
    });
  };

  return (
    <div style={{ color: 'red' }}>
      <Button onClick={() => fetchdata({ duid: '1234234' })}>jjjjj</Button>
    </div>
  );
};

export default Home;
