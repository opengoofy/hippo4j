import React, { createContext, useState, ReactNode } from 'react';

export enum THEME_NAME {
  DEFAULT = 'default',
  DARK = 'dark',
}

export const MyThemeContext = createContext<{ themeName: string; setThemeName: (name: THEME_NAME) => void } | null>(
  null
);

export const ThemeStore: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [themeName, setThemeName] = useState<string>(THEME_NAME.DEFAULT);
  return <MyThemeContext.Provider value={{ themeName, setThemeName }}>{children}</MyThemeContext.Provider>;
};
