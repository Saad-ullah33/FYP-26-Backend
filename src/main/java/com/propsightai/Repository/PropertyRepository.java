package com.propsightai.Repository;

import com.propsightai.Model.Property;
import com.propsightai.Role.PropertyType;
import com.propsightai.Role.PurposeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PropertyRepository extends JpaRepository<Property,Integer> {


    List<Property> findByPropertyTypeAndTrendingTrue(PropertyType type);

    List<Property> findByPropertyTypeAndFeaturedTrue(PropertyType type);

    List<Property> findByPropertyTypeOrderByCreatedAtDesc(PropertyType type);


    List<Property> findByPropertyType(PropertyType propertyType);

    @Query("SELECT p FROM Property p WHERE " +
            "(:cityId IS NULL OR p.city.id = :cityId) AND " +
            "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
            "(:purpose IS NULL OR p.purpose = :purpose) AND " +
            "(:minPrice IS NULL OR p.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.price <= :maxPrice)")
    Page<Property> searchProperties(@Param("cityId") Long cityId,
                                    @Param("propertyType") PropertyType propertyType,
                                    @Param("purpose") PurposeType purpose,
                                    @Param("minPrice") Double minPrice,
                                    @Param("maxPrice") Double maxPrice,
                                    Pageable pageable);

    int countByOwner_Id(Integer ownerId);

    List<Property> findByOwner_Id(Integer ownerId);

    @Modifying
    @Query(value = "DELETE FROM prediction_records WHERE property_id = :propertyId", nativeQuery = true)
    void deletePredictionRecordsByPropertyId(@Param("propertyId") Integer propertyId);

    @Modifying
    @Query(value = "DELETE FROM auctions WHERE property_id = :propertyId", nativeQuery = true)
    void deleteAuctionRecordsByPropertyId(@Param("propertyId") Integer propertyId);
    @Query("SELECT p FROM Property p WHERE p.id NOT IN :excludedIds AND FUNCTION('KEY_ENUM_TO_STRING_OR_CAST', p.propertyType) IN :types")
    List<Property> findByPropertyTypeNamesInAndIdNotIn(
            @Param("types") Collection<String> types,
            @Param("excludedIds") Collection<Integer> excludedIds,
            Pageable pageable);

    @Query("SELECT p FROM Property p WHERE p.propertyType = :type AND p.purpose = :purpose AND p.city = :city AND p.id <> :excludeId")
    List<Property> findSimilarPropertiesQuery(
            @Param("type") Object type,
            @Param("purpose") Object purpose,
            @Param("city") Object city,
            @Param("excludeId") Integer excludeId,
            Pageable pageable);
}
