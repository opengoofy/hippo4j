import { ReactNode } from 'react';
import React from 'react';

export enum THEME_NAME {
  DEFAULT = 'default',
  DARK = 'dark',
}

export enum LANG_NAME {
  ZH = 'zh',
  EN = 'en',
}

export type IRouterList = {
  path: string;
  component: () => React.JSX.Element;
};

export type IMenuList = {
  label: string | ReactNode;
  key: string;
  icon?: ReactNode;
};

export interface MyStoreValues {
  themeName?: THEME_NAME | undefined;
  lang?: LANG_NAME;
  setThemeName?: (name: THEME_NAME) => void;
  setLang?: (lang: LANG_NAME) => void;
}
