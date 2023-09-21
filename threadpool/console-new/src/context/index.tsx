import React, { createContext, useState, ReactNode, useEffect } from 'react';
import { ConfigProvider, theme } from 'antd';
import { DefaultTheme, ThemeProvider } from 'styled-components';
import enUS from 'antd/locale/en_US';
import zhCN from 'antd/locale/zh_CN';
import { darkDefaultTheme, lightDefaultTheme } from '@/config/theme';
import { defaultAlgorithm } from '@/config/theme/default-algnorithm';
import { darkAlgorithm } from '@/config/theme/dark-algorithm';
import { useTranslation } from 'react-i18next';

export enum THEME_NAME {
  DEFAULT = 'default',
  DARK = 'dark',
}

export enum LANG_NAME {
  ZH = 'zh',
  EN = 'en',
}

export type TENANT_INFO = {
  id: undefined;
  tenantId: '';
  tenantName: '';
  owner: '';
  tenantDesc: '';
};

export const MyContext = createContext<{
  themeName: string;
  lang: LANG_NAME;
  tenantInfo: object;
  setThemeName: (name: THEME_NAME) => void;
  setLang: (lang: LANG_NAME) => void;
  setTenantInfo: (tenantInfo: TENANT_INFO) => void;
} | null>(null);

export const MyStore: React.FC<{
  children: ReactNode;
}> = ({ children }) => {
  const [themeName, setThemeName] = useState<THEME_NAME>(THEME_NAME.DEFAULT);
  const [lang, setLang] = useState<LANG_NAME>(LANG_NAME.ZH);
  const [tenantInfo, setTenantInfo] = useState<TENANT_INFO>({
    id: undefined,
    tenantId: '',
    tenantName: '',
    owner: '',
    tenantDesc: '',
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

  const changeTenantInfo = (tenantInfo: TENANT_INFO) => {
    setTenantInfo(tenantInfo);
  };

  useEffect(() => {
    changeMode(themeName);
  }, [themeName]);

  useEffect(() => {
    // change lang
    i18n.changeLanguage(lang);
  }, [lang, i18n]);

  useEffect(() => {
    changeTenantInfo(tenantInfo);
  }, [tenantInfo]);

  return (
    <MyContext.Provider value={{ themeName, lang, tenantInfo, setThemeName, setLang, setTenantInfo }}>
      <ConfigProvider locale={lang === LANG_NAME.ZH ? zhCN : enUS} theme={themes}>
        <ThemeProvider theme={myThemes}>{children}</ThemeProvider>
      </ConfigProvider>
    </MyContext.Provider>
  );
};
