import { Layout } from 'antd';
import { Routes, Route, Link } from 'react-router-dom';
import Home from './page/home';
import About from './page/about';
import style from './index.module.less';

const { Header, Sider, Content } = Layout;

function App() {
  return (
    <div className={style.container}>
      <Header className={style.header}>header</Header>
      <Layout>
        <Sider className={style.sider}>
          <Link to="/home">home</Link>
          <Link to="/about">about</Link>
        </Sider>
        <Content className={style.content}>
          <Routes>
            <Route path="/home" Component={Home}></Route>
            <Route path="/about" Component={About}></Route>
          </Routes>
        </Content>
      </Layout>
    </div>
  );
}

export default App;
