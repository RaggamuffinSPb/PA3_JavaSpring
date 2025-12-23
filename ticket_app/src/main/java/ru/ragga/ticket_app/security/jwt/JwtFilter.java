package ru.ragga.ticket_app.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String requestURI = request.getRequestURI();

        log.info("————————————————————————————————————————————————————————————————————————————————————————");
        log.info("-== JwtFilter: Начало обработки запроса на {} ==-", requestURI);
        log.info("————————————————————————————————————————————————————————————————————————————————————————");
        log.info("Authorization: {}", authHeader);

        // Если это публичный эндпоинт - сразу пропускаем
        if (requestURI.equals("/api/auth/login")) {
            log.info("Запрос идёт к публичному узлу ../api/auth/login");
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            log.info("Токен извлечён, первые 5 символов: {}...", token.substring(0, Math.min(5, token.length())));

            if (jwtTokenUtil.validateToken(token)) {
                String username = jwtTokenUtil.getUsernameFromToken(token);
                log.info("{} : токен валидный", username);

                // Создаём объект аутентификации
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                // Устанавливаем аутентификацию в контекст
                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Аутентификация установлена в SecurityContext");

            } else {
                log.warn("Токен НЕВАЛИДЕН или просрочен!");
            }
        } else {
            log.warn("Отсутствует или неверный заголовок Authorization. Формат должен быть: 'Bearer <token>'");
        }

        log.info("Пропускаем запрос дальше по цепочке фильтров...");
        filterChain.doFilter(request, response);
    }
}