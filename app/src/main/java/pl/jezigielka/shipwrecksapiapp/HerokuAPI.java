package pl.jezigielka.shipwrecksapiapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;




public interface HerokuAPI {

        String BASE_URL = "https://peaceful-garden-71472.herokuapp.com/";


        @GET("shipwrecks")
        Call<QuestionsList<Question>> getQuestions();


//        @GET("shipwrecks")
//        Call<QuestionsList<Question>> getQuestions(@Query("feature_type") String type);


}


