package com.isedol_clip_backend.config;

import com.isedol_clip_backend.auth.JwtAuthenticationEntryPoint;
import com.isedol_clip_backend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

@Slf4j
@EnableWebSecurity
@Component
@RequiredArgsConstructor
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
//                .cors().and().csrf().disable()
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

//                .headers()
//                .addHeaderWriter(new XFrameOptionsHeaderWriter(new WhiteListedAllowFromStrategy(Arrays.asList("localhost"))))
//                .frameOptions().sameOrigin();
//                .and()
//                .addFilterBefore(new AccessControllFilter(), AccessControllFilter.class)
//                .addFilterBefore(new AccessControlFilter(), UsernamePasswordAuthenticationFilter.class);
                .addFilterBefore(new JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}
