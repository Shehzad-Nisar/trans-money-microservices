package com.transmoney.accountservice.dto;


import com.transmoney.accountservice.Entity.AccountType;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {


    @NotBlank(message = "Account holder name is required.")
    private String accountHolderName;

    @NotBlank(message = "Account holder's email is required.")
    @Email(message = "Invalid email format.")
    private String email;

    @NotBlank(message = "Account holder's phone number is required.")
    private String phone;

    @NotNull(message = "Account Type is required.")
    private AccountType accountType;

    @NotNull(message = "Initial deposit is required.")
    @Positive(message = "Initial deposit must be positive.")
    private BigDecimal initialDeposit;



}
