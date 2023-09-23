import Cookie from 'js-cookie';
// import { Buffer } from 'buffer';
// import crypto from 'crypto-browserify';

// /**
//  * generate key
//  * @returns {string} key(length 16)
//  */
// export function genKey() {
//   let chars = 'abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
//   let result = '';
//   for (let i = 16; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
//   return result;
// }
// /**
//  * encode message
//  * @param msg message
//  * @param key key
//  * @returns {string} encoded message
//  */
// export function encrypt(msg: string, key: string) {
//   try {
//     let pwd = Buffer.from(key);
//     let iv = (crypto as any).randomBytes(12);
//     let cipher = (crypto as any).createCipheriv('aes-128-gcm', pwd, iv);
//     let enc = cipher.update(msg, 'utf8', 'base64');
//     enc += cipher.final('base64');
//     let tags = cipher.getAuthTag();
//     enc = Buffer.from(enc, 'base64') as any;
//     let totalLength = iv.length + enc.length + tags.length;
//     let bufferMsg = Buffer.concat([iv, enc as any, tags], totalLength);
//     return bufferMsg.toString('base64');
//   } catch (e) {
//     console.log('Encrypt is error', e);
//     return null;
//   }
// }
import _ from 'lodash';

// is plain object
const TokenKey = 'Admin-Token';
const isPlainObject = (obj: { [key: string]: any }): boolean => {
  let proto, Ctor;
  if (!obj || typeof obj !== 'object') return false;
  proto = Object.getPrototypeOf(obj);
  if (!proto) return true;
  Ctor = Object.prototype.hasOwnProperty.call(proto, 'constructor') && proto.constructor;
  return typeof Ctor === 'function' && Ctor === Object; // insure is new by Object or {}
};

const setToken = (token: string) => {
  Cookie.set(TokenKey, token);
};

/**
 * @description 忽略 object 中 value 为空的元素
 * @param obj
 * @returns
 */
const filterEmptyField = (obj: { [key: string]: any }) => {
  return _.omitBy(obj, isEmpty);
};

/**
 * @description 判断基本类型是否为空
 * @param value
 * @returns
 */
const isNilValue = (value: any) => {
  return value === undefined || value === '' || value === null || Number.isNaN(value);
};

/**
 * @description 判断对值「基本类型/对象」是否为空
 * @param value
 * @returns
 */
const isEmpty = (value: any) => {
  return typeof value === 'object' ? _.isEmpty(value) : isNilValue(value);
};

export { isPlainObject, isEmpty, filterEmptyField, setToken };
