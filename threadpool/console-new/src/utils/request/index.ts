import { getToken, isPlainObject } from '../common';
import { notification, message } from 'antd';
import Qs from 'qs';

type HttpMethods = 'POST' | 'post' | 'GET' | 'get' | 'DELETE' | 'delete' | 'PUT' | 'put';

interface HeaderConfig extends Record<string, any> {
  Accept?: string;
  'Content-Type'?: string;
}

interface RequestOptions {
  headers?: HeaderConfig;
  method?: HttpMethods;
  params?: { [key: string]: any } | null;
  body?: { [key: string]: any } | null;
  timeout?: number;
  credentials?: boolean;
  moda?: 'cors' | 'same-origin';
  cache?: 'no-cache' | 'default' | 'force-cache';
  customize?: boolean;
  responseType?: 'TEXT' | 'JSON' | 'BLOB' | 'ARRAYBUFFER';
}

type Response<T = any> = {
  success: boolean;
  data: T;
  module?: T;
  msg?: string;
  status?: number;
  message?: string;
  code?: string | number;
};

let baseURL = '';

const inital: RequestOptions = {
  method: 'GET',
  params: null,
  body: null,
  headers: {
    'Content-Type': 'application/json',
  },
  // headers,
  credentials: true,
  responseType: 'JSON',
  cache: 'no-cache',
};
const env = process.env.NODE_ENV || 'development';

enum IENV_ENUM {
  DEVELOPMENT = 'development',
  TEST = 'test',
  PRODUCTION = 'production',
}

switch (env) {
  case IENV_ENUM.DEVELOPMENT:
    baseURL = '';
    break;
  case IENV_ENUM.TEST:
    baseURL = '';
    break;
  case IENV_ENUM.PRODUCTION:
    baseURL = '';
    break;
}

const codeMessage: { [key: string]: string } = {
  '200': '请求已成功被服务器处理，并返回了请求的内容。',
  '201': '请求已成功，并且服务器创建了一个新的资源作为响应。',
  '202': '一个请求已进入后台排队',
  '204': '请求已成功处理，但响应中没有返回任何内容。',
  '400': '客户端发送的请求有错误，服务器无法处理。',
  '401': '客户端需要提供有效的身份验证信息，以便访问受保护的资源。',
  '403': '服务器理解了请求，但拒绝执行该请求。',
  '404': '服务器未找到请求的资源。',
  '500': '服务器在处理请求时发生了未知的错误。',
};

function request<T>(url: string, config: RequestOptions): Promise<Response<T>> {
  if (config === null || typeof config !== 'object') {
    config = {};
  }
  if (config.headers && isPlainObject(config.headers)) {
    config.headers = Object.assign({}, inital.headers, config.headers);
    if (!config.headers?.Authorization) {
      config.headers.Authorization = getToken();
    }
  } else {
    config.headers = { Authorization: getToken(), 'Content-Type': 'application/json' };
  }

  let { method, params, body, headers, credentials, responseType } = Object.assign({}, inital, config) as any;
  if (typeof url !== 'string') throw new TypeError('url is not an string');
  if (!/^http(s?):\/\//i.test(url)) url = baseURL + url;
  if (params !== null) {
    if (isPlainObject(params)) {
      params = Qs.stringify(params);
    }
    url += `${url.includes('?') ? '&' : '?'}${params}`;
  }
  if (body !== null) {
    if (isPlainObject(body)) {
      let contentType = headers['Content-Type'] || 'application/json';
      if (contentType.includes('urlencoded')) body = Qs.stringify(body);
      if (contentType.includes('json')) body = JSON.stringify(body);
    }
  }
  credentials = credentials ? 'include' : 'same-origin';
  method = method.toUpperCase();
  responseType = responseType.toUpperCase();
  config = {
    ...config,
    method,
    credentials,
    responseType,
  };
  if (/^(POST|PUT|PATCH)$/i.test(method)) {
    config.body = body;
  } else {
    config.body = null;
  }

  return fetch(url, config as any).then(function onfulfilled(response) {
    let { status, statusText } = response;
    if (status >= 200 && status < 400) {
      let result;
      switch (responseType) {
        case 'TEXT':
          result = response.text();
          break;
        case 'JSON':
          result = response.json();
          break;
        case 'BLOB':
          result = response.blob();
          break;
        case 'ARRAYBUFFER':
          result = response.arrayBuffer();
          break;
      }
      result?.then(res => {
        if (!res?.success) {
          notification.error({
            message: res.message,
          });
        }
      });
      return result;
    }
    let tip = codeMessage[String(status)];
    notification.error({
      message: tip,
    });
    return Promise.reject({
      code: 'STATUS ERROR',
      status,
      statusText,
    }).catch(function onrejected(reason) {
      if (!navigator.onLine) {
        message.error('好像断网了');
      }
      return Promise.reject(reason);
    });
  });
}

export default request;
