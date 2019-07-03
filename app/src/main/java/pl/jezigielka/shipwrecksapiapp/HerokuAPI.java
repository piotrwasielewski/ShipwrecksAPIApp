package pl.jezigielka.shipwrecksapiapp;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;




public interface HerokuAPI {

        String BASE_URL = "https://peaceful-garden-71472.herokuapp.com/";

        @Headers("Content-Type: application/json")
//        @GET("shipwrecks")
//        Call<List<Question>> getQuestions();


        @GET("shipwrecks")
        Call<List<Question>> getQuestions(@Query("feature_type") String type);


}


