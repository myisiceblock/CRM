package com.bjpowernode.crm.web.filter;

import com.bjpowernode.crm.settings.domain.User;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFilter implements Filter {
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        System.out.println("进入到验证有没有登录过的过滤器");

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String path = request.getServletPath();
        //不应该被拦截的资源，自动放行请求
        if("/login.jsp".equals(path) || "/setting/user/login.do".equals(path)){
            chain.doFilter(req,resp);
        }else{
            HttpSession session = request.getSession();
            User user = (User)session.getAttribute("user");

            //如果user不为null，说明登陆过
            if(user != null){
                chain.doFilter(req,resp);

                //没有登陆过
            }else{
                //重定向到登录页
                response.sendRedirect(request.getContextPath() + "/login.jsp");
            }
        }
    }
}
