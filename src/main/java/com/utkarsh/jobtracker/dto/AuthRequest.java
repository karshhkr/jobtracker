package com.utkarsh.jobtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Login aur signup dono ke liye (login mein name ignore hota hai). */
public record AuthRequest(
        @Size(max = 100) String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, max = 100, message = "Password must be 6-100 characters") String password) {
}