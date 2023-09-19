import { lazy } from 'react';
import { IRouterList } from '@/typings';

const Login = lazy(() => import('./index'));

const routerList: IRouterList[] = [{ path: '/login', component: () => <Login /> }];

export default routerList;
