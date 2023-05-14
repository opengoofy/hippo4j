import Vue from 'vue';

import Cookies from 'js-cookie';

import 'normalize.css/normalize.css'; // a modern alternative to CSS resets

import Element from 'element-ui';
import './styles/element-variables.scss';

import '@/styles/index.scss'; // global css

import App from './App.vue';
import store from './store';
import router from './router';

import './icons'; // icon
import './permission'; // permission control
// import './utils/error-log' // error log

import i18n from '@/locale'
import '@/utils/i18n-utils'
import * as filters from './filters'; // global filters
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
import cookie from 'vue-cookie';
Vue.prototype.$cookie = cookie;

Vue.use(Element, {
  size: Cookies.get('size') || 'medium', // set element-ui default size
  i18n: (key, value) => i18n.t(key, value)
});

// register global utility filters
Object.keys(filters).forEach((key) => {
  Vue.filter(key, filters[key]);
});

Vue.config.productionTip = false;

new Vue({
  el: '#app',
  router,
  store,
  i18n,
  render: (h) => h(App),
});
