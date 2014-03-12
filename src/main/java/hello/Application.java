package hello;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.security.AuthenticationManagerConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;

@EnableAutoConfiguration
@Configuration
@ComponentScan({"hello","controllers","models"})
public class Application {

    public static void main(String[] args) throws Throwable {


        SpringApplication.run(Application.class, args);
    }

}
