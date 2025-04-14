package  webtech.com.courierdriver.communication


import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import webtech.com.courierdriver.communication.response.*
import webtech.com.courierdriver.constants.ApiConstant


/*
Created by ​
Hannure Abdulrahim
WebTech Co.
Swissbell Plaza Kuwait Hotel,
9th Floor, Office No.- 915, Kuwait City, KUWAIT

on 1/28/2018.n
 */

interface ApiPostService : APIGetService {

    /*
     * On top of the method is the @POST annotation, which indicates that we want to execute a POST request when this method is called.
     * The argument value for the @POST annotation is the endpoint—which is /posts. So the full URL will be http://jsonplaceholder.typicode.com/posts.
      *
     *
     here FULL URL will be
     https://makan.place/makanwebserives_test/mkapi/login_update.php

    * */

    /*//@POST("/posts")
    @POST("login_update.php")
    @FormUrlEncoded
    fun savePost(@Field("api_key") api_key: String,
                 @Field("username") username: String,
                 @Field("password") password: String): Call<Post>*/

    //@POST("/posts")
    @POST(ApiConstant.LOGIN)
    @FormUrlEncoded
    fun postLogin(@Field("email_id") userName: String,
                  @Field("password") password: String,
                  @Field("fcm_key") fcmToken: String,
                  @Field("app_version") versionName: String): Call<LoginResp>



    //@POST("/posts")
    @POST(ApiConstant.LOGOUT)
    @FormUrlEncoded
    fun postLogout(@Field("email_id") userName: String): Call<LogoutResp>


    //@POST("/posts")
    @POST(ApiConstant.UPDATE_LOCATION)
    @FormUrlEncoded
    fun postDriverLocation(@Field("email_id") userName: String,
                           @Field("Loct_latt") latitude: String,
                           @Field("Loct_long") longitude: String): Call<LocationUpdateResp>


//    //@POST("/posts")
//    @POST(ApiConstant.GO_ONLINE)
//    @FormUrlEncoded
//    fun postDriverOnlineStatus(@Field("email_id") userName: String): Call<DriverOnlineStatusResp>
//
//    //@POST("/posts")
//    @POST(ApiConstant.GO_OFFLINE)
//    @FormUrlEncoded
//    fun postDriverOfflineStatus(@Field("email_id") userName: String): Call<DriverOfflineStatusResp>


    //@POST("/posts")
    @POST(ApiConstant.CHANGE_DRIVER_ONLINE_STATUS)
    @FormUrlEncoded
    fun postDriverOnlineStatusNew(@Field("email_id") userName: String,
                                  @Field("live_status") driverOnlineStatus: String): Call<DriverOnlineStatusResp>


    /*
    *
    *   email_id:abdul@gmail.com
        order_id:OID-3212-4TOV
        c_id:2
    *
    * */
    //@POST("/posts")
    @POST(ApiConstant.ACCEPT_ORDER)
    @FormUrlEncoded
    fun postAcceptOrdersRequest(@Field("email_id") userName: String,
                                @Field("order_id") orderId: String,
                                @Field("c_id") companyId: String): Call<AcceptedOrderResponse>


    /*
    *
    *
email_id:raju@gmail.com
order_id:OID-1120-OPWG
c_id:15
order_status_int:6

//////////////#########
0 PENDING
1 DRIVER ACCEPTED
2 DRIVER_AT_SOURCE
3 ORDER_COLLECTED
4 DRIVER_AT_DESTINATION
5 DELIVERED
6 COMPLETED
7 CANCEL
8 REFUNDED


###########
old status
    public final static String PENDING = "Pending";
    public final static String CANCELLED = "Cancelled";
    public final static String ORDER_SUBMITTED = "Order Submited";
    public final static String BEING_PREPARED = "Being prepared";
    public final static String ORDER_BEING_COLLECTED = "Order being collected";
    public final static String OUT_FOR_DELIVERY = "Out for delivery";
    public final static String ORDER_DELIVERD = "Order deliverd";




//////////////#########
    *
    * */

    //@POST("/posts")
    @POST(ApiConstant.CHANGE_ORDER_STATUS)
    @FormUrlEncoded
    fun postUpdateOrderStatus(@Field("email_id") userName: String,
                              @Field("order_id") orderId: String,
                              @Field("c_id") companyId: String,
                              @Field("order_status_int") orderStatus: String): Call<UpdateOderStatusResp>

