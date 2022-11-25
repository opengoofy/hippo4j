export default {
  // 系统
  system: {
    login: '登 陆',
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
    NoDeletionPermissionTip: '请联系管理员删除'
  },

  // 公共
  common: {
    query: '搜索',
    insert: '添加',
    operation: '操作',
    create: '创建',
    edit: '编辑',
    delete: '删除',
    cancel: '取消',
    confirm: '确认',
    ok: '确定',
    num: '序号',
    createTime: '创建时间',
    updateTime: '修改时间',
    hint: '提示'
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
    tenantName: '租户名称',
    owner: '负责人',
    tenantIntro: '租户简介'
  },
  
  // 项目管理
  projectManage: {
    item: '项目',
    itemName: '项目名称',
    owner: '负责人',
    itemIntro: '项目简介'
  },
  
  // 线程池管理
  threadPool: {
    threadPool: '线程池',
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
  }
}
