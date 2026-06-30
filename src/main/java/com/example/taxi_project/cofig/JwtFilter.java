package com.example.taxi_project.cofig;




import com.example.taxi_project.repository.SessionRepository;
import com.example.taxi_project.service.impl.UserDetailsServiceImpl;
import com.example.taxi_project.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;
    private final SessionRepository sessionRepository;

    @Override
    public void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String appVersion = request.getHeader("X-App-Version");
        String deviceID   = request.getHeader("X-Device-ID");
        String deviceName = request.getHeader("X-Device-Name");
        String platform   = request.getHeader("X-Platform");

        GlobalVar.setAppVersion(appVersion);
        GlobalVar.setDeviceId(deviceID);
        GlobalVar.setDeviceName(deviceName);
        GlobalVar.setPlatform(platform);



        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String username = jwtUtils.getUsernameFromToken(token);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    if (jwtUtils.validateToken(token)) {

                        boolean isRevoked = sessionRepository
                                .findByAccessToken(token)
                                .map(session -> session.getRevokedAt() != null)
                                .orElse(false);

                        if (isRevoked) {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"status\":401,\"message\":\"Token bekor qilingan, qayta login qiling\"}"
                            );
                            return;
                        }


                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails, null, userDetails.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"status\":401,\"message\":\"Token yaroqsiz\"}"
                );
                return;
            }
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            GlobalVar.removeAll();
        }


    }





}
