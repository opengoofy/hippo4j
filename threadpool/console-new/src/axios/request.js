import axios from 'axios';
import { BASE_URL, TIMEOUT } from './config';
const instance = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT,
});

instance.interceptors.request.use(
  config => {
    config.data = JSON.stringify(config.data);
    config.headers = {
      'Content-Type': 'application/json',
    }
    return config
  }, err => {
    return Promise.reject(err)
  }
);

instance.interceptors.response.use(
  res => {
    if (res.code === 'A000004') {
      removeToken()
      resetRouter()
      alert(res.message)
      document.location.href = 'index.html'
    } else if (res.code !== '20000' && res.code !== '0' && res.code !== '200') {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === '50008' || res.code === '50012' || res.code === '50014') {
        // to re-login
        MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
          confirmButtonText: 'Re-Login',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      console.log(res)
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      const { data } = response
      const { code } = data
      // 状态码为0||200表示api成功
      if (code === '0') {
        const { data: res } = data
        return res
      } else if (code === '200') {
        return data
      } else {
        // 返回数据
        return res
      }
    }
    return response
  }, err => {
    console.log('err' + err) // for debug
      Message({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
      })
      return Promise.reject(err)
  }
);

/**
 * 封装get
 * @param {String} url 
 * @param {String} param 
 * @returns 
 */
export function get(url, param = {}) {
  return new Promise(
    (resolve, reject) => {
      axios.get(url, {
        params: param
      }).then((response) => {
        console.log("get error: url, params, state",url, params, response.data)
        resolve(response.data)
      }).catch(err => {
        msg(err)
        reject(err)
      })
    }
  );
}

/**
 * 
 * 封装post
 * @param {String} url 
 * @param {Object} param 
 * @returns {Promise}
 */
export function post(url, param) {
  return new Promise(
    (resolve, reject) => {
      axios.post(url, param).then(response => {
        resolve(response.data)
      }, err => {
        reject(err)
      })
    }
  );
}
export default function (fecth, url, param) {
  return new Promise(
    (resolve, reject) => {
      switch(fecth) {
        case "get":
          console.log("a get request, url:", url)
          get(url, param)
          .then(response => {
            resolve(response)
          }).catch(err => {
            console.log("GET request error, err:", err)
            reject(err)
          })
          break
        case "post":
          post(url, param)
          .then(response => {
            resolve(response)
          }).catch(err => {
            console.log("POST request error, err:", err)
            reject(err)
          })
          break
        default:
          break
      }
    }
  );
}

function msg(err) {
  if (err && err.response) {
    switch (err.response.status) {
      case 400:
        alert(err.response.data.error.details);
        break;
      case 401:
        alert("未授权，请登录");
        break;

      case 403:
        alert("拒绝访问");
        break;

      case 404:
        alert("请求地址出错");
        break;

      case 408:
        alert("请求超时");
        break;

      case 500:
        alert("服务器内部错误");
        break;

      case 501:
        alert("服务未实现");
        break;

      case 502:
        alert("网关错误");
        break;

      case 503:
        alert("服务不可用");
        break;

      case 504:
        alert("网关超时");
        break;

      case 505:
        alert("HTTP版本不受支持");
        break;
      default:
    }
  }
}