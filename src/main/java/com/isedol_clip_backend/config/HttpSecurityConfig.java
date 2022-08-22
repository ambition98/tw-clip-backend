package com.isedol_clip_backend.config;

import com.isedol_clip_backend.auth.JwtAuthenticationEntryPoint;
import com.isedol_clip_backend.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@EnableWebSecurity
@Component
@RequiredArgsConstructor
public class HttpSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .configurationSource(corsConfigurationSource())
                .and()

                .csrf().disable()

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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> originList = new ArrayList<>();
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> getOriginList() {
        List<String> originList = new ArrayList<>();
        originList.add("http://localhost:8080");
        originList.add("https://isedol-clip.xyz");

        return originList;
    }

    private List<String> getHeaderList() {
        List<String> headerList = new ArrayList<>();
        headerList.add("Content-Type");
        headerList.add("Access-Control-Allow-Origin");

        return headerList;
    }

    private List<String> getMethodList() {
        List<String> methodList = new ArrayList<>();
        methodList.add("GET");
        methodList.add("POST");
        methodList.add("PUT");
        methodList.add("DELETE");

        return methodList;
    }
}
