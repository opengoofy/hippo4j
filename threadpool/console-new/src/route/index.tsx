import { IRouterList } from '@/typings';
import homeRouter from '@/page/home/router';
import aboutRouter from '@/page/about/router';

const routerList: IRouterList[] = [...homeRouter, ...aboutRouter];
export default routerList;
