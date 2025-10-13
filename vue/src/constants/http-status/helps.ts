import { HttpStatus } from "./httpStatus";


export const isSuccessResponse = (code: number | string): boolean => {
    if (typeof code === 'string') {
      code = parseInt(code, 10);
    }
    return code === HttpStatus.SUCCESS;
 }; 