import Vue from 'vue'
import VueI18n from 'vue-i18n'

import { i18nConfig } from './config'

Vue.use(VueI18n)

const i18n = new VueI18n(i18nConfig)

export default i18n
