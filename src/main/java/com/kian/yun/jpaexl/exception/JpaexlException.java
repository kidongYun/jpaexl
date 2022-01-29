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
}
