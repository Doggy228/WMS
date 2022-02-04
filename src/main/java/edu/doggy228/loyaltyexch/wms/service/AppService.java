package edu.doggy228.loyaltyexch.wms.service;

import edu.doggy228.loyaltyexch.wms.AppConfig;
import edu.doggy228.loyaltyexch.wms.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

@Service
public class AppService {
    private AppConfig appConfig;
    private MessageSource messageSource;
    private WalletUserRepository walletUserRepository;
    private LoyaltySystemRepository loyaltySystemRepository;
    private LoyaltyUserRepository loyaltyUserRepository;
    private OperRepository operRepository;
    private CustomRepository customRepository;

    public AppService(AppConfig appConfig, MessageSource messageSource){
        this.appConfig = appConfig;
        this.messageSource = messageSource;
    }

    public AppConfig getAppConfig(){
        return appConfig;
    }

    @Autowired
    public void setWalletUserRepository(WalletUserRepository walletUserRepository){
        this.walletUserRepository = walletUserRepository;
    }

    public WalletUserRepository getWalletUserRepository(){
        return walletUserRepository;
    }

    @Autowired
    public void setLoyaltySystemRepository(LoyaltySystemRepository loyaltySystemRepository){
        this.loyaltySystemRepository = loyaltySystemRepository;
    }

    public LoyaltySystemRepository getLoyaltySystemRepository(){
        return loyaltySystemRepository;
    }

    @Autowired
    public void setLoyaltyUserRepository(LoyaltyUserRepository loyaltyUserRepository){
        this.loyaltyUserRepository = loyaltyUserRepository;
    }

    public LoyaltyUserRepository getLoyaltyUserRepository(){
        return loyaltyUserRepository;
    }

    @Autowired
    public void setOperRepository(OperRepository operRepository){
        this.operRepository = operRepository;
    }

    public OperRepository getOperRepository(){
        return operRepository;
    }

    @Autowired
    public void setCustomRepository(CustomRepository customRepository){
        this.customRepository = customRepository;
    }

    public CustomRepository getCustomRepository(){
        return customRepository;
    }

}
