package com.splitapp.splitApp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DebtResponse {
    private String fromUser;
    private String toUser;
    private BigDecimal amount;
}