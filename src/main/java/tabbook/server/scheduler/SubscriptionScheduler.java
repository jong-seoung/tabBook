package tabbook.server.scheduler;

import java.time.LocalDate;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import tabbook.server.model.Subscription;
import tabbook.server.repository.SubscriptionRepository;
import tabbook.server.service.PaymentService;

@Component
public class SubscriptionScheduler {
    
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentService paymentService;

    public SubscriptionScheduler(SubscriptionRepository subscriptionRepository, PaymentService paymentService) {
        this.subscriptionRepository = subscriptionRepository;
        this.paymentService = paymentService;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runDailyPaymentTask() {
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
    }
}
