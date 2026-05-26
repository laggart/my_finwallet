package com.myfinwallet.finwallet_api.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter

public class TransferRequest {

    @NotBlank(message = "El número de cuenta destino es obligatorio")
    private String receiverAccountNumber;

    @NotNull(message = "El importe es obligatorio")
    @DecimalMin(value = "0.01", message = "El importe mínimo es de 0.01€")
    private BigDecimal amount;

    private String description;
    
}
