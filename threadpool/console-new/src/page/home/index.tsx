import { Button, Calendar } from 'antd';
import dayjs from 'dayjs';
import request from '@/utils';
import { useTranslation } from 'react-i18next';

const Home = () => {
  const { t } = useTranslation();
  const fetchdata = (body: { duid: string }) => {
    return request<{ phone: string }>('https://mock.xiaojukeji.com/mock/16635/am/marketing/mis/member/archive/phone', {
      method: 'post',
      body,
    });
  };

  return (
    <div style={{ color: 'red' }}>
      <Button>{t('hello')}</Button>
      <Calendar fullscreen={false} value={dayjs()} />
    </div>
  );
};

export default Home;
