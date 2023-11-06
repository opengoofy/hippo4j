import React, { useContext, useEffect, useState } from 'react';
import { DefaultTheme, ThemeProvider } from 'styled-components';
import { ConfigProvider, theme } from 'antd';
import { darkAlgorithm } from '../../config/theme/dark-algorithm';
import { defaultAlgorithm } from '../../config/theme/default-algnorithm';
import { lightDefaultTheme, darkDefaultTheme } from '../../config/theme';
import { MyContext } from '../../context';
import zhCN from 'antd/es/locale/zh_CN';
import { THEME_NAME } from '@/typings';

interface ThemeProps {
  children: React.ReactNode;
}

const ThemeComponent = ({ children }: ThemeProps) => {
  const [themes, setThemes] = useState(defaultAlgorithm);
  const [myThemes, setMyThemes] = useState<DefaultTheme>(lightDefaultTheme);
  const { themeName } = useContext<any>(MyContext);

  const changeMode = (themeName: THEME_NAME) => {
    if (themeName === THEME_NAME.DARK) {
      darkAlgorithm.algorithm = theme.darkAlgorithm;
      setThemes(darkAlgorithm);
      setMyThemes(darkDefaultTheme);
    } else {
      defaultAlgorithm.algorithm = theme.defaultAlgorithm;
      setThemes(defaultAlgorithm);
      setMyThemes(lightDefaultTheme);
    }
  };

  useEffect(() => {
    changeMode(themeName);
  }, [themeName]);

  return (
    <ConfigProvider locale={zhCN} theme={themes}>
      <ThemeProvider theme={myThemes}>{children}</ThemeProvider>
    </ConfigProvider>
  );
};
export default ThemeComponent;
