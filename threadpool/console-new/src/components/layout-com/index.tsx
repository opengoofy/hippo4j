import { useState, useContext, ReactNode, useEffect } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';
import { Layout, Menu, MenuProps } from 'antd';
import HeaderChild from '../header';
import style from './index.module.less';
import { useLocation } from 'react-router-dom';
import { useThemeMode } from '@/hooks';
type MenuItem = Required<MenuProps>['items'][number];

const { Header, Sider, Content } = Layout;

interface ILayoutCom {
  children?: ReactNode;
  sideMenuList: MenuItem[];
  isSider?: boolean;
  isHeader?: boolean;
}
const LayoutCom = (props: ILayoutCom) => {
  const { sideMenuList, children, isSider = true, isHeader = true } = props;
  const myThemes: DefaultTheme = useContext<any>(ThemeContext);
  const [currentKey, setCurrentKey] = useState<string>('');
  const { isDark } = useThemeMode();

  const location = useLocation();
  useEffect(() => {
    setCurrentKey(location.pathname);
  }, [location]);

  const onClick = (e: any) => {
    setCurrentKey(e.key);
  };

  useEffect(() => {
    document.body.style.backgroundColor = myThemes.backgroundColor.bg1;
  }, [isDark, myThemes]);

  return (
    <main className={style.container} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
      {isHeader && (
        <Header className={style.header}>
          <HeaderChild />
        </Header>
      )}
      <Layout
        style={{ backgroundColor: myThemes.backgroundColor.bg1, height: `calc(100vh - ${isHeader ? '64px' : 0})` }}
      >
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }} collapsible>
            <Menu onClick={onClick} selectedKeys={[currentKey]} mode="inline" items={sideMenuList} />
          </Sider>
        )}
        <Content
          className={style.content}
          style={{
            backgroundColor: myThemes.backgroundColor.bgContent,
            height: `calc(100vh - ${isHeader ? '64px' : 0})`,
          }}
        >
          {children}
        </Content>
      </Layout>
    </main>
  );
};

export default LayoutCom;
