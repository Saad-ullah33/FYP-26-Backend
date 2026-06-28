package com.propsightai.Service;

import com.propsightai.Dto.PropertyScoreResponse;
import com.propsightai.Model.Property;
import com.propsightai.Role.PropertyType;
import org.springframework.stereotype.Service;

@Service
public class PropertyScoringService {

    public PropertyScoreResponse calculateScore(Property p) {

        int score = 50; // base score

        // 1. Location boost
        if (p.getArea().contains("Canal Road") ||
            p.getArea().contains("Citi Housing") ||
            p.getArea().contains("Wapda City")) {
            score += 20;
        } else {
            score += 10;
        }

        // 2. Property type value
        switch (p.getPropertyType()) {
            case PropertyType.HOUSE -> score += 15;
            case PropertyType.APARTMENT -> score += 10;
            case PropertyType.COMMERCIAL -> score += 20;
            default -> score += 5;
        }

        // 3. Price logic (VERY important)
        if (p.getPrice() < 10000000) {
            score += 15;
        } else if (p.getPrice() < 30000000) {
            score += 10;
        } else {
            score -= 10;
        }

        // 4. Bedrooms heuristic
        if (p.getBedrooms() >= 3) score += 10;

        // Clamp score
        score = Math.max(0, Math.min(100, score));

        String label;
        String reason;

        if (score >= 85) {
            label = "Excellent Investment";
            reason = "High demand location + good pricing";
        } else if (score >= 70) {
            label = "Good Value";
            reason = "Balanced price and location";
        } else if (score >= 50) {
            label = "Average";
            reason = "Standard property with moderate value";
        } else {
            label = "Risky";
            reason = "Overpriced or low-demand area";
        }

        PropertyScoreResponse res = new PropertyScoreResponse();
        res.setScore(score);
        res.setLabel(label);
        res.setReason(reason);

        return res;
    }
}