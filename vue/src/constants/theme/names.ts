/**
 * 主题名称常量
 */
export const THEME_DARK_RED = 'theme-dark-red';
export const THEME_LIGHT_SOFT_LAVENDER = 'theme-light-soft-lavender';
export const THEME_LIGHT_RED = 'theme-light-red';

export const DEFAULT_THEME = THEME_LIGHT_SOFT_LAVENDER;

export type ThemeName =
  | typeof THEME_DARK_RED
  | typeof THEME_LIGHT_SOFT_LAVENDER
  | typeof THEME_LIGHT_RED; 