import { Form, Input, Button } from 'antd';
import React, { useContext } from 'react';
import { getLogin } from './service';
import { MyContext } from '@/context';

const Login = (props: any) => {
  const { setUserInfo } = useContext<any>(MyContext);

  const data = {
    passwordType: 'password',
    capsTooltip: false,
    loading: false,
    showDialog: false,
    redirect: undefined,
    otherQuery: {},
    loginForm: {
      username: '',
      password: '',
      rememberMe: 1,
    },
    // loginRules: {
    //   // username: [{ required: true, trigger: 'blur', validator: validateUsername }],
    //   // password: [{ required: true, trigger: 'blur', validator: this.validatePassword }],
    // },
  };

  const validatePassword = (_: any, value: any) => {
    if (value.length < 6) {
      return Promise.reject(new Error('The password can not be less than 6 digits'));
    } else if (value.length > 72) {
      return Promise.reject(new Error('The password can not be greater than 72 digits'));
    }
    return Promise.resolve();
  };

  const [form] = Form.useForm();
  const onFinish = () => {
    data.loginForm.username = form.getFieldValue('username');
    data.loginForm.password = form.getFieldValue('password');
    data.loginForm.rememberMe = 1;

    data.loading = true;
    getLogin(data.loginForm)
      .then((resolve: any) => {
        console.log(resolve);
        setUserInfo(data.loginForm);
        //登录成功后将当前登录用户写入cookie
        // this.$cookie.set('userName', this.loginForm.username);
        // console.log('success submit.');
        // this.$router.push({ path: this.redirect || '/', query: this.otherQuery });
        data.loading = false;
      })
      .catch((e: any) => {
        console.log('login error.', e);
        data.loading = false;
      });
  };
  return (
    <div className="login-container">
      <Form name="loginForm" form={form} onFinish={onFinish} style={{ maxWidth: 600 }}>
        <div className="title-container">
          <h3 className="title">你好呀</h3>
          {/* <h3 className="title">{{ $t('system.login') }}</h3> */}
        </div>
        <Form.Item name="username" label="用户名" rules={[{ required: true, message: 'Username is required' }]}>
          <Input placeholder="用户名" />
        </Form.Item>
        <Form.Item
          name="password"
          label="密码"
          rules={[{ validator: validatePassword }, { required: true, message: 'Street is required' }]}
        >
          <Input placeholder="密码" />
        </Form.Item>
        <Form.Item name="submit">
          <Button type="primary" htmlType="submit" className="login-button">
            登录
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};
export default Login;
