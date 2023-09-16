import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';
import tenantRouter from '@/page/tenant/router';

const routerList: IRouterList[] = [...homeRouter, ...aboutRouter, ...tenantRouter];
export default routerList;
