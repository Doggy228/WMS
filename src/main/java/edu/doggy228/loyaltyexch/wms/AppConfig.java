package edu.doggy228.loyaltyexch.wms;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Getter
@Configuration
public class AppConfig {
    @Value("${app.baseurl}")
    private String appBaseurl;
    @Value("${app.lsemu.baseurl}")
    private String lsemuBaseurl;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("i18/msg");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}