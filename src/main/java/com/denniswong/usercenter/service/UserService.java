package com.denniswong.usercenter.service;

import com.denniswong.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author xiganghuang
* @description 针对表【user】的数据库操作Service
* @createDate 2022-12-24 02:22:30
*/
public interface UserService extends IService<User> {
    /**
     * 用户登入验证
     * @param userAccount
     * @param userPassword
     * @param checkPassword
     * @return 用户ID
     */

    long userRegister(String userAccount, String userPassword, String checkPassword);

    User doLogin(String userAccount, String userPassword, HttpServletRequest request);

    User getSafeUser(User user);
}
