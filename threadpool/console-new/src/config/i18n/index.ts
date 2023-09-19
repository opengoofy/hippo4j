// i18n.js

import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import zh from './locales/zh';
import en from './locales/en';
import LanguageDetector from 'i18next-browser-languagedetector';

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    resources: {
      zh: {
        translation: zh,
      },
      en: {
        translation: en,
      },
    },
    // debug: true,
    fallbackLng: 'zh', // 默认语言
    interpolation: {
      escapeValue: false, // 不转义HTML标签
    },
  });

export default i18n;
