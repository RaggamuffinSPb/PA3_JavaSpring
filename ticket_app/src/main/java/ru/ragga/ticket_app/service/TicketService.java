package ru.ragga.ticket_app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ragga.ticket_app.dto.TicketRequest;
import ru.ragga.ticket_app.dto.TicketResponse;
import ru.ragga.ticket_app.entity.Ticket;
import ru.ragga.ticket_app.entity.User;
import ru.ragga.ticket_app.repository.TicketRepository;
import ru.ragga.ticket_app.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    /**
     * Создание нового залогового билета
     */
    @Transactional
    public TicketResponse createTicket(TicketRequest request, String currentUsername) {
        log.info("Создание билета для пользователя: {}", currentUsername);

        // 1. Находим текущего пользователя (из SecurityContext)
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new NoSuchElementException("Пользователь не найден"));

        // 2. Создаём сущность Ticket
        Ticket ticket = new Ticket();
        ticket.setUser(currentUser);
        ticket.setClientName(request.getClientName());
        ticket.setAmount(request.getAmount());
        ticket.setInterestRate(request.getInterestRate());

        // 3. Устанавливаем даты (выдача = сегодня, возврат = сегодня + срок)
        LocalDate today = LocalDate.now();
        ticket.setIssueDate(today);
        ticket.setDueDate(today.plusDays(request.getTermDays()));
        ticket.setProlonged(false);

        // 4. Сохраняем в БД
        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Билет создан с ID: {}", savedTicket.getId());

        // 5. Преобразуем в DTO для ответа
        return toResponse(savedTicket);
    }

    /**
     * Пролонгация (продление) залогового билета
     */
    @Transactional
    public TicketResponse prolongTicket(Long ticketId, String currentUsername) {
        log.info("Пролонгация билета ID: {} для пользователя: {}", ticketId, currentUsername);

        // ищем тикет
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Билет не найден"));

        // проверка чей тикет
        if (!ticket.getUser().getUsername().equals(currentUsername)) {
            throw new SecurityException("У вас нет прав на продление этого билета");
        }

        // проверка, пролонгировался ли тикет ранее
        if (ticket.isProlonged()) {
            throw new IllegalStateException("Билет уже был продлён ранее");
        }

        // логика продления:
        // сдвигаем дату возврата ещё на 30 дней
        // +5% штрафа
        LocalDate newDueDate = ticket.getDueDate().plusDays(30);
        BigDecimal newInterestRate = ticket.getInterestRate()
                .multiply(BigDecimal.valueOf(1.05)) // +5%
                .setScale(2, BigDecimal.ROUND_HALF_UP);

        // 5. Обновляем билет
        ticket.setDueDate(newDueDate);
        ticket.setInterestRate(newInterestRate);
        ticket.setProlonged(true);

        Ticket updatedTicket = ticketRepository.save(ticket);
        // логируем что "свершилось"
        log.info("Билет ID: {} успешно продлён до {}", ticketId, newDueDate);

        return toResponse(updatedTicket);
    }

    /**
     * Entity ----> DTO
     */
    private TicketResponse toResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getClientName(),
                ticket.getAmount(),
                ticket.getInterestRate(),
                ticket.getIssueDate(),
                ticket.getDueDate(),
                ticket.isProlonged()
        );
    }
}