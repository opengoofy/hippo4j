import { useTranslation } from 'react-i18next';

export const useTran = (str: string): string => {
  const { t } = useTranslation();
  return t(str);
};
