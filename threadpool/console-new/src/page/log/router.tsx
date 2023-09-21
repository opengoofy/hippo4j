import { lazy } from 'react';
import { IRouterList } from '@/typings';

const LogManage = lazy(() => import('./index'));

const routerList: IRouterList[] = [{ path: '/log', component: () => <LogManage /> }];

export default routerList;
