import React, { useContext } from 'react';
import style from './index.module.less';
import { UserOutlined } from '@ant-design/icons';
import { Avatar, Button, Col, Dropdown, Row, Switch } from 'antd';
import { MyContext } from '@/context';
import IconFont from '@/components/icon';
import { LANG_NAME, MyStoreValues, THEME_NAME } from '@/typings';
import { useNavigate } from 'react-router-dom';

const HeaderChild = () => {
  const { lang, themeName, setLang, setThemeName } = useContext<MyStoreValues>(MyContext);
  const navigate = useNavigate();
  // console.log('setLang:::', setLang);
  // setLang && setLang(LANG_NAME.EN);

  const items = [
    {
      key: '1',
      label: (
        <a target="_blank" rel="noopener noreferrer" href="https://www.antgroup.com">
          1st menu
        </a>
      ),
    },
    {
      key: '2',
      label: (
        <a target="_blank" rel="noopener noreferrer" href="https://www.aliyun.com">
          2nd menu
        </a>
      ),
    },
    {
      key: '3',
      label: <a onClick={() => navigate('/login')}>登出</a>,
    },
  ];

  return (
    <div className={style['header-wrapper']}>
      <div className={style['logo']} onClick={() => navigate('/home')}>
        <img src="https://nageoffer.com/img/logo3.png" alt="" style={{ cursor: 'pointer' }} />
      </div>
      <div className={style['edit-container']}>
        <Row gutter={[16, 16]}>
          <Col></Col>
          <Col>
            <Dropdown menu={{ items }} placement="bottomRight" trigger={['click']}>
              <Avatar size={30} icon={<UserOutlined />} style={{ cursor: 'pointer' }} />
            </Dropdown>
          </Col>
          <Col>
            <Button
              type="link"
              onClick={() => {
                lang === LANG_NAME.EN ? setLang && setLang(LANG_NAME.ZH) : setLang && setLang(LANG_NAME.EN);
              }}
            >
              <IconFont type="icon-qiehuanyuyan"></IconFont>
            </Button>
          </Col>
          <Col>
            <Switch
              checkedChildren={'🌞'}
              unCheckedChildren={'🌛'}
              defaultChecked={themeName === THEME_NAME.DARK}
              onChange={e => {
                setThemeName && setThemeName(e ? THEME_NAME.DARK : THEME_NAME.DEFAULT);
              }}
            ></Switch>
          </Col>
        </Row>
      </div>
    </div>
  );
};
export default HeaderChild;
