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
    emptyWarning: '{name}不允许为空',
    queryFailure: '查询失败，请尝试刷新页面',
    updateFailure: '修改线程池失败',
    auditApprovedMessage: '此操作将接受线程池变更申请, 是否继续?',
    auditRejectionMessage: '此操作将拒绝线程池变更申请, 是否继续?',
    inputMessage: '请输入{target}',
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
    info: '详情',
    stack: '堆栈',
    audit: '审核',
    yes: '是',
    no: '否',
    allTenant: '所有租户'
  },

  // 运行报表
  report: {
    poolInstance: '线程池实例',
    user: '登录用户',
    role: '用户角色',
    tenants: '所属租户',
    threadPoolId: '线程池',
    taskCount: '任务数',
    inst: '实例数',
  },

  // 租户管理
  tenantManage: {
    tenant: '租户',
    tenantRequired: '租户（必填）',
    tenantName: '租户名称',
    owner: '负责人',
    tenantIntro: '租户简介',
  },

  // 项目管理
  projectManage: {
    item: '项目',
    itemRequired: '项目（必填）',
    itemName: '项目名称',
    owner: '负责人',
    itemIntro: '项目简介',
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
    threadsNumErrorTip: '最大线程必须大于等于核心线程',
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
    stackRequestFail: '当前线程池暂无堆栈信息',
  },

  //线程池监控
  threadPoolMonitor: {
    ipPort: 'IP : Port',
    ipPortRequired: 'IP : Port（必填）',
    noResultsYet: '暂无结果',
  },

  //框架线程池
  frameworkThreadPool: {
    threadPoolIDRequired: '线程池标识（必填）',
    threadPoolID: '线程池标识',
    threadPoolType: '线程池类型',
  },

  //线程池审核
  threadPoolAudit: {
    changeType: '变更类型',
    modifiedBy: '修改人',
    auditStatus: '审核状态',
    reviewer: '审核人',
    submissionTime: '提交时间',
    auditTime: '审核时间',
    unaudited: '待审核',
    expired: '失效',
    auditApproved: '审核通过',
    auditRejection: '审核拒绝',
    manage: '管理',
    instance: '实例',
    container: '容器',
    framework: '框架',
    threadPoolManage: '线程池管理',
    threadPoolInstance: '线程池实例',
    containerThreadPool: '容器线程池',
    frameworkThreadPool: '框架线程池',
  },

  //通知报警
  notifyAlarm: {
    platform: '通知平台',
    type: '通知类型',
    enabled: '是否启用',
    interval: '通知间隔',
    receiver: '接收者',
    enabling: '启用',
    disabling: '禁用',
    token: 'Token',
    receiverTip:
      '多个接收者使用英文逗号 , 分割 (注意不要有空格)\n' +
      '- 钉钉：填写手机号\n' +
      '- 企微：填写user_id会以@的消息发给用户，否则填写姓名，如：小马哥\n' +
      '- 飞书：填写ou_开头用户唯一标识会以@的消息发给用户，填写手机号则是普通的@',
  },

  //用户权限
  userAuthority: {
    userName: '用户名',
    role: '角色',
    password: '密码',
  },

  //日志管理
  logManage: {
    bizType: '业务类型',
    bizID: '业务标识',
    operator: '操作人',
    logContent: '日志内容',
  },

  //菜单
  menu: {
    dashboard: '运行报表',
    tenantManage: '租户管理',
    itemManage: '项目管理',
    dynamicThreadPool: '动态线程池',
    threadPoolManage: '线程池管理',
    threadPoolInstance: '线程池实例',
    threadPoolMonitor: '线程池监控',
    containerThreadPool: '容器线程池',
    tomcat: 'Tomcat',
    undertow: 'Undertow',
    jetty: 'Jetty',
    frameworkThreadPool: '框架线程池',
    dubbo: 'Dubbo',
    hystrix: 'Hystrix',
    rabbitMQ: 'rabbitMQ',
    rocketMQ: 'RocketMQ',
    alibabaDubbo: 'AlibabaDubbo',
    rabbitMQStream: 'RabbitMQStream',
    rocketMQStream: 'RocketMQStream',
    threadPoolAudit: '线程池审核',
    notifyAlarm: '通知报警',
    userAuthority: '用户权限',
    logManage: '日志管理',
    officialWebsite: '官网外链',
  },
};
