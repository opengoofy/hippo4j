"use strict";
exports.__esModule = true;
var react_1 = require("react");
var client_1 = require("react-dom/client");
var App_1 = require("./App");
var react_router_dom_1 = require("react-router-dom");
var themeContext_1 = require("./context/themeContext");
var theme_com_1 = require("./components/theme-com");
require("antd/dist/reset.css");
var root = client_1["default"].createRoot(document.getElementById('root'));
root.render(react_1["default"].createElement(react_1["default"].StrictMode, null,
    react_1["default"].createElement(react_router_dom_1.BrowserRouter, null,
        react_1["default"].createElement(themeContext_1.ThemeStore, null,
            react_1["default"].createElement(theme_com_1["default"], null,
                react_1["default"].createElement(App_1["default"], null))))));
