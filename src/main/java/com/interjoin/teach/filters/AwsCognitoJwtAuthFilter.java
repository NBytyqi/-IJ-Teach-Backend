package com.interjoin.teach.filters;

import com.interjoin.teach.config.exceptions.InterjoinException;
import com.interjoin.teach.config.exceptions.TokenExpiredException;
import com.interjoin.teach.jwt.AwsCognitoIdTokenProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AwsCognitoJwtAuthFilter extends OncePerRequestFilter {

    private static final Log logger = LogFactory.getLog(AwsCognitoJwtAuthFilter.class);
    private final AwsCognitoIdTokenProcessor cognitoIdTokenProcessor;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ResponseStatusException, ServletException, IOException {
        Authentication authentication;

        try {
            authentication = this.cognitoIdTokenProcessor.authenticate((HttpServletRequest)request);
            if (authentication != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (TokenExpiredException ex) {
            logger.error("Cognito ID Token processing error", ex);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"error\": \"Session expired. Please login again.\" }");
            return;
        }

        catch (InterjoinException ex) {
            logger.error("Cognito ID Token processing error", ex);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{ \"error\": \"Session expired. Please login again.\" }");
            return;
        }
        catch (Exception ex) {
            logger.error("Cognito ID Token processing error", ex);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}