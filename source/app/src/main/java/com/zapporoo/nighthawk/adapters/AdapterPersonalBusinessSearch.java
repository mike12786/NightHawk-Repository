package com.zapporoo.nighthawk.adapters;

import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackPersonalBusinessItem;
import com.zapporoo.nighthawk.callbacks.ICallbackSearchableAdapter;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.util.NhLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 4.12.15..
 */
public class AdapterPersonalBusinessSearch extends RecyclerView.Adapter<AdapterPersonalBusinessSearch.ViewHolderHotDeal> implements ICallbackSearchableAdapter<SearchResponse> {
    private final List<ModUser> mItems;
    private final ICallbackPersonalBusinessItem mItemCallback;
    private boolean mAllowChecking;

//    private final List<ModUser> mShowingItems;

    private final List<ModUser> mSearchResults;
    private int mCurrentSearchOffset;
    private static final int MAX_SEARCH_RESULTS = 60;

    public AdapterPersonalBusinessSearch(List<ModUser> pHotDeals, ICallbackPersonalBusinessItem pItemCallback, boolean pAllowChecking) {
        if(pHotDeals == null)
            pHotDeals = new ArrayList<>();

        if(pItemCallback == null) {
            throw new IllegalArgumentException("You must provide callback for business items.");
        }

        this.mItems = pHotDeals;
        this.mItemCallback = pItemCallback;
        this.mAllowChecking = pAllowChecking;
//        this.mShowingItems = new ArrayList<>(mItems);
        this.mSearchResults = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(final ViewHolderHotDeal vHolder, int arg1) {
        vHolder.setAllowChecking(mAllowChecking);

        final ModUser currentItem = mItems.get(arg1);

        vHolder.imgBtnBusinessRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mItemCallback != null) {
                    mItemCallback.onFavoriteBusiness(currentItem);
                }
            }
        });

        mItemCallback.onLoadBusiness(vHolder, currentItem);
        vHolder.cbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCallback != null){
                    mItemCallback.onCheckBoxClick(currentItem);
                }
            }
        });

        vHolder.wrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemCallback != null)
                    mItemCallback.onBusinessOpen(currentItem);
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
        return new ViewHolderHotDeal(inflater.inflate(R.layout.view_personal_business_search_item, arg0, false), mAllowChecking);
    }

    public void updateItems(List<ModUser> pHotDeals) {
        if(pHotDeals == null)
            pHotDeals = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pHotDeals);

        notifyDataSetChanged();
    }

    public List<ModUser> getAllItems(){
        return mItems;
    }

    public void addItems(List<ModUser> pHotDeals) {
        if(pHotDeals == null)
            pHotDeals = new ArrayList<>();

        mItems.addAll(pHotDeals);
        notifyItemRangeChanged(mItems.size() - pHotDeals.size() - 1, pHotDeals.size());
    }

    public void setAllowChecking(boolean pAllowChecking) {
        this.mAllowChecking = pAllowChecking;
        notifyDataSetChanged();
    }

    public boolean isAllowChecking(){
        return this.mAllowChecking;
    }

    @Override
    public int getCurrentSearchOffset() {
        return mCurrentSearchOffset;
    }

    @Override
    public int getMaxSearchResults() {
        return MAX_SEARCH_RESULTS;
    }

    @Override
    public void onSearchResults(final SearchResponse pSearchResults) {
        // TODO use search results
        int addedCount = 0;
        List<ModUser> addList = new ArrayList<>();

        for (Business b : pSearchResults.businesses()) {
            NhLog.e("Business result:", "" + b.id() + " #  " + b.name());

            addList.add(ModUser.createFromYelpBusiness(b));
            addedCount++;

/*            if (b.deals() != null)
                for (Deal deal : b.deals()) {
                    NhLog.e("---deal:", deal.title());
                }*/
        }

        ModUser.addMultiBusiness(addList);
        mItems.addAll(addList);
        mCurrentSearchOffset += addedCount;

        final int finalAddedCount = addedCount;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mItemCallback.onSearchResults(pSearchResults);
                notifyItemRangeChanged(mItems.size() - finalAddedCount - 1, finalAddedCount);
            }
        });

    }

    @Override
    public int getCurrentSearchId() {
        return mItemCallback.getCurrentSearchId();
    }

    public void clearItems() {
        mItems.clear();
        mCurrentSearchOffset = 0;
        notifyDataSetChanged();
    }

    public static class ViewHolderHotDeal extends RecyclerView.ViewHolder {
        public final TextView tvBusinessName, tvTypeOfEstablishment, tvAddress, tvBody;
        public final ImageView imgBusinessParse, imgBusinessYelp;
        public final ImageView imgHotDealPlaceholderParse, imgHotDealPlaceholderYelp;
        public final ImageButton imgBtnBusinessRate;
        public final CheckBox cbCheck;
        public final RatingBar rbBusinessRating;
        public final View wrap, rlImgBusinessYelp, rlImgBusinessParse;

        public ViewHolderHotDeal(View itemView, boolean pAllowChecking) {
            super(itemView);

            rlImgBusinessYelp = itemView.findViewById(R.id.rlImgBusinessYelp);
            rlImgBusinessParse = itemView.findViewById(R.id.rlImgBusinessParse);

            tvBusinessName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvTypeOfEstablishment = (TextView) itemView.findViewById(R.id.tvTypeOfEstablishment);
            tvAddress = (TextView) itemView.findViewById(R.id.tvBusinessAddress);
            tvBody = (TextView) itemView.findViewById(R.id.tvHotDealBody);
            cbCheck = (CheckBox) itemView.findViewById(R.id.cbBusinessCheck);
            imgBusinessParse = (ImageView) itemView.findViewById(R.id.imgBusinessParse);
            imgBusinessYelp = (ImageView) itemView.findViewById(R.id.imgBusinessYelp);
            imgHotDealPlaceholderParse = (ImageView) itemView.findViewById(R.id.imgBusinessPlaceholderParse);
            imgHotDealPlaceholderYelp = (ImageView) itemView.findViewById(R.id.imgBusinessPlaceholderYelp);
            rbBusinessRating = (RatingBar) itemView.findViewById(R.id.rbBusinessRating);
            imgBtnBusinessRate = (ImageButton) itemView.findViewById(R.id.imgBtnBusinessRate);
            wrap = itemView.findViewById(R.id.llBusinessDetailsWrap);
        }

        public void setAllowChecking(boolean pAllowChecking) {
            if(pAllowChecking) {
                cbCheck.setVisibility(View.VISIBLE);
            }else
            {
                cbCheck.setVisibility(View.GONE);
            }
        }
    }
}
