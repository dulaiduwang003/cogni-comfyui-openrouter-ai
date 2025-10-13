import { Moon, Sunny } from '@element-plus/icons-vue';
import type { ThemeName } from './names';
import { THEME_DARK_RED, THEME_LIGHT_SOFT_LAVENDER, THEME_LIGHT_RED } from './names';

export interface ThemeOption {
    value: ThemeName;
    label: string;
    labelKey: string; // i18n key for translation
    icon: any;
}

export const themeOptions: ThemeOption[] = [
    { value: THEME_DARK_RED, label: '深色 - 红色', labelKey: 'theme.darkRed', icon: Moon },
    { value: THEME_LIGHT_SOFT_LAVENDER, label: '浅色 - 薰衣草', labelKey: 'theme.lightSoftLavender', icon: Sunny },
    { value: THEME_LIGHT_RED, label: '浅色 - 红色', labelKey: 'theme.lightRed', icon: Sunny },
]; 