import React, { useEffect, useState, useContext } from 'react';
import { DefaultTheme, ThemeContext } from 'styled-components';

import { Form, Tooltip, Input, Button } from 'antd';
import { Routes, Route, Link } from 'react-router-dom';
import style from './index.module.less';

import { AppstoreOutlined, MailOutlined, SettingOutlined } from '@ant-design/icons';
import { THEME_NAME, MyThemeContext } from '@/context/themeContext';

const Login = () => {
  const [form] = Form.useForm();
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
    },
    loginRules: {
      // username: [{ required: true, trigger: 'blur', validator: validateUsername }],
      // password: [{ required: true, trigger: 'blur', validator: this.validatePassword }],
    },
  };

  const validatePassword = (rule: any, value: string | any[], callback: (arg0: Error | undefined) => void) => {
    if (value.length < 6) {
      callback(new Error('The password can not be less than 6 digits'));
    } else if (value.length > 72) {
      callback(new Error('The password can not be greater than 72 digits'));
    } else {
      // callback();
    }
  };

  const onFinish = () => {
    let loginParams = {
      username: form.getFieldValue('username'),
      password: form.getFieldValue('password'),
      rememberMe: 1,
    };
    data.loginForm.username = form.getFieldValue('username');
    console.log('hhhhhh', loginParams);
  };
  const showPwd = () => {
    if (data.passwordType === 'password') {
      data.passwordType = '';
    } else {
      data.passwordType = 'password';
    }
    // $nextTick(() => {
    //     $refs.password.focus();
    // });
  };
  return (
    <div className="login-container">
      <Form name="loginForm" form={form} onFinish={onFinish}
      style={{ maxWidth: 600 }}>
        <div className="title-container">
          <h3 className="title">你好呀</h3>
          {/* <h3 className="title">{{ $t('system.login') }}</h3> */}
        </div>
        <Form.Item name="username" label="用户名"
          rules={[{ required: true, message: 'Username is required' }]}
        >
          <Input placeholder="用户名" />
        </Form.Item>
        <Form.Item name="password" label="密码"
          rules={[{ required: true, message: 'Street is required' }]}
        >
          <Input placeholder="密码" />
        </Form.Item>
        <Form.Item name="submit">
          <Button type="primary" htmlType="submit" className="login-button">登录</Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default Login;
