import { useState, useContext, ReactNode, useEffect, useMemo } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';
import { Layout, Menu, MenuProps } from 'antd';
import HeaderChild from '../header';
import style from './index.module.less';
import { useLocation } from 'react-router-dom';
import classNames from 'classnames';
type MenuItem = Required<MenuProps>['items'][number];

const { Header, Sider, Content } = Layout;

interface ILayoutCom {
  children?: ReactNode;
  sideMenuList: MenuItem[];
}
const LayoutCom = (props: ILayoutCom) => {
  const { sideMenuList, children } = props;
  const myThemes: DefaultTheme = useContext<any>(ThemeContext);
  const [currentKey, setCurrentKey] = useState<string>('');

  const location = useLocation();
  useEffect(() => {
    setCurrentKey(location.pathname);
  }, [location]);

  const onClick = (e: any) => {
    setCurrentKey(e.key);
  };

  useEffect(() => {
    document.body.style.backgroundColor = myThemes.backgroundColor.bg1;
  }, [myThemes]);

  const isHeader = useMemo(() => {
    const isHeader = location.pathname !== '/login';
    return isHeader;
  }, [location]);

  const isSider = useMemo(() => {
    const isSider = location.pathname !== '/login';
    return isSider;
  }, [location]);

  return (
    <main className={style.container} style={{ backgroundColor: myThemes.backgroundColor.bg1 }}>
      {isHeader && (
        <Header className={style.header}>
          <HeaderChild />
        </Header>
      )}
      <Layout
        style={{
          backgroundColor: myThemes.backgroundColor.bg1,
          height: `calc(100vh - ${isHeader ? '64px' : '0px'})`,
          margin: isHeader ? '10px 10px 0px 0px' : '0px',
        }}
      >
        {isSider && (
          <Sider className={style.sider} style={{ backgroundColor: myThemes.backgroundColor.bg1 }} collapsible>
            <Menu onClick={onClick} selectedKeys={[currentKey]} mode="inline" items={sideMenuList} />
          </Sider>
        )}
        <Content
          className={classNames(style.content, {
            [style.pureContent]: location.pathname === '/login',
          })}
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
