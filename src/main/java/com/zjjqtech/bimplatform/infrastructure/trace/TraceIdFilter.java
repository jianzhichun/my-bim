package com.zjjqtech.bimplatform.infrastructure.trace;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.slf4j.Marker;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * @author zao
 * @date 2020/09/23
 */
@Component
@Slf4j
public class TraceIdFilter extends OncePerRequestFilter {

    public static final String REQ = "REQ|", OTH = "OTH|", TRACE_ID = "TRACE_ID", X_REQUEST_ID = "X-Request-ID";

    static {
        LoggerContext loggerContext = (LoggerContext)LoggerFactory.getILoggerFactory();
        loggerContext.addTurboFilter(new TurboFilter() {
            @Override
            public FilterReply decide(Marker marker, Logger logger, Level level, String format, Object[] params, Throwable t) {
                String traceId = MDC.get(TRACE_ID);
                if (null == traceId) {
                    MDC.put(TRACE_ID, OTH + getTraceId());
                }
                return FilterReply.NEUTRAL;
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getHeader(X_REQUEST_ID);
        if (requestId == null) {
            requestId = getTraceId();
        }
        MDC.put(TRACE_ID, REQ + requestId);
        response.setHeader(X_REQUEST_ID, requestId);
        filterChain.doFilter(request, response);
    }

    private static String getTraceId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }
}
