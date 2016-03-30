package com.zapporoo.nighthawk.callbacks;

import com.zapporoo.nighthawk.adapters.AdapterPersonalBusinessCheckedIn;
import com.zapporoo.nighthawk.model.ModCheckIn;

/**
 * Created by dare on 15.1.16..
 */
public interface ICallbackBusinessCheckedIn {
    void onLoadBusinessCheckedInItem(AdapterPersonalBusinessCheckedIn.ViewHolderCheckedIn pViewHolder, ModCheckIn pProfileData);
    void onBusinessCheckedInItemClick(ModCheckIn pProfileData);
}
