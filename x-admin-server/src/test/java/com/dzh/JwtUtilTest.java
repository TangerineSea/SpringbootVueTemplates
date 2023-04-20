package com.dzh;

import com.dzh.common.utils.JwtUtil;
import com.dzh.sys.entity.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author DongZH
 * @date 2023/4/18 15:43
 */
@SpringBootTest
public class JwtUtilTest {
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void testCreateJwt() {
        User user = new User();
        user.setUsername("张三");
        user.setPhone("123456");
        String token = jwtUtil.createToken(user);
        System.out.println(token);
    }

    @Test
    public void testParseJwt() {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI0ZWU4YWEzMi03MWE2LTRkM2UtYWJjMS0xZGQ4N2I4ZWQ4MmEiLCJzdWIiOiJ7XCJwaG9uZVwiOlwiMTIzNDU2XCIsXCJ1c2VybmFtZVwiOlwi5byg5LiJXCJ9IiwiaXNzIjoic3lzdGVtIiwiaWF0IjoxNjgxODAzOTAyLCJleHAiOjE2ODE4MDU3MDJ9.HTcD6CRJgiyuy2toxo3SkVleebcZRQmqimUDAXq7C44";
//        Claims claims = jwtUtil.parseToken(token);
//        System.out.println(claims);
        User user = jwtUtil.parseToken(token, User.class);
        System.out.println(user);
    }
}
