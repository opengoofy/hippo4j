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
  webpack: {
    alias: {
      '@': resolve('src'),
    },
  },
  devServer: {
    port: 3000,
    headers: {
      'Access-Control-Allow-Origin': '*',
    },
    proxy: {
      '/hippo4j/v1/cs': {
        target: 'http://console.hippo4j.cn:6691/hippo4j/v1/cs',
        pathRewrite: { '^/hippo4j/v1/cs': '' },
        changeOrigin: true,
        secure: false,
        onProxyReq: proxyReq => {
          console.log(`Proxying request to: ${proxyReq.path}`);
        },
        onProxyRes: proxyRes => {
          console.log(`Received response with status: ${proxyRes.statusCode}`);
        },
      },
    },
  },
};
