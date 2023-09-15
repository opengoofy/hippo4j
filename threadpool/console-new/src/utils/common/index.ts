// is plain object
const isPlainObject = (obj: { [key: string]: any }): boolean => {
  let proto, Ctor;
  if (!obj || typeof obj !== 'object') return false;
  proto = Object.getPrototypeOf(obj);
  if (!proto) return true;
  Ctor = Object.prototype.hasOwnProperty.call(proto, 'constructor') && proto.constructor;
  return typeof Ctor === 'function' && Ctor === Object; // insure is new by Object or {}
};

export { isPlainObject };
