package ch.unil.eda.activmatch.io;

import java.util.List;

import ch.unil.eda.activmatch.models.Group;
import ch.unil.eda.activmatch.models.Message;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ActivMatchServices {

    @GET("message/{id}")
    Call<List<Message>> getMessages(@Path("id") String groupId);

    @Headers({"Content-Type: application/json"})
    @POST("message")
    Call<Message> sendMessage(@Body Message message);

    @Headers({"Content-Type: application/json"})
    @POST("group")
    Call<Group> createGroup(@Body Group group);

    @GET("group/matches")
    Call<List<String>> getMatchingTopics(@Query("query") String query);
}