package com.zapporoo.nighthawk.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.karumi.headerrecyclerview.HeaderRecyclerViewAdapter;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackBusinessDetails;
import com.zapporoo.nighthawk.model.ModRating;
import com.zapporoo.nighthawk.model.ModUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 15.1.16..
 */
public class AdapterPersonalBusinessReviews extends HeaderRecyclerViewAdapter<ViewHolderReviewBase, ModUser, ModRating, ModRating> {
    private final ICallbackBusinessDetails mItemCallback;
    private List<ModRating> items;

    public AdapterPersonalBusinessReviews(List<ModRating> pReviewItems, ModUser pBusinessData, ICallbackBusinessDetails pItemCallback) {
        if(pReviewItems == null)
            pReviewItems = new ArrayList<>();

        if(pItemCallback == null) {
            throw new IllegalArgumentException("You must provide callback for business details.");
        }

        this.mItemCallback = pItemCallback;

        this.items = pReviewItems;
        setItems(this.items);
        setHeader(pBusinessData);
    }

    public void updateList(List<ModRating> pReviewItems){
        this.items = pReviewItems;
        setItems(pReviewItems);
        notifyDataSetChanged();
    }

    public void addSingle(ModRating rating){
        if (this.items == null)
            this.items = new ArrayList<>();

        items.add(rating);
        setItems(items);

        notifyDataSetChanged();
    }

    @Override
    protected ViewHolderReviewBase onCreateHeaderViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View headerView = inflater.inflate(R.layout.view_personal_business_details_header, parent, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    protected ViewHolderReviewBase onCreateItemViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View headerView = inflater.inflate(R.layout.view_personal_business_review_item, parent, false);
        return new ReviewViewHolder(headerView);
    }

    @Override
    protected ViewHolderReviewBase onCreateFooterViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    protected void onBindHeaderViewHolder(ViewHolderReviewBase holder, int position) {
        final ModUser businessData = getHeader();

        if(mItemCallback != null) {
            mItemCallback.onLoadBusinessHeader((HeaderViewHolder) holder, businessData);
        }

        View.OnClickListener itemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnBusinessDetailsWriteReview: {
                        if(mItemCallback != null) {
                            mItemCallback.onWriteReview(businessData);
                        }
                        break;
                    }
                    case R.id.tvBusinessDetailsWebsite: {
                        if(mItemCallback != null) {
                            mItemCallback.onOpenBusinessWebsite(businessData);
                        }
                        break;
                    }
                    case R.id.tvBusinessDetailsAddress: {
                        if(mItemCallback != null) {
                            mItemCallback.onNavigationShowAddress(businessData);
                        }
                        break;
                    }
                }
            }
        };

        HeaderViewHolder vHolder = (HeaderViewHolder) holder;
        vHolder.setWriteReviewListener(itemClickListener);
        vHolder.tvBusinessWebsite.setOnClickListener(itemClickListener);
        vHolder.tvBusinessAddress.setOnClickListener(itemClickListener);
    }

    @Override
    protected void onBindItemViewHolder(ViewHolderReviewBase holder, int position) {
        ModRating review = getItem(position);

        if(mItemCallback != null) {
            mItemCallback.onLoadBusinessReview((ReviewViewHolder) holder, review);
        }
    }

    @Override
    protected void onBindFooterViewHolder(ViewHolderReviewBase holder, int position) {

    }



    public class HeaderViewHolder extends ViewHolderReviewBase {
        public final TextView tvBusinessName, tvBusinessRate, tvHotDealsTitle, tvHotDealsBody;
        public final ImageView imgHotDeal;

        public final ImageView imgBusiness;
        public final TextView tvBusinessAddress;
        public final TextView tvBusinessPhone;
        public final TextView tvBusinessWebsite;
        public final Button btnWriteReview;
        public final TextView tvBusinessDetailsShortDescription;

        public final View llLocationWrap;
        public final View llContactWrap;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            tvBusinessName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvBusinessRate = (TextView) itemView.findViewById(R.id.tvMessageText);
            tvHotDealsTitle = (TextView) itemView.findViewById(R.id.tvBusinessHotDealsTitle);
            tvHotDealsBody = (TextView) itemView.findViewById(R.id.tvHotDealsBody);
            imgHotDeal = (ImageView) itemView.findViewById(R.id.imgHotDeal);
            tvBusinessDetailsShortDescription = (TextView) itemView.findViewById(R.id.tvBusinessDetailsShortDescription);

            tvBusinessAddress = (TextView) itemView.findViewById(R.id.tvBusinessDetailsAddress);
            tvBusinessPhone = (TextView) itemView.findViewById(R.id.tvBusinessDetailsPhone);
            tvBusinessWebsite = (TextView) itemView.findViewById(R.id.tvBusinessDetailsWebsite);
            imgBusiness = (ImageView) itemView.findViewById(R.id.imgBusinessParse);
            btnWriteReview = (Button) itemView.findViewById(R.id.btnBusinessDetailsWriteReview);

            llLocationWrap = itemView.findViewById(R.id.llLocationWrap);
            llContactWrap = itemView.findViewById(R.id.llLocationContactWrap);
        }

        public void setWriteReviewListener(View.OnClickListener writeReviewListener) {
            btnWriteReview.setOnClickListener(writeReviewListener);
        }
    }


}
