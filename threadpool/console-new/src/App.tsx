import LayoutCom from './components/layout-com';
import { Routes, Route } from 'react-router-dom';
import routeList from './route';
import Login from '@/page/login';
import { AppstoreOutlined, MailOutlined } from '@ant-design/icons';

const sideMenuList = [
  {
    label: <a href="/about">about</a>,
    key: 'mail',
    icon: <MailOutlined />,
  },
  {
    label: <a href="/home">主页</a>,
    key: 'app',
    icon: <AppstoreOutlined />,
  },
];

const App = () => {
  return (
    <LayoutCom sideMenuList={sideMenuList} isSider={false}>
      <Routes>
            <Route path="/Login" Component={Login}></Route>
        {routeList.map(item => (
          <Route key={item.path} path={item.path} Component={item.component} />
        ))}
      </Routes>
    </LayoutCom>
  );
};

export default App;
