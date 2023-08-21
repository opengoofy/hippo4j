"use strict";
exports.__esModule = true;
var react_1 = require("react");
var icons_1 = require("@ant-design/icons");
var antd_1 = require("antd");
var Search = function () {
    var _a = react_1["default"].useState(false), passwordVisible = _a[0], setPasswordVisible = _a[1];
    return (react_1["default"].createElement(antd_1.Space, { direction: "vertical" },
        react_1["default"].createElement(antd_1.Input.Password, { placeholder: "input password" }),
        react_1["default"].createElement(antd_1.Input.Password, { placeholder: "input password", iconRender: function (visible) { return (visible ? react_1["default"].createElement(icons_1.EyeTwoTone, null) : react_1["default"].createElement(icons_1.EyeInvisibleOutlined, null)); } }),
        react_1["default"].createElement(antd_1.Space, { direction: "horizontal" },
            react_1["default"].createElement(antd_1.Input.Password, { placeholder: "input password", visibilityToggle: { visible: passwordVisible, onVisibleChange: setPasswordVisible } }),
            react_1["default"].createElement(antd_1.Button, { style: { width: 80 }, onClick: function () { return setPasswordVisible(function (prevState) { return !prevState; }); } }, passwordVisible ? 'Hide' : 'Show'),
            react_1["default"].createElement(antd_1.Button, { style: { width: 80 }, onClick: function () { return setPasswordVisible(function (prevState) { return !prevState; }); } }, passwordVisible ? 'Hide' : 'Show'))));
};
exports["default"] = Search;
