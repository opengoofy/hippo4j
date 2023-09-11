import { useContext, useEffect } from 'react';
import { useLocalStorageState } from 'ahooks';
import { MyThemeContext, THEME_NAME } from '@/context/themeContext';

const useThemeMode = () => {
  const [isDark, setIsDark] = useLocalStorageState<boolean>('current-mode', { defaultValue: false });
  const { setThemeName } = useContext<any>(MyThemeContext);

  useEffect(() => {
    isDark ? setThemeName(THEME_NAME.DARK) : setThemeName(THEME_NAME.DEFAULT);
  }, [isDark, setThemeName]);

  return [setIsDark];
};

export default useThemeMode;
