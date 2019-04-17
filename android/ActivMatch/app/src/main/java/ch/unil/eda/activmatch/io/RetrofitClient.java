package ch.unil.eda.activmatch.io;

import ch.unil.eda.activmatch.BuildConfig;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClient {
    private static Retrofit serverInstance;

    static ActivMatchServices getRetrofitInstance() {
        if (serverInstance == null) {
            serverInstance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.ServerUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return serverInstance.create(ActivMatchServices.class);
    }
}
