package tabbook.server.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tabbook.server.dto.GoogleUserInfo;
import tabbook.server.dto.TokenRequest;
import tabbook.server.model.Subscription;
import tabbook.server.repository.SubscriptionRepository;
import tabbook.server.service.GoogleApiService;
import tabbook.server.util.EncryptUtil;


@RestController
@RequestMapping("/api")
public class PaymentController {
    
    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private EncryptUtil encryptUtil;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @PostMapping("/redirect-to-payment")
    public ResponseEntity<RedirectResponse> redirectToPayment(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(new RedirectResponse("토큰이 제공되지 않았습니다."));
        }

        try{
            GoogleUserInfo userInfo = googleApiService.getEmailFromGoogle(token);
            String email = userInfo.getEmail();
            String uid = userInfo.getId();
            String hashedEmail = encryptUtil.hashEmail(email);

            String responseUrl = redirectUrl + "payment?user=" + hashedEmail + "&uid=" + uid;
            return ResponseEntity.ok(new RedirectResponse(responseUrl));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedirectResponse("이메일 가져오기 실패"));
        }
    }

    @PostMapping("/cancel-subscription")
    public ResponseEntity<RedirectResponse> cancelPayment(@RequestBody TokenRequest tokenRequest) {
        String token = tokenRequest.getToken();
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(new RedirectResponse("토큰이 제공되지 않았습니다."));
        }

        try{
            GoogleUserInfo userInfo = googleApiService.getEmailFromGoogle(token);
            String email = userInfo.getEmail();
            Optional<Subscription> optionalSubscription = subscriptionRepository.findByEmail(email);

                if (optionalSubscription.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new RedirectResponse("구독 정보를 찾을 수 없습니다."));
            }

            Subscription subscription = optionalSubscription.get();
            subscription.setActive(false);
            subscriptionRepository.save(subscription);

            return ResponseEntity.ok(new RedirectResponse("구독이 성공적으로 취소되었습니다."));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RedirectResponse("이메일 가져오기 실패"));
        }
    }

    static class RedirectResponse {
        private String redirectUrl;

        public RedirectResponse(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }
    }
}