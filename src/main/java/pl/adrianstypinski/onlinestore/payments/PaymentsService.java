package pl.adrianstypinski.onlinestore.payments;

import org.springframework.stereotype.Service;

@Service
public class PaymentsService {
    public PaymentResponseDto performPayment(PaymentRequestDto paymentRequestDto) {
        return new PaymentResponseDto();
    }
}
