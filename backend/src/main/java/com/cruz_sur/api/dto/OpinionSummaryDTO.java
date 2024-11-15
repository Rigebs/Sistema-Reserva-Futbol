package com.cruz_sur.api.dto;

import lombok.Data;

@Data
public class OpinionSummaryDTO {
    private Long companiaId;
    private int star1Count;
    private int star2Count;
    private int star3Count;
    private int star4Count;
    private int star5Count;
    private double averageRating;
    private int totalReviews;

}
