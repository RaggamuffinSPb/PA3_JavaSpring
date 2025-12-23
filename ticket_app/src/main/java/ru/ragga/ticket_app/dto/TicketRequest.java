package ru.ragga.ticket_app.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TicketRequest {
    // Клиент
    @NotBlank(message = "Client name is required")
    @Size(min = 2, max = 100, message = "Client name must be between 2 and 100 characters")
    private String clientName;
    // Сумма залога
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least 1.00")
    @DecimalMax(value = "1000000.00", message = "Amount cannot exceed 1,000,000.00")
    private BigDecimal amount;
    // Процентная ставка (годовых)
    @NotNull(message = "Interest rate is required")
    @DecimalMin(value = "0.01", message = "Interest rate must be at least 0.01%")
    @DecimalMax(value = "50.00", message = "Interest rate cannot exceed 50.00%")
    private BigDecimal interestRate;
    // Срок займа в днях (по умолчанию 30)
    @Min(value = 1, message = "Term must be at least 1 day")
    @Max(value = 365, message = "Term cannot exceed 365 days")
    private Integer termDays = 30;
}