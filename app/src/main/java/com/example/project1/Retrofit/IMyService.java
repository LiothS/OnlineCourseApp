package com.example.project1.Retrofit;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
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


}
