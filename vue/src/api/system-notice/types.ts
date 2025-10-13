export namespace SystemNoticeApi {
  export type NoticeVo = {
    title: string
    publisher: string
    content: string
    createTime: string
  }

  export type NoticeSetDto = {
    title: string
    publisher: string
    content: string
    createTime?: string
  }
}


