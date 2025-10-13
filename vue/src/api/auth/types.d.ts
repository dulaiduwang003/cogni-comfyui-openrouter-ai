export namespace PasswordLoginApi {

    export interface Params {
        email: string
        password: string
    }
    
}

export namespace EmailLoginApi {

    
    export interface Params {
        email: string
        code: string
    }
    
}




export namespace GetVerificationCodeApi {

    
    export interface Params {
        email: string
        password: string
    }
    
}


export namespace RegisterApi {

    export interface Params {
        email: string
        code: string
        password: string
    }
}

export namespace ForgotPasswordApi {
    export interface Params {
        email: string
        code: string
        password: string
    }
}

