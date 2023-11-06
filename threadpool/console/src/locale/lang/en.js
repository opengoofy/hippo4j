export default {
  // 系统
  system: {
    login: 'Login Form',
    username: 'Username',
    password: 'Password',
    logOut: 'Log Out',
  },

  // 信息
  message: {
    requiredError: 'this is required',
    success: 'Success',
    createdSuccess: 'Created Successfully',
    updateSuccess: 'Update Successfully',
    deleteSuccess: 'Delete Successfully',
    deleteMessage: 'This action will delete {name}, Do you want to continue?',
    selectMessage: 'Please select a {target}',
    NoDeletionPermissionTip: 'Contact the administrator to delete it',
    emptyWarning: "The '{name}' cannot be empty",
    queryFailure: 'The query fails. Please refresh the page',
    updateFailure: 'Failed to modify thread-pool',
    auditApprovedMessage:
      'This operation will accept thread-pool change requests. Do you want to continue?',
    auditRejectionMessage:
      'This operation will reject the thread-pool change request. Do you want to continue?',
    inputMessage: 'Please input {target}',
  },

  // 公共
  common: {
    query: 'Query',
    insert: 'Insert',
    operation: 'Operation',
    create: 'Create',
    edit: 'Edit',
    delete: 'Delete',
    detail: 'Detail',
    cancel: 'Cancel',
    confirm: 'Confirm',
    reset: 'Reset',
    ok: 'Ok',
    num: 'Num',
    close: 'Close',
    refresh: 'Refresh',
    createTime: 'Create Time',
    updateTime: 'Update Time',
    hint: 'Warning',
    info: 'Info',
    stack: 'Stack',
    audit: 'Audit',
    yes: 'Yes',
    no: 'No',
    allTenant: 'ALL TENANT'
  },

  // 运行报表
  report: {
    poolInstance: 'Pool Instance',
    user: 'User',
    role: 'Role',
    tenants: 'Tenants',
    threadPoolId: 'ThreadPool ID',
    taskCount: 'Task Count',
    inst: 'Instance',
  },

  // 租户管理
  tenantManage: {
    tenant: 'Tenant',
    tenantRequired: 'Tenant(Required)',
    tenantName: 'Tenant Name',
    owner: 'Owner',
    tenantIntro: 'Tenant Intro',
  },

  // 项目管理
  projectManage: {
    item: 'Item',
    itemRequired: 'Item(Required)',
    itemName: 'Item Name',
    owner: 'Owner',
    itemIntro: 'Item Intro',
  },

  // 线程池管理
  threadPool: {
    threadPool: 'ThreadPool',
    threadPoolRequired: 'ThreadPool(Required)',
    coreSize: 'Core Size',
    maximumSize: 'Maximum Size',
    queueType: 'Queue Type',
    queueCapacity: 'Queue Capacity',
    rejectedHandler: 'Rejected Handler',
    executionTimeout: 'Execution Timeout',
    executionTimeoutUnit: 'Execution Timeout （ms）',
    keepAliveTime: 'Keep Alive Time',
    keepAliveTimeUnit: 'Keep Alive Time（seconds）',
    isTimeout: 'Is Timeout',
    timeout: 'Timeout',
    noTimeout: 'No Timeout',
    isAlarm: 'Is Alarm',
    alarm: 'Alarm',
    noAlarm: 'No Alarm',
    activeAlarm: 'Active Alarm',
    capacityAlarm: 'Capacity Alarm',
    customRejectedHandler: 'Custom Rejected Handler',
    customRejectedHandlerTip: 'Please enter a custom SPI Deny Policy ID',
    threadsNumErrorTip: 'The maximum thread must be greater than or equal to the core thread',
  },

  // 线程池实例
  threadPoolInstance: {
    instanceID: 'Instance ID',
    active: 'Active',
    stack: 'Stack',
    basicInformation: 'Basic Information',
    runningState: 'Running state',
    activeID: 'Active',
    instanceHost: 'Instance Host',
    threadPoolID: 'ThreadPool ID',
    LoadInformation: 'Load Information',
    CurrentLoad: 'Current Load',
    PeakLoad: 'Peak Load',
    RemainingMemory: 'Remaining Memory',
    MemoryRatio: 'Memory Ratio',
    threadInformation: 'Thread Information',
    currentSize: 'Current Size',
    activeSize: 'Active Size',
    largestSize: 'Largest Size',
    queueInformation: 'Queue Information',
    queueCount: 'Queue Count',
    queueRemainingCapacity: 'Queue Remaining Capacity',
    queueType: 'Queue Type',
    otherInformation: 'Other Information',
    totalTask: 'Total Task',
    totalRejection: 'Total Rejection',
    lastUpdateTime: 'Last update time',
    allowCoreThreadTimeOut: 'Allow Core Thread Time Out',
    changeAll: 'Change All',
    stackRequestFail: 'The current thread-pool has no stack information',
  },

  //线程池监控
  threadPoolMonitor: {
    ipPort: 'IP : Port',
    ipPortRequired: 'IP : Port(Required)',
    noResultsYet: 'No results yet',
  },

  //框架线程池
  frameworkThreadPool: {
    threadPoolIDRequired: 'ThreadPool ID(Required)',
    threadPoolID: 'ThreadPool ID',
    threadPoolType: 'ThreadPool Type',
  },

  //线程池审核
  threadPoolAudit: {
    changeType: 'Change Type',
    modifiedBy: 'Modified By',
    auditStatus: 'Audit Status',
    reviewer: 'Reviewer',
    submissionTime: 'Submission Time',
    auditTime: 'Audit Time',
    unaudited: 'Unaudited',
    expired: 'Expired',
    auditApproved: 'Audit Approved',
    auditRejection: 'Audit Rejection',
    manage: 'Manage',
    instance: 'Instance',
    container: 'Container',
    framework: 'Framework',
    threadPoolManage: 'ThreadPool Manage',
    threadPoolInstance: 'ThreadPool Instance',
    containerThreadPool: 'Container ThreadPool',
    frameworkThreadPool: 'Framework ThreadPool',
  },

  //通知报警
  notifyAlarm: {
    platform: 'Platform',
    type: 'Type',
    enabled: 'Enabled',
    interval: 'Interval',
    receiver: 'Receiver',
    enabling: 'Enabling',
    disabling: 'Disabling',
    token: 'Token',
    receiverTip:
      'Use English commas for multiple recipients and separate them (be careful not to have spaces)\n' +
      '-Ding: Fill in the phone number\n' +
      "-WeChat: Fill in 'userid' and it will be sent to the user as a @ message, otherwise fill in the name, such as: XiaomaGe\n" +
      "-Lark: Fill in with 'ou_' the unique user ID at the beginning will be sent to the user as a @ message, and filling in the phone number is normal@",
  },

  //用户权限
  userAuthority: {
    userName: 'User Name',
    role: 'Role',
    password: 'Password',
  },

  //日志管理
  logManage: {
    bizType: 'Biz Type',
    bizID: 'Biz ID',
    operator: 'Operator',
    logContent: 'Log Content',
  },

  //菜单
  menu: {
    dashboard: 'Dashboard',
    tenantManage: 'Tenant Manage',
    itemManage: 'Item Manage',
    dynamicThreadPool: 'Dynamic ThreadPool',
    threadPoolManage: 'ThreadPool Manage',
    threadPoolInstance: 'ThreadPool Instance',
    threadPoolMonitor: 'ThreadPool Monitor',
    containerThreadPool: 'Container ThreadPool',
    tomcat: 'Tomcat',
    undertow: 'Undertow',
    jetty: 'Jetty',
    frameworkThreadPool: 'Framework ThreadPool',
    dubbo: 'Dubbo',
    hystrix: 'Hystrix',
    rabbitMQ: 'RabbitMQ',
    rocketMQ: 'RocketMQ',
    alibabaDubbo: 'AlibabaDubbo',
    rabbitMQStream: 'RabbitMQStream',
    rocketMQStream: 'RocketMQStream',
    threadPoolAudit: 'ThreadPool Audit',
    notifyAlarm: 'Notify Alarm',
    userAuthority: 'User Authority',
    logManage: 'Log Manage',
    officialWebsite: 'Official Website',
  },
};
