package com.sao_rafael.crm_core.infrastructure.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class StatusLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(StatusLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        StatusCaptureResponse wrappedResponse = new StatusCaptureResponse(response);
        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, wrappedResponse);
        } catch (Exception ex) {
            long durationMs = System.currentTimeMillis() - start;
            logger.error("HTTP {} {} -> 500 (exception) in {} ms", request.getMethod(), request.getRequestURI(), durationMs, ex);
            throw ex;
        } finally {
            int status = wrappedResponse.getStatus();
            if (status != HttpServletResponse.SC_OK) {
                long durationMs = System.currentTimeMillis() - start;
                String query = request.getQueryString();
                String path = query == null ? request.getRequestURI() : request.getRequestURI() + "?" + query;
                logger.warn("HTTP {} {} -> {} in {} ms", request.getMethod(), path, status, durationMs);
            }
        }
    }

    private static class StatusCaptureResponse extends HttpServletResponseWrapper {
        private int httpStatus = SC_OK;

        StatusCaptureResponse(HttpServletResponse response) {
            super(response);
        }

        @Override
        public void setStatus(int sc) {
            this.httpStatus = sc;
            super.setStatus(sc);
        }

        @Override
        public void sendError(int sc) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            this.httpStatus = sc;
            super.sendError(sc, msg);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            this.httpStatus = SC_FOUND;
            super.sendRedirect(location);
        }

        @Override
        public int getStatus() {
            return this.httpStatus;
        }
    }
}
