package edu.doggy228.loyaltyexch.wms.api.v1.admin;

import com.fasterxml.jackson.databind.JsonNode;
import edu.doggy228.loyaltyexch.wms.Utils;
import edu.doggy228.loyaltyexch.wms.api.v1.ApiException;
import edu.doggy228.loyaltyexch.wms.api.v1.RspListLoyaltySystem;
import edu.doggy228.loyaltyexch.wms.modeljson.LoyaltySystem;
import edu.doggy228.loyaltyexch.wms.modeljson.ResponseError;
import edu.doggy228.loyaltyexch.wms.service.ApiReq;
import edu.doggy228.loyaltyexch.wms.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.List;

@Tag(name = "Адміністрування системи.", description = "Функціонал для АРМу Адміністратора.")
@RestController
@RequestMapping("/api/v1/wms/admin")
public class AdminController {
    private AppService appService;

    @Autowired
    public void setAppService(AppService appService) {
        this.appService = appService;
    }

    @Operation(summary = "Отримання списку систем лояльності.",
            description = "Список доступних систем лояльності.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Отримання повного списку систем лояльності.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RspListLoyaltySystem.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @GetMapping("/ls")
    public ResponseEntity<RspListLoyaltySystem> loyaltySystemGetAll(@RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            apiReq.checkAuthAdmin();
            List<edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem> loyaltySystemListDb = appService.getLoyaltySystemRepository().findAll();
            RspListLoyaltySystem rsp = new RspListLoyaltySystem();
            if(loyaltySystemListDb==null || loyaltySystemListDb.isEmpty()){
                rsp.setListLoyaltySystem(new LoyaltySystem[0]);
            } else {
                rsp.setListLoyaltySystem(new LoyaltySystem[loyaltySystemListDb.size()]);
                for(int i=0;i<loyaltySystemListDb.size();i++){
                    rsp.getListLoyaltySystem()[i] = loyaltySystemListDb.get(i).toJson();
                }
            }
            return new ResponseEntity<>(rsp, HttpStatus.OK);
        } catch (Throwable e) {
            if (e instanceof ApiException) throw (ApiException)e;
            throw new ApiException(apiReq, "Помилка виконання запиту", ""+e, e);
        }
    }


    @Operation(summary = "Створення нової системи лояльності.",
            description = "Конфігурація нової системи лояльності.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Нова система лояльності створена.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoyaltySystem.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/ls")
    public ResponseEntity<LoyaltySystem> loyaltySystemCreate(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Форма регістрації системи лояльності.") @RequestBody ReqLoyaltySystemCreate req,
                                                             @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemDb = new edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem();
            loyaltySystemDb.setId(req.getId());
            loyaltySystemDb.setName(req.getName());
            appService.getLoyaltySystemRepository().save(loyaltySystemDb);
            return new ResponseEntity<>(loyaltySystemDb.toJson(), HttpStatus.OK);
        } catch (Throwable e) {
            if (e instanceof ApiException) throw (ApiException)e;
            throw new ApiException(apiReq, "Помилка виконання запиту", ""+e, e);
        }
    }

    @Operation(summary = "Зміна параметрів системи лояльності.",
            description = "Зміна конфігураційних параметрів системи лояльності. Включає в себе синхронізацію параметрів баллів с зовнішньою системою.",
            security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успіх. Конфігурація системи лояльності змінена.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LoyaltySystem.class))}),
            @ApiResponse(responseCode = "500", description = "Помилка виконання.",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))})
    })
    @PostMapping("/ls/{id}")
    public ResponseEntity<LoyaltySystem> loyaltySystemUpdate(@Parameter(description = "Ідентифікатор системи лояльності") @PathVariable String id,
                                                             @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Форма оновлення системи лояльності.") @RequestBody ReqLoyaltySystemUpdate req,
                                                             @RequestHeader HttpHeaders httpHeaders) {
        ApiReq apiReq = new ApiReq(appService, httpHeaders, null);
        try {
            edu.doggy228.loyaltyexch.wms.modeldb.LoyaltySystem loyaltySystemDb = appService.getLoyaltySystemRepository().findById(id).orElse(null);
            if(loyaltySystemDb==null) throw new ApiException(apiReq, "Система лояльності {"+id+"} не знайдена.", null, null);
            if(req.getName()!=null && !req.getName().isEmpty()) loyaltySystemDb.setName(req.getName());
            RestTemplate restTemplate = new RestTemplate();
            JsonNode root = Utils.getRestResponseRoot(apiReq, restTemplate.exchange(appService.getAppConfig().getLsemuBaseurl()+"/ls/"+id, HttpMethod.GET, Utils.createRestRequest(null,null), String.class));
            loyaltySystemDb.setVcAlias(root.path("vcAlias").asText());
            loyaltySystemDb.setVcName(root.path("vcName").asText());
            loyaltySystemDb.setVcRate(root.path("vcRate").asText());
            loyaltySystemDb.setVcScale(root.path("vcScale").asInt());
            appService.getLoyaltySystemRepository().save(loyaltySystemDb);
            return new ResponseEntity<>(loyaltySystemDb.toJson(), HttpStatus.OK);
        } catch (Throwable e) {
            if (e instanceof ApiException) throw (ApiException)e;
            throw new ApiException(apiReq, "Помилка виконання запиту", ""+e, e);
        }
    }

}
