package com.propsightai.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "prediction_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id")
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private Property property;

    @Column(name = "predicted_price", nullable = false)
    private Double predictedPrice;

    @Column(name = "actual_price")
    private Double actualPrice;

    @Column(name = "prediction_error_percentage")
    private Double predictionErrorPercentage;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "predicted_at", nullable = false)
    private LocalDateTime predictedAt;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

    @Column(name = "is_accurate")
    private Boolean isAccurate;

    public void calculateError() {
        if (this.actualPrice != null && this.predictedPrice != null) {
            this.predictionErrorPercentage = Math.abs((this.predictedPrice - this.actualPrice) / this.actualPrice) * 100;
            this.isAccurate = this.predictionErrorPercentage <= 15; // Within 15% is considered accurate
        }
    }
}