    //This will cancel order and update status in backend
//    @POST(ApiConstant.CHANGE_ORDER_CANCEL)
//    @FormUrlEncoded
//    fun postCancelOrder(@Field("email_id") userName: String,
//                        @Field("order_id") orderId: String,
//                        @Field("order_status_int") orderStatus: String): Call<OrderCancelledResp>

    //@POST("/posts")
    @POST(ApiConstant.CANCEL_ORDER)
    @FormUrlEncoded
    fun postCancelOrder(@Field("email_id") userName: String): Call<CancelOrderResponse>





    //@POST("/posts")
    @POST(ApiConstant.INVOICE_ORDER)
    @FormUrlEncoded
    fun postInvoiceOrder(@Field("email_id") userName: String): Call<InvoiceOrderResp>


    /// this api is for switching  order


    @POST(ApiConstant.SWITCHING_ORDER)
    @FormUrlEncoded
    fun postSwitchingOrder(@Field("email_id") userName: String,
                           @Field("name") name: String,
                           @Field("phone") phone: String,
                        @Field("order_id") orderId: String,
                        @Field("loct_latt") loctLat: String,
                        @Field("loct_long") loctLong: String,
                        @Field("vehicle_type") vehicleType: String,
                           @Field("vehicle_no") vehicle_no: String,
                           @Field("cid") cid: String

                        ): Call<OrderSwitchingResp>




//    email_id:rocky@ogo.delivery
//    order_id:OID-2201-OCZL

    @POST(ApiConstant.SWITCHED_ORDER_ASSIGNED)
    @FormUrlEncoded
    fun postSwitchedOrderAssigned(@Field("email_id") userName: String,
                        @Field("order_id") orderId: String

    ): Call<OrderSwitchedAndAssignedResp>




    ///individual Order History

    //@POST("/posts")
    /////PaymentType => ALL or  COD or KNET or CREDIT_CARD
    @POST(ApiConstant.ORDER_HISTORY)
    @FormUrlEncoded
    fun postOrdersHistory(@Field("email_id") userName: String,
                          @Field("FromDate") fromDate: String,
                          @Field("ToDate") toDate: String,
                          @Field("PaymentType") PaymentType: String): Call<OrderHistoryResponse>


    ///individual Order History

    //@POST("/posts")
    /////PaymentType => ALL or  COD or KNET or CREDIT_CARD
    @POST(ApiConstant.BULK_ORDER_HISTORY)
    @FormUrlEncoded
    fun postBulkOrdersHistory(@Field("email_id") userName: String,
                          @Field("FromDate") fromDate: String,
                          @Field("ToDate") toDate: String): Call<BulkOrderHistoryResponse>


    ///driver current assigned Order
    //@POST("/posts")
    @POST(ApiConstant.CURRENT_ASSIGN_ORDER)
    @FormUrlEncoded
    fun postReceiveCurrentOrder(@Field("email_id") userName: String): Call<ReceiveCurrentOrderResponse>

    ///driver current assigned Order
    //@POST("/posts")
    @POST(ApiConstant.STUCK_ORDER)
    @FormUrlEncoded
    fun postStuckOrder(@Field("email_id") userName: String): Call<StuckOrderResp>


    ///check here order status
    //@POST("/posts")
    @POST(ApiConstant.CHECK_ORDER_STATUS)
    @FormUrlEncoded
    fun postCheckOrderStatus(@Field("order_id") orderID: String): Call<CheckOrderStatusResp>







    ///update order rating
    /*
    *
    *
        order_id:OID-2111-VMLR
        email_id:john@gmail.com
        type_of_user:DRIVER
        rating:5
        review_message:NA

    * */

    //@POST("/posts")
    @POST(ApiConstant.DRIVER_FEEDBACK)
    @FormUrlEncoded
    fun postUpdateOrderRating(@Field("order_id") orderId: String,
                              @Field("email_id") emailId: String,
                              @Field("type_of_user") typeOfUser: String,
                              @Field("rating") rating: Int,
                              @Field("review_message") reviewMessage: String): Call<UpdateOrderRatingResp>


}