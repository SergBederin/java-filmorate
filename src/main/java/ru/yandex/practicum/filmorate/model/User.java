package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString

@NoArgsConstructor

public class User {
    private Long id;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    private String login;
    @Size(max = 100)
    private String name;
    @NotNull
    private LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

    public User(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }
}
