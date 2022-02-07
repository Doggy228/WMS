package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class LoyaltyUserPay {
    @Schema(description = "Ідентифікатор картки користувача в WMS")
    @JsonProperty(value = "loyaltyUserId")
    private String loyaltyUserId;
    @Schema(description = "Вказана власником гаманця сума обміну балів з даної картки лояльності.")
    @JsonProperty(value = "sendAmount")
    private String sendAmount;

}
