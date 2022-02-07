package edu.doggy228.loyaltyexch.wms.modeldb;

import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.modeljson.AttrValue;
import edu.doggy228.loyaltyexch.wms.modeljson.CurrencyType;
import edu.doggy228.loyaltyexch.wms.modeljson.OperStateType;
import edu.doggy228.loyaltyexch.wms.modeljson.OperType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class Oper {
    @Id
    private String id;
    @Indexed(unique = false)
    private String walletUserId;
    private OperType operType;
    private OperStateType state;
    private String stateMsg;
    private LocalDateTime operCreateDt;
    private LocalDateTime operExecDt;
    private String loyaltySystemId;
    private String loyaltyUserId;
    private String operAmount;
    private CurrencyType operCurrencyType;
    private String operCurrencyAlias;
    private String operPurpose;
    private AttrValue[] details;
    private Trans[] listTrans;
    private String refLoyaltySystems;
    private String refLoyaltyUsers;

    public edu.doggy228.loyaltyexch.wms.modeljson.Oper toJson(Map<String,LoyaltySystem> mapLoyaltySystems){
        edu.doggy228.loyaltyexch.wms.modeljson.Oper objJson = new edu.doggy228.loyaltyexch.wms.modeljson.Oper();
        objJson.setId(getId());
        objJson.setWalletUserId(getWalletUserId());
        objJson.setOperType(getOperType());
        objJson.setState(getState());
        objJson.setStateMsg(getStateMsg());
        objJson.setOperCreateDt(Utils.getDateTimeStr(getOperCreateDt()));
        objJson.setOperExecDt(Utils.getDateTimeStr(getOperExecDt()));
        objJson.setLoyaltySystemId(getLoyaltySystemId());
        objJson.setLoyaltySystemName(mapLoyaltySystems.get(loyaltySystemId).getName());
        objJson.setLoyaltyUserId(getLoyaltyUserId());
        objJson.setOperAmount(getOperAmount());
        objJson.setOperCurrencyType(getOperCurrencyType());
        objJson.setOperCurrencyAlias(getOperCurrencyAlias());
        objJson.setOperPurpose(getOperPurpose());
        objJson.setDetails(getDetails());
        if(listTrans!=null){
            objJson.setListTrans(new edu.doggy228.loyaltyexch.wms.modeljson.Trans[listTrans.length]);
            for(int i=0;i<listTrans.length;i++){
                objJson.getListTrans()[i] = listTrans[i].toJson(mapLoyaltySystems.get(listTrans[i].getLoyaltySystemId()));
            }
        }
        return objJson;
    }

}
