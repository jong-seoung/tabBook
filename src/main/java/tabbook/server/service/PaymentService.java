package tabbook.server.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tabbook.server.model.Subscription;

@Service
public class PaymentService {
    
    @Autowired
    private IamportTokenService iamportTokenService;
    private final RestTemplate restTemplate = new RestTemplate();

    public void charge(Subscription subscription){
        String accessToken = iamportTokenService.getValidAccessToken();
        String paymentApiUrl = "https://api.iamport.kr/subscribe/payments/again";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        Map<String, Object> request = Map.of(
            "customer_uid", subscription.getUserUid(),
            "merchant_uid", subscription.getPaymentId(),
            "amount", subscription.getPrice()
        );

        restTemplate.exchange(
                paymentApiUrl,
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(request, headers),
                Void.class
            );
    }
}
