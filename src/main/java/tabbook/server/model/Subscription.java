package tabbook.server.model;

import java.time.LocalDate;

import jakarta.persistence.Column; 
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="subscription")
@Getter @Setter
@ToString
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String paymentId; // 결제 고유 ID
    
    @Column(nullable = false)
    private String email; // 결제 email

    @Column(nullable = false)
    private String userUid; // Google OAuth UID

    @Column(nullable = false)
    private String Plan; // 구독 플랜

    @Column(nullable = false)
    private Long price; // 결제 금액
    
    @Column(nullable = false)
    private LocalDate startDate; // 구독 시작일
    
    @Column(nullable = false)
    private LocalDate nextPaymentDate; // 다음 결제일
    
    @Column(nullable = false)
    private boolean isActive; // 구독 활성 상태
    
    @Column(nullable = true)
    private String promotionCode; // 할인 정보
    // Getter, Setter, Constructor
}