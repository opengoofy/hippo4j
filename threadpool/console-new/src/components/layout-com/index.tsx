import { useState, useContext, ReactNode } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';
import { Breadcrumb, Layout, Menu } from 'antd';
import HeaderChild from '../header';
import { IMenuList } from '@/typings';
import style from './index.module.less';

const { Header, Sider, Content } = Layout;

interface ILayoutCom {
  children?: ReactNode;
  sideMenuList: IMenuList[];
  isSider?: boolean;
}
const LayoutCom = (props: ILayoutCom) => {
  const { sideMenuList, children, isSider = true } = props;
  const myThemes: DefaultTheme = useContext<any>(ThemeContext);
  const [current, setCurrent] = useState('mail');
  const onClick = (e: any) => {
    setCurrent(e.key);
  };

  return (
    <main className={style.container} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
      <Header className={style.header}>
        <HeaderChild />
      </Header>
      <Layout style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
            <Menu
              className={style.menu}
              onClick={onClick}
              selectedKeys={[current]}
              mode="inline"
              items={sideMenuList}
            />
          </Sider>
        )}
        <Layout style={{ padding: '0 24px 24px' }}>
          <Breadcrumb className={style.breadcrumb}>
            <Breadcrumb.Item>Home</Breadcrumb.Item>
            <Breadcrumb.Item>List</Breadcrumb.Item>
            <Breadcrumb.Item>App</Breadcrumb.Item>
          </Breadcrumb>
          <Content className={style.content}>{children}</Content>
        </Layout>
      </Layout>
    </main>
  );
};

export default LayoutCom;
