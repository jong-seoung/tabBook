package tabbook.server.dto;

import lombok.Data;

@Data
public class SubscriptionDto {
    private String imp_uid;
    private String merchant_uid;
    private String name;
    private int paid_amount;
    private String buyer_email;
    private String buyer_name;
    private String buyer_tel;
    private String customer_uid;
    private boolean success;
}
