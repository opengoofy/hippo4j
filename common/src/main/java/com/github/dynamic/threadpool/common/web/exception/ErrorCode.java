package com.github.dynamic.threadpool.common.web.exception;

/**
 * 异常码
 *
 * @author chen.ma
 * @date 2021/3/19 16:07
 */
public enum ErrorCode {

    UNKNOWN_ERROR {
        @Override
        public String getCode() {
            return "1";
        }

        @Override
        public String getMessage() {
            return "未知错误";
        }
    },

    VALIDATION_ERROR {
        @Override
        public String getCode() {
            return "2";
        }

        @Override
        public String getMessage() {
            return "参数错误";
        }
    },

    SERVICE_ERROR {
        @Override
        public String getCode() {
            return "3";
        }

        @Override
        public String getMessage() {
            return "服务异常";
        }
    };

    public abstract String getCode();

    public abstract String getMessage();

}
