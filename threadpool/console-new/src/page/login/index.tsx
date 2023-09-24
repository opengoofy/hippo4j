import { useCallback, useMemo, useState } from 'react';
import { Typography, Tabs, TabsProps, Form, Input, Checkbox, Button, message } from 'antd';
import service from './service';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import style from './index.module.less';
import { useRequest } from 'ahooks';
import { setToken } from '@/utils';
import { useNavigate } from 'react-router-dom';
import { STR_MAP } from '@/config/i18n/locales/constants';
import { useTranslation } from 'react-i18next';

const { Paragraph } = Typography;

enum TABS_KEY {
  LOGIN = 'login',
  PHONE = 'phoneLogin',
}

const Login = () => {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { validateFields } = form;
  const [remenberMe, setRemenberMe] = useState(0);
  const { t } = useTranslation();

  const { run, loading } = useRequest(service.fetchLogin, {
    manual: true,
    onSuccess: res => {
      if (res) {
        message.success(t(STR_MAP.LOGIN_SUCCESSFUL));
        navigate('/thread-poll/index');
        setToken(res?.data);
      }
    },
  });

  const handleLogin = useCallback(() => {
    validateFields()
      .then(values => {
        console.log('value:::', values, remenberMe);
        run({
          password: '1BsL68bUgS52alKirqFprU1QfWJyPFlb3dA2AzEMc6kMTpTHN1doEN4=',
          rememberMe: 1,
          tag: 'lw4xNmj6QuamOFsy',
          username: 'baoxinyi_user',
        });
      })
      .catch(err => console.log('err:::', err));
  }, [remenberMe, validateFields, run]);

  const formNode = useMemo(
    () => (
      <Form form={form}>
        <Form.Item
          name="username"
          initialValue={'baoxinyi_user'}
          rules={[
            {
              required: true,
              message: t(STR_MAP.USER_INPUT_MESSAGE),
            },
          ]}
        >
          <Input
            placeholder={t(STR_MAP.USER_INPUT_MESSAGE)}
            prefix={<UserOutlined className={'prefixIcon'} />}
            size="large"
            allowClear
          ></Input>
        </Form.Item>
        <Form.Item
          name="password"
          initialValue={'baoxinyi_user'}
          rules={[
            {
              required: true,
              message: t(STR_MAP.PASSWORD_INPUT_MESSAGE),
            },
          ]}
        >
          <Input.Password
            placeholder={t(STR_MAP.PASSWORD_INPUT_MESSAGE)}
            prefix={<LockOutlined className={'prefixIcon'} />}
            size="large"
            allowClear
          ></Input.Password>
        </Form.Item>
        <Form.Item name="rememberMe">
          <div className={style['login-edit']}>
            <Checkbox
              value={1}
              checked
              onChange={e => {
                setRemenberMe(e.target.checked ? 1 : 0);
              }}
            >
              {t(STR_MAP.REMERBER_PASSWORD)}
            </Checkbox>
            <a>{t(STR_MAP.FORGOT_PASSWORD)}</a>
          </div>
        </Form.Item>
        <Form.Item>
          <Button
            htmlType="submit"
            type="primary"
            style={{ width: '100%' }}
            size="large"
            onClick={handleLogin}
            loading={loading}
          >
            {t(STR_MAP.LOGIN)}
          </Button>
        </Form.Item>
      </Form>
    ),
    [form, loading, handleLogin, t]
  );

  const items: TabsProps['items'] = [
    {
      key: TABS_KEY.LOGIN,
      label: t(STR_MAP.ACCOUNT_PASSWORD_LOGIN),
      children: formNode,
    },
    {
      key: TABS_KEY.PHONE,
      label: t(STR_MAP.PHONE_LOGIN),
      children: formNode,
    },
  ];
  return (
    <div className={style['login-wrapper']}>
      <div className={style['login-bgi']}></div>
      <div className={style['login-form-wrapper']}>
        <div className={style['img-wrapper']}>
          <img src="https://nageoffer.com/img/logo3.png" alt="" />
        </div>
        <Paragraph className={style['tip']}>{t(STR_MAP.GLOBAL_TITLE)}</Paragraph>
        <div className={style['form-content']}>
          <Tabs centered defaultActiveKey={TABS_KEY.LOGIN} items={items} />
        </div>
      </div>
    </div>
  );
};
export default Login;
