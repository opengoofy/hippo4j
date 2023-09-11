const proxy = require('http-proxy-middleware')

module.exports = function(app){
	app.use(
		proxy.createProxyMiddleware('/hippo4j/v1/cs',{ 
			target:'http://console.hippo4j.cn:6691/hippo4j/v1/cs', 
			changeOrigin:true,
      secure: false,
			pathRewrite:{'^/hippo4j/v1/cs': ''}
		}),
	)
}
