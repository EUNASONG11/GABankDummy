package com.example.projectdummy;

import net.datafaker.Faker;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.annotation.Rollback;

import java.util.Locale;

@MybatisTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false)
public class DummyDefault {
    @Autowired protected SqlSessionFactory sqlSessionFactory;
    protected Faker kofaker = new Faker(new Locale("ko"));
    protected Faker enfaker = new Faker(new Locale("en"));
}
