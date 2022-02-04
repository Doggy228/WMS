package edu.doggy228.loyaltyexch.wms;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.doggy228.loyaltyexch.wms.api.v1.ApiException;
import edu.doggy228.loyaltyexch.wms.service.ApiReq;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.StringTokenizer;

public final class Utils {
    public static LocalDateTime getDateTimeCur() {
        return LocalDateTime.now();
    }
    public static LocalDateTime getDateTimeCurUTC() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    public static LocalDateTime truncateToDayStart(LocalDateTime ldt) {
        if (ldt == null) return null;
        return LocalDateTime.of(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth(), 0, 0, 0, 0);
    }

    public static LocalDateTime truncateToDayNextStart(LocalDateTime ldt) {
        if (ldt == null) return null;
        LocalDateTime res = truncateToDayStart(ldt);
        return res.plusDays(1);
    }

    public static String getDateTimeStr(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        } else {
            return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

    public static String getDateStr(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        } else {
            return ldt.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    public static String getTimestampStr() {
        return getDateTimeStr(getDateTimeCurUTC());
    }

    public static LocalDateTime parseDateTimeStr(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return ldt;
        }
    }

    public static LocalDateTime parseDateStr(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        } else {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
        }
    }

    public static String maskEmail(String s) {
        if (s == null || s.isEmpty()) return s;
        int pos = s.lastIndexOf(".");
        StringBuilder sb = new StringBuilder();
        sb.append(s.charAt(0));
        for (int i = 1; i < s.length(); i++) {
            if (s.charAt(i) == '@') {
                sb.append('@');
            } else if (pos < 0 || i < pos) {
                sb.append('X');
            } else {
                sb.append(s.charAt(i));
            }
        }
        return sb.toString();
    }

    public static String maskTel(String s) {
        if (s == null || s.isEmpty()) return s;
        if (s.length() > 10) {
            StringBuilder sb = new StringBuilder();
            sb.append(s.substring(0, 3));
            for (int i = 3; i < s.length() - 4; i++) {
                sb.append('X');
            }
            sb.append(s.substring(s.length() - 4));
            return sb.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(s.substring(0, 1));
            for (int i = 1; i < s.length() - 1; i++) {
                sb.append('X');
            }
            sb.append(s.substring(s.length() - 1));
            return sb.toString();
        }
    }

    public static String maskName(String s) {
        if (s == null || s.isEmpty()) return s;
        StringTokenizer st = new StringTokenizer(s, " ");
        StringBuilder sb = new StringBuilder();
        if (st.countTokens() == 1) {
            String s1 = st.nextToken();
            sb.append(s1.charAt(0));
            for (int i = 1; i < s1.length(); i++) {
                sb.append('X');
            }
        } else {
            sb.append(st.nextToken());
            while (st.hasMoreTokens()) {
                sb.append(' ');
                String s1 = st.nextToken();
                sb.append(s1.charAt(0));
                for (int i = 1; i < s1.length(); i++) {
                    sb.append('X');
                }
            }
        }
        return sb.toString();
    }

    public static String maskBankCardNum(String s) {
        if (s == null || s.isEmpty() || s.length()!=16) return s;
        return s.substring(0,4)+" "+s.substring(4,6)+"XX XXXX "+s.substring(12,16);
    }


    public static long toCurrencyValueLong(String value) {
        if (value == null || value.isEmpty() || value.equals("0") || value.equals("0.00")) return 0;
        int pos = value.lastIndexOf('.');
        if (pos < 0) {
            pos = value.lastIndexOf(',');
        }
        if (pos < 0) return Long.parseLong(value, 10) * 100L;
        if (pos == value.length() - 1) return Long.parseLong(value.substring(0, value.length() - 1), 10) * 100L;
        long coin = 0;
        if (pos == value.length() - 2) {
            coin = Long.parseLong(value.substring(pos + 1), 10) * 10L;
        } else if (pos == value.length() - 3) {
            coin = Long.parseLong(value.substring(pos + 1), 10);
        }
        long res = Long.parseLong(value.substring(0, pos), 10) * 100L;
        if (res < 0) res = res - coin;
        else res = res + coin;
        return res;
    }

    public static String toCurrencyValueStr(long value, boolean flnull) {
        if(value==0) return flnull?null:"0.00";
        long val1 = value/100L;
        long val2 = Math.abs(value%100L);
        if(val2==0) return val1+".00";
        if(val2<10) return val1+".0"+val2;
        return val1+"."+val2;
    }

    public static HttpEntity<String> createRestRequest(String bearerAuth, String bodyJson){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        if(bearerAuth!=null && !bearerAuth.isEmpty()) headers.setBearerAuth(bearerAuth);
        if(bodyJson!=null && !bodyJson.isEmpty()) {
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new HttpEntity<>(bodyJson, headers);
        }
        return new HttpEntity<>(headers);
    }
    public static JsonNode getRestResponseRoot(ApiReq apiReq, ResponseEntity<String> response) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        switch(response.getStatusCode()){
            case OK: case CREATED:
                if(!response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)) throw new ApiException(apiReq, "Помилка запиту до зовнішньої системи", "Response not JSON.",null);
                return mapper.readTree(response.getBody());
            default:
                if(response.getHeaders().getContentType().includes(MediaType.APPLICATION_JSON)){
                    JsonNode root = mapper.readTree(response.getBody());
                    throw new ApiException(apiReq, root.path("msg").asText("Помилка запиту до зовнішньої системи"), root.path("detail").asText("HTTP-ERR-"+response.getStatusCodeValue()),null);
                } else {
                    throw new ApiException(apiReq, "Помилка запиту до зовнішньої системи", "HTTP-ERR-"+response.getStatusCodeValue(),null);
                }
        }
    }
}