import { STR_MAP } from './constants';

const zhTranslationMap: { [key: string]: string } = {
  [STR_MAP.DYNAMIC_THREAD_POOL]: '动态线程池',
  [STR_MAP.THREAD_POOL_MANAGE]: '线程池管理',
  [STR_MAP.PROJECT]: '项目',
  [STR_MAP.SEARCH]: '搜索',
  [STR_MAP.ADD]: '添加',
  [STR_MAP.SERIAL_NUMBER]: '序号',
  [STR_MAP.TENANTRY]: '租户',
  [STR_MAP.THREAD_POOL]: '线程池',
  [STR_MAP.CORE_THREAD]: '核心线程',
  [STR_MAP.MAXIMUM_THREAD]: '最大线程',
  [STR_MAP.QUEUE_TYPE]: '队列类型',
  [STR_MAP.QUEUE_CAPACITY]: '队列容量',
  [STR_MAP.REJECTION_STRATEGY]: '拒绝策略',
  [STR_MAP.EXECUTION_TIMEOUT]: '执行超时',
  [STR_MAP.ALARM_OR_NOT]: '是否报警',
  [STR_MAP.CREATION_TIME]: '创建时间',
  [STR_MAP.UPDATE_TIME]: '更新时间',
  [STR_MAP.EDIT]: '操作',
};

export default zhTranslationMap;
