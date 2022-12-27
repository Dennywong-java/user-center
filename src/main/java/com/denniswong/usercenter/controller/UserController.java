package com.denniswong.usercenter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.denniswong.usercenter.model.domain.User;
import com.denniswong.usercenter.model.request.UserLoginRequest;
import com.denniswong.usercenter.model.request.UserRegisterRequest;
import com.denniswong.usercenter.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.denniswong.usercenter.constant.UserConstant.ADMIN_ROLE;
import static com.denniswong.usercenter.constant.UserConstant.USER_LOGIN_STATE;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @PostMapping("/register")
    public Long userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null) return null;
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) return null;
        return userService.userRegister(userAccount, userPassword, checkPassword);
    }

    @PostMapping("/login")
    public User userLogin(@RequestBody UserLoginRequest UserLoginRequest, HttpServletRequest request){
        if(UserLoginRequest == null) return null;
        String userAccount = UserLoginRequest.getUserAccount();
        String userPassword = UserLoginRequest.getUserPassword();
        if(StringUtils.isAnyBlank(userAccount, userPassword)) return null;
        return userService.doLogin(userAccount, userPassword, request);
    }

    @GetMapping("/search")
    public List<User> searchUser(String username, HttpServletRequest request){
        // ToDo: 过滤返回信息 - 不返回用户密码登敏感信息
        
        // 鉴权：仅管理员可以操作
        if(!isAdmin(request)) return new ArrayList<>();
        // 执行操作
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.eq("username",username);
        }
        List<User> userList = userService.list(queryWrapper);
        return userList.stream().map(user -> userService.getSafeUser(user)).collect(Collectors.toList());
    }

    @GetMapping("/getCurrentUser")
    public User getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if(currentUser == null) return null;
        long userId = currentUser.getId();
        User userById = userService.getById(userId);
        // toDo 校验用户是否合法

        return userService.getSafeUser(userById);
    }
    @PostMapping("/delete")
    public boolean deleteUser(@RequestBody long id, HttpServletRequest request){
        // 鉴权：仅管理员可以操作
        if(!isAdmin(request)) return false;
        //执行操作
        if(id <= 0) return false;
        return userService.removeById(id);
    }

    /**
     * 是否为管理员
     * @param request
     * @return boolean
     */
    private boolean isAdmin(HttpServletRequest request){
        // 鉴权：是否为管理员
        Object UserAttribute = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) UserAttribute;
        if(user == null || user.getUserRole() != ADMIN_ROLE) return false;
        return true;
    }

}
