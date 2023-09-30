import { lazy } from 'react';
import { IRouterList } from '@/typings';

const Tenant = lazy(() => import('./index'));

const routerList: IRouterList[] = [{ path: '/tenant', component: () => <Tenant /> }];

export default routerList;
