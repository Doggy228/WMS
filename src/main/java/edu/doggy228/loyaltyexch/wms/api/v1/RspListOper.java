package edu.doggy228.loyaltyexch.wms.api.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import edu.doggy228.loyaltyexch.wms.modeljson.Oper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RspListOper {
    @Schema(description = "Список операцій.")
    @JsonProperty(value = "listOper")
    private Oper[] listOper;
}
