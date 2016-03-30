package com.zapporoo.nighthawk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackPersonalBusinessItem;
import com.zapporoo.nighthawk.model.ModUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 4.12.15..
 */
public class AdapterPersonalBusiness extends RecyclerView.Adapter<AdapterPersonalBusiness.ViewHolderHotDeal> {
    private final List<ModUser> mItems;
    private final ICallbackPersonalBusinessItem mItemCallback;

    public AdapterPersonalBusiness(List<ModUser> pBusinessList, ICallbackPersonalBusinessItem pItemCallback) {
        if(pBusinessList == null)
            pBusinessList = new ArrayList<>();

        if(pItemCallback == null) {
            throw new IllegalArgumentException("You must provide callback for business items.");
        }

        this.mItems = pBusinessList;
        this.mItemCallback = pItemCallback;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderHotDeal vHolder, int arg1) {
        final ModUser currentItem = mItems.get(arg1);

        mItemCallback.onLoadBusiness(vHolder, currentItem);
        vHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCallback != null) {
                    mItemCallback.onBusinessOpen(currentItem);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolderHotDeal onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        return new ViewHolderHotDeal(inflater.inflate(R.layout.view_personal_business_item, arg0, false));
    }

    public void updateItems(List<ModUser> pHotDeals) {
        if(pHotDeals == null)
            pHotDeals = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pHotDeals);

        notifyDataSetChanged();
    }

    public static class ViewHolderHotDeal extends RecyclerView.ViewHolder {
        public final TextView tvBusinessName, tvBusinessRate, tvBusinessTitle, tvHotDealsBody, tvBusinessDetailsSectionHint;
        public final ImageView imgHotDeal;
        public final ViewGroup clickableGroup;

        public ViewHolderHotDeal(View itemView) {
            super(itemView);

            clickableGroup = (ViewGroup) itemView.findViewById(R.id.rlDetails);

            tvBusinessName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvBusinessRate = (TextView) itemView.findViewById(R.id.tvMessageText);
            tvBusinessTitle = (TextView) itemView.findViewById(R.id.tvBusinessHotDealsTitle);
            tvHotDealsBody = (TextView) itemView.findViewById(R.id.tvHotDealsBody);
            tvBusinessDetailsSectionHint = (TextView) itemView.findViewById(R.id.tvBusinessDetailsShortDescription);
            tvBusinessDetailsSectionHint.setVisibility(View.GONE);
            imgHotDeal = (ImageView) itemView.findViewById(R.id.imgHotDeal);
        }

        public void setOnClickListener(View.OnClickListener pClickListener) {
            clickableGroup.setOnClickListener(pClickListener);
        }
    }
}
