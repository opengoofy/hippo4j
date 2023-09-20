import React, { useContext, useEffect, useState } from 'react';
import style from './index.module.less';
import { UserOutlined } from '@ant-design/icons';
import { Avatar, Button, Col, Dropdown, Row, Switch, Select, Space } from 'antd';
import { getTenantList, updataTenant } from '../../api/tenant';
import { STR_MAP } from '../../config/i18n/locales/constants';
import { useThemeMode } from '@/hooks/useThemeMode';
import { MyContext } from '@/context';
import IconFont from '@/components/icon';
import { useSetState } from 'ahooks';
import { useTran } from '../../hooks';
import { resolve } from 'path';

const HeaderChild = () => {
  const { isDark, setIsDark } = useThemeMode();
  const { lang, setLang } = useContext<any>(MyContext);
  const { tenantInfo, setTenantInfo } = useContext<any>(MyContext);

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

  let tenantTitle = useTran(STR_MAP.CHANGE_TENANT);
  let tenantList = [
    {
      gmtCreate: '2023-09-20 11:08:37',
      gmtModified: '2023-09-20 21:34:40',
      id: 8,
      owner: 'test',
      tenantDesc: 'test',
      tenantId: 'test',
      tenantName: 'test1',
    },
    {
      gmtCreate: '2023-09-20 11:08:37',
      gmtModified: '2023-09-20 21:34:40',
      id: 8,
      owner: 'abc',
      tenantDesc: 'abc',
      tenantId: 'tesabct',
      tenantName: 'abc',
    },
  ];

  const handleTenantChange = (value: string) => {
    let formData = {
      id: tenantInfo.id,
      tenantId: tenantInfo.tenantId,
      tenantName: value,
      tenantDesc: tenantInfo.tenantDesc,
      owner: tenantInfo.owner,
    };
    updataTenant(formData).then((resolve: any) => {
      console.log('tenantList', resolve);
      setTenantInfo(formData);
    });
  };

  useEffect(() => {
    let formData = {
      current: 1,
      desc: true,
      size: 10,
      tenantId: '',
    };
    getTenantList(formData).then((resolve: any) => {
      console.log('tenantList', resolve);
      // eslint-disable-next-line react-hooks/exhaustive-deps, react-hooks/rules-of-hooks
      tenantList = useSetState(resolve.records);
    });
    console.log('use effect');
  }, [setTenantInfo, tenantList]);

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
                defaultValue={tenantList[0].tenantName}
                style={{ width: 120 }}
                onChange={handleTenantChange}
                options={tenantList.map(tenant => ({ label: tenant.tenantName, value: tenant.tenantName }))}
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
