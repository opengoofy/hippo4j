package cn.hippo4j.common.web.exception;

/**
 * Error code enum.
 *
 * @author chen.ma
 * @date 2021/3/19 16:07
 */
public enum ErrorCodeEnum {

    /**
     * UNKNOWN_ERROR
     */
    UNKNOWN_ERROR {
        @Override
        public String getCode() {
            return "1";
        }

        @Override
        public String getMessage() {
            return "UNKNOWN_ERROR";
        }
    },

    /**
     * VALIDATION_ERROR
     */
    VALIDATION_ERROR {
        @Override
        public String getCode() {
            return "2";
        }

        @Override
        public String getMessage() {
            return "VALIDATION_ERROR";
        }
    },

    /**
     * SERVICE_ERROR
     */
    SERVICE_ERROR {
        @Override
        public String getCode() {
            return "3";
        }

        @Override
        public String getMessage() {
            return "SERVICE_ERROR";
        }
    },

    /**
     * NOT_FOUND
     */
    NOT_FOUND {
        @Override
        public String getCode() {
            return "404";
        }

        @Override
        public String getMessage() {
            return "NOT_FOUND";
        }
    };

    public abstract String getCode();

    public abstract String getMessage();

}
