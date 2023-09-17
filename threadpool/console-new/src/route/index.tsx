import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';
import ThreadPoolRouter from '@/page/thread-pool/router';
import tenantRouter from '@/page/tenant/router';

const routerList: IRouterList[] = [...homeRouter, ...aboutRouter, ...tenantRouter, ...ThreadPoolRouter];
export default routerList;
