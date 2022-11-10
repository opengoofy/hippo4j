// 导入elementUi默认中英文语言包
import elZh from 'element-ui/lib/locale/lang/zh-CN'
import elEn from 'element-ui/lib/locale/lang/en'

import zh from './lang/zh'
import en from './lang/en'

const lang = localStorage.getItem('locale_lang')

export const i18nConfig = {
  locale: lang || 'en', // 默认语种
  messages: {
    zh: { ...zh, ...elZh }, // 中文包
    en: { ...en, ...elEn } // 英文包
  }
}


export const langSelectList = () => {
  return [
    {
      lang: 'en',
      name: 'English'
    },
    {
      lang: 'zh',
      name: '简体中文'
    },
  ]
}
