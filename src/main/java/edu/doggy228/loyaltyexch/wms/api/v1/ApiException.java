package edu.doggy228.loyaltyexch.wms.api.v1;

import edu.doggy228.loyaltyexch.wms.modeljson.ResponseError;
import edu.doggy228.loyaltyexch.wms.service.ApiReq;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private ApiReq apiReq;
    protected String msg;

    public ApiException(ApiReq apiReq, String msg, String message, Throwable cause){
        super(message, cause);
        this.apiReq = apiReq;
        this.msg = msg;
    }

    public ResponseError createResponseError(){
        return new ResponseError(msg, toString());
    }

    @Override
    public String toString() {
        String res = getMessage();
        if(res==null || res.isEmpty()) return "Api exception";
        return res;
    }
}
