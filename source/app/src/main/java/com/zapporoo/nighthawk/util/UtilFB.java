package com.zapporoo.nighthawk.util;

import android.content.Context;
import android.os.Bundle;

import java.net.URISyntaxException;

//import com.facebook.AccessToken;
//import com.facebook.GraphRequest;
//import com.facebook.GraphResponse;
//import com.facebook.HttpMethod;
//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;
/**
 * Created by Pile on 2/23/2016.
 */
public class UtilFB {


//    private void scFbGetProfileImage(final Context context, String id) {
//        Bundle parameters = new Bundle();
//        parameters.putString("type", "large");//small, normal, album, large, square
//        //parameters.putString("redirect", "0");
//        GraphRequest request = new GraphRequest(
//                AccessToken.getCurrentAccessToken(),
//                "/" + id +"/picture",
//                parameters,
//                HttpMethod.GET,
//                new GraphRequest.Callback() {
//                    public void onCompleted(GraphResponse response) {
//                         /* handle the result */
//                        if (response != null){
//                            try {
//                                String imageUrl = response.getConnection().getURL().toURI().toString();
//                                Picasso.with(context).load(imageUrl)
//                                        //.placeholder(R.drawable.user_placeholder)
//                                        //.error(R.drawable.user_placeholder_error)
//                                        .into(ivProfileImage, new Callback() {
//                                            @Override
//                                            public void onSuccess() {
//                                                isImageSelected = true;
//                                            }
//
//                                            @Override
//                                            public void onError() {
//
//                                            }
//                                        });
//                            } catch (URISyntaxException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }
//        );
//        request.executeAsync();
//    }
}
