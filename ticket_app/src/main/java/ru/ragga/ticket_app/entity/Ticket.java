package ru.ragga.ticket_app.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tickets")
@Data
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // у одного пользователя может быть несколько билетов
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // ссылка на пользователя
    private User user;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "interest_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "is_prolonged", nullable = false)
    private boolean isProlonged = false;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    // таймштамп при создании записи
    @PrePersist
    protected void onCreate() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
    }
}