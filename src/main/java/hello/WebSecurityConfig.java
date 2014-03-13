package hello;

import models.TwoFAUserDetailsService;
import models.TwoFAUserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;

@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TwoFAUserRepository userRepository;

    @Autowired
    AuthSuccessHandler authSuccessHandler;

    @Autowired
    CustomAuthenticationDetailsSource authenticationDetailsSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(new AuthFilter(), SwitchUserFilter.class)
            .authorizeRequests()
                .antMatchers("/", "/home", "/verify2fa").permitAll()
                .antMatchers("/welcome").hasRole("ADMIN")
                .anyRequest().authenticated();
        http
            .formLogin()
                .loginPage("/login")
                .permitAll()
                .successHandler(authSuccessHandler)
                .authenticationDetailsSource(authenticationDetailsSource)
                //.defaultSuccessUrl("/2fa", true)
                .and()
            .logout()
                .permitAll();


    }
    
    @Override
    public void configure(WebSecurity web)
    {
    	web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new TwoFAUserDetailsService(userRepository);
    }
}