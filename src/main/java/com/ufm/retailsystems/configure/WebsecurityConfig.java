package com.ufm.retailsystems.configure;

import com.ufm.retailsystems.configure.handle.CartQuantityInterceptor;
import com.ufm.retailsystems.configure.handle.CustomAccessDeniedHandler;
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
import org.springframework.security.web.access.AccessDeniedHandler;
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


    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/img/**", "/icon/**");
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/add-to-cart","/quantity-product","/vn-pay-callback", "/add-quantity","/payment","/add-quantity","/management-order-detail/**","/change-password","/payment-vnpay",
                        "/register","/customer/register","/update-order","remove-item","/add-quantity","/update-profile", "/vn-pay/**").permitAll()
                .antMatchers("/management-order/**").hasAnyAuthority(ERole.USER.toString())
                .antMatchers("/management-order/**","/management-product","/management-discount",
                        "/dashboard","/add-discount").hasAnyAuthority(ERole.ADMIN.toString())
                .antMatchers("/resources/**", "/register","/customer/login","/login",("/mobile/**"),"/slider","/add-to-cart",
                        "/quantity-product","/cart","/daily","/add-quantity","/add-quantity",
                        "/add-product","/customer/register","/update-order","/change-password",
                        "/remove-item", "/order-page/**", "/save-order","/order-tracking/**","/dashboard/**",
                        "/update-profile","/vn-pay/**","/payment/vn-pay-callback","/payment-vnpay","/payment",
                        "/product/**","/product/add","/add","/add-a").permitAll().anyRequest().authenticated().and()
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
                .successHandler(new LoginSuccessHandler()).and().logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler());

    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
