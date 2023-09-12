"use strict";
exports.__esModule = true;
var layout_com_1 = require("./components/layout-com");
var react_router_dom_1 = require("react-router-dom");
var route_1 = require("./route");
var login_1 = require("@/page/login");
var icons_1 = require("@ant-design/icons");
var sideMenuList = [
    {
        label: React.createElement("a", { href: "/about" }, "about"),
        key: 'mail',
        icon: React.createElement(icons_1.MailOutlined, null)
    },
    {
        label: React.createElement("a", { href: "/home" }, "\u4E3B\u9875"),
        key: 'app',
        icon: React.createElement(icons_1.AppstoreOutlined, null)
    },
];
var App = function () {
    return (React.createElement(layout_com_1["default"], { sideMenuList: sideMenuList, isSider: false },
        React.createElement(react_router_dom_1.Routes, null,
            React.createElement(react_router_dom_1.Route, { path: "/Login", Component: login_1["default"] }),
            route_1["default"].map(function (item) { return (React.createElement(react_router_dom_1.Route, { key: item.path, path: item.path, Component: item.component })); }))));
};
exports["default"] = App;
