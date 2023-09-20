import LayoutCom from './components/layout-com';
import { Routes, Route, Link } from 'react-router-dom';
import routeList from './route';
import { MenuProps } from 'antd';
import { useTran } from './hooks';
import { STR_MAP } from './config/i18n/locales/constants';
import IconFont from './components/icon';

type MenuItem = Required<MenuProps>['items'][number];

const App = () => {
  const sideMenuList: MenuItem[] = [
    {
      label: useTran(STR_MAP.DYNAMIC_THREAD_POOL),
      key: STR_MAP.DYNAMIC_THREAD_POOL,
      icon: <IconFont type="icon-hot-for-ux"></IconFont>,
      children: [
        { label: <Link to={'/thread-poll/index'}>{useTran(STR_MAP.THREAD_POOL)}</Link>, key: '/thread-poll/index' },
      ],
    },
  ];

  return (
    <LayoutCom sideMenuList={sideMenuList}>
      <Routes>
        {routeList.map(item => (
          <Route key={item.path} path={item.path} Component={item.component} />
        ))}
      </Routes>
    </LayoutCom>
  );
};

export default App;
