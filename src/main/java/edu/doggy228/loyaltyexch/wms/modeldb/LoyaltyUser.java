package edu.doggy228.loyaltyexch.wms.modeldb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

@Getter
@Setter
public class LoyaltyUser {
    @Id
    private String id;
    @Indexed(unique = false)
    private String walletUserId;
    private String loyaltySystemId;
    private String extrnId;
}
