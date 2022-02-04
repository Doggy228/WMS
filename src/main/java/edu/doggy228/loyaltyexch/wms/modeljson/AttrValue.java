package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttrValue {
    @Schema(description = "Ідентифікатор атрибута.")
    @JsonProperty(value = "id")
    private String id;
    @Schema(description = "Значення атрибута.")
    @JsonProperty(value = "v")
    private String v;
}
