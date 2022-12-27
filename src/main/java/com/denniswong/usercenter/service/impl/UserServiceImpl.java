package com.denniswong.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.denniswong.usercenter.model.domain.User;
import com.denniswong.usercenter.service.UserService;
import com.denniswong.usercenter.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.denniswong.usercenter.constant.UserConstant.USER_LOGIN_STATE;

/**
* @author xiganghuang
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-12-24 02:22:30
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Resource
    private UserMapper userMapper;
    private static final String SALT = "user_center";
    private static final String VALIDPATTERNS = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 校验输入长度是否符合规则
        if(StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) return -1;
        if(userAccount.length() < 4 || userPassword.length()< 8 || checkPassword.length() < 8) return -1;
        // 是否包含特殊字符
        Matcher matcher = Pattern.compile(VALIDPATTERNS).matcher(userAccount);
        if(matcher.find()) return -1;
        // 是否账户已经存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if(count > 0) return -1;
        // 密码和校验密码相同
        if(!userPassword.equals(checkPassword)) return -1;
        // 加密
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(md5Password);
        boolean savedResult = this.save(user);
        if(!savedResult) return -1;
        return user.getId();
    }

    @Override
    public User doLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 校验输入长度是否符合规则
        if(StringUtils.isAnyBlank(userAccount, userPassword)) return null;
        if(userAccount.length() < 4 || userPassword.length()< 8) return null;
        // 是否包含特殊字符
        Matcher matcher = Pattern.compile(VALIDPATTERNS).matcher(userAccount);
        if(matcher.find()) return null;
        // 加密
        String md5Password = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 是否账户已经存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount",userAccount);
        queryWrapper.eq("userPassword",md5Password);
        User user = userMapper.selectOne(queryWrapper);
        if(user == null){
            log.info("User Login Failed. Inputs Not Match.");
            return null;
        }
        // 用户脱敏
        User safetyUser = getSafeUser(user);
        // 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return safetyUser;
    }

    /**
     * 用户脱敏
     * @param user
     * @return
     */
    @Override
    public User getSafeUser(User user){
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        return safetyUser;
    }
}




