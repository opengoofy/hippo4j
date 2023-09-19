import { Suspense } from 'react';
import LayoutCom from './components/layout-com';
import { Routes, Route, Link } from 'react-router-dom';

import routeList from './route';
import Login from '@/page/login';
import { MenuProps, Spin, ConfigProvider } from 'antd';
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
    {
      // label: <Link to={'/tenant'}>{useTran(STR_MAP.LOG_MANAGE)}</Link>,
      label: <Link to={'/tenant'}>租户管理</Link>,
      key: STR_MAP.TENANT_MANAGE,
      icon: <IconFont type="icon-hot-for-ux"></IconFont>,
    },
    {
      // label: <Link to={'/item'}>{useTran(STR_MAP.PROJECT_MANAGE)}</Link>,
      label: <Link to={'/item'}>项目管理</Link>,
      key: STR_MAP.PROJECT_MANAGE,
      icon: <IconFont type="icon-hot-for-ux"></IconFont>,
    },
    {
      // label: <Link to={'/user'}>{useTran(STR_MAP.USE_RIGHT)}</Link>,
      label: <Link to={'/user'}>用户权限</Link>,
      key: STR_MAP.USE_RIGHT,
      icon: <IconFont type="icon-hot-for-ux"></IconFont>,
    },
    {
      // label: <Link to={'/log'}>{useTran(STR_MAP.LOG_MANAGE)}</Link>,
      label: <Link to={'/log'}>日志管理</Link>,
      key: STR_MAP.LOG_MANAGE,
      icon: <IconFont type="icon-hot-for-ux"></IconFont>,
    },
  ];

  return (
    <ConfigProvider>
      <Suspense fallback={<Spin size="small" />}>
        <LayoutCom sideMenuList={sideMenuList} isSider={true}>
          <Routes>
            <Route path="/Login" Component={Login}></Route>
            {routeList.map(item => (
              <Route key={item.path} path={item.path} Component={item.component} />
            ))}
          </Routes>
        </LayoutCom>
      </Suspense>
    </ConfigProvider>
  );
};

export default App;
