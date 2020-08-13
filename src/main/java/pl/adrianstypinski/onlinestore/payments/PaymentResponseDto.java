package pl.adrianstypinski.onlinestore.payments;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private long userId;
    private boolean successful;
    private long paid;
}
