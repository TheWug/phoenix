package hello;

import models.TwoFAUserDetailsService;
import models.TwoFAUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //auth
        //    .inMemoryAuthentication()
        //        .withUser("user").password("password").roles("USER");

        //auth
        //        .inMemoryAuthentication()
        //        .withUser("admin").password("password").roles("ADMIN");


        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(org.postgresql.Driver.class);
        dataSource.setUrl("jdbc:postgresql://localhost:5432/auth");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");
/*
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select user_id as principal, password as credentials, true from tn_users where username = ?")
                .authoritiesByUsernameQuery("select user_id as principal, authority as role from tn_roles where user_id = ?")
                .rolePrefix("ROLE_");
                */

        auth
               .userDetailsService(userDetailsService());
               //.jdbcAuthentication()
                //.dataSource(dataSource);

    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new TwoFAUserDetailsService(userRepository);
    }
}