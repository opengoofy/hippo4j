import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';
import ThreadPoolRouter from '@/page/thread-pool/router';

const routerList: IRouterList[] = [...homeRouter, ...aboutRouter, ...ThreadPoolRouter];
export default routerList;
