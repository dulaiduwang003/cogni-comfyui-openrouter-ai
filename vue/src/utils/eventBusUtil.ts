import mitt from 'mitt';

//登录弹窗
export const OPEN_AUTH_DIALOG = 'open-auth-dialog';
//兑换码弹窗
export const OPEN_REDEMPTION_DIALOG = 'open-redemption-dialog';
//登录成功
export const LOGIN_SUCCESS = 'login-success';
//公告弹窗
export const OPEN_NOTICE_ANNOUNCER = 'open-notice-announcer';

type Events = {
  [OPEN_AUTH_DIALOG]: void;
  [OPEN_REDEMPTION_DIALOG]: void;
  [LOGIN_SUCCESS]: void;
  [OPEN_NOTICE_ANNOUNCER]: void;
};

const emitter = mitt<Events>();

export default emitter; 