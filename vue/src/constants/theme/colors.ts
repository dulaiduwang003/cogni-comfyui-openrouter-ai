import type { ThemeName } from './names';
import { THEME_DARK_RED, THEME_LIGHT_SOFT_LAVENDER, THEME_LIGHT_RED } from './names';

export interface ThemeVariables {
  // Element Plus 主题变量
  'el-color-primary': string;
  'el-color-primary-light-3': string;
  'el-color-primary-light-5': string;
  'el-color-primary-light-7': string;
  'el-color-primary-light-8': string;
  'el-color-primary-light-9': string;
  'el-color-primary-dark-2': string;

  'el-bg-color': string;
  'el-bg-color-page': string;
  'el-bg-color-overlay': string;

  'el-text-color-primary': string;
  'el-text-color-regular': string;
  'el-text-color-secondary': string;
  'el-text-color-placeholder': string;

  'el-border-color': string;
  'el-border-color-light': string;
  'el-border-color-lighter': string;
  'el-border-color-extra-light': string;
  'el-border-color-dark': string;
  'el-border-color-darker': string;

  'el-fill-color': string;
  'el-fill-color-light': string;
  'el-fill-color-lighter': string;
  'el-fill-color-extra-light': string;
  'el-fill-color-dark': string;
  'el-fill-color-darker': string;

  // 遮罩层颜色
  'el-mask-color': string;

  // 自定义变量
  'custom-accent-color': string;
  'custom-text-on-accent': string;
  'custom-login-button-bg'?: string;
  'custom-login-button-text'?: string;
  'custom-badge-bg'?: string;
  'custom-badge-text'?: string;
  
  // Banner 渐变色配置
  'custom-gradient-start': string;  // 渐变起始色
  'custom-gradient-end': string;    // 渐变结束色
}

