package tabbook.server.config;

import java.time.LocalDate;
import java.util.function.Function;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import tabbook.server.model.Subscription;
import tabbook.server.repository.SubscriptionRepository;
import tabbook.server.service.PaymentService;


@Configuration
@RequiredArgsConstructor
public class FunctionConfig {
    
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentService paymentService;

    @Bean
    public Function<String, String> scheduledTask(){
        return input -> {
            LocalDate tomorrow = LocalDate.now().plusDays(1);
            List<Subscription> dueSubscriptions = subscriptionRepository.findSubscriptionsDueForPayment(tomorrow);

            for (Subscription sub : dueSubscriptions) {
                try {
                    paymentService.charge(sub);

                    int monthsToAdd = "연간".equalsIgnoreCase(sub.getPlan()) ? 12 : 1;
                    sub.setNextPaymentDate(sub.getNextPaymentDate().plusMonths(monthsToAdd));
                    subscriptionRepository.save(sub);

                } catch (Exception e) {
                    System.err.println("결제 실패: " + e);
                }
            }
            return "Done";
        };
    }
}
