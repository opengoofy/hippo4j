import { lazy } from 'react';
import { IRouterList } from '@/typings';

const ItemManage = lazy(() => import('./index'));

const routerList: IRouterList[] = [{ path: '/item', component: () => <ItemManage /> }];

export default routerList;
