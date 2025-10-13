declare module '@google/model-viewer' {
  export interface ModelViewerElement extends HTMLElement {
    src: string
    alt: string
    autoRotate: boolean
    cameraControls: boolean
    environmentImage: string
    exposure: number
    shadowIntensity: number
    toneMapping: string
    loading: 'auto' | 'lazy' | 'eager'
  }
}

declare global {
  namespace JSX {
    interface IntrinsicElements {
      'model-viewer': React.DetailedHTMLProps<React.HTMLAttributes<HTMLElement>, HTMLElement> & {
        src?: string
        alt?: string
        'auto-rotate'?: boolean
        'camera-controls'?: boolean
        'environment-image'?: string
        exposure?: number
        'shadow-intensity'?: number
        'tone-mapping'?: string
        loading?: 'auto' | 'lazy' | 'eager'
      }
    }
  }
}

export {} 