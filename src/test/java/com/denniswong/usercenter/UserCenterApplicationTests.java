package com.denniswong.usercenter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class UserCenterApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void md5Test() throws NoSuchAlgorithmException {
        String s = DigestUtils.md5DigestAsHex(("abcd" + "mypassword").getBytes());
        System.out.println(s);
    }

}
