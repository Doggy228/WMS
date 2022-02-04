package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.databind.JsonNode;
import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.api.v1.ApiException;
import edu.doggy228.loyaltyexch.wms.api.v1.RspListLoyaltySystem;
import edu.doggy228.loyaltyexch.wms.modeljson.LoyaltyUser;
import edu.doggy228.loyaltyexch.wms.modeljson.ResponseError;
import edu.doggy228.loyaltyexch.wms.modeljson.WalletUserWithLoyalty;
import edu.doggy228.loyaltyexch.wms.service.ApiReq;
import edu.doggy228.loyaltyexch.wms.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Tag(name = "Мобільний гаманець ккористувача.", description = "Функціонал для мобільного гаманця.")
@RestController
@RequestMapping("/api/v1/wms/wallet")
public class WalletController {
    private AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @Operation(summary = "Отримання списку карт лояльності користувача з балансами по ним.",
            description = "Список доступних карт лояльності з актуальними балансами.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Список карт лояльності ккористуввача.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = WalletUserWithLoyalty.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @GetMapping("/wu")
    public ResponseEntity<WalletUserWithLoyalty> walletUserGetCur(@RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            List<LoyaltyUser> listLoyaltyUser = new ArrayList<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> listLoyaltyUserDb = appService.getCustomRepository().loyaltyUserFindByWalletUser(apiReq.getWalletUser().getId());
            if(listLoyaltyUserDb==null) listLoyaltyUserDb = new ArrayList<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> listLoyaltyUserDbNew = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl()+"/find/user/bytel/"+apiReq.getWalletUser().getId(), HttpMethod.GET, Utils.createRestRequest(null,null), String.class));
            for(JsonNode el: root.path("listLoyaltyUser")){
                String extrnId = el.path("id").asText();
                edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb = null;
                for(edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser el1: listLoyaltyUserDb){
                    if(el1.getExtrnId().equals(extrnId)){
                        loyaltyUserDb = el1;
                        break;
                    }
                }
                if(loyaltyUserDb==null) {
                    edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystem1 = appService.getLoyaltySystemRepository().findById(el.path("loyaltySystemId").asText()).orElse(null);
                    if (loyaltySystem1 != null) {
                        loyaltyUserDb = new edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser();
                        loyaltyUserDb.setId(UUID.randomUUID().toString());
                        loyaltyUserDb.setExtrnId(extrnId);
                        loyaltyUserDb.setWalletUserId(apiReq.getWalletUser().getId());
                        loyaltyUserDb.setLoyaltySystemId(loyaltySystem1.getId());
                        listLoyaltyUserDbNew.add(loyaltyUserDb);
                    }
                }
                if(loyaltyUserDb!=null){
                    listLoyaltyUser.add(new LoyaltyUser(loyaltyUserDb, el));
                }
            }
            if(!listLoyaltyUserDbNew.isEmpty()) appService.getLoyaltyUserRepository().saveAll(listLoyaltyUserDbNew);
            WalletUserWithLoyalty walletUserWithLoyalty = new WalletUserWithLoyalty();
            walletUserWithLoyalty.setId(apiReq.getWalletUser().getId());
            walletUserWithLoyalty.setListLoyaltyUser(listLoyaltyUser.toArray(new LoyaltyUser[0]));
            return new ResponseEntity<>(walletUserWithLoyalty, HttpStatus.OK);
        } catch (Throwable e) {
            if (e instanceof ApiException) throw (ApiException)e;
            throw new ApiException(apiReq, "Помилка виконання запиту", ""+e, e);
        }
    }
}
