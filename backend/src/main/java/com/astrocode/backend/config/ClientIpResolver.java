package com.astrocode.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.util.matcher.IpAddressMatcher;
import org.springframework.stereotype.Component;

/**
 * Resolve o IP do cliente considerando proxies confiáveis e {@code X-Forwarded-For}.
 */
@Component
public class ClientIpResolver {

    private final String trustedProxies;

    public ClientIpResolver(@Value("${app.trusted-proxies:}") String trustedProxies) {
        this.trustedProxies = trustedProxies != null ? trustedProxies.trim() : "";
    }

    /**
     * @param request requisição HTTP atual
     * @return IP do cliente ou "unknown"
     */
    public String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr() != null ? request.getRemoteAddr() : "unknown";
        if (trustedProxies.isBlank()) {
            return remoteAddr;
        }
        for (String cidr : trustedProxies.split(",")) {
            String trimmed = cidr.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            try {
                IpAddressMatcher matcher = new IpAddressMatcher(trimmed);
                if (matcher.matches(remoteAddr)) {
                    String client = firstClientFromForwardedFor(request.getHeader("X-Forwarded-For"));
                    if (client != null && !client.isBlank()) {
                        return client;
                    }
                    break;
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        return remoteAddr;
    }

    private static String firstClientFromForwardedFor(String xForwardedFor) {
        if (xForwardedFor == null || xForwardedFor.isBlank()) {
            return null;
        }
        String first = xForwardedFor.split(",")[0].trim();
        if (first.startsWith("\"") && first.endsWith("\"") && first.length() >= 2) {
            first = first.substring(1, first.length() - 1).trim();
        }
        if (first.startsWith("[") && first.contains("]")) {
            return first.substring(1, first.indexOf(']'));
        }
        int lastColon = first.lastIndexOf(':');
        if (lastColon > 0 && first.chars().filter(ch -> ch == ':').count() == 1) {
            return first.substring(0, lastColon);
        }
        return first;
    }
}
