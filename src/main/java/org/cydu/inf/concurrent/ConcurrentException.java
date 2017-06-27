package org.cydu.inf.concurrent;


import static org.cydu.inf.concurrent.consts.ConcurrentExceptionCodes.getMsg;

public class ConcurrentException extends RuntimeException {
    private static final long serialVersionUID = 7126601916487893261L;

    private String code;

    public ConcurrentException(String code) {
        super("ConcurrentException[" + code + "]: "+getMsg(code) );
        this.code = code;
    }

    public ConcurrentException(String code, Throwable t) {
        super("ConcurrentException[" + code + "]: " + getMsg(code), t);
        this.code = code;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
