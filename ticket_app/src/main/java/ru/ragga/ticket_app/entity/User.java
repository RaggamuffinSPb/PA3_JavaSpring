package ru.ragga.ticket_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor; // вроде не запрещено использовать ломбок?
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users") // ticketdb.public.users
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * названия колонок == названия полей класса
     * id "bigint" not null
     * username text not null
     * password text not null (или хэш пароля для секурности? https://bcrypt-generator.com )
     * мб роль?
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // автоинкремент idшника
    private Long id;

    @Column(unique = true, nullable = false, length = 255)
    private String username;

    @Column(nullable = false, length = 255)
    private String password; /* https://bcrypt-generator.com !!!*/

    private String role = "ROLE_USER";

}