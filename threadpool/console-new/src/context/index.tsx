import React, { createContext, useState, ReactNode } from 'react';

export enum THEME_NAME {
  DEFAULT = 'default',
  DARK = 'dark',
}

export const StoreContext = createContext<{
  themeName: string;
  setThemeName: (name: THEME_NAME) => void;
} | null>(null);

export const Store: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [themeName, setThemeName] = useState<string>(THEME_NAME.DEFAULT);
  return <StoreContext.Provider value={{ themeName, setThemeName }}>{children}</StoreContext.Provider>;
};
