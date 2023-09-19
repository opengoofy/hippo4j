import { lazy } from 'react';
import { IRouterList } from '@/typings';

const UserManage = lazy(() => import('./index'));

const routerList: IRouterList[] = [{ path: '/user', component: () => <UserManage /> }];

export default routerList;
