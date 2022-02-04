package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletUserWithLoyalty {
    @Schema(description = "Ідентифікатор мобільного гаманця (номер телефону).")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Список карток лояльності гаманця.")
    @JsonProperty(value = "listLoyaltyUser")
    private LoyaltyUser[] listLoyaltyUser;
}
