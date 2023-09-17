import { Suspense } from 'react';
import LayoutCom from './components/layout-com';
import { Link, Route, Routes } from 'react-router-dom';
import routeList from './route';
import Login from '@/page/login';
import {
  AppstoreOutlined,
  MailOutlined,
  UsergroupDeleteOutlined,
  DiffOutlined,
  UserOutlined,
  ProjectOutlined,
} from '@ant-design/icons';
import { Spin, ConfigProvider } from 'antd';

const sideMenuList = [
  {
    label: <Link to="/about">about</Link>,
    key: 'mail',
    icon: <MailOutlined />,
  },
  {
    label: <Link to="/home">主页</Link>,
    key: 'app',
    icon: <AppstoreOutlined />,
  },
  {
    label: <Link to="/tenant">租户管理</Link>,
    key: 'tenant',
    icon: <UsergroupDeleteOutlined />,
  },
  {
    label: <Link to="/item">项目管理</Link>,
    key: 'item',
    icon: <ProjectOutlined />,
  },
  {
    label: <Link to="/user">用户权限</Link>,
    key: 'user',
    icon: <UserOutlined />,
  },
  {
    label: <Link to="/log">日志管理</Link>,
    key: 'log',
    icon: <DiffOutlined />,
  },
];

const App = () => {
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
