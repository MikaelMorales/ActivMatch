package ch.unil.eda.activmatch.io;

import ch.unil.eda.activmatch.BuildConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class RetrofitClient {
    private static Retrofit serverInstance;

    static ActivMatchServices getRetrofitInstance() {
        if (serverInstance == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
            serverInstance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.ServerUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return serverInstance.create(ActivMatchServices.class);
    }
}
