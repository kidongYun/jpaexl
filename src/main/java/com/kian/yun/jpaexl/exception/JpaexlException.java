package com.kian.yun.jpaexl.exception;

import com.kian.yun.jpaexl.code.JpaexlCode;

public class JpaexlException extends RuntimeException {
    private final JpaexlCode code;

    public JpaexlException(JpaexlCode code) {
        this.code = code;
    }

    public JpaexlException(JpaexlCode code, String msg) {
        super(msg);
        this.code = code;
    }

    public static JpaexlException of(JpaexlCode code) {
        return new JpaexlException(code, code.getDesc());
    }

    public static JpaexlException of(JpaexlCode code, String msg) {
        return new JpaexlException(code, msg);
    }
}
