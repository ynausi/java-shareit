package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @NotNull
    private Integer id;
    @NotBlank
    private String email;
    @NotNull
    private String name;
}
