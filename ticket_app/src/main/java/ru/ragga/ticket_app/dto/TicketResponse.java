package ru.ragga.ticket_app.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TicketResponse {
    private Long id;
    private String clientName;
    private BigDecimal amount;
    private BigDecimal interestRate;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean isProlonged;
    private String status; // "ACTIVE", "PROLONGED", "OVERDUE" (можно добавить логику позже)

    // Конструктор для удобного преобразования из Entity
    public TicketResponse(Long id, String clientName, BigDecimal amount,
                          BigDecimal interestRate, LocalDate issueDate,
                          LocalDate dueDate, boolean isProlonged) {
        this.id = id;
        this.clientName = clientName;
        this.amount = amount;
        this.interestRate = interestRate;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.isProlonged = isProlonged;
        this.status = isProlonged ? "PROLONGED" : "ACTIVE";
    }
}