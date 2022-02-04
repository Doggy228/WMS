package edu.doggy228.loyaltyexch.wms.service;

import com.fasterxml.jackson.databind.JsonNode;
import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.api.v1.ApiException;
import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem;
import edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser;
import edu.doggy228.loyaltyexch.wms.modeldb.WalletUser;
import lombok.Getter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

@Getter
public class ApiReq {
    private final static String TKN_TYPE_ADMIN = "ad";
    private final static String TKN_TYPE_WALLET = "wu";
    private final static String TKN_TYPE_LOYALTY = "ls";
    private AppService appService;
    private HttpHeaders httpHeaders;
    private String tknType;
    private LoyaltySystem loyaltySystem;
    private WalletUser walletUser;
    private boolean bearerKeyError;

    public ApiReq(AppService appService, HttpHeaders httpHeaders, String reqParamL) {
        this.appService = appService;
        this.httpHeaders = httpHeaders;
        bearerKeyError = false;
        String authorization = httpHeaders.getFirst("Authorization");
        if (authorization != null && !authorization.isEmpty()) {
            StringTokenizer st = new StringTokenizer(authorization.trim(), " ");
            if (st.countTokens() > 1 && st.nextToken().equals("Bearer")) {
                String bearerKey = st.nextToken();
                try {
                    StringTokenizer st1 = new StringTokenizer(bearerKey, ":");
                    if(st1.countTokens()!=2) throw new ApiException(this, "Помилка авторізації.", "Bearer error.", null);
                    tknType = st1.nextToken();
                    if(TKN_TYPE_ADMIN.equals(tknType)){
                    } else if(TKN_TYPE_WALLET.equals(tknType)){
                        String walletUserId = st1.nextToken();
                        walletUser = appService.getWalletUserRepository().findById(walletUserId).orElse(null);
                        if(walletUser==null){
                            if(walletUserId.length()!=12) throw new ApiException(this, "Хибнмй ідентифікатор гаманця.", "Bearer error.", null);
                            walletUser = new edu.doggy228.loyaltyexch.wms.modeldb.WalletUser();
                            walletUser.setId(walletUserId);
                            List<LoyaltyUser> listLoyaltyUser = new ArrayList<>();
                            RestTemplate restTemplate = new RestTemplate();
                            JsonNode root = Utils.getRestResponseRoot(this, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl()+"/find/user/bytel/"+walletUserId, HttpMethod.GET, Utils.createRestRequest(null,null), String.class));
                            for(JsonNode el: root.path("listLoyaltyUser")){
                                LoyaltySystem loyaltySystem1 = appService.getLoyaltySystemRepository().findById(el.path("loyaltySystemId").asText()).orElse(null);
                                if(loyaltySystem1!=null) {
                                    LoyaltyUser loyaltyUser = new LoyaltyUser();
                                    loyaltyUser.setId(UUID.randomUUID().toString());
                                    loyaltyUser.setExtrnId(el.path("id").asText());
                                    loyaltyUser.setWalletUserId(walletUser.getId());
                                    loyaltyUser.setLoyaltySystemId(loyaltySystem1.getId());
                                    listLoyaltyUser.add(loyaltyUser);
                                }
                            }
                            appService.getWalletUserRepository().save(walletUser);
                            if(!listLoyaltyUser.isEmpty()){
                                appService.getLoyaltyUserRepository().saveAll(listLoyaltyUser);
                            }
                        }
                    } else if(TKN_TYPE_LOYALTY.equals(tknType)){
                        String loyaltySystemId = st1.nextToken();
                        loyaltySystem = appService.getLoyaltySystemRepository().findById(loyaltySystemId).orElse(null);
                        if(loyaltySystem==null) throw new ApiException(this, "Хибнмй ідентифікатор системи лояльності..", "Bearer error.", null);
                    } else {
                        throw new ApiException(this, "Помилка авторізації.", "Bearer error.", null);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    bearerKeyError = true;
                    throw new ApiException(this, "Помилка авторізації.", "Bearer error.", null);
                }
            }
        }
    }

    public boolean isAdmin() {
        if (!bearerKeyError && TKN_TYPE_ADMIN.equals(tknType)) return true;
        return false;
    }

    public boolean isWalletUser() {
        if (!bearerKeyError && walletUser != null) return true;
        return false;
    }

    public boolean isLoyaltySystem() {
        if (!bearerKeyError && loyaltySystem != null) return true;
        return false;
    }

    public void checkAuthAdmin() throws ApiException {
        if (!isAdmin()) throw new ApiException(this, "Адміністратор не авторизований.", "Bearer error.", null);
    }

    public void checkAuthWalletUser() throws ApiException {
        if (!isWalletUser()) throw new ApiException(this, "Гаманець не авторизовано.", "Bearer error.", null);
    }

    public void checkAuthLoyaltySystem() throws ApiException {
        if (!isLoyaltySystem()) throw new ApiException(this, "Система лояльності не авторизована.", "Bearer error.", null);
    }

}


