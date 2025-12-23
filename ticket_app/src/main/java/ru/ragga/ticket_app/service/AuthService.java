package ru.ragga.ticket_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ragga.ticket_app.entity.User;
import ru.ragga.ticket_app.repository.UserRepository;
import ru.ragga.ticket_app.security.jwt.JwtTokenUtil;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Аутентификация пользователя и выдача JWT токена
     * @param username логин
     * @param password пароль
     * @return JWT/null
     * обилие логов из-за постоянных ошибок аутентификации
     */
    public String authenticateAndGetToken(String username, String password) {
        try {
            log.info("————————————————————————————————————————————————————————————————————————————————————————");
            log.info("=== Аутентификация для пользователя: {}", username);
            log.info("————————————————————————————————————————————————————————————————————————————————————————");
            // ищем пользователя в базе по юзернейму шаблонным методом
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // логировать, чтобы понимать, где ошибки
            log.info("Найден пользователь: {}", user.getUsername());
            log.info("Хеш из БД: {}", user.getPassword());
            log.info("Длина хеша из БД: {}", user.getPassword().length());

            // считаем хэш пароля из body запроса, f()==SHA256
            String rawPasswordHash = DigestUtils.sha256Hex(password);
            log.info("Хеш введённого пароля '{}': {}", password, rawPasswordHash);
            log.info("Длина введённого хеша: {}", rawPasswordHash.length());

            // должно совпасть
            log.info("Совпадение хешей: {}", rawPasswordHash.equals(user.getPassword()));

            if (!rawPasswordHash.equals(user.getPassword())) {
                log.warn("Неверный пароль для пользователя: {}", username);
                return null;
            }

            // отдаём токен
            String token = jwtTokenUtil.generateToken(username);
            log.info("Сгенерирован токен длиной: {}", token.length());

            return token;

        } catch (Exception e) {
            log.error("Ошибка аутентификации: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * Регистрация нового пользователя
     */
    @Transactional
    public void register(String username, String password) {
        log.info("Регистрация нового пользователя: {}", username);

        // проверка, занят ли логин
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }

        // если не занят - создаём объект USER
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password)); // Шифруем пароль
        user.setRole("ROLE_USER");

        // пишем нового USER'а в БД
        userRepository.save(user);
        log.info("Пользователь {} успешно зарегистрирован", username);
    }
}