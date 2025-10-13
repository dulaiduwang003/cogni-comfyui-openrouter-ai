export interface Message {
  id: string
  content: string
  isUser: boolean
  timestamp: number
  userName?: string
  aiModel?: string
  aiModelIcon?: string
  reasoningContent?: string
  citations?: UrlCitation[]
  imageUrls?: string[]
  pdfFiles?: { url: string; filename: string }[]
  audioFiles?: { url: string }[]
}

export interface UrlCitation {
  title?: string
  url?: string
  content?: string
  startIndex?: number
  endIndex?: number
} 