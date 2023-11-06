import Cookie from 'js-cookie';
import { Buffer } from 'buffer';
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
  localStorage.setItem(TokenKey, token);
  Cookie.set(TokenKey, token);
};

const removeToken = () => {
  localStorage.removeItem(TokenKey);
  Cookie.remove(TokenKey);
};

const getToken = () => {
  return localStorage.getItem(TokenKey) || Cookie.get(TokenKey);
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

function genKey() {
  let chars = 'abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  let result = '';
  for (let i = 16; i > 0; --i) result += chars[Math.floor(Math.random() * chars.length)];
  return result;
}

async function encrypt(msg: string, key: any) {
  try {
    let pwd = Buffer.from(key);
    const cryptoKey = await window.crypto.subtle.importKey('raw', pwd, { name: 'AES-GCM' }, false, ['encrypt']);
    const iv = window.crypto.getRandomValues(new Uint8Array(12));
    const encodedMsg = new TextEncoder().encode(msg);
    const encryptedData = await window.crypto.subtle.encrypt(
      {
        name: 'AES-GCM',
        iv: iv,
      },
      cryptoKey,
      encodedMsg
    );
    const encryptedArray = new Uint8Array(encryptedData);
    const totalLength = iv.length + encryptedArray.length;
    const combinedArray = new Uint8Array(totalLength);
    combinedArray.set(iv);
    combinedArray.set(encryptedArray, iv.length);
    return btoa(String.fromCharCode(...combinedArray));
  } catch (e) {
    return null;
  }
}

export { isPlainObject, isEmpty, filterEmptyField, setToken, removeToken, getToken, genKey, encrypt };
