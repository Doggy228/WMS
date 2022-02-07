package edu.doggy228.loyaltyexch.wms.api.v1.wallet;

import com.fasterxml.jackson.databind.JsonNode;
import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.api.v1.ApiException;
import edu.doggy228.loyaltyexch.wms.api.v1.RspListOper;
import edu.doggy228.loyaltyexch.wms.modeldb.Core;
import edu.doggy228.loyaltyexch.wms.modeljson.*;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Tag(name = "Мобільний гаманець користувача.", description = "Функціонал для мобільного гаманця.")
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
            if (listLoyaltyUserDb == null) listLoyaltyUserDb = new ArrayList<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> listLoyaltyUserDbNew = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl() + "/find/user/bytel/" + apiReq.getWalletUser().getId(), HttpMethod.GET, Utils.createRestRequest(null, null), String.class));
            for (JsonNode el : root.path("listLoyaltyUser")) {
                String extrnId = el.path("id").asText();
                edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb = null;
                for (edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser el1 : listLoyaltyUserDb) {
                    if (el1.getExtrnId().equals(extrnId)) {
                        loyaltyUserDb = el1;
                        break;
                    }
                }
                if (loyaltyUserDb == null) {
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
                if (loyaltyUserDb != null) {
                    listLoyaltyUser.add(new LoyaltyUser(loyaltyUserDb, el));
                }
            }
            if (!listLoyaltyUserDbNew.isEmpty()) appService.getLoyaltyUserRepository().saveAll(listLoyaltyUserDbNew);
            WalletUserWithLoyalty walletUserWithLoyalty = new WalletUserWithLoyalty();
            walletUserWithLoyalty.setId(apiReq.getWalletUser().getId());
            walletUserWithLoyalty.setListLoyaltyUser(listLoyaltyUser.toArray(new LoyaltyUser[0]));
            return new ResponseEntity<>(walletUserWithLoyalty, HttpStatus.OK);
        } catch (Throwable e) {
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }

    @Operation(summary = "Отримати інформацію про можливість використання бонусів для майбутньої операції.",
            description = "Список доступних карт лояльності з можливістю їх використання.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Інформація про можливість використання бонусів.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RspOperPayCheck.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/oper-pay-check")
    public ResponseEntity<RspOperPayCheck> operPayCheck(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметри платіжної операції отримані від  POS терміналу.") @RequestBody ReqOperPayCheck req,
                                                            @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemOperDb = appService.getLoyaltySystemRepository().findById(req.getLoyaltySystemId()).orElse(null);
            if (loyaltySystemOperDb == null)
                throw new ApiException(apiReq, "Система лояльності " + req.getLoyaltySystemId() + " не підтримується.", null, null);
            RspOperPayCheck rspOperPayCheck = new RspOperPayCheck();
            rspOperPayCheck.setLoyaltySystemId(loyaltySystemOperDb.getId());
            rspOperPayCheck.setLoyaltySystemName(loyaltySystemOperDb.getName());
            rspOperPayCheck.setVcAlias(loyaltySystemOperDb.getVcAlias());
            rspOperPayCheck.setVcName(loyaltySystemOperDb.getVcName());
            rspOperPayCheck.setVcScale(loyaltySystemOperDb.getVcScale());
            BigDecimal bonusAmountInMax = new BigDecimal(req.getBonusAmountInMax()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
            rspOperPayCheck.setBonusAmountInMax(bonusAmountInMax.toPlainString());
            BigDecimal vcRate = new BigDecimal(loyaltySystemOperDb.getVcRate());
            BigDecimal rateSell = vcRate.multiply(new BigDecimal("1.02"));
            rspOperPayCheck.setRateSell(rateSell.toPlainString());
            if (bonusAmountInMax.signum() <= 0) {
                rspOperPayCheck.setListLoyaltyUserPayInfo(new LoyaltyUserPayInfo[0]);
                return new ResponseEntity<>(rspOperPayCheck, HttpStatus.OK);
            }
            List<LoyaltyUser> listLoyaltyUser = new ArrayList<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> listLoyaltyUserDb = appService.getCustomRepository().loyaltyUserFindByWalletUser(apiReq.getWalletUser().getId());
            if (listLoyaltyUserDb == null) listLoyaltyUserDb = new ArrayList<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> listLoyaltyUserDbNew = new ArrayList<>();
            RestTemplate restTemplate = new RestTemplate();
            JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl() + "/find/user/bytel/" + apiReq.getWalletUser().getId(), HttpMethod.GET, Utils.createRestRequest(null, null), String.class));
            for (JsonNode el : root.path("listLoyaltyUser")) {
                String extrnId = el.path("id").asText();
                edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb = null;
                for (edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser el1 : listLoyaltyUserDb) {
                    if (el1.getExtrnId().equals(extrnId)) {
                        loyaltyUserDb = el1;
                        break;
                    }
                }
                if (loyaltyUserDb == null) {
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
                if (loyaltyUserDb != null) {
                    listLoyaltyUser.add(new LoyaltyUser(loyaltyUserDb, el));
                }
            }
            if (!listLoyaltyUserDbNew.isEmpty()) appService.getLoyaltyUserRepository().saveAll(listLoyaltyUserDbNew);

            if (listLoyaltyUser.isEmpty()) {
                rspOperPayCheck.setListLoyaltyUserPayInfo(new LoyaltyUserPayInfo[0]);
                return new ResponseEntity<>(rspOperPayCheck, HttpStatus.OK);
            }

            List<LoyaltyUserPayInfo> listLoyaltyUserPayInfo = new ArrayList<>();
            BigDecimal bonusAmountInRest = bonusAmountInMax;
            for (int i = 0; i < listLoyaltyUser.size(); i++) {
                if (listLoyaltyUser.get(i).getLoyaltySystemId().equals(loyaltySystemOperDb.getId())) {
                    BigDecimal balanceAmount = new BigDecimal(listLoyaltyUser.get(i).getBalanceAmount()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                    BigDecimal amountMax = balanceAmount;
                    if (balanceAmount.signum() > 0) {
                        if (balanceAmount.compareTo(bonusAmountInRest) >= 0) {
                            amountMax = bonusAmountInRest;
                            bonusAmountInRest = BigDecimal.ZERO.setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                        } else {
                            bonusAmountInRest = bonusAmountInRest.subtract(balanceAmount);
                        }
                        LoyaltyUserPayInfo loyaltyUserPayInfo = new LoyaltyUserPayInfo();
                        loyaltyUserPayInfo.setLoyaltyUserId(listLoyaltyUser.get(i).getId());
                        loyaltyUserPayInfo.setLoyaltyUserExtrnId(listLoyaltyUser.get(i).getExtrnId());
                        loyaltyUserPayInfo.setBalanceAmount(balanceAmount.toPlainString());
                        loyaltyUserPayInfo.setLoyaltySystemId(loyaltySystemOperDb.getId());
                        loyaltyUserPayInfo.setLoyaltySystemName(loyaltySystemOperDb.getName());
                        loyaltyUserPayInfo.setVcAlias(loyaltySystemOperDb.getVcAlias());
                        loyaltyUserPayInfo.setVcName(loyaltySystemOperDb.getVcName());
                        loyaltyUserPayInfo.setVcScale(loyaltySystemOperDb.getVcScale());
                        loyaltyUserPayInfo.setLocalTrans(true);
                        BigDecimal vcRate1 = new BigDecimal(loyaltySystemOperDb.getVcRate());
                        BigDecimal rateBuy = vcRate1.multiply(new BigDecimal("0.98"));
                        loyaltyUserPayInfo.setRateBuy(rateBuy.toPlainString());
                        loyaltyUserPayInfo.setBonusAmountInMax(amountMax.toPlainString());
                        listLoyaltyUserPayInfo.add(loyaltyUserPayInfo);
                    }
                    listLoyaltyUser.remove(i);
                    break;
                }
            }
            if (bonusAmountInRest.signum() > 0 && !listLoyaltyUser.isEmpty()) {
                for (int i = 0; i < listLoyaltyUser.size(); i++) {
                    edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemDb = appService.getLoyaltySystemRepository().findById(listLoyaltyUser.get(i).getLoyaltySystemId()).orElse(null);
                    if (loyaltySystemDb != null) {
                        BigDecimal balanceAmount = new BigDecimal(listLoyaltyUser.get(i).getBalanceAmount()).setScale(loyaltySystemDb.getVcScale(), RoundingMode.HALF_UP);
                        if (balanceAmount.signum() > 0) {
                            BigDecimal vcRate1 = new BigDecimal(loyaltySystemDb.getVcRate());
                            BigDecimal rateBuy = vcRate1.multiply(new BigDecimal("0.98"));
                            BigDecimal amount = bonusAmountInRest.multiply(rateSell).setScale(0, RoundingMode.UP);
                            amount = amount.divide(rateBuy, loyaltySystemDb.getVcScale(), RoundingMode.UP);
                            BigDecimal amountMax = balanceAmount;
                            if (balanceAmount.compareTo(amount) >= 0) {
                                amountMax = amount;
                            }
                            LoyaltyUserPayInfo loyaltyUserPayInfo = new LoyaltyUserPayInfo();
                            loyaltyUserPayInfo.setLoyaltyUserId(listLoyaltyUser.get(i).getId());
                            loyaltyUserPayInfo.setLoyaltyUserExtrnId(listLoyaltyUser.get(i).getExtrnId());
                            loyaltyUserPayInfo.setBalanceAmount(balanceAmount.toPlainString());
                            loyaltyUserPayInfo.setLoyaltySystemId(loyaltySystemDb.getId());
                            loyaltyUserPayInfo.setLoyaltySystemName(loyaltySystemDb.getName());
                            loyaltyUserPayInfo.setVcAlias(loyaltySystemDb.getVcAlias());
                            loyaltyUserPayInfo.setVcName(loyaltySystemDb.getVcName());
                            loyaltyUserPayInfo.setVcScale(loyaltySystemDb.getVcScale());
                            loyaltyUserPayInfo.setLocalTrans(false);
                            loyaltyUserPayInfo.setRateBuy(rateBuy.toPlainString());
                            loyaltyUserPayInfo.setBonusAmountInMax(amountMax.toPlainString());
                            listLoyaltyUserPayInfo.add(loyaltyUserPayInfo);
                        }
                    }
                }
            }
            rspOperPayCheck.setListLoyaltyUserPayInfo(listLoyaltyUserPayInfo.toArray(new LoyaltyUserPayInfo[0]));
            return new ResponseEntity<>(rspOperPayCheck, HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }

    @Operation(summary = "Створення операції та транзакцій конвертації балів з інших систем лояльності.",
            description = "Створюється операція та транзакції конвертації без запуску їх на виконання.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Операція гаманця.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Oper.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/oper-pay-create")
    public ResponseEntity<Oper> operPayCreate(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметри платіжної операції отримані від мобільного додатку.") @RequestBody ReqOperPayCreate req,
                                                 @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemOperDb = appService.getLoyaltySystemRepository().findById(req.getLoyaltySystemId()).orElse(null);
            if (loyaltySystemOperDb == null || loyaltySystemOperDb.getVcRate()==null || loyaltySystemOperDb.getVcRate().isEmpty())
                throw new ApiException(apiReq, "Система лояльності " + req.getLoyaltySystemId() + " не підтримується.", null, null);
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserOperDb = appService.getCustomRepository().loyaltyUserFindByWalletUserAndLoyaltySystem(apiReq.getWalletUser().getId(), loyaltySystemOperDb.getId());
            if(loyaltyUserOperDb==null){
                RestTemplate restTemplate = new RestTemplate();
                String bodyJson = "{\"tel\":\""+apiReq.getWalletUser().getId()+"\"}";
                JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl() + "/lu", HttpMethod.POST, Utils.createRestRequest(loyaltySystemOperDb.getId()+":_", bodyJson), String.class));
                loyaltyUserOperDb = new edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser();
                loyaltyUserOperDb.setId(UUID.randomUUID().toString());
                loyaltyUserOperDb.setWalletUserId(apiReq.getWalletUser().getId());
                loyaltyUserOperDb.setLoyaltySystemId(loyaltySystemOperDb.getId());
                loyaltyUserOperDb.setExtrnId(root.path("id").asText());
                appService.getLoyaltyUserRepository().save(loyaltyUserOperDb);
            }
            BigDecimal vcRate = new BigDecimal(loyaltySystemOperDb.getVcRate()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
            BigDecimal rateSell = vcRate.multiply(new BigDecimal("1.02"));
            edu.doggy228.loyaltyexch.wms.modeldb.Oper operDb = new edu.doggy228.loyaltyexch.wms.modeldb.Oper();
            operDb.setId(UUID.randomUUID().toString());
            operDb.setWalletUserId(apiReq.getWalletUser().getId());
            operDb.setOperType(OperType.PAY);
            operDb.setState(OperStateType.CREATE);
            operDb.setOperCreateDt(Utils.getDateTimeCur());
            operDb.setLoyaltySystemId(req.getLoyaltySystemId());
            operDb.setLoyaltyUserId(loyaltyUserOperDb.getId());
            operDb.setOperAmount(req.getOperAmount());
            operDb.setOperCurrencyType(CurrencyType.FIAT_CURRENCY);
            operDb.setOperCurrencyAlias(Core.FC_ALIAS_UAH);
            operDb.setOperPurpose(req.getOperPurpose());
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> refLoyaltySystems = new HashMap<>();
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> refLoyaltyUsers = new HashMap<>();
            refLoyaltySystems.put(loyaltySystemOperDb.getId(), loyaltySystemOperDb);
            refLoyaltyUsers.put(loyaltyUserOperDb.getId(), loyaltyUserOperDb);
            List<edu.doggy228.loyaltyexch.wms.modeldb.Trans> listTrans = new ArrayList<>();
            BigDecimal bonusAmountIn = BigDecimal.ZERO.setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
            for(LoyaltyUserPay loyaltyUserPay: req.getListLoyaltyUserPay()){
                edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb = appService.getLoyaltyUserRepository().findById(loyaltyUserPay.getLoyaltyUserId()).orElse(null);
                if(loyaltyUserDb==null) continue;
                edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemDb = appService.getLoyaltySystemRepository().findById(loyaltyUserDb.getLoyaltySystemId()).orElse(null);
                if(loyaltySystemDb==null || loyaltySystemDb.getVcRate()==null || loyaltySystemDb.getVcRate().isEmpty()) continue;
                BigDecimal sendAmount = new BigDecimal(loyaltyUserPay.getSendAmount()).setScale(loyaltySystemDb.getVcScale(), RoundingMode.HALF_UP);
                if(loyaltySystemDb.getId().equals(loyaltySystemOperDb.getId())){
                    bonusAmountIn = bonusAmountIn.add(sendAmount);
                    continue;
                }
                BigDecimal vcRate1 = new BigDecimal(loyaltySystemDb.getVcRate()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                BigDecimal rateBuy = vcRate1.multiply(new BigDecimal("0.98"));
                BigDecimal scAmount = sendAmount.multiply(rateBuy).setScale(0, RoundingMode.HALF_UP);
                BigDecimal rcptAmount = scAmount.divide(rateSell, loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                edu.doggy228.loyaltyexch.wms.modeldb.Trans transDb = new edu.doggy228.loyaltyexch.wms.modeldb.Trans();
                transDb.setId(UUID.randomUUID().toString());
                transDb.setOperId(operDb.getId());
                transDb.setWalletUserId(operDb.getWalletUserId());
                transDb.setTransType(TransType.LS_EXCH);
                transDb.setState(TransStateType.CREATE);
                transDb.setTransCreateDt(Utils.getDateTimeCur());
                transDb.setLoyaltySystemId(loyaltySystemDb.getId());
                transDb.setLoyaltyUserId(loyaltyUserDb.getId());
                transDb.setSendAmount(sendAmount.toPlainString());
                transDb.setSendCurrencyType(CurrencyType.VIRTUAL_CURRENCY);
                transDb.setSendCurrencyAlias(loyaltySystemDb.getVcAlias());
                transDb.setRcptAmount(rcptAmount.toPlainString());
                transDb.setRcptCurrencyType(CurrencyType.VIRTUAL_CURRENCY);
                transDb.setRcptCurrencyAlias(loyaltySystemOperDb.getVcAlias());
                transDb.setTransPurpose("Обмін "+loyaltySystemDb.getVcAlias()+"["+loyaltySystemDb.getName()+"] -> "+loyaltySystemOperDb.getVcAlias()+"["+loyaltySystemOperDb.getName()+"]");
                transDb.setDetails(new AttrValue[0]);
                listTrans.add(transDb);
                bonusAmountIn = bonusAmountIn.add(rcptAmount);
                refLoyaltySystems.put(loyaltySystemDb.getId(), loyaltySystemDb);
                refLoyaltyUsers.put(loyaltyUserDb.getId(), loyaltyUserDb);
            }
            List<AttrValue> detailsOper = new ArrayList<>();
            detailsOper.add(new AttrValue("bonusAmountIn", bonusAmountIn.toPlainString()));
            operDb.setDetails(detailsOper.toArray(new AttrValue[0]));
            StringBuilder sbRefLoyaltySystems = new StringBuilder();
            for(String el: refLoyaltySystems.keySet()){
                sbRefLoyaltySystems.append(";"+el+";");
            }
            operDb.setRefLoyaltySystems(sbRefLoyaltySystems.toString());
            StringBuilder sbRefLoyaltyUsers = new StringBuilder();
            for(String el: refLoyaltyUsers.keySet()){
                sbRefLoyaltyUsers.append(";"+el+";");
            }
            operDb.setRefLoyaltyUsers(sbRefLoyaltyUsers.toString());
            operDb.setListTrans(listTrans.toArray(new edu.doggy228.loyaltyexch.wms.modeldb.Trans[0]));
            appService.getOperRepository().save(operDb);
            return new ResponseEntity<>(operDb.toJson(refLoyaltySystems), HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }

    @Operation(summary = "Акцептування операції клієнтом та запуск її на виконання.",
            description = "Виконується обмін балів лояльності.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Операція виконана.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RspOperPayExec.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/oper-pay-exec")
    public ResponseEntity<RspOperPayExec> operPayExec(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметри акцептування операції.") @RequestBody ReqOperPayExec req,
                                              @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            edu.doggy228.loyaltyexch.wms.modeldb.Oper operDb = appService.getOperRepository().findById(req.getOperId()).orElse(null);
            if (operDb == null) throw new ApiException(apiReq, "Операція " + req.getOperId() + " не знайдена.", null, null);
            switch(operDb.getState()){
                case CREATE:
                    break;
                case OK:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " вже виконана.", null, null);
                case ERR:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " вже виконувалася.", null, null);
                case EXEC:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " вже виконується.", null, null);
                case CANCEL:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " скасована.", null, null);
                default:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " не може бути викконана.", null, null);
            }
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemOperDb = appService.getLoyaltySystemRepository().findById(operDb.getLoyaltySystemId()).orElse(null);
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserOperDb = appService.getLoyaltyUserRepository().findById(operDb.getLoyaltyUserId()).orElse(null);
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> refLoyaltySystems = new HashMap<>();
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser> refLoyaltyUsers = new HashMap<>();
            refLoyaltySystems.put(loyaltySystemOperDb.getId(), loyaltySystemOperDb);
            refLoyaltyUsers.put(loyaltyUserOperDb.getId(), loyaltyUserOperDb);
            BigDecimal vcRate = new BigDecimal(loyaltySystemOperDb.getVcRate());
            BigDecimal rateSell = vcRate.multiply(new BigDecimal("1.02"));
            List<AttrValue> operDetails = Utils.attrValueToList(operDb.getDetails());
            BigDecimal bonusAmountIn = new BigDecimal(Utils.attrValueGet(operDb.getDetails(),"bonusAmountIn", null)).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
            boolean flerr = false;
            String msgerr = null;
            for(int i=0;i<operDb.getListTrans().length;i++){
                List<AttrValue> transDetails = Utils.attrValueToList(operDb.getListTrans()[i].getDetails());
                try {
                    edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemDb = appService.getLoyaltySystemRepository().findById(operDb.getListTrans()[i].getLoyaltySystemId()).orElse(null);
                    edu.doggy228.loyaltyexch.wms.modeldb.LoyaltyUser loyaltyUserDb = appService.getLoyaltyUserRepository().findById(operDb.getListTrans()[i].getLoyaltyUserId()).orElse(null);
                    refLoyaltySystems.put(loyaltySystemDb.getId(), loyaltySystemOperDb);
                    refLoyaltyUsers.put(loyaltyUserDb.getId(), loyaltyUserOperDb);
                    BigDecimal sendAmount = new BigDecimal(operDb.getListTrans()[i].getSendAmount()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                    BigDecimal vcRate1 = new BigDecimal(loyaltySystemDb.getVcRate());
                    BigDecimal rateBuy = vcRate1.multiply(new BigDecimal("0.98"));
                    BigDecimal scAmount = sendAmount.multiply(rateBuy).setScale(0, RoundingMode.HALF_UP);
                    RestTemplate restTemplate = new RestTemplate();
                    String bodyJson = "{" +
                            "\"extrnId\":\""+operDb.getListTrans()[i].getId()+"\"," +
                            "\"loyaltyUserId\":\""+loyaltyUserDb.getExtrnId()+"\"," +
                            "\"bonusAmountChange\":\"-"+sendAmount.toPlainString()+"\"," +
                            "\"systemAmountChange\":\""+scAmount.toPlainString()+"\"," +
                            "\"purpose\":\""+operDb.getListTrans()[i].getTransPurpose()+"\"" +
                            "}";
                    JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl() + "/transexternal-withdrawal", HttpMethod.POST, Utils.createRestRequest(loyaltySystemDb.getId()+":_", bodyJson), String.class));
                    Utils.attrValueSet(transDetails, "sendTransExtrnId", root.path("id").asText());
                    operDb.getListTrans()[i].setSendAmount(root.path("bonusAmountChange").asText().substring(1));
                    sendAmount = new BigDecimal(operDb.getListTrans()[i].getSendAmount()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                    scAmount = sendAmount.multiply(rateBuy).setScale(0, RoundingMode.HALF_UP);
                    BigDecimal rcptAmount = scAmount.divide(rateSell, loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                    BigDecimal rcptAmountOld = new BigDecimal(operDb.getListTrans()[i].getRcptAmount()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP);
                    restTemplate = new RestTemplate();
                    bodyJson = "{" +
                            "\"extrnId\":\""+operDb.getListTrans()[i].getId()+"\"," +
                            "\"loyaltyUserId\":\""+loyaltyUserOperDb.getExtrnId()+"\"," +
                            "\"bonusAmountChange\":\""+rcptAmount.toPlainString()+"\"," +
                            "\"systemAmountChange\":\"-"+scAmount.toPlainString()+"\"," +
                            "\"purpose\":\""+operDb.getListTrans()[i].getTransPurpose()+"\"" +
                            "}";
                    root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl() + "/transexternal-replenishment", HttpMethod.POST, Utils.createRestRequest(loyaltySystemOperDb.getId()+":_", bodyJson), String.class));
                    Utils.attrValueSet(transDetails, "rcptTransExtrnId", root.path("id").asText());
                    operDb.getListTrans()[i].setRcptAmount(root.path("bonusAmountChange").asText());
                    rcptAmountOld = rcptAmountOld.subtract(new BigDecimal(operDb.getListTrans()[i].getRcptAmount()).setScale(loyaltySystemOperDb.getVcScale(), RoundingMode.HALF_UP));
                    if(rcptAmountOld.signum()!=0) bonusAmountIn = bonusAmountIn.subtract(rcptAmountOld);
                    operDb.getListTrans()[i].setState(TransStateType.OK);
                    operDb.getListTrans()[i].setStateMsg(null);
                    operDb.getListTrans()[i].setTransExecDt(Utils.getDateTimeCur());
                } catch (Throwable e1){
                    e1.printStackTrace();
                    operDb.getListTrans()[i].setState(TransStateType.ERR);
                    operDb.getListTrans()[i].setStateMsg("Помилка обміну балів лояльності.");
                    operDb.getListTrans()[i].setTransExecDt(Utils.getDateTimeCur());
                    flerr = true;
                    if(msgerr==null) msgerr = operDb.getListTrans()[i].getStateMsg();
                }
                operDb.getListTrans()[i].setDetails(transDetails.toArray(new AttrValue[0]));
            }
            Utils.attrValueSet(operDetails, "bonusAmountIn", bonusAmountIn.toPlainString());
            operDb.setDetails(operDetails.toArray(new AttrValue[0]));
            operDb.setOperExecDt(Utils.getDateTimeCur());
            if(flerr){
                operDb.setState(OperStateType.ERR);
                operDb.setStateMsg(msgerr);
            } else {
                operDb.setState(OperStateType.OK);
                operDb.setStateMsg(null);
            }
            appService.getOperRepository().save(operDb);
            RspOperPayExec rsp = new RspOperPayExec();
            rsp.setOper(operDb.toJson(refLoyaltySystems));
            rsp.setLoyaltySystemId(loyaltySystemOperDb.getId());
            rsp.setLoyaltySystemName(loyaltySystemOperDb.getName());
            rsp.setVcAlias(loyaltySystemOperDb.getVcAlias());
            rsp.setVcName(loyaltySystemOperDb.getVcName());
            rsp.setVcScale(loyaltySystemOperDb.getVcScale());
            rsp.setLoyaltyUserId(loyaltyUserOperDb.getId());
            rsp.setLoyaltyUserExtrnId(loyaltyUserOperDb.getExtrnId());
            rsp.setBonusAmountIn(bonusAmountIn.toPlainString());
            return new ResponseEntity<>(rsp, HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }

    @Operation(summary = "Інформування про виконання операції на POS терміналі торгової точки.",
            description = "Додається інформація про операцію на терміналі торгової точки.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Операція виконана.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Oper.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/oper-pay-lspay")
    public ResponseEntity<Oper> operPayLSPay(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Параметри операції на терміналі торгової точки.") @RequestBody ReqOperPayLSPay req,
                                                      @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            edu.doggy228.loyaltyexch.wms.modeldb.Oper operDb = appService.getOperRepository().findById(req.getOperId()).orElse(null);
            if (operDb == null) throw new ApiException(apiReq, "Операція " + req.getOperId() + " не знайдена.", null, null);
            switch(operDb.getState()){
                case CREATE:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " не виконувалася.", null, null);
                case OK:
                    break;
                case ERR:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " вже виконувалася.", null, null);
                case EXEC:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " вже виконується.", null, null);
                case CANCEL:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " скасована.", null, null);
                default:
                    throw new ApiException(apiReq, "Операція " + req.getOperId() + " не може бути викконана.", null, null);
            }
            operDb.setOperAmount(req.getLsTransAmount());
            operDb.setOperPurpose(req.getLsTransPurpose());
            List<AttrValue> operDetails = Utils.attrValueToList(operDb.getDetails());
            Utils.attrValueSet(operDetails, "bonusAmountIn", req.getLsBonusAmountIn());
            Utils.attrValueSet(operDetails, "bonusAmountOut", req.getLsBonusAmountOut());
            Utils.attrValueSet(operDetails, "payAmount", req.getLsTransAmountPay());
            operDb.setDetails(operDetails.toArray(new AttrValue[0]));
            edu.doggy228.loyaltyexch.wms.modeldb.Trans transLSPayDb = new edu.doggy228.loyaltyexch.wms.modeldb.Trans();
            transLSPayDb.setId(UUID.randomUUID().toString());
            transLSPayDb.setOperId(operDb.getId());
            transLSPayDb.setWalletUserId(operDb.getWalletUserId());
            transLSPayDb.setTransType(TransType.LS_PAY);
            transLSPayDb.setState(TransStateType.OK);
            transLSPayDb.setTransCreateDt(Utils.getDateTimeCur());
            transLSPayDb.setTransExecDt(Utils.getDateTimeCur());
            transLSPayDb.setLoyaltySystemId(operDb.getLoyaltySystemId());
            transLSPayDb.setLoyaltyUserId(operDb.getLoyaltyUserId());
            transLSPayDb.setSendAmount(req.getLsTransAmount());
            transLSPayDb.setSendCurrencyType(CurrencyType.FIAT_CURRENCY);
            transLSPayDb.setSendCurrencyAlias(Core.FC_ALIAS_UAH);
            transLSPayDb.setRcptAmount(req.getLsTransAmount());
            transLSPayDb.setRcptCurrencyType(CurrencyType.FIAT_CURRENCY);
            transLSPayDb.setRcptCurrencyAlias(Core.FC_ALIAS_UAH);
            transLSPayDb.setTransPurpose(req.getLsTransPurpose());
            List<AttrValue> transDetails = new ArrayList<>();
            if(req.getLsBonusAmountIn()!=null && !req.getLsBonusAmountIn().isEmpty()) transDetails.add(new AttrValue("bonusAmountIn", req.getLsBonusAmountIn()));
            if(req.getLsBonusAmountOut()!=null && !req.getLsBonusAmountOut().isEmpty()) transDetails.add(new AttrValue("bonusAmountOut", req.getLsBonusAmountOut()));
            if(req.getLsTransAmountPay()!=null && !req.getLsTransAmountPay().isEmpty()) transDetails.add(new AttrValue("payAmount", req.getLsTransAmountPay()));
            if(req.getLsTransId()!=null && !req.getLsTransId().isEmpty()) transDetails.add(new AttrValue("lsTransId", req.getLsTransId()));
            if(req.getLsTransDt()!=null && !req.getLsTransDt().isEmpty()) transDetails.add(new AttrValue("lsTransDt", req.getLsTransDt()));
            transLSPayDb.setDetails(transDetails.toArray(new AttrValue[0]));
            List<edu.doggy228.loyaltyexch.wms.modeldb.Trans> listTrans = new ArrayList<>();
            if(operDb.getListTrans()!=null && operDb.getListTrans().length>0){
                for(edu.doggy228.loyaltyexch.wms.modeldb.Trans el: operDb.getListTrans()){
                    listTrans.add(el);
                }
            }
            listTrans.add(transLSPayDb);
            operDb.setListTrans(listTrans.toArray(new edu.doggy228.loyaltyexch.wms.modeldb.Trans[0]));
            appService.getOperRepository().save(operDb);
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> mapLoyaltySystems = new HashMap<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> listLoyaltySystemDb = appService.getLoyaltySystemRepository().findAll();
            for(edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem el: listLoyaltySystemDb){
                mapLoyaltySystems.put(el.getId(), el);
            }
            return new ResponseEntity<>(operDb.toJson(mapLoyaltySystems), HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }

    @Operation(summary = "Останні 100 операцій по гаманцю.",
            description = "Отримання останніх операцій по гаманцю.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Список останніх операцій.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RspListOper.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @GetMapping("/find/oper/byall")
    public ResponseEntity<RspListOper> findOperByAll(@RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthWalletUser();
            List<edu.doggy228.loyaltyexch.wms.modeldb.Oper> listDb = appService.getCustomRepository().operFindByWalletUserLast100(apiReq.getWalletUser().getId());
            RspListOper rsp = new RspListOper();
            if(listDb==null || listDb.isEmpty()){
                rsp.setListOper(new Oper[0]);
                return new ResponseEntity<>(rsp, HttpStatus.OK);
            }
            Map<String, edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> mapLoyaltySystems = new HashMap<>();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> listLoyaltySystemDb = appService.getLoyaltySystemRepository().findAll();
            for(edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem el: listLoyaltySystemDb){
                mapLoyaltySystems.put(el.getId(), el);
            }
            rsp.setListOper(new Oper[listDb.size()]);
            for(int i=0;i<listDb.size();i++){
                rsp.getListOper()[i] = listDb.get(i).toJson(mapLoyaltySystems);
            }
            return new ResponseEntity<>(rsp, HttpStatus.OK);
        } catch (Throwable e) {
            e.printStackTrace();
            if (e instanceof ApiException) throw (ApiException) e;
            throw new ApiException(apiReq, "Помилка виконання запиту", "" + e, e);
        }
    }
}
