package edu.doggy228.loyaltyexch.wms.modeldb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
public class WalletUser {
    @Id
    private String id;
}
