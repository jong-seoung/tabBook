package tabbook.server.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import tabbook.server.dto.GoogleUserInfo;

@Service
public class GoogleApiService {
    
    private final RestTemplate restTemplate = new RestTemplate();


    public GoogleUserInfo getEmailFromGoogle(String accessToken) throws Exception{
    String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<GoogleUserInfo> response = restTemplate.exchange( url, HttpMethod.GET, entity, GoogleUserInfo.class);

    if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
        return response.getBody();
    } else {
        throw new Exception("Google API 호출 실패");
    }
}
}


