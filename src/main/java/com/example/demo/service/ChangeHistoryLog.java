package com.example.demo.service;


import com.example.demo.models.ChangeHistory;
import com.example.demo.repository.ChangeHistoryRepository;
import com.example.demo.security.jwt.AuthTokenFilter;
import com.example.demo.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class ChangeHistoryLog {

    @Autowired
    private ChangeHistoryRepository changeHistoryRepository;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthTokenFilter authTokenFilter;

    public void saveChange(String id, String payLoadRequest, String payLoadResponse, String action, HttpServletRequest request) {
        ChangeHistory changeHistory = new ChangeHistory();
        changeHistory.setLogId(id);
        changeHistory.setLogDate(LocalDateTime.now());
        changeHistory.setMessageRequest(payLoadRequest);
        changeHistory.setMessageResponse(payLoadResponse);
        changeHistory.setModifiedBy(getUser(request));
        changeHistory.setAction(action);

        changeHistoryRepository.save(changeHistory);
    }

    public String getUser(HttpServletRequest request) {

        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            String jwt = headerAuth.substring(7, headerAuth.length());
//            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
//                return jwtUtils.getUserNameFromJwtToken(jwt);
//            }
            return extractUsername(jwt);
        }
        return "null";
    }

    public String extractUsername(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return "Invalid token format";
            }

            // Decode payload (2nd part)
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            JSONObject payload = new JSONObject(payloadJson);

            String username = payload.getString("sub");
            return username;

        } catch (Exception e) {
            return "Error decoding token: " + e.getMessage();
        }
    }

}

