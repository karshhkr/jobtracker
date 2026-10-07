package com.utkarsh.jobtracker.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    @Override
    protected  void  doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException{

        String header = req.getHeader("Authorization");
    if(header != null&& header.startsWith("Bearer ")){
        String token =header.substring(7);
        String userId = jwtUtil.extractUserId(token);
        if(userId!= null && SecurityContextHolder.getContext().getAuthentication() == null){
if(jwtUtil.isTokenValid(token)){
    var authToken =new UsernamePasswordAuthenticationToken(userId,null, List.of());
    SecurityContextHolder.getContext().setAuthentication(authToken);

}
    }
}


chain.doFilter(req, res);
}
        }

