const devBaseUrl = 'http://console.hippo4j.cn:6691/hippo4j/v1/cs'
const proBaseUrl = 'http://console.hippo4j.cn:6691/hippo4j/v1/cs  '
export const BASE_URL = process.env.NODE_ENV === 'development' ? devBaseUrl : proBaseUrl
export const TIMEOUT = 5000
