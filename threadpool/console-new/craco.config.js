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
        modifyLessRule: lessRule => {
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
  // style: {
  //   modules: {
  //     localIdentName: '[local]_[hash:base64:5]', // 可以自定义你的类名生成规则
  //   },
  // },
  webpack: {
    alias: {
      '@': resolve('src'),
      '@i18': resolve('public/locales'),
    },
  },
  devServer: {
    port: 3001,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
  },
};
