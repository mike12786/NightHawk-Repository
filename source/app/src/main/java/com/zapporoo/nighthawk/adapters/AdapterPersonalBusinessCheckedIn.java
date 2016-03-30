package com.zapporoo.nighthawk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackBusinessCheckedIn;
import com.zapporoo.nighthawk.model.ModCheckIn;
import com.zapporoo.nighthawk.model.ModUser;
import com.zapporoo.nighthawk.ui.views.CircularImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 16.1.16..
 */
public class AdapterPersonalBusinessCheckedIn extends RecyclerView.Adapter<AdapterPersonalBusinessCheckedIn.ViewHolderCheckedIn>{
    private final List<ModCheckIn> mItems;
    private ICallbackBusinessCheckedIn mCheckedInCallback;

    public AdapterPersonalBusinessCheckedIn(List<ModCheckIn> pCheckedInItems, ICallbackBusinessCheckedIn pCheckedInCallback) {
        if(pCheckedInItems == null)
            pCheckedInItems = new ArrayList<>();

        this.mItems = pCheckedInItems;
        this.mCheckedInCallback = pCheckedInCallback;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderCheckedIn vHolder, int arg1) {
        final ModCheckIn currentItem = mItems.get(arg1);

        if(mCheckedInCallback != null) {
            mCheckedInCallback.onLoadBusinessCheckedInItem(vHolder, currentItem);
            vHolder.circProfileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCheckedInCallback.onBusinessCheckedInItemClick(currentItem);
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolderCheckedIn onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        return new ViewHolderCheckedIn(inflater.inflate(R.layout.view_business_checked_in_item, arg0, false));
    }

    public void updateItems(List<ModCheckIn> pCheckedInUsers) {
        if(pCheckedInUsers == null)
            pCheckedInUsers = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pCheckedInUsers);

        notifyDataSetChanged();
    }

    public void addSingleItem(ModCheckIn checkIn) {
        if (!mItems.contains(checkIn))
            mItems.add(checkIn);
        notifyDataSetChanged();
    }

    public void removeSingleItem(ModCheckIn checkIn) {
        if (mItems.contains(checkIn))
            mItems.remove(checkIn);
        notifyDataSetChanged();
    }

    public boolean contains(ModUser mBusinessData) {
        for (ModCheckIn checkIn:mItems)
            if (mBusinessData.isEqual(checkIn.getFrom()))
                return true;

        return false;
    }

    public static class ViewHolderCheckedIn extends RecyclerView.ViewHolder {
        public final CircularImageView circProfileImage;
        public String lastId = "";


        public ViewHolderCheckedIn(View itemView) {
            super(itemView);

            circProfileImage = (CircularImageView) itemView.findViewById(R.id.circProfileImage);
        }
    }
}
