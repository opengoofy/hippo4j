"use strict";
exports.__esModule = true;
var react_1 = require("react");
var styled_components_1 = require("styled-components");
var antd_1 = require("antd");
var useThemeMode_1 = require("@/hooks/useThemeMode");
var index_module_less_1 = require("./index.module.less");
var Header = antd_1.Layout.Header, Sider = antd_1.Layout.Sider, Content = antd_1.Layout.Content;
var LayoutCom = function (props) {
    var sideMenuList = props.sideMenuList, children = props.children, _a = props.isSider, isSider = _a === void 0 ? true : _a;
    var myThemes = react_1.useContext(styled_components_1.ThemeContext);
    var _b = react_1.useState('mail'), current = _b[0], setCurrent = _b[1];
    var onClick = function (e) {
        setCurrent(e.key);
    };
    var setIsDark = useThemeMode_1["default"]()[0];
    return (React.createElement("main", { className: index_module_less_1["default"].container, style: { backgroundColor: myThemes.backgroundColor.bg1 } },
        React.createElement(Header, { className: index_module_less_1["default"].header, style: { backgroundColor: myThemes.backgroundColor.bg2 } },
            React.createElement(antd_1.Button, { onClick: function () { return setIsDark(function (pre) { return !pre; }); } }, "\u5207\u6362\u4E3B\u9898")),
        React.createElement(antd_1.Layout, { style: { backgroundColor: myThemes.backgroundColor.bg1, height: 'calc(100vh - 64px)' } },
            isSider && (React.createElement(Sider, { className: index_module_less_1["default"].sider, style: { backgroundColor: myThemes.backgroundColor.bg1 } },
                React.createElement(antd_1.Menu, { onClick: onClick, selectedKeys: [current], mode: "inline", items: sideMenuList }))),
            React.createElement(Content, { className: index_module_less_1["default"].content }, children))));
};
exports["default"] = LayoutCom;
