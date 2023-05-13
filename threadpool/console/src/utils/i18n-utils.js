import Vue from 'vue'
import i18n from '@/locale'


/**
 * @param langMap {[lang]: any} object
 * @param [defaultText] any
 * @returns any
 */
export function langMatch(langMap, defaultText = undefined) {
  if (Object.prototype.toString.call(langMap) !== '[object Object]') {
    throw Error('The first argument to the langMatch method must be the object type')
  }
  const lang = i18n.locale
  return Object.prototype.hasOwnProperty.call(langMap, lang) ? langMap[lang] : defaultText
}


Vue.prototype.$langMatch = langMatch

