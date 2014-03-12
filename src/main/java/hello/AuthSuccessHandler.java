package hello;

import models.TwoFAUser;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {
    public AuthSuccessHandler() {}

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        TwoFAUser user = (TwoFAUser)authentication.getPrincipal();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            CustomAuthenticationDetails details = (CustomAuthenticationDetails)(auth.getDetails());
            details.setTwoFARequired(user.isTwoFactorEnabled());
        }
        if (user.isTwoFactorEnabled()) {
            response.sendRedirect("/2fa");
        } else {
            response.sendRedirect("/home");
        }
    }
}
