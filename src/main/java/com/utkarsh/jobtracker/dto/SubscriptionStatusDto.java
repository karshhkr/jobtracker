package com.utkarsh.jobtracker.dto;

import java.time.LocalDate;

/** limit = -1 matlab unlimited. betaFree = true matlab Pro abhi free beta mein mil raha hai. */
public record SubscriptionStatusDto(String plan, boolean pro, LocalDate expiresOn,
                                    long used, int limit, boolean emailReminders,
                                    long pricePaise, boolean betaFree) {
}