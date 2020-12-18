package pl.alpaka.rentalapp.domain.payment;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentServiceTest {

    private PaymentRepository paymentRepository = Mockito.mock(PaymentRepository.class);
    private PaymentService paymentService = new PaymentService(paymentRepository);

    @Test
    void shouldCreatePayment(){
        //given
        Payment payment = Payment.builder()
                .id(1)
                .paymentType(PaymentType.PAYMENT)
                .amount(BigDecimal.TEN)
                .paymentDeadline(LocalDate.now().plusWeeks(1))
                .description("Test payment")
                .rentId(1)
                .paid(false)
                .build();
        //when
        paymentService.create(payment);
        //then
        Mockito.verify(paymentRepository).create(payment);
    }

    @Test
    void shouldUpdatePayment(){
        //given
        Payment payment = Payment.builder()
                .id(1)
                .paymentType(PaymentType.PAYMENT)
                .amount(BigDecimal.TEN)
                .paymentDeadline(LocalDate.now().plusWeeks(1))
                .description("Test payment")
                .rentId(1)
                .paid(false)
                .build();
        //when
        paymentService.create(payment);
        payment.setAmount(BigDecimal.valueOf(155));
        paymentService.update(payment);
        //then
        Mockito.verify(paymentRepository).create(payment);
        Mockito.verify(paymentRepository).update(payment);
        Assertions.assertEquals(BigDecimal.valueOf(155), payment.getAmount());
    }

    @Test
    void shouldDeletePayment(){
        //given
        Payment payment = Payment.builder()
                .id(1)
                .paymentType(PaymentType.PAYMENT)
                .amount(BigDecimal.TEN)
                .paymentDeadline(LocalDate.now().plusWeeks(1))
                .description("Test payment")
                .rentId(1)
                .paid(false)
                .build();
        //when
        paymentService.create(payment);
        paymentService.deleteById(payment.getId());
        //then
        Mockito.verify(paymentRepository).create(payment);
        Mockito.verify(paymentRepository).deleteById(payment.getRentId());
    }
}
