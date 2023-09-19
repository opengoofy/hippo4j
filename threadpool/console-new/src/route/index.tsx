import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';
import ThreadPoolRouter from '@/page/thread-pool/router';
import tenantRouter from '@/page/tenant/router';
import LoginRouter from '@/page/login/router';

const routerList: IRouterList[] = [...homeRouter, ...aboutRouter, ...tenantRouter, ...ThreadPoolRouter, ...LoginRouter];
export default routerList;
