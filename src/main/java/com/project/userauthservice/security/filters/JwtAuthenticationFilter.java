package com.project.userauthservice.security.filters;

import com.project.userauthservice.exceptions.InvalidJwtTokenException;
import com.project.userauthservice.security.service.CustomUserDetailsService;
import com.project.userauthservice.utils.Constants;
import com.project.userauthservice.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtils, CustomUserDetailsService customUserDetailsService) {
        this.jwtUtils = jwtUtils;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException {
        try {
            String token = extractTokenFromRequest(request);
            String userEmail = jwtUtils.extractUsername(token);
            if (userEmail != null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
                if (jwtUtils.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else{
                    throw new InvalidJwtTokenException("Invalid token or expired token");
                }

            }
            else throw new InvalidJwtTokenException("Invalid token, subject doesn't exist");
            filterChain.doFilter(request, response);
        }catch(InvalidJwtTokenException ex){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
        }catch (Exception ex) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the token: " +ex.getMessage() );
        }
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String token = getJwtFromRequest(request);
        if (token == null) {
            // If token not found in header, try to get from cookie
            token = getJwtFromCookie(request);
        }
        return token;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request)  {
        String path = request.getRequestURI();
        return path.startsWith("/signup") ||
                path.startsWith("/signin") ||
                path.startsWith("/verifyEmail") ||
                path.startsWith("/verificationEmail") ||
                path.startsWith("/oauth2/") ||
                path.startsWith("/favicon") ;
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith(Constants.BEARER_TOKEN)) {
            return bearerToken.substring(7);
        }
        return null;
    }


    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (Constants.AUTHORIZATION.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
