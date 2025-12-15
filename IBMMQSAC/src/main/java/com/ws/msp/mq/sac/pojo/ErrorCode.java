package com.ws.msp.mq.sac.pojo;

public interface ErrorCode {

    public static final int USER_NOT_FOUND = 1001;
    public static final int USER_SUSPENDED = 1002;
    public static final int USER_EXPIRED   = 1003;
    public static final int PWD_INCORRECT  = 1004;
    public static final int ACCESS_DENY    = 1005;
    
    public static final int RUNTIME_ERROR  = 9999;
}
