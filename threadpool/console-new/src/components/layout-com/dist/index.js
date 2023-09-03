"use strict";
exports.__esModule = true;
var react_1 = require("react");
var styled_components_1 = require("styled-components");
var antd_1 = require("antd");
var react_router_dom_1 = require("react-router-dom");
var home_1 = require("@/page/home");
var about_1 = require("@/page/about");
var login_1 = require("@/page/login");
var index_module_less_1 = require("./index.module.less");
var table_1 = require("../table");
var search_1 = require("@/page/search");
var icons_1 = require("@ant-design/icons");
var themeContext_1 = require("@/context/themeContext");
var Header = antd_1.Layout.Header, Sider = antd_1.Layout.Sider, Content = antd_1.Layout.Content;
var items = [
    {
        label: react_1["default"].createElement("a", { href: "/about" }, "Navigation One"),
        key: 'mail',
        icon: react_1["default"].createElement(icons_1.MailOutlined, null)
    },
    {
        label: 'Navigation Two',
        key: 'app',
        icon: react_1["default"].createElement(icons_1.AppstoreOutlined, null),
        disabled: true
    },
    {
        label: react_1["default"].createElement("a", { href: "/about" }, "Navigation One"),
        key: 'app',
        icon: react_1["default"].createElement(icons_1.AppstoreOutlined, null)
    },
    {
        label: 'Navigation Three - Submenu',
        key: 'SubMenu',
        icon: react_1["default"].createElement(icons_1.SettingOutlined, null),
        children: [
            {
                type: 'group',
                label: 'Item 1',
                children: [
                    {
                        label: 'Option 1',
                        key: 'setting:1'
                    },
                    {
                        label: 'Option 2',
                        key: 'setting:2'
                    },
                ]
            },
            {
                type: 'group',
                label: 'Item 2',
                children: [
                    {
                        label: 'Option 3',
                        key: 'setting:3'
                    },
                    {
                        label: 'Option 4',
                        key: 'setting:4'
                    },
                ]
            },
        ]
    },
    {
        label: (react_1["default"].createElement("a", { href: "https://ant.design", target: "_blank", rel: "noopener noreferrer" }, "Navigation Four - Link")),
        key: 'alipay'
    },
];
var LayoutCom = function () {
    var myThemes = react_1.useContext(styled_components_1.ThemeContext);
    var _a = react_1.useContext(themeContext_1.MyThemeContext), themeName = _a.themeName, setThemeName = _a.setThemeName;
    var _b = react_1.useState('mail'), current = _b[0], setCurrent = _b[1];
    var onClick = function (e) {
        console.log('click ', e);
        setCurrent(e.key);
    };
    var _c = react_1.useState(false), isDark = _c[0], setIsDark = _c[1];
    react_1.useEffect(function () {
        isDark ? setThemeName(themeContext_1.THEME_NAME.DARK) : setThemeName(themeContext_1.THEME_NAME.DEFAULT);
    }, [isDark, setThemeName]);
    return (react_1["default"].createElement("main", { className: index_module_less_1["default"].container, style: { backgroundColor: myThemes.backgroundColor.bg1 } },
        react_1["default"].createElement(Header, { className: index_module_less_1["default"].header, style: { backgroundColor: myThemes.backgroundColor.bg1 } },
            react_1["default"].createElement(antd_1.Button, { onClick: function () { return setIsDark(function (pre) { return !pre; }); } }, "\u5207\u6362\u4E3B\u9898")),
        react_1["default"].createElement(antd_1.Layout, null,
            react_1["default"].createElement(Sider, { className: index_module_less_1["default"].sider, style: { backgroundColor: myThemes.backgroundColor.bg1 } },
                react_1["default"].createElement(antd_1.Menu, { onClick: onClick, selectedKeys: [current], mode: "inline", items: items })),
            react_1["default"].createElement(Content, { className: index_module_less_1["default"].content },
                react_1["default"].createElement(react_router_dom_1.Routes, null,
                    react_1["default"].createElement(react_router_dom_1.Route, { path: "/Search", Component: search_1["default"] }),
                    react_1["default"].createElement(react_router_dom_1.Route, { path: "/Table", Component: table_1["default"] }),
                    react_1["default"].createElement(react_router_dom_1.Route, { path: "/Login", Component: login_1["default"] }),
                    react_1["default"].createElement(react_router_dom_1.Route, { path: "/home", Component: home_1["default"] }),
                    react_1["default"].createElement(react_router_dom_1.Route, { path: "/about", Component: about_1["default"] }))))));
};
exports["default"] = LayoutCom;
