<template>
    <div class="container" @mousemove="onMouseMove" ref="containerRef">
        <!-- 动态背景层 -->
        <div class="bg-layers">
            <!-- 渐变背景 -->
            <div class="gradient-bg"></div>

            <!-- 装饰圆圈 -->
            <div class="bg-decoration">
                <div class="bg-circle bg-circle-1"></div>
                <div class="bg-circle bg-circle-2"></div>
                <div class="bg-circle bg-circle-3"></div>
                <div class="bg-circle bg-circle-4"></div>
                <div class="bg-circle bg-circle-5"></div>
            </div>

            <!-- 优化后的粒子系统 -->
            <div class="particle-system">
                <!-- 主要粒子 -->
                <div class="particle-dots">
                    <div v-for="i in 15" :key="`dot-${i}`" class="particle-dot" :style="getDotStyle(i)"></div>
                </div>

                <!-- 光点粒子 -->
                <div class="light-particles">
                    <div v-for="i in 8" :key="`light-${i}`" class="light-particle" :style="getLightParticleStyle(i)"></div>
                </div>

                <!-- 数据流粒子 -->
                <div class="data-stream">
                    <div v-for="i in 5" :key="`stream-${i}`" class="stream-particle" :style="getStreamStyle(i)"></div>
                </div>

                <!-- 星尘粒子 -->
                <div class="stardust-particles">
                    <div v-for="i in 10" :key="`stardust-${i}`" class="stardust-particle" :style="getStardustStyle(i)"></div>
                </div>
            </div>

            <!-- 简化光波效果 -->
            <div class="wave-effects">
                <div class="wave-ring wave-ring-1"></div>
                <div class="wave-ring wave-ring-2"></div>
            </div>

            <!-- 扫描线 -->
            <div class="scan-overlay">
                <div class="scan-line scan-line-h"></div>
                <div class="scan-line scan-line-v"></div>
                <div class="scan-grid"></div>
            </div>

            <!-- 鼠标跟随光效 -->
            <div class="mouse-glow" :style="mouseGlowStyle"></div>

            <!-- 简化能量脉冲 -->
            <div class="energy-pulses">
                <div class="energy-pulse pulse-1"></div>
            </div>
        </div>
        
        <div class="content-wrapper" @mouseenter="onHover" @mouseleave="onLeave">
            <div class="text-section">
                <h1 class="title">{{ displayTitle }}<span class="typing-cursor" v-show="showCursor">|</span></h1>
                <p class="description" :class="{ 'typing-complete': typingComplete }">{{ displayDesc }}</p>
            </div>
            <div class="animation-section" :class="{ 'hovered': isHovered }">
                <div class="tech-animation">
                    <svg viewBox="0 0 120 120" class="tech-svg" aria-hidden="true">
                        <defs>
                            <filter id="fogBlur" x="-50%" y="-50%" width="200%" height="200%">
                                <feGaussianBlur in="SourceGraphic" stdDeviation="6" />
                            </filter>
                            <radialGradient id="fogGrad" cx="50%" cy="50%" r="50%">
                                <stop offset="0%" :stop-color="'rgba('+getPrimaryRgb()+',0.35)'" />
                                <stop offset="60%" :stop-color="'rgba('+getPrimaryRgb()+',0.18)'" />
                                <stop offset="100%" :stop-color="'rgba('+getPrimaryRgb()+',0)'" />
                            </radialGradient>
                            <linearGradient id="rayGrad" x1="0%" y1="0%" x2="0%" y2="100%">
                                <stop offset="0%" :stop-color="'rgba('+getPrimaryRgb()+',0)'" />
                                <stop offset="50%" :stop-color="'rgba('+getPrimaryRgb()+',0.7)'" />
                                <stop offset="100%" :stop-color="'rgba('+getPrimaryRgb()+',0)'" />
                            </linearGradient>
                        </defs>

                        <!-- 雾化光晕（使用矩形避免圆形外观） -->
                        <rect x="10" y="10" width="100" height="100" fill="url(#fogGrad)" filter="url(#fogBlur)">
                            <animate attributeName="opacity" values="0.18;0.3;0.18" dur="6s" repeatCount="indefinite" />
                        </rect>

                        <!-- 核心六边形 -->
                        <polygon points="60,32 84.25,46 84.25,74 60,88 35.75,74 35.75,46" fill="none" stroke="rgba(255, 255, 255, 0.8)" stroke-width="2">
                            <animate attributeName="stroke-opacity" values="0.6;1;0.6" dur="3s" repeatCount="indefinite" />
                        </polygon>

                        <!-- 外层六边形（扫描虚线） -->
                        <polygon points="60,24 91.18,42 91.18,78 60,96 28.82,78 28.82,42" fill="none" stroke="rgba(255, 255, 255, 0.6)" stroke-width="1.5" stroke-dasharray="10 6" opacity="0.6">
                            <animate attributeName="stroke-dashoffset" values="0;32" dur="5s" repeatCount="indefinite" />
                        </polygon>

                        <!-- 中心小菱形（呼吸动画） -->
                        <polygon points="60,56 64,60 60,64 56,60" fill="rgba(255, 255, 255, 0.9)">
                            <animate attributeName="opacity" values="0.6;1;0.6" dur="2.4s" repeatCount="indefinite" />
                            <animateTransform attributeName="transform" type="scale" values="1;1.15;1" dur="2.4s" additive="sum" repeatCount="indefinite" />
                        </polygon>

                        <!-- 角向电路线 -->
                        <g stroke="rgba(255, 255, 255, 0.7)" stroke-opacity="0.6" stroke-width="1">
                            <polyline points="60,24 60,14 70,14" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" repeatCount="indefinite" />
                            </polyline>
                            <polyline points="91.18,42 101,42 101,52" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" begin="0.5s" repeatCount="indefinite" />
                            </polyline>
                            <polyline points="91.18,78 101,78 101,68" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" begin="1s" repeatCount="indefinite" />
                            </polyline>
                            <polyline points="60,96 60,106 50,106" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" begin="1.5s" repeatCount="indefinite" />
                            </polyline>
                            <polyline points="28.82,78 19,78 19,68" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" begin="2s" repeatCount="indefinite" />
                            </polyline>
                            <polyline points="28.82,42 19,42 19,52" fill="none">
                                <animate attributeName="stroke-opacity" values="0.2;0.8;0.2" dur="3s" begin="2.5s" repeatCount="indefinite" />
                            </polyline>
                        </g>

                        <!-- 直线射线（缓慢旋转） -->
                        <g opacity="0.7">
                            <animateTransform attributeName="transform" type="rotate" values="0 60 60;360 60 60" dur="18s" repeatCount="indefinite" />
                            <g>
                                <line x1="60" y1="6" x2="60" y2="22" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                                <line x1="60" y1="98" x2="60" y2="114" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                            </g>
                            <g transform="rotate(45 60 60)">
                                <line x1="60" y1="6" x2="60" y2="22" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                                <line x1="60" y1="98" x2="60" y2="114" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                            </g>
                            <g transform="rotate(90 60 60)">
                                <line x1="60" y1="6" x2="60" y2="22" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                                <line x1="60" y1="98" x2="60" y2="114" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                            </g>
                            <g transform="rotate(135 60 60)">
                                <line x1="60" y1="6" x2="60" y2="22" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                                <line x1="60" y1="98" x2="60" y2="114" stroke="url(#rayGrad)" stroke-width="2" stroke-linecap="round" />
                            </g>
                        </g>
                    </svg>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, reactive, computed } from 'vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()

