package tabbook.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.Data;
import tabbook.server.dto.SubscriptionDto;
import tabbook.server.model.Subscription;
import tabbook.server.repository.SubscriptionRepository;
import tabbook.server.util.EncryptUtil;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Data
@Controller
public class SubscriptionController {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private EncryptUtil encryptUtil;

    @Value("${hash-secret}")
    private String secret;

    @Value("${IMP_ID}")
    private String IMP_ID;

    @Value("${channelKey}")
    private String channelKey;

    @Value("${redirectUrl}")
    private String redirectUrl;

    @GetMapping({"","/"})
    public String index(){
        return "index";
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSubscriptionStatus(@RequestParam String email) {
        System.out.println(email);

        // 구독 정보가 없을 경우 404 Not Found 응답을 반환
        Optional<Subscription> optionalSubscription = subscriptionRepository.findByEmail(email);
        Map<String, Object> response = new HashMap<>();
        if (optionalSubscription.isEmpty()) {
            response.put("email", "email is not found");
        }else{
            Subscription subscription = optionalSubscription.get();
            response.put("email", email);
            response.put("uid", subscription.getUserUid());
            response.put("expireDate", subscription.getNextPaymentDate());
            response.put("isActive", subscription.isActive());
        }
        return ResponseEntity.ok(response);
    }
    

    @GetMapping("/payment")
    public String showPaymentPage(@RequestParam("user") String hashedEmail, @RequestParam("uid") String uid, Model model) {
        String Email = encryptUtil.decryptEmail(hashedEmail);

        model.addAttribute("IMP_ID", IMP_ID);
        model.addAttribute("channelKey", channelKey);
        model.addAttribute("redirectUrl", redirectUrl);

        model.addAttribute("email", Email);
        model.addAttribute("uid", uid);
        return "paymentPage";
    }

    @PostMapping("/subscriptions")
    @ResponseBody
    public String createSubscription(@RequestBody SubscriptionDto rsp) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.findByUserUid(rsp.getCustomer_uid());
            String planType = rsp.getName().split(" ")[0];

        int monthsToAdd;
        if ("월별".equalsIgnoreCase(planType)) {
            monthsToAdd = 1;
        } else if ("연간".equalsIgnoreCase(planType)) {
            monthsToAdd = 12;
        } else {
            throw new IllegalArgumentException("알 수 없는 플랜입니다: " + planType);
        }

        if (optionalSubscription.isPresent()) {
            Subscription existingSubscription = optionalSubscription.get();

            existingSubscription.setEmail(rsp.getBuyer_email());
            existingSubscription.setPaymentId(rsp.getMerchant_uid());
            existingSubscription.setPlan(planType);
            existingSubscription.setPrice((long) rsp.getPaid_amount());
            existingSubscription.setActive(true);
            existingSubscription.setPromotionCode(null);

            LocalDate currentNextPaymentDate = existingSubscription.getNextPaymentDate();
            LocalDate baseDate = currentNextPaymentDate.isAfter(LocalDate.now()) ? currentNextPaymentDate : LocalDate.now();
            existingSubscription.setNextPaymentDate(baseDate.plusMonths(monthsToAdd));

            subscriptionRepository.save(existingSubscription);
        } else {
            Subscription newSubscription = new Subscription();
            newSubscription.setEmail(rsp.getBuyer_email());
            newSubscription.setPaymentId(rsp.getMerchant_uid());
            newSubscription.setUserUid(rsp.getCustomer_uid());
            newSubscription.setPlan(planType);
            newSubscription.setPrice((long) rsp.getPaid_amount());
            newSubscription.setStartDate(LocalDate.now());
            newSubscription.setNextPaymentDate(LocalDate.now().plusMonths(monthsToAdd));
            newSubscription.setActive(true);
            newSubscription.setPromotionCode(null);

            subscriptionRepository.save(newSubscription);
        }
        return "Done"; 
    }
}
