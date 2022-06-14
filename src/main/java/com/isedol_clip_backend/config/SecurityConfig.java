package com.isedol_clip_backend.config;

import com.isedol_clip_backend.filter.JwtAuthenticationFilter;
import com.isedol_clip_backend.util.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Slf4j
//@EnableWebSecurity(debug = true)
@EnableWebSecurity
@Component
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and().csrf().disable();

        http
                .authorizeRequests()
//                    .antMatchers("/user", "/oauth").authenticated()
                    .anyRequest().permitAll()
                .and()

                .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .headers()
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))))
                .frameOptions().sameOrigin()
                .and()

//                .formLogin()
//                        .loginPage("/login")
//                        .permitAll();                        .and()
//                .and()

                .logout();
//                        .permitAll();

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }

//    @Bean
//    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilter() {
//        FilterRegistrationBean<JwtAuthenticationFilter> registrationBean
//                = new FilterRegistrationBean<>(new JwtAuthenticationFilter());
//
//        registrationBean.addUrlPatterns("/oauth");
//
//        return registrationBean;
//    }
}
