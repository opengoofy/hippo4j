import _ from 'lodash';

// is plain object
const isPlainObject = (obj: { [key: string]: any }): boolean => {
  let proto, Ctor;
  if (!obj || typeof obj !== 'object') return false;
  proto = Object.getPrototypeOf(obj);
  if (!proto) return true;
  Ctor = Object.prototype.hasOwnProperty.call(proto, 'constructor') && proto.constructor;
  return typeof Ctor === 'function' && Ctor === Object; // insure is new by Object or {}
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

export { isPlainObject, isEmpty, filterEmptyField };
