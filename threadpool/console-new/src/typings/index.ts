import { ReactNode } from 'react';
import React from 'react';

export type IRouterList = {
  path: string;
  component: () => React.JSX.Element;
};

export type IMenuList = {
  label: string | ReactNode;
  key: string;
  icon?: ReactNode;
};
