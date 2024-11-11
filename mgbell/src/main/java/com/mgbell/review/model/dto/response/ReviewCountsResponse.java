package com.mgbell.review.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewCountsResponse {
    @JsonProperty("BEST")
    private int best;
    @JsonProperty("GOOD")
    private int good;
    @JsonProperty("NOTBAD")
    private int notBad;
    @JsonProperty("NOTGOOD")
    private int notGood;
}
