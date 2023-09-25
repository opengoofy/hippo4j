import { IRouterList } from '@/typings';
import ThreadPoolMonitor from '.';

const routerList: IRouterList[] = [
  {
    path: '/echarts',
    component: ThreadPoolMonitor,
  },
];

export default routerList;
