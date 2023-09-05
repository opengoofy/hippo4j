// craco.config.js
const CracoLessPlugin = require('craco-less');
const lessModuleRegex = /\.module\.less$/;
const path = require('path');
const basePath = path.resolve(__dirname, '.');
const resolve = dir => path.resolve(basePath, dir);

module.exports = {
  plugins: [
    {
      plugin: CracoLessPlugin,
      options: {
        // less loader options
        lessLoaderOptions: {
          lessOptions: {
            // modifyVars: { "@primary-color": "#1DA57A" },
            javascriptEnabled: true,
          },
        },

        // A callback function that receives two arguments: the webpack rule,
        // and the context. You must return an updated rule object.
        modifyLessRule: (lessRule, context) => {
          lessRule.test = lessModuleRegex;
          lessRule.exclude = /node_modules|antd\.css/;
          return lessRule;
        },

        // Passing an options object to configure the css-loaders
        cssLoaderOptions: {
          modules: { localIdentName: '[local]_[hash:base64:5]' },
        },
      },
    },
  ],
  webpack: {
    alias: {
      '@': resolve('src'),
    },
  },
  devServer: {
    // 本地服务的端口号
    port: 3001,
    // 本地服务的响应头设置
    headers: {
      // 允许跨域
      'Access-Control-Allow-Origin': '*',
    },
  },
};
