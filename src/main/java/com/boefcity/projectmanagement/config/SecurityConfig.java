package com.boefcity.projectmanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig { //extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
/*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()  // Disables CSRF protection for simplicity in development (re-enable in production)
                .authorizeRequests()
                .antMatchers("/", "/users/registerDisplay").permitAll() // Allows unrestricted access to the homepage and registration page
                .anyRequest().authenticated()  // All other requests require authentication
                .and()
                .formLogin()
                .loginPage("/users/loginDisplay")  // Specifies the path to the custom login page
                .loginProcessingUrl("/users/login")  // Specifies the URL, it must handle POST request for login
                .defaultSuccessUrl("/", true)  // Redirect to homepage after login
                .permitAll()  // Allows unrestricted access to the login page
                .and()
                .logout()
                .permitAll();  // Allows unrestricted access to logout
    }

 */
}
