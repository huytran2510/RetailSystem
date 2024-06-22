package com.ufm.retailsystems.configure;

import com.ufm.retailsystems.configure.handle.CartQuantityInterceptor;
import com.ufm.retailsystems.configure.handle.LoginSuccessHandler;
import com.ufm.retailsystems.entities.enums.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

@Configuration
@EnableWebSecurity
public class WebsecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    @Qualifier("myUserDetailsService")
    private UserDetailsService myUserDetailsService;

    @Autowired
    @Qualifier("myCustomerDetailsService")
    private UserDetailsService myCustomerDetailsService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(myUserDetailsService).passwordEncoder(passwordEncoder);
        auth.userDetailsService(myCustomerDetailsService).passwordEncoder(passwordEncoder);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/add-to-cart","/quantity-product", "/add-quantity","/payment").permitAll()
                .antMatchers("/management-order","/management-product").hasAnyAuthority(ERole.USER.toString(), ERole.ADMIN.toString())
                .antMatchers("/resources/**", "/register","/customer/login","/login",("/mobile/**"),"/slider","/add-to-cart","/quantity-product","/cart",
                        "/remove-item", "/order-page/**", "/save-order").permitAll().anyRequest().authenticated().and()
                .exceptionHandling().accessDeniedPage("/login?error=access-denied")
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/products")
                .loginProcessingUrl("/user/process-login")
                .permitAll()
                .and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                .and()
                .formLogin()
                .loginPage("/customer/login")
                .defaultSuccessUrl("/products")
                .loginProcessingUrl("/customer/process-login")
                .permitAll()
                .successHandler(new LoginSuccessHandler()).and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll();

    }


//    @Bean
//    public AuthenticationManager customAuthenticationManager() throws Exception {
//        return authenticationManager();
//    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
//        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder());
//    }
}
