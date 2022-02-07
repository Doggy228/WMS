package edu.doggy228.loyaltyexch.wms.modeldb;

import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.modeljson.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;

@Getter
@Setter
public class Trans {
    private String id;
    private String operId;
    private String walletUserId;
    private TransType transType;
    private TransStateType state;
    private String stateMsg;
    private LocalDateTime transCreateDt;
    private LocalDateTime transExecDt;
    private String loyaltySystemId;
    private String loyaltyUserId;
    private String sendAmount;
    private CurrencyType sendCurrencyType;
    private String sendCurrencyAlias;
    private String rcptAmount;
    private CurrencyType rcptCurrencyType;
    private String rcptCurrencyAlias;
    private String transPurpose;
    private AttrValue[] details;

    public edu.doggy228.loyaltyexch.wms.modeljson.Trans toJson(LoyaltySystem loyaltySystem){
        edu.doggy228.loyaltyexch.wms.modeljson.Trans objJson = new edu.doggy228.loyaltyexch.wms.modeljson.Trans();
        objJson.setId(getId());
        objJson.setOperId(getOperId());
        objJson.setWalletUserId(getWalletUserId());
        objJson.setTransType(getTransType());
        objJson.setState(getState());
        objJson.setStateMsg(getStateMsg());
        objJson.setTransCreateDt(Utils.getDateTimeStr(getTransCreateDt()));
        objJson.setTransExecDt(Utils.getDateTimeStr(getTransExecDt()));
        objJson.setLoyaltySystemId(getLoyaltySystemId());
        objJson.setLoyaltySystemName(loyaltySystem.getName());
        objJson.setLoyaltyUserId(getLoyaltyUserId());
        objJson.setSendAmount(getSendAmount());
        objJson.setSendCurrencyType(getSendCurrencyType());
        objJson.setSendCurrencyAlias(getSendCurrencyAlias());
        objJson.setRcptAmount(getRcptAmount());
        objJson.setRcptCurrencyType(getRcptCurrencyType());
        objJson.setRcptCurrencyAlias(getRcptCurrencyAlias());
        objJson.setTransPurpose(getTransPurpose());
        objJson.setDetails(getDetails());
        return objJson;
    }

}
