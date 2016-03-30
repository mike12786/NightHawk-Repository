package com.zapporoo.nighthawk.quickblox.push;

/**
 * Created by Pile on 3/6/2016.
 */
public interface IRegGCM {
    void onGcmRegSuccess();
    void onGcmRegFailed(String error);
}
