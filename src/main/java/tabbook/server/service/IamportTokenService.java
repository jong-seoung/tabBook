package tabbook.server.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class IamportTokenService {

    private final RestTemplate restTemplate = new RestTemplate();

    private String accessToken;
    private long expiredAt;

    @Value("${IMP_KEY}")
    private String IMP_KEY;

    @Value("${IMP_SECRET}")
    private String IMP_SECRET;

    public String getValidAccessToken() {
        long now = Instant.now().getEpochSecond();

        if (accessToken == null || now >= expiredAt) {
            requestNewToken();
        }

        return accessToken;
    }

    private void requestNewToken() {
    String url = "https://api.iamport.kr/users/getToken";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    Map<String, Object> body = new HashMap<>();
    body.put("imp_key", IMP_KEY);
    body.put("imp_secret", IMP_SECRET);

    try {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(body);

        HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody().get("response");
            this.accessToken = (String) responseBody.get("access_token");
            this.expiredAt = ((Number) responseBody.get("expired_at")).longValue();
        } else {
            System.out.println(response.getBody());
        }
    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("아임포트 토큰 발급 실패: " + e.getMessage());
    }
}
}
