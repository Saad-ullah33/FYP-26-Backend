package com.propsightai;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AuditService {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuditService.class);

    @Autowired
    private AuditEventRepository repo;

    public void record(AuditEventType type, Integer userId, String details) {
        try {
            AuditEvent ev = new AuditEvent(type, userId, details);
            repo.save(ev);
        } catch (Exception e) {
            // don't block main flow if audit fails
            logger.warn("Failed to record audit: {}", e.getMessage());
        }
    }
}