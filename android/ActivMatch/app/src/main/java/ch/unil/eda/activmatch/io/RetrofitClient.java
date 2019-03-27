package ch.unil.eda.activmatch.io;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit serverInstance;

    public static Retrofit getRetrofitInstance() {
        if (serverInstance == null) {
            serverInstance = new Retrofit.Builder()
                    .baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return serverInstance;
    }
}