const containerRef = ref(null)
const titleRef = ref(null)
const descRef = ref(null)
const displayTitle = ref('')
const displayDesc = ref('')
const showCursor = ref(true)
const typingComplete = ref(false)
const isHovered = ref(false)

// 主题主色 RGB，用于SVG渐变
const primaryRgb = ref('')
const getPrimaryRgb = () => primaryRgb.value || '64,158,255'

// 鼠标位置状态
const mousePosition = reactive({
    x: 0,
    y: 0,
    isInside: false
})

// 鼠标光效样式
const mouseGlowStyle = ref({
    left: '50%',
    top: '50%',
    opacity: 0
})

const fullTitle = computed(() => t('comfyui.banner.title'))
const fullDesc = computed(() => t('comfyui.banner.description'))

let titleIndex = 0
let descIndex = 0
let titleTimer = null
let descTimer = null
let cursorTimer = null

// 打字机效果
const typeTitle = () => {
    if (titleIndex < fullTitle.value.length) {
        displayTitle.value += fullTitle.value[titleIndex]
        titleIndex++
        titleTimer = setTimeout(typeTitle, 100)
    } else {
        // 标题打完后开始描述
        setTimeout(typeDesc, 300)
    }
}

const typeDesc = () => {
    if (descIndex < fullDesc.value.length) {
        displayDesc.value += fullDesc.value[descIndex]
        descIndex++
        descTimer = setTimeout(typeDesc, 50)
    } else {
        typingComplete.value = true
        showCursor.value = false
    }
}

