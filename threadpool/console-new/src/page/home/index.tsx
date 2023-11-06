import { Button, Calendar } from 'antd';
import dayjs from 'dayjs';
import { useTranslation } from 'react-i18next';

const Home = () => {
  const { t } = useTranslation();

  return (
    <div style={{ color: 'red' }}>
      <Button>{t('hello')}</Button>
      <Calendar fullscreen={false} value={dayjs()} />
    </div>
  );
};

export default Home;
