package unfairtools.com.pongonline2;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by brianroberts on 10/12/16.
 */

public interface ApiService {
    @POST("login")
    public Call<InfoObject> postLogin(@Header("username") String username, @Header("password") String password);

    @POST("invites")
    public Call<InfoObject> postInvites(@Header("username") String username);
}
