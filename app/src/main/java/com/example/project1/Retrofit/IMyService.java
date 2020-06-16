package com.example.project1.Retrofit;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface IMyService {
    @POST("register")
    @FormUrlEncoded
    Observable<Response<String>> registerUser(@Field("name") String name,
                                             @Field("email") String email,
                                             @Field("password") String password,
                                             @Field("phone") String phone,
                                             @Field("address") String address,
                                             @Field("description") String descript,
                                             @Field("gender") String gender);
    @POST("active-account")
    @FormUrlEncoded
    Observable<String>  activeAccUser(@Field("email") String email,
                                    @Field("activeToken") String actToken);
    @POST("login")
    @FormUrlEncoded
    Observable<Response<String>>  loginUser(@Field("email") String email,
                                      @Field("password") String actToken);
    @PUT("change-password")
    @FormUrlEncoded
    Observable<Response<String>>  changePass(@Field("oldpassword") String oldPass,
                                             @Field("newpassword") String newPass,
                                             @Header("auth-token") String authToken);
    @PUT("change-profile")
    @FormUrlEncoded
    Observable<Response<String>>  changeProfile(@Field("name") String oldPass,
                                                @Field("phone") String phone,
                                                @Field("address") String address,
                                                @Field("description") String description,
                                                @Field("gender") String gender,
                                                @Header("auth-token") String authToken);
    @Multipart
    @PUT("change-avatar")
    Observable<Response<String>>  changeAva(@Part MultipartBody.Part file,

                                            @Header("auth-token") String authToken);
    @POST("forgot-password")
    @FormUrlEncoded
    Observable<String>  forgotPass(@Field("email") String email);
    @POST("reset-password")
    @FormUrlEncoded
    Observable<String>  resetPass(@Field("email") String email,
                                  @Field("token") String token,
                                  @Field("password") String pass);
    @GET("category/get-all-category")
    Observable<String>  getAllCategory();
    @Multipart
    @POST("course/create")

    Observable<String>  createCourse(@Part("name") String name,
                                     @Part("goal") String goal,
                                     @Part("description") String mota,
                                     @Part("category") String category,
                                     @Part("price") String price,
                                     @Part("discount") String discount,
                                     @Part MultipartBody.Part file,
                                     @Header("auth-token") String authToken
                                     );
    @GET("logout")
    Observable<String>  userLogout(@Header("auth-token") String authToken);
    @GET("course/get-all")
    Observable<String>  getAllCourse();
    @GET("course/get-free")
    Observable<String>  getFreeCourse();
    @GET("course/get-top")
    Observable<String>  getTopCourse();
    @GET
    Observable<String>  getCourseByCategory(@Url String urlGet);
    @GET
    Observable<String>  getCourseByIDUser(@Url String urlGet);
    @Multipart
    @PUT
    Observable<String>  courseUpdate(@Url String urlPut,@Part MultipartBody.Part file,
                                               @Part("name") String name,
                                               @Part("goal") String goal,
                                               @Part("description") String mota,
                                               @Part("category") String category,
                                               @Part("price") String price,
                                               @Part("discount") String discount,

                                            @Header("auth-token") String authToken);
    @Multipart
    @PUT
    Observable<String>  courseUpdate1(@Url String urlPut,
                                     @Part("name") String name,
                                     @Part("goal") String goal,
                                     @Part("description") String mota,
                                     @Part("category") String category,
                                     @Part("price") String price,
                                     @Part("discount") String discount,

                                     @Header("auth-token") String authToken);

    @DELETE
    Observable<String>  deleteCourse(@Url String urlGet, @Header("auth-token") String authToken);
    @POST("join/create-join")
    @FormUrlEncoded
    Observable<Response<String>>  joinCourse( @Field("idUser") String name,
                                    @Field("idCourse") String goal


    );
    @GET
    Observable<String>  getJoinedCourse(@Url String urlGet);
    @Multipart
    @POST("lesson/create-lesson")
    Observable<String> createLesson(
            @Part("title") String name,
            @Part("order") String order,
            @Part("multipleChoices") String quiz,
            @Part("idCourse") String id,
            @Part  MultipartBody.Part video,
            @Part List<MultipartBody.Part> files,
            @Header("auth-token") String authToken);
    @GET
    Observable<String>  getLesson(@Url String urlGet);
    @PUT
    @FormUrlEncoded
    Observable<String>  updateLessonInfo(@Url String urlGet,
                                         @Field("idCourse") String idcourse,
                                         @Field("order") String order,
                                         @Field("title") String title,
                                         @Header("auth-token") String authToken);
    @DELETE
    Observable<String>  deleteFile(@Url String urlGet, @Header("auth-token") String authToken);

    @Multipart
    @PUT
    Observable<String>  addLessonFile(@Url String urlGet,@Part MultipartBody.Part file,

                                            @Header("auth-token") String authToken);
    @PUT
    Observable<String> updateMultipleChoice (@Url String urlGet,
                                             @Body RequestBody body,

                                             @Header("auth-token") String authToken);
    @POST("lesson/upload-image-multiple-choice")
    @Multipart
    Observable<String>  popUpImage(@Part MultipartBody.Part file,
                                   @Header("auth-token") String authToken);
    @POST("/comment/add-comment")
    @Multipart
    Observable<String>  addComment(
                                   @Part  MultipartBody.Part image,
                                   @Part("idParent") String idParent,
                                   @Part("idCourse") String idCourse,
                                   @Part("content") String content,
                                   @Part("idUser") String idUser,
                                   @Part("idLesson") String idLesson
                                  );
    @GET
    Observable<String>  getListComment(@Url String urlGet);




}
