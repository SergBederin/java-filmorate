package ru.yandex.practicum.filmorate.model;

import lombok.*;

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
@AllArgsConstructor

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
}
