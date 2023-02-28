export default {
  // 系统
  system: {
    login: '登 录',
    username: '用户名',
    password: '密码',
    logOut: '注销',
  },

  // 信息
  message: {
    requiredError: '这是必填项',
    success: '成功',
    createdSuccess: '创建成功',
    updateSuccess: '更新成功',
    deleteSuccess: '删除成功',
    deleteMessage: '此操作将删除 {name}, 是否继续?',
    selectMessage: '请选择{target}',
    NoDeletionPermissionTip: '请联系管理员删除',
    emptyWarning: "{name}不允许为空",
    queryFailure: '查询失败，请尝试刷新页面'
  },

  // 公共
  common: {
    query: '搜索',
    insert: '添加',
    operation: '操作',
    create: '创建',
    edit: '编辑',
    delete: '删除',
    detail: '查看',
    cancel: '取消',
    confirm: '确认',
    reset: '重置',
    close: '关闭',
    refresh: '刷新',
    ok: '确定',
    num: '序号',
    createTime: '创建时间',
    updateTime: '修改时间',
    hint: '提示',
    info: '详情'
  },

  // 运行报表
  report: {
    poolInstance: '线程池实例',
    user: '登录用户',
    role: '用户角色',
    tenants: '所属租户'
  },

  // 租户管理
  tenantManage: {
    tenant: '租户',
    tenantRequired: '租户（必填）',
    tenantName: '租户名称',
    owner: '负责人',
    tenantIntro: '租户简介'
  },

  // 项目管理
  projectManage: {
    item: '项目',
    itemRequired: '项目（必填）',
    itemName: '项目名称',
    owner: '负责人',
    itemIntro: '项目简介'
  },

  // 线程池管理
  threadPool: {
    threadPool: '线程池',
    threadPoolRequired: '线程池（必填）',
    coreSize: '核心线程',
    maximumSize: '最大线程',
    queueType: '队列类型',
    queueCapacity: '队列容量',
    rejectedHandler: '拒绝策略',
    executionTimeout: '执行超时',
    executionTimeoutUnit: '执行超时 （毫秒）',
    keepAliveTime: '空闲回收',
    keepAliveTimeUnit: '空闲回收（秒）',
    isTimeout: '是否超时',
    timeout: '超时',
    noTimeout: '不超时',
    isAlarm: '是否报警',
    alarm: '报警',
    noAlarm: '不报警',
    activeAlarm: '活跃报警',
    capacityAlarm: '容量报警',
    customRejectedHandler: '自定义拒绝策略',
    customRejectedHandlerTip: '请输入自定义 SPI 拒绝策略标识',
    threadsNumErrorTip: '最大线程必须大于等于核心线程'
  },


  // 线程池实例
  threadPoolInstance: {
    instanceID: '实例标识',
    active: 'Active',
    stack: '堆栈',
    basicInformation: '基础信息',
    runningState: '运行状态',
    activeID: '环境标识',
    instanceHost: '实例Host',
    threadPoolID: '线程池',
    LoadInformation: '负载信息',
    CurrentLoad: '当前负载',
    PeakLoad: '峰值负载',
    RemainingMemory: '剩余内存',
    MemoryRatio: '内存占比',
    threadInformation: '线程信息',
    currentSize: '当前线程',
    activeSize: '活跃线程',
    largestSize: '同存最大线程',
    queueInformation: '队列信息',
    queueCount: '队列元素',
    queueRemainingCapacity: '队列剩余容量',
    queueType: '阻塞队列',
    otherInformation: '其它信息',
    totalTask: '任务总量',
    totalRejection: '拒绝次数',
    lastUpdateTime: '最后更新时间',
    allowCoreThreadTimeOut: '线程超时',
    changeAll: '全部修改',
    stackRequestFail: '当前线程池暂无堆栈信息'
  }
}
