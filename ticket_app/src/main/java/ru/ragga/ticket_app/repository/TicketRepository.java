package ru.ragga.ticket_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.ragga.ticket_app.entity.Ticket;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    // использовать дефолтные save, findById, findAll.... delete?
}