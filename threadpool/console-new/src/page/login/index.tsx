import { useCallback, useMemo, useState } from 'react';
import { Typography, Tabs, TabsProps, Form, Input, Checkbox, Button, message } from 'antd';
import service from './service';
import { LockOutlined, UserOutlined } from '@ant-design/icons';
import style from './index.module.less';
import { useRequest } from 'ahooks';
import { setToken } from '@/utils';
import { useNavigate } from 'react-router-dom';

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

  const { run, loading } = useRequest(service.fetchLogin, {
    manual: true,
    onSuccess: res => {
      if (res) {
        message.success('登陆成功');
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
              message: '请输入用户名!',
            },
          ]}
        >
          <Input
            placeholder="用户名"
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
              message: '请输入密码！',
            },
          ]}
        >
          <Input.Password
            placeholder="密码"
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
              记住密码
            </Checkbox>
            <Button type="link">忘记密码</Button>
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
            登陆
          </Button>
        </Form.Item>
      </Form>
    ),
    [form, loading, handleLogin]
  );

  const items: TabsProps['items'] = [
    {
      key: TABS_KEY.LOGIN,
      label: '账号密码登陆',
      children: formNode,
    },
    {
      key: TABS_KEY.PHONE,
      label: '手机号登陆',
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
        <Paragraph className={style['tip']}>全球最好用的线程池管理工具</Paragraph>
        <div className={style['form-content']}>
          <Tabs centered defaultActiveKey={TABS_KEY.LOGIN} items={items} />
        </div>
      </div>
    </div>
  );
};
export default Login;
