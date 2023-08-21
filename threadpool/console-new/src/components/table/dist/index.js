"use strict";
exports.__esModule = true;
var react_1 = require("react");
var antd_1 = require("antd");
var columns = [
    {
        title: 'Name',
        dataIndex: 'name',
        key: 'name',
        render: function (text) { return react_1["default"].createElement("a", null, text); }
    },
    {
        title: 'Age',
        dataIndex: 'age',
        key: 'age'
    },
    {
        title: 'Address',
        dataIndex: 'address',
        key: 'address'
    },
    {
        title: 'Tags',
        key: 'tags',
        dataIndex: 'tags',
        render: function (_, _a) {
            var tags = _a.tags;
            return (react_1["default"].createElement(react_1["default"].Fragment, null, tags.map(function (tag) {
                var color = tag.length > 5 ? 'geekblue' : 'green';
                if (tag === 'loser') {
                    color = 'volcano';
                }
                return (react_1["default"].createElement(antd_1.Tag, { color: color, key: tag }, tag.toUpperCase()));
            })));
        }
    },
    {
        title: 'Action',
        key: 'action',
        render: function (_, record) { return (react_1["default"].createElement(antd_1.Space, { size: "middle" },
            react_1["default"].createElement("a", null,
                "Invite ",
                record.name),
            react_1["default"].createElement("a", null, "Delete"))); }
    },
];
var data = [
    {
        key: '1',
        name: 'John Brown',
        age: 32,
        address: 'New York No. 1 Lake Park',
        tags: ['nice', 'developer']
    },
    {
        key: '2',
        name: 'Jim Green',
        age: 42,
        address: 'London No. 1 Lake Park',
        tags: ['loser']
    },
    {
        key: '3',
        name: 'Joe Black',
        age: 32,
        address: 'Sydney No. 1 Lake Park',
        tags: ['cool', 'teacher']
    },
];
var TableBox = function () { return react_1["default"].createElement(antd_1.Table, { columns: columns, dataSource: data }); };
exports["default"] = TableBox;
