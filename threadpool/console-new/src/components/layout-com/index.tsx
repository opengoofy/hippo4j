import { useState, useContext, ReactNode } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';
import { Layout, Menu, MenuProps } from 'antd';
import HeaderChild from '../header';
import { IMenuList } from '@/typings';
import style from './index.module.less';
type MenuItem = Required<MenuProps>['items'][number];

const { Header, Sider, Content } = Layout;

interface ILayoutCom {
  children?: ReactNode;
  sideMenuList: MenuItem[];
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
      <Layout style={{ backgroundColor: myThemes.backgroundColor.bg1, height: 'calc(100vh - 64px)' }}>
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
            <Menu onClick={onClick} selectedKeys={[current]} mode="inline" items={sideMenuList} />
          </Sider>
        )}
        <Content className={style.content} style={{ backgroundColor: myThemes.backgroundColor.bgContent }}>
          {children}
        </Content>
      </Layout>
    </main>
  );
};

export default LayoutCom;
