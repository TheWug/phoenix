package hello;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/hello").setViewName("hello");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/2fa").setViewName("2fa");
        registry.addViewController("/welcome").setViewName("welcome");
        registry.addViewController("/enrollform").setViewName("enrollform");
        registry.addViewController("/enrolled").setViewName("enrolled");
        registry.addViewController("/controlPanel").setViewName("controlPanel");
    }

}
