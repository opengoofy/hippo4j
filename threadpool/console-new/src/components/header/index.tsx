import React, { useContext } from 'react';
import style from './index.module.less';
import { UserOutlined } from '@ant-design/icons';
import { Avatar, Button, Col, Dropdown, Row, Switch, Select, Space } from 'antd';
import { getTenantList } from '../../api/tenant';
import { useThemeMode } from '@/hooks/useThemeMode';
import { MyContext } from '@/context';
import IconFont from '@/components/icon';

const HeaderChild = () => {
  const { isDark, setIsDark } = useThemeMode();
  const { lang, setLang } = useContext<any>(MyContext);
  const { setTenantInfo } = useContext<any>(MyContext);

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
      label: (
        <a target="_blank" rel="noopener noreferrer" href="https://www.luohanacademy.com">
          登出
        </a>
      ),
    },
  ];

  const tenantList = [
    {
      value: 'option1',
      key: 1,
      id: undefined,
      tenantId: '',
      tenantName: '',
      owner: '',
      tenantDesc: '',
    },
    {
      value: 'option2',
      key: 2,
      id: undefined,
      tenantId: '',
      tenantName: '',
      owner: '',
      tenantDesc: '',
    },
  ];

  const handleTenantChange = (value: string) => {
    setTenantInfo(tenantList[0]);
  };

  getTenantList().then((resolve: any) => {
    console.log('tenantList', resolve);
    // this.tenantList = resolve;
  });

  return (
    <div className={style['header-wrapper']}>
      <div className={style['logo']}>
        <img src="https://demo.knowstreaming.com/layout/assets/image/ks-logo.png" alt="" />
      </div>
      <div className={style['edit-container']}>
        <Row gutter={[16, 16]}>
          <Col>
            <Space wrap>
              <Select
                defaultValue={tenantList[0].value}
                style={{ width: 120 }}
                onChange={handleTenantChange}
                options={tenantList.map(tenant => ({ label: tenant.value, value: tenant.value }))}
              />
            </Space>
          </Col>
          <Col>
            <Dropdown menu={{ items }} placement="bottomRight" trigger={['click']}>
              <Avatar size={30} icon={<UserOutlined />} style={{ cursor: 'pointer' }} />
            </Dropdown>
          </Col>
          <Col>
            {/* <Tag
              onClick={() => {
                lang === 'zh' ? setLang('en') : setLang('zh');
              }}
              style={{ cursor: 'pointer' }}
            >
              {lang === 'zh' ? 'zh' : 'en'}
            </Tag> */}
            <Button
              type="link"
              onClick={() => {
                lang === 'zh' ? setLang('en') : setLang('zh');
              }}
            >
              <IconFont type="icon-qiehuanyuyan"></IconFont>
            </Button>
          </Col>
          <Col>
            <Switch
              checkedChildren={'🌞'}
              unCheckedChildren={'🌛'}
              defaultChecked={isDark}
              onChange={e => {
                setIsDark(e);
              }}
            ></Switch>
          </Col>
        </Row>
      </div>
    </div>
  );
};
export default HeaderChild;
