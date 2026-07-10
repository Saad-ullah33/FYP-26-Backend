package com.propsightai.Service;

import com.propsightai.Dto.PropertyResponseDto;

import java.util.List;

public interface RecommendationService {

    /**
     * Get property recommendations for a user.
     *
     * @param userId the user ID
     * @param limit maximum number of recommendations
     * @return list of recommended properties
     */
    List<PropertyResponseDto> getRecommendations(Integer userId, Integer limit);

    /**
     * Get personalized recommendations based on user's activity.
     *
     * @param userId the user ID
     * @param limit maximum number of recommendations
     * @return list of recommendations based on views, bids, and category similarity
     */
    List<PropertyResponseDto> getPersonalizedRecommendations(Integer userId, Integer limit);

    /**
     * Get trending properties (most viewed/bid on recently).
     *
     * @param limit maximum number of results
     * @return trending properties
     */
    List<PropertyResponseDto> getTrendingProperties(Integer limit);

    /**
     * Get similar properties to a given property.
     *
     * @param propertyId the reference property ID
     * @param limit maximum number of similar properties
     * @return list of similar properties
     */
    List<PropertyResponseDto> getSimilarProperties(Integer propertyId, Integer limit);
}
