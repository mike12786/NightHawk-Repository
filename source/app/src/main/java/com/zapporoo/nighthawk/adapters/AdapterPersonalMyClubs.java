package com.zapporoo.nighthawk.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import com.zapporoo.nighthawk.R;
import com.zapporoo.nighthawk.callbacks.ICallbackMyClubItem;
import com.zapporoo.nighthawk.model.ModMyClub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dare on 4.12.15..
 */
public class AdapterPersonalMyClubs extends RecyclerView.Adapter<AdapterPersonalMyClubs.ViewHolderMyClubs>{
    private final List<ModMyClub> mItems;
    private boolean mAllowChecking;
    private ICallbackMyClubItem mClubItemCallback;

    public AdapterPersonalMyClubs(List<ModMyClub> pMyClubs, boolean pAllowChecking, ICallbackMyClubItem pClubItemCallback) {
        if(pMyClubs == null)
            pMyClubs = new ArrayList<>();

        this.mItems = pMyClubs;
        this.mAllowChecking = pAllowChecking;
        this.mClubItemCallback = pClubItemCallback;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onBindViewHolder(ViewHolderMyClubs vHolder, int arg1) {
        vHolder.setAllowChecking(mAllowChecking);

        final ModMyClub currentItem = mItems.get(arg1);

        if (currentItem.getTo() == null){
            vHolder.tvRating.setText(currentItem.getRatingYelp());
            vHolder.tvName.setText(currentItem.getNameYelp());
        }else{
            vHolder.tvRating.setText(currentItem.getTo().getRatingString());
            vHolder.tvName.setText(currentItem.getTo().getBusinessName());
        }

        vHolder.cbSelected.setChecked(currentItem.isSelected());
                vHolder.cbSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                currentItem.setSelected(isChecked);
                mClubItemCallback.onItemSelectionChanged(currentItem, isChecked);
            }
        });

        if(mClubItemCallback != null) {
            vHolder.imgBtnRate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mClubItemCallback.onItemFavorite(currentItem);
                }
            });
        }
        // TODO fill other fields as well as CheckBox.
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolderMyClubs onCreateViewHolder(ViewGroup arg0, int arg1) {
        LayoutInflater inflater = LayoutInflater.from(arg0.getContext());
        return new ViewHolderMyClubs(inflater.inflate(R.layout.view_personal_my_club, arg0, false));
    }

    public void updateItems(List<ModMyClub> pMyClubs) {
        if(pMyClubs == null)
            pMyClubs = new ArrayList<>();

        mItems.clear();
        mItems.addAll(pMyClubs);

        notifyDataSetChanged();
    }

    public void setAllowChecking(boolean pAllowChecking) {
        this.mAllowChecking = pAllowChecking;
        notifyDataSetChanged();
    }

    public static class ViewHolderMyClubs extends RecyclerView.ViewHolder {
        public final TextView tvName, tvRating;
        public final CheckBox cbSelected;
        public final ImageButton imgBtnRate;


        public ViewHolderMyClubs(View itemView) {
            super(itemView);

            tvName = (TextView) itemView.findViewById(R.id.tvProfileName);
            tvRating = (TextView) itemView.findViewById(R.id.tvMessageText);
            cbSelected = (CheckBox) itemView.findViewById(R.id.cbClubSelect);
            imgBtnRate = (ImageButton) itemView.findViewById(R.id.imgBtnRate);
        }

        public void setAllowChecking(boolean pAllowChecking) {
            if(pAllowChecking) {
                cbSelected.setVisibility(View.VISIBLE);
            }else
            {
                cbSelected.setVisibility(View.GONE);
            }
        }
    }
}