// 光标闪烁
const blinkCursor = () => {
    showCursor.value = !showCursor.value
    cursorTimer = setTimeout(blinkCursor, 500)
}

// 优化后的粒子样式生成
const getDotStyle = (index) => {
    const size = 2 + Math.random() * 2
    const x = Math.random() * 100
    const y = Math.random() * 100
    const delay = Math.random() * 5
    const duration = 3 + Math.random() * 3

    return {
        left: x + '%',
        top: y + '%',
        width: size + 'px',
        height: size + 'px',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
    }
}

// 简化光点粒子样式
const getLightParticleStyle = (index) => {
    const size = 3 + Math.random() * 3
    const x = Math.random() * 100
    const y = Math.random() * 100
    const delay = Math.random() * 6
    const duration = 4 + Math.random() * 4

    return {
        left: x + '%',
        top: y + '%',
        width: size + 'px',
        height: size + 'px',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
    }
}

// 简化数据流样式
const getStreamStyle = (index) => {
    const x = Math.random() * 100
    const y = Math.random() * 100
    const delay = Math.random() * 8
    const duration = 5 + Math.random() * 5
    const angle = Math.random() * 360

    return {
        left: x + '%',
        top: y + '%',
        animationDelay: delay + 's',
        animationDuration: duration + 's',
        transform: `rotate(${angle}deg)`
    }
}

// 简化星尘粒子样式
const getStardustStyle = (index) => {
    const x = Math.random() * 100
    const y = Math.random() * 100
    const size = 1 + Math.random() * 2
    const delay = Math.random() * 10
    const duration = 4 + Math.random() * 6

    return {
        left: x + '%',
        top: y + '%',
        width: size + 'px',
        height: size + 'px',
        animationDelay: delay + 's',
        animationDuration: duration + 's'
    }
}

// 鼠标移动事件
const onMouseMove = (event) => {
    if (!containerRef.value) return

    const rect = containerRef.value.getBoundingClientRect()
    const x = ((event.clientX - rect.left) / rect.width) * 100
    const y = ((event.clientY - rect.top) / rect.height) * 100

    mousePosition.x = x
    mousePosition.y = y
    mousePosition.isInside = true

    mouseGlowStyle.value = {
        left: x + '%',
        top: y + '%',
        opacity: 0.8,
        transform: 'translate(-50%, -50%) scale(1)'
    }
}

// 鼠标交互
const onHover = () => {
    isHovered.value = true
}

const onLeave = () => {
    isHovered.value = false
    mousePosition.isInside = false
    mouseGlowStyle.value = {
        ...mouseGlowStyle.value,
        opacity: 0
    }
}

onMounted(() => {
    // 读取主题主色RGB
    try {
        const rootStyles = getComputedStyle(document.documentElement)
        const rgb = rootStyles.getPropertyValue('--el-color-primary-rgb').trim()
        if (rgb) primaryRgb.value = rgb
    } catch (e) {
        // ignore
    }

    // 延迟开始打字机效果
    setTimeout(() => {
        typeTitle()
        blinkCursor()
    }, 500)
})

onUnmounted(() => {
    if (titleTimer) clearTimeout(titleTimer)
    if (descTimer) clearTimeout(descTimer)
    if (cursorTimer) clearTimeout(cursorTimer)
})
</script>

