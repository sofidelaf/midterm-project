package com.ironhack.midterm_project.Security;

import com.ironhack.midterm_project.Service.Impl.CustomUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailService customUserDetailService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers(HttpMethod.POST,"/accounts/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST,"/third_party_users").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH,"/accounts/third_party_transfer/**").hasRole("THIRD_PARTY_USER")
                .antMatchers(HttpMethod.PATCH,"/accounts/my_account/update_balance/**").authenticated()
                .antMatchers(HttpMethod.PATCH,"/accounts/my_account/transfer/**").authenticated()
                .antMatchers(HttpMethod.PATCH,"/accounts/update_balance/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/accounts/**/my_accounts").authenticated()
                .antMatchers(HttpMethod.GET,"/accounts/my_accounts").authenticated()
                .antMatchers(HttpMethod.GET,"/accounts/by_id/**").hasRole(("ADMIN"))
                .antMatchers(HttpMethod.GET,"/accounts/**").hasRole(("ADMIN"))
                .antMatchers(HttpMethod.GET,"/accounts").hasRole(("ADMIN"))
                .antMatchers(HttpMethod.GET,"/third_party_users").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET,"/users**").hasRole("ADMIN")
                .anyRequest().permitAll();
    }
}
