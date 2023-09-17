import { useContext, useEffect } from 'react';
import { useLocalStorageState } from 'ahooks';
import { MyContext, THEME_NAME } from '@/context';

export const useThemeMode = (): { isDark: boolean | undefined; setIsDark: (isDark: boolean) => void } => {
  const [isDark, setIsDark] = useLocalStorageState<boolean>('current-mode', { defaultValue: false });
  const { setThemeName } = useContext<any>(MyContext);

  useEffect(() => {
    isDark ? setThemeName(THEME_NAME.DARK) : setThemeName(THEME_NAME.DEFAULT);
  }, [isDark, setThemeName]);

  return { isDark, setIsDark };
};
