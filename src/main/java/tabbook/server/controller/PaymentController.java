package tabbook.server.controller;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import tabbook.server.dto.TokenRequest;
import tabbook.server.service.GoogleApiService;
import tabbook.server.util.EncryptUtil;
import tabbook.server.dto.GoogleUserInfo;


@RestController
@RequestMapping("/redirect-to-payment")
public class PaymentController {
    
    @Autowired
    private GoogleApiService googleApiService;

    @Autowired
    private EncryptUtil encryptUtil;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @PostMapping
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
            System.out.println(8);
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