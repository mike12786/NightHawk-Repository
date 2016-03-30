package com.zapporoo.nighthawk.adapters;

import android.view.View;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;

/**
 * Created by Pile on 1/28/2016.
 */
public class ReviewViewHolder extends ViewHolderReviewBase {
    public final TextView tvBusinessReviewTitle, tvBusinessReviewBody;

    public ReviewViewHolder(View itemView) {
        super(itemView);

        tvBusinessReviewBody = (TextView) itemView.findViewById(R.id.tvBusinessReviewBody);
        tvBusinessReviewTitle = (TextView) itemView.findViewById(R.id.tvBusinessReviewTitle);

    }
}
