package com.subhash.urlbackend.security;


import com.subhash.urlbackend.security.jwt.JWTFilter;
import com.subhash.urlbackend.security.service.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        return http
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(request->request
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){

        CorsConfiguration configuration=new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        configuration.setMaxAge(3600L);
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider provider=new DaoAuthenticationProvider();
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        provider.setUserDetailsService(myUserDetailsService);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
