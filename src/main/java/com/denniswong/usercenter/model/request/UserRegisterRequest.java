package com.denniswong.usercenter.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = -6863879653555565979L;

    private String userAccount;
    private String userPassword;
    private String checkPassword;
}
