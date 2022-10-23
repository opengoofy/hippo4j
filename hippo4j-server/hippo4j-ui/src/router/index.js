import Vue from 'vue';
import Router from 'vue-router';

Vue.use(Router);

/* Layout */
import Layout from '@/layout';

export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path*',
        component: () => import('@/views/redirect/index'),
      },
    ],
  },
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true,
  },
  {
    path: '/auth-redirect',
    component: () => import('@/views/login/auth-redirect'),
    hidden: true,
  },
  {
    path: '/404',
    component: () => import('@/views/error-page/404'),
    hidden: true,
  },
  {
    path: '/401',
    component: () => import('@/views/error-page/401'),
    hidden: true,
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        component: () => import('@/views/dashboard/admin/index'),
        name: 'Dashboard',
        meta: { title: '运行报表', icon: 'dashboard2', affix: true },
      },
    ],
  },
  {
    path: '/hippo4j/monitor',
    component: () => import('@/views/hippo4j/monitor/index'),
    hidden: true,
  },
];

/**
 * asyncRoutes
 * the routes that need to be dynamically loaded based on user roles
 */
export const asyncRoutes = [
  {
    path: '/profile',
    component: Layout,
    redirect: '/profile/index',
    hidden: true,
    children: [
      {
        path: 'index',
        component: () => import('@/views/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user', noCache: true },
      },
    ],
  },
  {
    path: '/error',
    component: Layout,
    redirect: 'noRedirect',
    name: 'ErrorPages',
    hidden: true,
    meta: {
      title: 'Error Pages',
      icon: '404',
    },
    children: [
      {
        path: '401',
        component: () => import('@/views/error-page/401'),
        name: 'Page401',
        meta: { title: '401', noCache: true },
      },
      {
        path: '404',
        component: () => import('@/views/error-page/404'),
        name: 'Page404',
        meta: { title: '404', noCache: true },
      },
    ],
  },
  {
    path: '/hippo4j/tenant',
    component: Layout,
    redirect: '/hippo4j/tenantList',
    name: 'tenant',
    meta: { title: '租户管理', icon: 'lessee' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/tenant/index'),
        meta: { title: '租户管理', icon: 'lessee' },
      },
    ],
  },
  {
    path: '/hippo4j/item',
    component: Layout,
    redirect: '/hippo4j/itemList',
    name: 'item',
    meta: { title: '项目管理', icon: 'item4' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/item/index'),
        meta: { title: '项目管理', icon: 'item4' },
      },
    ],
  },

  {
    path: '/hippo4j/dynamic/thread-pool',
    component: Layout,
    redirect: '/hippo4j/dynamic/thread-pool',
    name: 'thread-pool',
    meta: { title: '动态线程池', icon: 'pool3' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/threadpool/index'),
        meta: { title: '线程池管理' },
      },
      {
        path: 'instance',
        name: 'instance',
        component: () => import('@/views/hippo4j/instance/index'),
        meta: { title: '线程池实例' },
      },
      {
        path: 'monitor',
        name: 'monitor',
        component: () => import('@/views/hippo4j/monitor/index'),
        meta: { title: '线程池监控' },
      },
    ],
  },
  {
    path: '/hippo4j/server',
    component: Layout,
    redirect: '/hippo4j/server',
    name: 'server-thread-pool',
    meta: { title: '容器线程池', icon: 'vessel3' },
    children: [
      {
        path: 'tomcat',
        name: 'tomcat',
        component: () => import('@/views/hippo4j/server/tomcat/index'),
        meta: { title: 'Tomcat' },
      },
      {
        path: 'undertow',
        name: 'undertow',
        component: () => import('@/views/hippo4j/server/undertow/index'),
        meta: { title: 'Undertow' },
      },
      {
        path: 'jetty',
        name: 'jetty',
        component: () => import('@/views/hippo4j/server/jetty/index'),
        meta: { title: 'Jetty' },
      },
    ],
  },
  {
    path: '/hippo4j/other',
    component: Layout,
    redirect: '/hippo4j/other',
    name: 'other-thread-pool',
    meta: { title: '框架线程池', icon: 'other4' },
    children: [
      {
        path: 'dubbo',
        name: 'dubbo',
        component: () => import('@/views/hippo4j/other/dubbo/index'),
        meta: { title: 'Dubbo' },
      },
      {
        path: 'hystrix',
        name: 'hystrix',
        component: () => import('@/views/hippo4j/other/hystrix/index'),
        meta: { title: 'Hystrix' },
      },
      {
        path: 'rabbitmq',
        name: 'rabbitmq',
        component: () => import('@/views/hippo4j/other/rabbitmq/index'),
        meta: { title: 'RabbitMQ' },
      },
      {
        path: 'rocketmq',
        name: 'rocketmq',
        component: () => import('@/views/hippo4j/other/rocketmq/index'),
        meta: { title: 'RocketMQ' },
      },
      {
        path: 'alibaba-dubbo',
        name: 'alibaba-dubbo',
        component: () => import('@/views/hippo4j/other/alibaba-dubbo/index'),
        meta: { title: 'AlibabaDubbo' },
      },
      {
        path: 'rabbitmq-stream',
        name: 'rabbitmq-stream',
        component: () => import('@/views/hippo4j/other/rabbitmq-stream/index'),
        meta: { title: 'RabbitMQStream' },
      },
      {
        path: 'rocketmq-stream',
        name: 'rocketmq-stream',
        component: () => import('@/views/hippo4j/other/rocketmq-stream/index'),
        meta: { title: 'RocketMQStream' },
      },
    ],
  },
  {
    path: '/hippo4j/verify',
    component: Layout,
    redirect: '/hippo4j/verifyList',
    name: 'config-modification-verify',
    meta: { title: '线程池审核', icon: 'audit', roles: ['ROLE_ADMIN'] },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/verify/index'),
        meta: { title: '线程池审核', icon: 'audit' },
      },
    ],
  },
  {
    path: '/hippo4j/notify',
    component: Layout,
    redirect: '/hippo4j/notifyList',
    name: 'notify',
    meta: { title: '通知报警', icon: 'notify' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/notify/index'),
        meta: { title: '通知报警', icon: 'notify' },
      },
    ],
  },
  {
    path: '/hippo4j/user',
    component: Layout,
    redirect: '/hippo4j/userList',
    name: 'user',
    meta: { title: '用户权限', icon: 'user6', roles: ['ROLE_ADMIN'] },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/user/index'),
        meta: { title: '用户权限', icon: 'user6' },
      },
    ],
  },
  {
    path: '/hippo4j/log',
    component: Layout,
    redirect: '/hippo4j/logList',
    name: 'log',
    meta: { title: '日志管理', icon: 'log3' },
    children: [
      {
        path: 'index',
        name: 'index',
        component: () => import('@/views/hippo4j/log/index'),
        meta: { title: '日志管理', icon: 'log3' },
      },
    ],
  },

  {
    path: 'external-link',
    component: Layout,
    children: [
      {
        path: 'https://hippo4j.cn',
        meta: { title: '官网外链', icon: 'link3' },
      },
    ],
  },

  { path: '*', redirect: '/404', hidden: true },
];

const createRouter = () =>
  new Router({
    // mode: 'history', // require service support
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRoutes,
  });

const router = createRouter();

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter();
  router.matcher = newRouter.matcher; // reset router
}

export default router;
