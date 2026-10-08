package com.utkarsh.jobtracker.dto;

public record AuthResponse(String token, String name, String email) {
}