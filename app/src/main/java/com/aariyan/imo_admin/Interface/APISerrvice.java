package com.aariyan.imo_admin.Interface;


import com.aariyan.imo_admin.Notification.MyResponse;
import com.aariyan.imo_admin.Notification.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APISerrvice {


    @Headers(
            {
                    "Content_Type:application/json",
                    "Authorization:key=AAAAY9qARRI:APA91bGmee8O7dJ4H8gE1bYfoklA2YnbG1XVD66wlQ_fYM0pCXtLm0NHSCTnYRuuhF1rSMx3uUzzNLx-i_tNmOsf7_7AiDyBaXfs2w_uk54h3vPvlmLnM5nyU01AXc-5_1jgPuDFMchi"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
