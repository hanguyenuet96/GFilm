package vnu.uet.hanguyen.gfilm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService{



    @Override
    public void onTokenRefresh() {
        String recent_token = FirebaseInstanceId.getInstance().getToken();
        registerToken(recent_token);
    }

    public void registerToken(String recent_token) {
        OkHttpClient httpClient = new OkHttpClient();
        RequestBody body = new FormBody.Builder().add("Token",recent_token).build();
        Request request = new Request.Builder().url("https://onfilm.000webhostapp.com/OnFilm/notification/register.php").post(body).build();
        try {
            httpClient.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
