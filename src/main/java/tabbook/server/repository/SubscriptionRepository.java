package tabbook.server.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tabbook.server.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long>{
    Optional<Subscription> findByUserUid(String userUid);

    Optional<Subscription> findByEmail(String email);

    @Query("SELECT s FROM Subscription s WHERE s.nextPaymentDate = :tomorrow AND s.isActive = true")
    List<Subscription> findSubscriptionsDueForPayment(@Param("tomorrow") LocalDate targetDate);
}
