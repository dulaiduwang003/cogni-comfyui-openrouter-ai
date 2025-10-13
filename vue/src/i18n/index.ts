import { createI18n } from 'vue-i18n'
import zhCN from './locales/zh-CN'
import enUS from './locales/en-US'
import { defaultLanguage, isSupportedLanguage } from './languages.config'


const getDefaultLocale = () => {
  const savedLocale = localStorage.getItem('app-locale')
  if (savedLocale && isSupportedLanguage(savedLocale)) {
    return savedLocale
  }
  return defaultLanguage
}

const i18n = createI18n({
  legacy: false, 
  locale: getDefaultLocale(),
  fallbackLocale: 'zh-CN',
  messages: {
    'zh-CN': zhCN,
    'en-US': enUS
  },
  globalInjection: true 
})

export default i18n

