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
    NoDeletionPermissionTip: 'Contact the administrator to delete it'
  },

  // 公共
  common: {
    query: 'Query',
    insert: 'Insert',
    operation: 'Operation',
    create: 'Create',
    edit: 'Edit',
    delete: 'Delete',
    cancel: 'Cancel',
    confirm: 'Confirm',
    ok: 'Ok',
    num: 'Num',
    createTime: 'Create Time',
    updateTime: 'Update Time',
    hint: 'Warning',
  },

  // 运行报表
  report: {
    poolInstance: 'Pool Instance',
    user: 'User',
    role: 'Role',
    tenants: 'Tenants'
  },

  // 租户管理
  tenantManage: {
    tenant: 'Tenant',
    tenantName: 'Tenant Name',
    owner: 'Owner',
    tenantIntro: 'Tenant Intro'
  },

  // 项目管理
  projectManage: {
    item: 'Item',
    itemName: 'Item Name',
    owner: 'Owner',
    itemIntro: 'Item Intro'
  },

  // 线程池管理
  threadPool: {
    threadPool: 'Thread Pool',
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
    threadsNumErrorTip: 'The maximum thread must be greater than or equal to the core thread'
  }
  
}
