package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoyaltyUser {
    @Schema(description = "Ідентифікатор картки користувача в WMS")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Ідентифікатор картки користувача в системі лояльності")
    @JsonProperty(value = "extrnId")
    private String extrnId;
    @Schema(description = "Ідентифікатор системи лояльності")
    @JsonProperty(value = "loyaltySystemId")
    private String loyaltySystemId;
    @Schema(description = "Поточний баланс картки в системі лояльності")
    @JsonProperty(value = "balanceAmount")
    private String balanceAmount;
    @Schema(description = "Назва системи лояльності")
    @JsonProperty(value = "loyaltySystemName")
    private String loyaltySystemName;
    @Schema(description = "Код віртуальної валюти")
    @JsonProperty(value = "vcAlias")
    private String vcAlias;
    @Schema(description = "Назва віртуальної валюти")
    @JsonProperty(value = "vcName")
    private String vcName;

    public LoyaltyUser(){}
    public LoyaltyUser(edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb, JsonNode jsonNode){
        this.id = loyaltyUserDb.getId();
        this.extrnId = loyaltyUserDb.getExtrnId();
        this.loyaltySystemId = loyaltyUserDb.getLoyaltySystemId();
        this.balanceAmount = jsonNode.path("balanceAmount").asText();
        this.loyaltySystemName = jsonNode.path("loyaltySystemName").asText();
        this.vcAlias = jsonNode.path("vcAlias").asText();
        this.vcName = jsonNode.path("vcName").asText();
    }
}
