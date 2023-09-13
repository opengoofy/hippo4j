import { useState, useContext, ReactNode } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';
import { Layout, Button, Menu } from 'antd';
import useThemeMode from '@/hooks/useThemeMode';
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
  const [setIsDark] = useThemeMode();
  return (
    <main className={style.container} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
      <Header className={style.header} style={{ backgroundColor: myThemes.backgroundColor.bg2 }}>
        <Button onClick={() => setIsDark(pre => !pre)}>切换主题</Button>
      </Header>
      <Layout style={{ backgroundColor: myThemes.backgroundColor.bg1, height: 'calc(100vh - 64px)' }}>
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
            <Menu onClick={onClick} selectedKeys={[current]} mode="inline" items={sideMenuList} />
          </Sider>
        )}
        <Content className={style.content}>{children}</Content>
      </Layout>
    </main>
  );
};

export default LayoutCom;
