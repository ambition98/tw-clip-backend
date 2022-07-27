package com.isedol_clip_backend.config;

import com.isedol_clip_backend.auth.JwtAuthenticationEntryPoint;
import com.isedol_clip_backend.filter.AccessControllFilter;
import com.isedol_clip_backend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.WhiteListedAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Slf4j
@EnableWebSecurity(debug = false)
@Component
@RequiredArgsConstructor
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable()
                .authorizeRequests()
                    .mvcMatchers("/api/user/**").authenticated()
                    .mvcMatchers("/api/auth/**").authenticated()
                .anyRequest().permitAll()
                .and()

                .exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()

                .headers()
                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))))
                .frameOptions().sameOrigin()
                .and()
//                .addFilterBefore(new AccessControllFilter(), AccessControllFilter.class)
                .addFilterBefore(new AccessControllFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }

//    @Bean
//    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
//        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean
//                = new FilterRegistrationBean<>(new JwtAuthenticationFilter());
//
//        registrationBean.addUrlPatterns("/isedol-clip/user/**");
//        registrationBean.setOrder(1);
//        registrationBean.setName("JWT Filter");
//
//        return registrationBean;
//    }
}
