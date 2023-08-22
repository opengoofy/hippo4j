"use strict";
exports.__esModule = true;
var react_1 = require("react");
var antd_1 = require("antd");
var Login = function () {
    var form = antd_1.Form.useForm()[0];
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
        },
        loginRules: {
        // username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        // password: [{ required: true, trigger: 'blur', validator: this.validatePassword }],
        }
    };
    var validatePassword = function (rule, value, callback) {
        if (value.length < 6) {
            callback(new Error('The password can not be less than 6 digits'));
        }
        else if (value.length > 72) {
            callback(new Error('The password can not be greater than 72 digits'));
        }
        else {
            // callback();
        }
    };
    var onFinish = function () {
        var loginParams = {
            username: form.getFieldValue('username'),
            password: form.getFieldValue('password'),
            rememberMe: 1
        };
        data.loginForm.username = form.getFieldValue('username');
        console.log('hhhhhh', loginParams);
    };
    var showPwd = function () {
        if (data.passwordType === 'password') {
            data.passwordType = '';
        }
        else {
            data.passwordType = 'password';
        }
        // $nextTick(() => {
        //     $refs.password.focus();
        // });
    };
    return (react_1["default"].createElement("div", { className: "login-container" },
        react_1["default"].createElement(antd_1.Form, { name: "loginForm", form: form, onFinish: onFinish, style: { maxWidth: 600 } },
            react_1["default"].createElement("div", { className: "title-container" },
                react_1["default"].createElement("h3", { className: "title" }, "\u4F60\u597D\u5440")),
            react_1["default"].createElement(antd_1.Form.Item, { name: "username", label: "\u7528\u6237\u540D", rules: [{ required: true, message: 'Username is required' }] },
                react_1["default"].createElement(antd_1.Input, { placeholder: "\u7528\u6237\u540D" })),
            react_1["default"].createElement(antd_1.Form.Item, { name: "password", label: "\u5BC6\u7801", rules: [{ required: true, message: 'Street is required' }] },
                react_1["default"].createElement(antd_1.Input, { placeholder: "\u5BC6\u7801" })),
            react_1["default"].createElement(antd_1.Form.Item, { name: "submit" },
                react_1["default"].createElement(antd_1.Button, { type: "primary", htmlType: "submit", className: "login-button" }, "\u767B\u5F55")))));
};
exports["default"] = Login;
