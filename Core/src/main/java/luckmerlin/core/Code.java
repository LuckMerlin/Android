package luckmerlin.core;

public interface Code {

    int CODE_FAIL = 0b00 ;
    int CODE_SUCCEED = 0b01 ;
    int CODE_ALREADY = CODE_SUCCEED << 1 ;
    int CODE_ARGS = CODE_ALREADY << 1 ;
    int CODE_ERROR = CODE_ARGS << 1 ;
    int CODE_EXCEPTION = CODE_ERROR << 1 ;
    int CODE_UNKNOWN = CODE_EXCEPTION << 1 ;
    int CODE_EMPTY = CODE_UNKNOWN << 1 ;
    int CODE_NOT_EXIST = CODE_EMPTY << 1 ;
    int CODE_NONE_ACCESS = CODE_NOT_EXIST << 1 ;
    int CODE_OUT_OF_BOUNDS = CODE_NONE_ACCESS << 1 ;
    int CODE_CANCEL = CODE_OUT_OF_BOUNDS << 1 ;
    int CODE_ALREADY_DONE = CODE_SUCCEED | CODE_ALREADY ;
    int CODE_ALREADY_DOING = CODE_FAIL | CODE_ALREADY ;

    static boolean isSucceed(int code){
        return (code&CODE_SUCCEED)>0;
    }

    default boolean isCodeSucceed(int code){
        return Code.isSucceed(code);
    }

    default int codeFail(int ...codes){
        int code=Code.CODE_FAIL;
        if (null!=codes&&codes.length>0){
            for (int child:codes) {
                code|=(child&~CODE_SUCCEED);
            }
        }
        return code;
    }

    default int codeSucceed(int ...codes){
        int code=Code.CODE_SUCCEED;
        if (null!=codes&&codes.length>0){
            for (int child:codes) {
                code|=(child&~CODE_FAIL);
            }
        }
        return code;
    }
}
