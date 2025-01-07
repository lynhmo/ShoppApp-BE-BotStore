package com.llu1ts.shopapp.security;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
@EnableWebMvc
public class WebSecurityConfig {

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String USER_ROLE = "USER";

    private final JwtTokenFilter jwtTokenFilter;

    @Value("${api.path}")
    private String apiContextPath;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
//                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> requests

                        // Default get endpoint (Những endpoint bắt buộc phải mở)
                        .requestMatchers(HttpMethod.GET, getGuestGetEndpoints()).permitAll()
                        .requestMatchers(HttpMethod.POST, getGuestPostEndpoints()).permitAll()

                        //Admin Request (Những enpoint mà chỉ có admin dùng được)
                        .requestMatchers(HttpMethod.PUT, getAdminPutEndpoints()).hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.GET, getAdminGetEndpoints()).hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, getAdminPostEndpoints()).hasRole(ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, getAdminDeleteEndpoints()).hasRole(ADMIN_ROLE)

                        //User Request (Những endpoint mà chỉ có user hoặc admin)
                        .requestMatchers(HttpMethod.GET, getUserGetEndpoints()).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(HttpMethod.PUT, getUserPutEndpoints()).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(HttpMethod.POST, getUserPostEndpoints()).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(HttpMethod.DELETE, getUserDeleteEndpoints()).hasAnyRole(USER_ROLE, ADMIN_ROLE)

                        // Test
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/fake").denyAll()

                        // Deny all request
                        .anyRequest().denyAll()
                );
        http.cors(httpSecurityCorsConfigurer -> {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("*"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowedHeaders(Arrays.asList("authorization", "content-type", "x-auth-token"));
            configuration.setExposedHeaders(List.of("x-auth-token"));
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });
        return http.build();
    }

    private String[] getAdminGetEndpoints() {
        return new String[]{
                apiContextPath + "/users/all",
                apiContextPath + "/orders/all-orders"
        };
    }

    private String[] getAdminPutEndpoints() {
        return new String[]{
                apiContextPath + "/categories/*",
                apiContextPath + "/products/*"
        };
    }

    private String[] getAdminPostEndpoints() {
        return new String[]{
                apiContextPath + "/categories",
                apiContextPath + "/products",
                apiContextPath + "/products/v2",
                apiContextPath + "/products/delete-many",
                apiContextPath + "/products/uploads/*"
        };
    }

    private String[] getAdminDeleteEndpoints() {
        return new String[]{
                apiContextPath + "/categories/*",
                apiContextPath + "/products/*",
                apiContextPath + "/users/*",
                apiContextPath + "/orders/*"
        };
    }

    private String[] getGuestPostEndpoints() {
        return new String[]{
                apiContextPath + "/users/login",
                apiContextPath + "/users/register"
        };
    }

    private String[] getGuestGetEndpoints() {
        return new String[]{
                apiContextPath + "/categories",
                apiContextPath + "/categories/*",
                apiContextPath + "/products/search/newest",
                apiContextPath + "/products/search/popular",
                apiContextPath + "/products/search/cheap",
                apiContextPath + "/products",
                apiContextPath + "/products/search",
                apiContextPath + "/products/*"
        };
    }

    private String[] getUserGetEndpoints() {
        return new String[]{
                apiContextPath + "/orders/*",
                apiContextPath + "/orders/user/*",
                apiContextPath + "/orders/user/detail/*",
                apiContextPath + "/order-details/*",
                apiContextPath + "/users/user-details/*",
                apiContextPath + "/order-details/order/*"
        };
    }

    private String[] getUserDeleteEndpoints() {
        return new String[]{
                apiContextPath + "/order-details/*",
                apiContextPath + "/order-details/order/*"
        };
    }

    private String[] getUserPostEndpoints() {
        return new String[]{
                apiContextPath + "/orders",
                apiContextPath + "/order-details",
                apiContextPath + "/payment/*",
                apiContextPath + "/order-details/*",
        };
    }

    private String[] getUserPutEndpoints() {
        return new String[]{
                apiContextPath + "/orders/*",
                apiContextPath + "/users/*",
                apiContextPath + "/users/update-password/*",
                apiContextPath + "/orders/status",
                apiContextPath + "/order-details/*"
        };
    }

}
