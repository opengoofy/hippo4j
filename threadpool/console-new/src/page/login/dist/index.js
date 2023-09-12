"use strict";
exports.__esModule = true;
var antd_1 = require("antd");
var user_1 = require("../../API/user");
var Login = function (props) {
    var data = {
        passwordType: 'password',
        capsTooltip: false,
        loading: false,
        showDialog: false,
        redirect: undefined,
        otherQuery: {},
        loginForm: {
            username: '',
            password: ''
        }
    };
    var validatePassword = function (_, value) {
        if (value.length < 6) {
            return Promise.reject(new Error('The password can not be less than 6 digits'));
        }
        else if (value.length > 72) {
            return Promise.reject(new Error('The password can not be greater than 72 digits'));
        }
        return Promise.resolve();
    };
    var form = antd_1.Form.useForm()[0];
    var onFinish = function () {
        var loginParams = {
            username: form.getFieldValue('username'),
            password: form.getFieldValue('password'),
            // username: 'baoxinyi_admin',
            // password: 'baoxinyi_admin',
            rememberMe: 1
        };
        data.loginForm.username = form.getFieldValue('username');
        console.log('loginParams: ', loginParams);
        data.loading = true;
        user_1["default"](loginParams)
            .then(function (resolve) {
            console.log(resolve);
            //登录成功后将当前登录用户写入cookie
            // this.$cookie.set('userName', this.loginForm.username) 
            // console.log('success submit.') 
            // this.$router.push({ path: this.redirect || '/', query: this.otherQuery }) 
            data.loading = false;
        })["catch"](function (e) {
            console.log('login error.', e);
            data.loading = false;
        });
    };
    return (React.createElement("div", { className: "login-container" },
        React.createElement(antd_1.Form, { name: "loginForm", form: form, onFinish: onFinish, style: { maxWidth: 600 } },
            React.createElement("div", { className: "title-container" },
                React.createElement("h3", { className: "title" }, "\u4F60\u597D\u5440")),
            React.createElement(antd_1.Form.Item, { name: "username", label: "\u7528\u6237\u540D", rules: [{ required: true, message: 'Username is required' }] },
                React.createElement(antd_1.Input, { placeholder: "\u7528\u6237\u540D" })),
            React.createElement(antd_1.Form.Item, { name: "password", label: "\u5BC6\u7801", rules: [{ validator: validatePassword }, { required: true, message: 'Street is required' }] },
                React.createElement(antd_1.Input, { placeholder: "\u5BC6\u7801" })),
            React.createElement(antd_1.Form.Item, { name: "submit" },
                React.createElement(antd_1.Button, { type: "primary", htmlType: "submit", className: "login-button" }, "\u767B\u5F55")))));
};
exports["default"] = Login;