export const themes: Record<ThemeName, ThemeVariables> = {
  [THEME_DARK_RED]: {
    'el-color-primary': '#FF3466',
    'el-color-primary-light-3': '#FF5577',
    'el-color-primary-light-5': '#FF7799',
    'el-color-primary-light-7': '#4D1F29',
    'el-color-primary-light-8': '#3D1A22',
    'el-color-primary-light-9': '#2D151C',
    'el-color-primary-dark-2': '#CC294F',
    'el-bg-color': '#0A0A0A',
    'el-bg-color-page': '#000000',
    'el-bg-color-overlay': '#1C1C1C',
    'el-text-color-primary': '#E0E0E0',
    'el-text-color-regular': '#BDBDBD',
    'el-text-color-secondary': '#9E9E9E',
    'el-text-color-placeholder': '#757575',
    'el-border-color': '#282828',
    'el-border-color-light': '#333333',
    'el-border-color-lighter': '#3A3A3A',
    'el-border-color-extra-light': '#424242',
    'el-border-color-dark': '#1F1F1F',
    'el-border-color-darker': '#121212',
    'el-fill-color': '#1F1F1F',
    'el-fill-color-light': '#2A2A2A',
    'el-fill-color-lighter': '#353535',
    'el-fill-color-extra-light': '#1A1A1A',
    'el-fill-color-dark': '#1A1A1A',
    'el-fill-color-darker': '#101010',
    'el-mask-color': 'rgba(0, 0, 0, 0.8)',
    'custom-accent-color': '#FF3466',
    'custom-text-on-accent': '#FFFFFF',
    'custom-login-button-bg': '#FF3466',
    'custom-login-button-text': '#FFFFFF',
    'custom-badge-bg': '#333333',
    'custom-badge-text': '#FFFFFF',
    // 深色红色主题：从深紫红到鲜红的渐变
    'custom-gradient-start': '#FF1744',  // 鲜艳的红色
    'custom-gradient-end': '#FF6B8A',    // 温暖的粉红色
  },
  [THEME_LIGHT_SOFT_LAVENDER]: {
    'el-color-primary': '#BB8FCE',
    'el-color-primary-light-3': '#CB99C9',
    'el-color-primary-light-5': '#DDA0DD',
    'el-color-primary-light-7': '#EAB9EA',
    'el-color-primary-light-8': '#F4D3F4',
    'el-color-primary-light-9': '#FAF0FA',
    'el-color-primary-dark-2': '#A569BD',
    'el-bg-color': '#FFFFFF',
    'el-bg-color-page': '#F9F9FB',
    'el-bg-color-overlay': '#FEFEFE',
    'el-text-color-primary': '#3B3B3B',
    'el-text-color-regular': '#5E5E5E',
    'el-text-color-secondary': '#8A8A8A',
    'el-text-color-placeholder': '#B8B8B8',
    'el-border-color': '#E0D4E0',
    'el-border-color-light': '#EBE0EB',
    'el-border-color-lighter': '#F2ECF2',
    'el-border-color-extra-light': '#F8F5F8',
    'el-border-color-dark': '#D3C8D3',
    'el-border-color-darker': '#C6BCC6',
    'el-fill-color': '#F9F9FB',
    'el-fill-color-light': '#FAF5FA',
    'el-fill-color-lighter': '#FCF9FC',
    'el-fill-color-extra-light': '#FEFDFE',
    'el-fill-color-dark': '#EDE5ED',
    'el-fill-color-darker': '#E0D8E0',
    'el-mask-color': 'rgba(0, 0, 0, 0.5)',
    'custom-accent-color': '#BB8FCE',
    'custom-text-on-accent': '#FFFFFF',
    // 薰衣草主题：从浅紫蓝到薰衣草紫的渐变
    'custom-gradient-start': '#667EEA',  // 梦幻蓝紫色
    'custom-gradient-end': '#F093FB',    // 亮粉色
  },
  [THEME_LIGHT_RED]: {
    'el-color-primary': '#FF3466',
    'el-color-primary-light-3': '#FF6B8E',
    'el-color-primary-light-5': '#FF94AB',
    'el-color-primary-light-7': '#FFBDC9',
    'el-color-primary-light-8': '#FFD6DE',
    'el-color-primary-light-9': '#FFEEF1',
    'el-color-primary-dark-2': '#CC294F',
    'el-bg-color': '#FFFFFF',
    'el-bg-color-page': '#F9F9FB',
    'el-bg-color-overlay': '#FEFEFE',
    'el-text-color-primary': '#3B3B3B',
    'el-text-color-regular': '#5E5E5E',
    'el-text-color-secondary': '#8A8A8A',
    'el-text-color-placeholder': '#B8B8B8',
    'el-border-color': '#E0D4D6',
    'el-border-color-light': '#EBE0E2',
    'el-border-color-lighter': '#F2ECED',
    'el-border-color-extra-light': '#F8F5F5',
    'el-border-color-dark': '#D3C8C9',
    'el-border-color-darker': '#C6BCBD',
    'el-fill-color': '#F9F9FB',
    'el-fill-color-light': '#FAF5F6',
    'el-fill-color-lighter': '#FCF9F9',
    'el-fill-color-extra-light': '#FEFDFD',
    'el-fill-color-dark': '#EDE5E6',
    'el-fill-color-darker': '#E0D8D9',
    'el-mask-color': 'rgba(0, 0, 0, 0.5)',
    'custom-accent-color': '#FF3466',
    'custom-text-on-accent': '#FFFFFF',
    'custom-login-button-bg': '#FF3466',
    'custom-login-button-text': '#FFFFFF',
    'custom-badge-bg': '#FFF0F3',
    'custom-badge-text': '#FF3466',
    // 浅色红色主题：从橙红到粉红的渐变
    'custom-gradient-start': '#FF6B35',  // 活力橙色
    'custom-gradient-end': '#F7931E',    // 温暖黄橙色
  }
}; 