<style scoped>
.container {
    width: 100%;
    height: 200px;
    position: relative;
    background: linear-gradient(135deg, var(--custom-gradient-start) 0%, var(--custom-gradient-end) 100%);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 16px;
    box-sizing: border-box;
    overflow: hidden;
    box-shadow: 0 8px 32px rgba(var(--el-color-primary-rgb), 0.15);
}

/* 背景层容器 */
.bg-layers {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 1;
}

/* 渐变背景 */
.gradient-bg {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, 
        rgba(var(--el-color-primary-rgb), 0.9) 0%,
        rgba(var(--el-color-primary-rgb), 0.7) 50%,
        rgba(var(--el-color-primary-rgb), 0.5) 100%),
    radial-gradient(
        ellipse at top right,
        rgba(255, 255, 255, 0.1) 0%,
        transparent 50%
    ),
    radial-gradient(
        ellipse at bottom left,
        rgba(255, 255, 255, 0.05) 0%,
        transparent 50%
    );
    animation: gradient-pulse 8s ease-in-out infinite;
}

@keyframes gradient-pulse {
    0%, 100% { opacity: 0.6; }
    50% { opacity: 1; }
}

/* 装饰圆圈 */
.bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.bg-circle {
    position: absolute;
    border: 1px solid rgba(255, 255, 255, 0.2);
    border-radius: 50%;
    opacity: 0.3;
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.1);
}

.bg-circle-1 {
    width: 120px;
    height: 120px;
    top: -30px;
    right: -30px;
    animation: rotate-glow 20s linear infinite;
    border-width: 2px;
}

.bg-circle-2 {
    width: 80px;
    height: 80px;
    bottom: -20px;
    left: -20px;
    animation: rotate-pulse 15s linear infinite reverse;
}

.bg-circle-3 {
    width: 60px;
    height: 60px;
    top: 50%;
    left: 10%;
    transform: translateY(-50%);
    animation: rotate-float 25s linear infinite;
    opacity: 0.2;
}

.bg-circle-4 {
    width: 40px;
    height: 40px;
    top: 20%;
    right: 20%;
    animation: rotate-bounce 18s ease-in-out infinite;
    border-color: rgba(255, 255, 255, 0.3);
}

.bg-circle-5 {
    width: 100px;
    height: 100px;
    bottom: 10%;
    right: 15%;
    animation: rotate-scale 22s ease-in-out infinite;
    opacity: 0.15;
}

