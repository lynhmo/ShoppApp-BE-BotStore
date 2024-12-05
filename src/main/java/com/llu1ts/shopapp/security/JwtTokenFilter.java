package com.llu1ts.shopapp.security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.llu1ts.shopapp.exception.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.modelmapper.internal.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    @Value("${api.path}")
    private String apiContextPath;

    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // Lấy ra những path được bypass check token
        if (isBypassToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy ra header Authorization chưa (Bearer Token)
        final String authHeader = request.getHeader("Authorization");

        // Check null header và xem có phải Bearer không
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            handleUnauthorizedError(response);
            return;
        }

        // Lấy ra token từ header
        final String token = authHeader.substring(7);
        // Trích xuất thông tin từ token
        final String userName;
        try {
            userName = jwtTokenUtils.getUsername(token);
        } catch (Exception e) {
            handleUnauthorizedError(response);
            return;
        }

        if (userName != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            // Valid token (check time, check có user tồn tại hay không)
            if (jwtTokenUtils.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Nếu token không hợp lệ thì sẽ trả lỗi
                handleUnauthorizedError(response);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }


    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of(apiContextPath + "/categories", "GET"),
                Pair.of(apiContextPath + "/products", "GET"),
                Pair.of(apiContextPath + "/users/register", "POST"),
                Pair.of(apiContextPath + "/users/login", "POST")
        );

        for (Pair<String, String> bypassToken : bypassTokens) {
            if (request.getRequestURI().contains(bypassToken.getLeft())
                    && request.getMethod().contains(bypassToken.getRight())) {
                return true;
            }
        }
        return false;
    }

    private void handleUnauthorizedError(HttpServletResponse response) throws IOException {
        ErrorResponse customErrorResponse = new ErrorResponse("Bad credentials", "401");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // Convert the custom error response to JSON
        String jsonResponse = objectMapper.writeValueAsString(customErrorResponse);
        response.getWriter().write(jsonResponse);
    }
}
