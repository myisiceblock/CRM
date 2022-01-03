package com.bjpowernode.crm.utils;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class MyBatisUtils {

    private static final String RESOURCE ="mybatis.xml";
    private static SqlSessionFactory factory ;

    static {
        try {
            InputStream in = Resources.getResourceAsStream(RESOURCE);
            //创建SqlSessionFactory对象,使用SqlSessionFactoryBuild
            factory = new SqlSessionFactoryBuilder().build(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //获取SqlSession方法
    public static SqlSession getSqlsession(){
        SqlSession sqlSession =null;
        if (factory != null){
            sqlSession = factory.openSession();//非自动提交事务
        }
        return sqlSession;
    }
}
