import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';
import ThreadPoolRouter from '@/page/thread-pool/router';
import tenantRouter from '@/page/tenant/router';
import LoginRouter from '@/page/login/router';
import itemRouter from '@/page/item/router';
import userRouter from '@/page/user/router';
import logRouter from '@/page/log/router';
import ThreadPoolMonitorRouter from '@/page/thread-pool-monitor/router';

const routerList: IRouterList[] = [
  ...homeRouter,
  ...aboutRouter,
  ...tenantRouter,
  ...ThreadPoolRouter,
  ...LoginRouter,
  ...itemRouter,
  ...userRouter,
  ...logRouter,
  ...ThreadPoolMonitorRouter,
];
export default routerList;
