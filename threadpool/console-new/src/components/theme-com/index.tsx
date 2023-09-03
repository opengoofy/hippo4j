import React, { useContext, useEffect, useState } from 'react';
import { DefaultTheme, ThemeProvider, useTheme } from 'styled-components';
import { ConfigProvider, theme } from 'antd';

import { darkAlgorithm } from '../../theme/dark-algorithm';
import { defaultAlgorithm } from '../../theme/default-algnorithm';
import { lightDefaultTheme, darkDefaultTheme } from '../../theme';
import { MyThemeContext, THEME_NAME } from '../../context/themeContext';
import zhCN from 'antd/es/locale/zh_CN';

interface ThemeProps {
  children: React.ReactNode;
}

const ThemeComponent = ({ children }: ThemeProps) => {
  const [themes, setThemes] = useState(defaultAlgorithm);
  const [myThemes, setMyThemes] = useState<DefaultTheme>(lightDefaultTheme);
  const { themeName } = useContext<any>(MyThemeContext);

  const changeColor = (themeName: THEME_NAME) => {
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
    changeColor(themeName);
  }, [themeName]);

  return (
    <ConfigProvider locale={zhCN} theme={themes}>
      <ThemeProvider theme={myThemes}>{children}</ThemeProvider>
    </ConfigProvider>
  );
};
export default ThemeComponent;
