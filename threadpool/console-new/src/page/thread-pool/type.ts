import { Params } from 'ahooks/lib/useAntdTable/types';

// body
export interface ThreadPoolTableBody {
  /**
   * current page
   */
  current: number;
  /**
   * project id
   */
  itemId?: string;
  /**
   * page size
   */
  size: number;
  /**
   *tenant Id
   */
  tenantId?: string;
  /**
   * thread pool ID
   */
  tpId?: string;
}

export interface Record {
  /**
   * 是否超时
   */
  allowCoreThreadTimeOut?: number;
  /**
   * 队列容量
   */
  capacity?: number;
  /**
   * 容量报警
   */
  capacityAlarm?: number;
  /**
   * 核心线程数
   */
  coreSize?: number;
  /**
   * 执行超市
   */
  executeTimeOut?: number;
  /**
   * 创建时间
   */
  gmtCreate?: string;
  /**
   * 修改时间
   */
  gmtModified?: string;
  /**
   * ID
   */
  id: string;
  /**
   * 是否报警
   */
  isAlarm?: number;
  /**
   * 项目ID
   */
  itemId?: string;
  /**
   * 空闲回收
   */
  keepAliveTime?: number;
  /**
   * 活跃度报警
   */
  livenessAlarm?: number;
  /**
   * 最大线程数
   */
  maxSize?: number;
  /**
   * 队列名称
   */
  queueName?: null;
  /**
   * 队列类型
   */
  queueType?: number;
  /**
   * 拒绝策略类型
   */
  rejectedType?: number;
  /**
   * 租户ID
   */
  tenantId?: string;
  /**
   * 线程池ID
   */
  tpId?: string;
}

export interface ThreadPoolTableRes {
  countId: null;
  current: number;
  desc: boolean;
  hitCount: boolean;
  itemId: string;
  maxLimit: null;
  optimizeCountSql: boolean;
  orders: string[];
  pages: number;
  records: Record[];
  searchCount: boolean;
  size: number;
  tenantId: string;
  total: number;
  tpId: string;
}

export interface Result {
  total: number;
  list: Record[];
}
