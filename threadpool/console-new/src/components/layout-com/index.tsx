import React, { useEffect, useState, useContext } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';

import { Layout, Button, Menu, Table } from 'antd';
import { Routes, Route, Link } from 'react-router-dom';
import { useLocalStorageState } from 'ahooks';
import Home from '@/page/home';
import About from '@/page/about';
import Login from '@/page/login';
import style from './index.module.less';
import TableBox from '../table';
import Search from '@/page/search';

import { AppstoreOutlined, MailOutlined, SettingOutlined } from '@ant-design/icons';
import { THEME_NAME, MyThemeContext } from '@/context/themeContext';
import useThemeMode from '@/hooks/useThemeMode';

const { Header, Sider, Content } = Layout;

interface ThemeProps {
  children: React.ReactNode;
}

const items = [
  {
    label: <a href="/about">Navigation One</a>,
    key: 'mail',
    icon: <MailOutlined />,
  },
  {
    label: 'Navigation Two',
    key: 'app',
    icon: <AppstoreOutlined />,
    disabled: true,
  },
  {
    label: <a href="/about">Navigation One</a>,
    key: 'app',
    icon: <AppstoreOutlined />,
  },
  {
    label: 'Navigation Three - Submenu',
    key: 'SubMenu',
    icon: <SettingOutlined />,
    children: [
      {
        type: 'group',
        label: 'Item 1',
        children: [
          {
            label: 'Option 1',
            key: 'setting:1',
          },
          {
            label: 'Option 2',
            key: 'setting:2',
          },
        ],
      },
      {
        type: 'group',
        label: 'Item 2',
        children: [
          {
            label: 'Option 3',
            key: 'setting:3',
          },
          {
            label: 'Option 4',
            key: 'setting:4',
          },
        ],
      },
    ],
  },
  {
    label: (
      <a href="https://ant.design" target="_blank" rel="noopener noreferrer">
        Navigation Four - Link
      </a>
    ),
    key: 'alipay',
  },
];

const LayoutCom = () => {
  const myThemes: DefaultTheme = useContext<any>(ThemeContext);
  const [current, setCurrent] = useState('mail');
  const onClick = (e: any) => {
    setCurrent(e.key);
  };
  const [isSider, setIsSider] = useState(true);
  const [setIsDark] = useThemeMode();

  return (
    <main className={style.container} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
      <Header className={style.header} style={{ backgroundColor: myThemes.backgroundColor.bg2 }}>
        <Button onClick={() => setIsDark(pre => !pre)}>切换主题</Button>
      </Header>
      <Layout style={{ backgroundColor: myThemes.backgroundColor.bg1, height: 'calc(100vh - 64px)' }}>
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
            <Menu onClick={onClick} selectedKeys={[current]} mode="inline" items={items} />
          </Sider>
        )}
        <Content className={style.content}>
          <Routes>
            <Route path="/Search" Component={Search}></Route>
            <Route path="/Table" Component={TableBox}></Route>
            <Route path="/Login" Component={Login}></Route>
            <Route path="/home" Component={Home}></Route>
            <Route path="/about" Component={About}></Route>
          </Routes>
        </Content>
      </Layout>
    </main>
  );
};

export default LayoutCom;
