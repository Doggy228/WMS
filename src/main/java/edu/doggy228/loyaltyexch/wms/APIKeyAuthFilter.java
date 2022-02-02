package com.pm1024.zugsee.apiwallet;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import javax.servlet.http.HttpServletRequest;

public class APIKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private String principalRequestHeader;

    public APIKeyAuthFilter(String principalRequestHeader) {
        this.principalRequestHeader = principalRequestHeader;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String principal = request.getHeader(principalRequestHeader);
        if(principal!=null && !principal.isEmpty()) return principal;
        principal = request.getParameter(principalRequestHeader.toLowerCase());
        if(principal!=null && !principal.isEmpty()) return principal;
        return "N/A";
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }
}

