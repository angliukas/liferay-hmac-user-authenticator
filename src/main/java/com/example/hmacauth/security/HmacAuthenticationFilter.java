package com.example.hmacauth.security;

import com.example.hmacauth.service.CustomUserDetailsService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class HmacAuthenticationFilter extends OncePerRequestFilter {

    private static final String HMAC_USER_HEADER = "X-HMAC-USER";
    private static final String HMAC_SIGNATURE_HEADER = "X-HMAC-SIGNATURE";
    private static final String HMAC_SECRET = "changeit";
    private static final String HMAC_COMPLETION_PATH = "/hmac/complete";

    private final CustomUserDetailsService userDetailsService;

    public HmacAuthenticationFilter(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String username = request.getHeader(HMAC_USER_HEADER);
        String signature = request.getHeader(HMAC_SIGNATURE_HEADER);

        if (StringUtils.hasText(username) && StringUtils.hasText(signature) && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (isValidSignature(username, signature)) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                if (request.getRequestURI().endsWith(HMAC_COMPLETION_PATH)) {
                    response.sendRedirect(request.getContextPath() + "/hmac/success");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isValidSignature(String username, String providedSignature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(HMAC_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(username.getBytes(StandardCharsets.UTF_8));
            String expected = Base64.getEncoder().encodeToString(digest);
            return expected.equals(providedSignature);
        } catch (Exception ex) {
            return false;
        }
    }
}
