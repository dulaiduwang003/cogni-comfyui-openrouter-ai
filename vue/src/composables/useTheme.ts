import { ref, watchEffect, onMounted } from 'vue';
import { themes } from '@/constants/theme/colors';
import { DEFAULT_THEME, type ThemeName } from '@/constants/theme/names';

const THEME_STORAGE_KEY = 'app_theme';

// 将十六进制颜色转换为 RGB
const hexToRgb = (hex: string): string | null => {
  const result = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex);
  return result 
    ? `${parseInt(result[1], 16)}, ${parseInt(result[2], 16)}, ${parseInt(result[3], 16)}`
    : null;
};

const defaultThemeName: ThemeName = DEFAULT_THEME;

const isValidTheme=(themeName: string): themeName is ThemeName => {
  return themeName in themes;
}

const getSavedTheme=(): ThemeName=>{
  const savedTheme = localStorage.getItem(THEME_STORAGE_KEY);
  if (savedTheme && isValidTheme(savedTheme)) {
    return savedTheme;
  }
  localStorage.removeItem(THEME_STORAGE_KEY);
  return defaultThemeName;
}

const currentThemeName = ref<ThemeName>(getSavedTheme());

const applyThemeVariables=(themeName: ThemeName) =>{
  const themeVariables = themes[themeName];
  if (!themeVariables) {
    console.warn(`Theme "${themeName}" not found. Falling back to default.`);
    applyThemeVariables(defaultThemeName);
    return;
  }

  const rootStyle = document.documentElement.style;

  // 先清除所有可能的主题变量
  Object.keys(themes[defaultThemeName]).forEach(key => {
    rootStyle.removeProperty(`--${key}`);
  });

  // 应用新主题的变量
  for (const [key, value] of Object.entries(themeVariables)) {
    if (value !== undefined && value !== null) {
      rootStyle.setProperty(`--${key}`, value);
      
      // 自动生成 RGB 变量（如果是颜色值）
      if (key.includes('color') && value.startsWith('#')) {
        const rgbValue = hexToRgb(value);
        if (rgbValue) {
          rootStyle.setProperty(`--${key}-rgb`, rgbValue);
        }
      }
    }
  }
}

// 应用初始主题
applyThemeVariables(currentThemeName.value);
document.documentElement.classList.add(currentThemeName.value);

// 在body上添加主题类
document.body.classList.add(currentThemeName.value);

export function useTheme() {
  const setTheme = (themeName: ThemeName) => {
    if (!isValidTheme(themeName)) {
      console.warn(`Invalid theme "${themeName}". Using default theme.`);
      currentThemeName.value = defaultThemeName;
    } else {
      currentThemeName.value = themeName;
    }
    localStorage.setItem(THEME_STORAGE_KEY, currentThemeName.value);
  };

  watchEffect(() => {
    applyThemeVariables(currentThemeName.value);

    const classesToRemove = Object.keys(themes);
    document.documentElement.classList.remove(...classesToRemove);
    document.documentElement.classList.add(currentThemeName.value);
    
    // 同时更新body上的主题类
    document.body.classList.remove(...classesToRemove);
    document.body.classList.add(currentThemeName.value);
  });

  onMounted(() => {
    if (!isValidTheme(currentThemeName.value)) {
      currentThemeName.value = defaultThemeName;
      localStorage.setItem(THEME_STORAGE_KEY, defaultThemeName);
    }
    applyThemeVariables(currentThemeName.value);
  });

  return {
    currentTheme: currentThemeName,
    setTheme,
  };
}