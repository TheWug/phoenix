package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class CustomAuthenticationDetailsSource extends WebAuthenticationDetailsSource {

    Logger logger = LoggerFactory.getLogger(CustomAuthenticationDetailsSource.class);

    @Override
    public CustomAuthenticationDetails buildDetails(HttpServletRequest context) {
        logger.debug("creating custom AuthDetails");
        return new CustomAuthenticationDetails(context);
        //return super.buildDetails(context);
    }
}
