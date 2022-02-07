package edu.doggy228.loyaltyexch.wms.modeljson;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum TransType {
    @JsonProperty("scBuy")
    SC_BUY,
    @JsonProperty("scSell")
    SC_SELL,
    @JsonProperty("lsPay")
    LS_PAY,
    @JsonProperty("lsExch")
    LS_EXCH
}
