package hello;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName="AuthFilter")
public class AuthFilter implements Filter {

    Logger logger = LoggerFactory.getLogger(AuthFilter.class.getName());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        logger.info("init");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        logger.info("doFilter");

        CustomAuthenticationDetails details = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            if (auth.getDetails() instanceof CustomAuthenticationDetails) {
                details = (CustomAuthenticationDetails)(auth.getDetails());
            }
        }
        
        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        
        System.out.println("name "+req.getRequestURI());
        
        if ((details != null) &&
             details.isTwoFARequired() &&
            (details.isTwoFAValid() == false)) {
        		if(!req.getRequestURI().contains("login") && !req.getRequestURI().contains("2fa")) {
        			res.sendRedirect("/login");
        		}
        }
        else {
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        logger.info("destroy");

    }
}
