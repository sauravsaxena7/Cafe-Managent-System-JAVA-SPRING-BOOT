package com.inn.cafe.JWT;


import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j


@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private CustomerUserDetailsServices customerUserDetailsServices;

    Claims claims = null;
    private String userName=null;
    @Autowired
    JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        log.info("Inside Do filterInternal");
        if(httpServletRequest.getServletPath().matches("/user/login|/user/forgotPassword|/user/signUp")){
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }else{
            String authorizationHeadr = httpServletRequest.getHeader("Authorization");
            String token  =null;

            log.info("This Is Not The Free Route");
            if(authorizationHeadr!=null && authorizationHeadr.startsWith("Bearer ")){
                token = authorizationHeadr.substring(7);
                userName=jwtUtil.extractUsername(token);
                claims = jwtUtil.extractAllClaims(token);
                log.info("extracting user detials");
            }

            if(userName!=null && SecurityContextHolder.getContext().getAuthentication()==null){
                UserDetails userDetails = customerUserDetailsServices.loadUserByUsername(userName);

                if ((jwtUtil.validateToken(token,userDetails))){
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

                    usernamePasswordAuthenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
                    );
                    log.info("SecurityContextHolder.getContext().getAuthentication()");

                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                }
            }
            log.info(httpServletRequest.getServletPath());
            filterChain.doFilter(httpServletRequest,httpServletResponse);

        }
    }

    public boolean isAdmin(){
        return "admin".equalsIgnoreCase((String) claims.get("role"));
    }

    public boolean isUser(){
        return "user".equalsIgnoreCase((String) claims.get("role"));
    }

    public String getUserName(){
        return userName;
    }

}