@keyframes rotate {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

@keyframes rotate-glow {
    0% {
        transform: rotate(0deg) scale(1);
        box-shadow: 0 0 20px rgba(var(--el-color-primary-rgb), 0.1);
    }
    50% {
        transform: rotate(180deg) scale(1.05);
        box-shadow: 0 0 30px rgba(var(--el-color-primary-rgb), 0.2);
    }
    100% {
        transform: rotate(360deg) scale(1);
        box-shadow: 0 0 20px rgba(var(--el-color-primary-rgb), 0.1);
    }
}

@keyframes rotate-pulse {
    0% {
        transform: rotate(0deg);
        opacity: 0.3;
    }
    25% {
        opacity: 0.6;
    }
    50% {
        transform: rotate(180deg);
        opacity: 0.3;
    }
    75% {
        opacity: 0.6;
    }
    100% {
        transform: rotate(360deg);
        opacity: 0.3;
    }
}

@keyframes rotate-float {
    0% {
        transform: translateY(-50%) rotate(0deg) translateX(0);
    }
    25% {
        transform: translateY(-50%) rotate(90deg) translateX(5px);
    }
    50% {
        transform: translateY(-50%) rotate(180deg) translateX(0);
    }
    75% {
        transform: translateY(-50%) rotate(270deg) translateX(-5px);
    }
    100% {
        transform: translateY(-50%) rotate(360deg) translateX(0);
    }
}

@keyframes rotate-bounce {
    0%, 100% {
        transform: rotate(0deg) scale(1);
    }
    25% {
        transform: rotate(90deg) scale(1.1);
    }
    50% {
        transform: rotate(180deg) scale(0.9);
    }
    75% {
        transform: rotate(270deg) scale(1.1);
    }
}

@keyframes rotate-scale {
    0% {
        transform: rotate(0deg) scale(1);
        opacity: 0.15;
    }
    33% {
        transform: rotate(120deg) scale(1.2);
        opacity: 0.25;
    }
    66% {
        transform: rotate(240deg) scale(0.8);
        opacity: 0.1;
    }
    100% {
        transform: rotate(360deg) scale(1);
        opacity: 0.15;
    }
}

/* 增强粒子系统 */
.particle-system {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.particle-dots {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.particle-dot {
    position: absolute;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 50%;
    opacity: 0.4;
    animation: dot-float-enhanced linear infinite;
    box-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
}

/* 光点粒子 */
.light-particles {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.light-particle {
    position: absolute;
    background: radial-gradient(circle, rgba(255, 255, 255, 0.8) 0%, transparent 70%);
    border-radius: 50%;
    opacity: 0.6;
    animation: light-pulse linear infinite;
    filter: blur(1px);
}

/* 数据流粒子 */
.data-stream {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.stream-particle {
    position: absolute;
    width: 20px;
    height: 2px;
    background: linear-gradient(90deg,
        transparent,
        rgba(255, 255, 255, 0.6),
        rgba(255, 255, 255, 0.8),
        rgba(255, 255, 255, 0.6),
        transparent);
    animation: stream-flow linear infinite;
    opacity: 0.7;
}

@keyframes dot-float-enhanced {
    0% {
        transform: translateY(0) scale(1);
        opacity: 0;
    }
    10% {
        opacity: 0.5;
    }
    90% {
        opacity: 0.5;
    }
    100% {
        transform: translateY(-20px) scale(0.8);
        opacity: 0;
    }
}

@keyframes light-pulse {
    0% {
        transform: scale(1);
        opacity: 0;
    }
    50% {
        transform: scale(1.2);
        opacity: 0.6;
    }
    100% {
        transform: scale(1.4);
        opacity: 0;
    }
}

@keyframes stream-flow {
    0% {
        transform: translateX(-100px) translateY(0);
        opacity: 0;
    }
    10% {
        opacity: 0.7;
    }
    90% {
        opacity: 0.7;
    }
    100% {
        transform: translateX(calc(100vw + 100px)) translateY(-20px);
        opacity: 0;
    }
}



/* 星尘粒子 */
.stardust-particles {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.stardust-particle {
    position: absolute;
    background: rgba(255, 255, 255, 0.4);
    border-radius: 50%;
    animation: stardust-twinkle ease-in-out infinite alternate;
}

@keyframes stardust-twinkle {
    0% {
        opacity: 0.3;
        transform: scale(1);
    }
    50% {
        opacity: 0.7;
        transform: scale(1.1);
    }
    100% {
        opacity: 0.3;
        transform: scale(1);
    }
}



/* 光波扩散效果 */
.wave-effects {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 100%;
    height: 100%;
    pointer-events: none;
}

.wave-ring {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    border: 1px solid rgba(255, 255, 255, 0.5);
    border-radius: 50%;
    opacity: 0;
    animation: wave-expand linear infinite;
}

.wave-ring-1 {
    animation-duration: 4s;
    animation-delay: 0s;
}

.wave-ring-2 {
    animation-duration: 4s;
    animation-delay: 1.3s;
}

.wave-ring-3 {
    animation-duration: 4s;
    animation-delay: 2.6s;
}

@keyframes wave-expand {
    0% {
        width: 0;
        height: 0;
        opacity: 0.8;
    }
    50% {
        opacity: 0.4;
    }
    100% {
        width: 200px;
        height: 200px;
        opacity: 0;
    }
}

/* 扫描线效果 */
.scan-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.scan-line {
    position: absolute;
    background: linear-gradient(90deg,
        transparent,
        rgba(255, 255, 255, 0.4),
        transparent);
    opacity: 0;
    box-shadow: 0 0 20px rgba(255, 255, 255, 0.2);
}

.scan-line-h {
    width: 100%;
    height: 2px;
    top: 50%;
    left: 0;
    animation: scan-horizontal-enhanced 8s ease-in-out infinite;
}

.scan-line-v {
    width: 2px;
    height: 100%;
    top: 0;
    left: 50%;
    background: linear-gradient(180deg,
        transparent,
        rgba(255, 255, 255, 0.4),
        transparent);
    animation: scan-vertical-enhanced 10s ease-in-out infinite 3s;
}

.scan-grid {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-image:
        linear-gradient(rgba(255, 255, 255, 0.05) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.05) 1px, transparent 1px);
    background-size: 20px 20px;
    opacity: 0;
    animation: grid-pulse 12s ease-in-out infinite;
}

/* 鼠标跟随光效 */
.mouse-glow {
    position: absolute;
    width: 100px;
    height: 100px;
    background: radial-gradient(circle,
        rgba(var(--el-color-primary-rgb), 0.2) 0%,
        rgba(var(--el-color-primary-rgb), 0.1) 30%,
        transparent 70%);
    border-radius: 50%;
    pointer-events: none;
    transition: all 0.3s ease;
    filter: blur(10px);
    z-index: 1;
}

/* 简化能量脉冲 */
.energy-pulses {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.energy-pulse {
    position: absolute;
    width: 3px;
    height: 3px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 50%;
    animation: energy-pulse-move linear infinite;
}

.pulse-1 {
    top: 30%;
    left: 20%;
    animation-duration: 8s;
    animation-delay: 0s;
}

@keyframes energy-pulse-move {
    0% {
        transform: translate(0, 0);
        opacity: 0;
    }
    20% {
        opacity: 0.6;
    }
    80% {
        opacity: 0.6;
    }
    100% {
        transform: translate(100px, -30px);
        opacity: 0;
    }
}

@keyframes scan-horizontal-enhanced {
    0%, 90%, 100% {
        opacity: 0;
        transform: translateY(0) scaleX(0.5);
    }
    5%, 85% {
        opacity: 1;
        transform: scaleX(1);
    }
    45% {
        transform: translateY(-40px) scaleX(1.2);
    }
}

@keyframes scan-vertical-enhanced {
    0%, 90%, 100% {
        opacity: 0;
        transform: translateX(0) scaleY(0.5);
    }
    5%, 85% {
        opacity: 1;
        transform: scaleY(1);
    }
    45% {
        transform: translateX(40px) scaleY(1.2);
    }
}

@keyframes grid-pulse {
    0%, 80%, 100% {
        opacity: 0;
    }
    10%, 70% {
        opacity: 0.3;
    }
    40% {
        opacity: 0.6;
    }
}

.content-wrapper {
    position: relative;
    z-index: 2;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 20px 30px;
    border-radius: 16px;
    width: 100%;
    height: 100%;
    box-sizing: border-box;
    transition: all 0.3s ease;
    transform-style: preserve-3d;
}

.content-wrapper:hover {
    box-shadow: 0 12px 40px rgba(var(--el-color-primary-rgb), 0.3);
    transform: translateY(-4px) scale(1.02);
}

.container:hover .gradient-bg {
    animation-duration: 5s;
    transform: scale(1.2);
}

.container:hover .particle-dot {
    animation-duration: 2.5s;
    opacity: 0.8;
}

.container:hover .bg-circle {
    animation-duration: 12s;
    opacity: 0.5;
}

.text-section {
    flex: 1;
    max-width: 500px;
    padding-right: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
}

.title {
    font-size: 1.5em;
    font-weight: 600;
    margin-bottom: 8px;
    color: white;
    line-height: 1.3;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.description {
    font-size: 0.9em;
    color: rgba(255, 255, 255, 0.9);
    line-height: 1.5;
    margin: 0;
    opacity: 0.9;
    transition: opacity 0.3s ease;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.description.typing-complete {
    opacity: 1;
}

.typing-cursor {
    color: rgba(255, 255, 255, 0.8);
    font-weight: 500;
    animation: cursor-blink 1s infinite;
}

@keyframes cursor-blink {
    0%, 50% { opacity: 1; }
    51%, 100% { opacity: 0; }
}

.animation-section {
    flex-shrink: 0;
    display: flex;
    align-items: center;
    justify-content: center;
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    transform-style: preserve-3d;
}

.animation-section.hovered {
    transform: scale(1.1) rotateY(5deg) rotateX(2deg);
    filter: drop-shadow(0 10px 20px rgba(var(--el-color-primary), 0.3));
}

.tech-animation {
    width: 180px;
    height: 180px;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
    transition: all 0.3s ease;
}

.tech-animation::before {
    content: '';
    position: absolute;
    top: -10px;
    left: -10px;
    right: -10px;
    bottom: -10px;
    background: radial-gradient(circle,
        rgba(var(--el-color-primary), 0.1) 0%,
        transparent 70%);
    border-radius: 50%;
    opacity: 0;
    transition: opacity 0.3s ease;
    animation: tech-glow 3s ease-in-out infinite;
}

.animation-section.hovered .tech-animation::before {
    opacity: 1;
}

@keyframes tech-glow {
    0%, 100% {
        transform: scale(1);
        opacity: 0.3;
    }
    50% {
        transform: scale(1.2);
        opacity: 0.6;
    }
}

.tech-svg {
    width: 100%;
    height: 100%;
}

.core-circle {
    fill: var(--el-color-primary);
}

.node {
    fill: var(--el-color-primary);
}

/* 性能优化 */
.container {
    will-change: transform;
}

.bg-layers * {
    will-change: transform, opacity;
}

.particle-system * {
    will-change: transform, opacity;
}

/* 响应式调整 */
@media (max-width: 768px) {
    .container {
        height: 180px;
    }

    .content-wrapper {
        flex-direction: column;
        text-align: center;
        padding: 15px;
    }

    .content-wrapper:hover {
        transform: translateY(-2px) scale(1.01);
    }

    .text-section {
        width: 100%;
        padding-right: 0;
        margin-bottom: 10px;
    }

    .title {
        font-size: 1.3em;
    }

    .description {
        font-size: 0.85em;
    }

    .animation-section {
        justify-content: center;
    }

    .animation-section.hovered {
        transform: scale(1.05) rotateY(2deg);
    }

    .tech-animation {
        width: 100px;
        height: 80px;
    }

    /* 移动端减少粒子数量以提升性能 */
    .particle-dot:nth-child(n+8) {
        display: none;
    }

    .light-particle:nth-child(n+4) {
        display: none;
    }

    .stream-particle:nth-child(n+3) {
        display: none;
    }

    .stardust-particle:nth-child(n+5) {
        display: none;
    }

    /* 简化移动端动画 */
    .bg-circle-4,
    .bg-circle-5 {
        display: none;
    }

    .wave-ring-3 {
        display: none;
    }
}

@media (max-width: 480px) {
    .container {
        height: 160px;
    }

    .content-wrapper {
        padding: 12px;
    }

    .title {
        font-size: 1.2em;
    }

    .description {
        font-size: 0.8em;
    }

    .tech-animation {
        width: 80px;
        height: 60px;
    }

    /* 超小屏幕进一步减少粒子 */
    .particle-dot:nth-child(n+6) {
        display: none;
    }

    .light-particle:nth-child(n+3) {
        display: none;
    }

    .stream-particle:nth-child(n+2) {
        display: none;
    }

    .stardust-particle:nth-child(n+4) {
        display: none;
    }
}

/* 暗色主题优化 */
@media (prefers-color-scheme: dark) {
    .particle-dot {
        box-shadow: 0 0 15px rgba(var(--el-color-primary-rgb), 0.5);
    }

    .mouse-glow {
        background: radial-gradient(circle,
            rgba(var(--el-color-primary-rgb), 0.3) 0%,
            rgba(var(--el-color-primary-rgb), 0.15) 30%,
            transparent 70%);
    }
}

/* 减少动画偏好设置 */
@media (prefers-reduced-motion: reduce) {
    * {
        animation-duration: 0.01ms !important;
        animation-iteration-count: 1 !important;
        transition-duration: 0.01ms !important;
    }

    .content-wrapper:hover {
        transform: none;
    }

    .animation-section.hovered {
        transform: scale(1.02);
    }
}
</style>