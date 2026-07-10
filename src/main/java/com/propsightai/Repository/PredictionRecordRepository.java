package com.propsightai.Repository;

import com.propsightai.Model.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Integer> {

    List<PredictionRecord> findByPropertyIdOrderByPredictedAtDesc(Integer propertyId);

    @Query("SELECT COUNT(pr) FROM PredictionRecord pr WHERE pr.actualPrice IS NOT NULL")
    Long countCompletedPredictions();

    @Query("SELECT AVG(pr.predictionErrorPercentage) FROM PredictionRecord pr WHERE pr.actualPrice IS NOT NULL")
    Double getAverageErrorPercentage();

    @Query("SELECT COUNT(pr) FROM PredictionRecord pr WHERE pr.actualPrice IS NOT NULL AND pr.isAccurate = true")
    Long countAccuratePredictions();

    @Query("SELECT pr FROM PredictionRecord pr WHERE pr.predictedAt BETWEEN ?1 AND ?2 AND pr.actualPrice IS NOT NULL ORDER BY pr.predictionErrorPercentage ASC")
    List<PredictionRecord> findAccurateRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
