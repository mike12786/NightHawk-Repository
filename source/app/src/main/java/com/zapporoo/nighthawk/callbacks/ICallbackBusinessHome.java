package com.zapporoo.nighthawk.callbacks;

import com.zapporoo.nighthawk.adapters.AdapterBusinessHomeReviews;
import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessReviews;
import com.zapporoo.nighthawk.adapters.ReviewViewHolder;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;

/**
 * Created by dare on 15.1.16..
 */
public interface ICallbackBusinessHome {
    void onLoadBusinessHeader(AdapterBusinessHomeReviews.HeaderViewHolder pHeaderViewHolder, ModUser pBusinessData);

    void onLoadBusinessReview(ReviewViewHolder pReviewViewHolder, ModRating pReviewData);

    void onWriteReview(ModUser pBusinessData);
}
