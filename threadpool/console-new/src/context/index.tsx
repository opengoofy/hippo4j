import React, { createContext, useState, ReactNode, useEffect } from 'react';
import { ConfigProvider, theme } from 'antd';
import { DefaultTheme, ThemeProvider } from 'styled-components';
import enUS from 'antd/locale/en_US';
import zhCN from 'antd/locale/zh_CN';
import { darkDefaultTheme, lightDefaultTheme } from '@/config/theme';
import { defaultAlgorithm } from '@/config/theme/default-algnorithm';
import { darkAlgorithm } from '@/config/theme/dark-algorithm';
import { useTranslation } from 'react-i18next';
import { useLocalStorageState } from 'ahooks';
import { LANG_NAME, MyStoreValues, THEME_NAME } from '@/typings';

export const MyContext = createContext<MyStoreValues>({});

export const MyStore: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [themeName, setThemeName] = useLocalStorageState<THEME_NAME>('current-mode', {
    defaultValue: THEME_NAME.DEFAULT,
  });
  const [lang, setLang] = useLocalStorageState<LANG_NAME>('current-lang', {
    defaultValue: LANG_NAME.ZH,
  });
  const [themes, setThemes] = useState(defaultAlgorithm);
  const [myThemes, setMyThemes] = useState<DefaultTheme>(lightDefaultTheme);

  const { i18n } = useTranslation();

  const changeMode = (themeName: THEME_NAME) => {
    if (themeName === THEME_NAME.DARK) {
      darkAlgorithm.algorithm = theme.darkAlgorithm;
      // for ant change mode
      setThemes(darkAlgorithm);
      // for custome use mode
      setMyThemes(darkDefaultTheme);
    } else {
      defaultAlgorithm.algorithm = theme.defaultAlgorithm;
      setThemes(defaultAlgorithm);
      setMyThemes(lightDefaultTheme);
    }
  };

  useEffect(() => {
    changeMode(themeName as THEME_NAME);
  }, [themeName]);

  useEffect(() => {
    i18n.changeLanguage(lang);
  }, [lang, i18n]);

  return (
    <MyContext.Provider value={{ themeName, lang, setThemeName, setLang }}>
      <ConfigProvider locale={lang === LANG_NAME.ZH ? zhCN : enUS} theme={themes}>
        <ThemeProvider theme={myThemes}>{children}</ThemeProvider>
      </ConfigProvider>
    </MyContext.Provider>
  );
};
