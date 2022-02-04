package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum CurrencyType {
    @JsonProperty("fc")
    FIAT_CURRENCY,
    @JsonProperty("sc")
    SYSTEM_CURRENCY,
    @JsonProperty("vc")
    VIRTUAL_CURRENCY
}
