/**
 * 语言配置文件
 * 添加新语言只需要在这里添加配置即可
 */

export interface LanguageConfig {
  code: string        // 语言代码，如 'zh-CN'
  name: string        // 语言名称，如 '中文'
  flag?: string       // 国旗 emoji（可选）
}

/**
 * 支持的语言列表
 */
export const supportedLanguages: LanguageConfig[] = [
  {
    code: 'zh-CN',
    name: '中文',
    flag: '🇨🇳'
  },
  {
    code: 'en-US',
    name: 'English',
    flag: '🇺🇸'
  }

]

/**
 * 默认语言
 */
export const defaultLanguage = 'zh-CN'

/**
 * 获取语言配置
 */
export const getLanguageConfig = (code: string): LanguageConfig | undefined => {
  return supportedLanguages.find(lang => lang.code === code)
}

/**
 * 获取语言显示名称
 */
export const getLanguageName = (code: string): string => {
  const config = getLanguageConfig(code)
  return config ? config.name : code
}

/**
 * 检查语言是否支持
 */
export const isSupportedLanguage = (code: string): boolean => {
  return supportedLanguages.some(lang => lang.code === code)
}

/**
 * 获取所有语言代码
 */
export const getAllLanguageCodes = (): string[] => {
  return supportedLanguages.map(lang => lang.code)
}

