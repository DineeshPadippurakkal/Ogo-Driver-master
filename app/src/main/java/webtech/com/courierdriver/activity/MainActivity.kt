package webtech.com.courierdriver.activity


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import br.com.joinersa.oooalertdialog.Animation
import br.com.joinersa.oooalertdialog.OnClickListener
import br.com.joinersa.oooalertdialog.OoOAlertDialog
import com.agrawalsuneet.loaderspack.loaders.CircularSticksLoader
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.crowdfire.cfalertdialog.CFAlertDialog
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import webtech.com.courierdriver.ApiCallUtils.ApiCall
import webtech.com.courierdriver.BuildConfig
import webtech.com.courierdriver.R
import webtech.com.courierdriver.communication.ApiPostService
import webtech.com.courierdriver.communication.ApiPostUtils
import webtech.com.courierdriver.communication.NotificationResponseData.OrderCancelledFromRemoteData
import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchFromRemoteData
import webtech.com.courierdriver.communication.NotificationResponseData.OrderSwitchedAndAssignedFromRemoteData
import webtech.com.courierdriver.communication.request.LocationSender
import webtech.com.courierdriver.communication.response.*
import webtech.com.courierdriver.constants.OGoConstant
import webtech.com.courierdriver.databinding.ActivityMainBinding
import webtech.com.courierdriver.events.*
import webtech.com.courierdriver.firebase.*
import webtech.com.courierdriver.firebase.UpdateRealTimeDriverOnlineStatus
import webtech.com.courierdriver.fragments.*
import webtech.com.courierdriver.service.*
import webtech.com.courierdriver.utilities.*
import java.io.IOException
import java.util.*


class MainActivity : MasterAppCombatActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        //// TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return false

    }


    var preferenceHelper: PreferenceHelper? = null

    private var driverId: String? = null

    var drawerToggle: ActionBarDrawerToggle? = null

    private var apiPostService: ApiPostService? = null

    internal var currentOrder: ReceiveCurrentOrderResponseData? = null

    private var locationServiceIntent: Intent? = null
    private var locationForegroundServicePACIIntent: Intent? = null
    private var nearByOrderQueueServiceIntent: Intent? = null

    private var ordersQueueServiceIntent: Intent? = null
    private var syncingIntentServiceIntent: Intent? = null


    private var language: Int = 0

    var lat: Double? = null
    var lng: Double? = null

    var client: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    var result: PendingResult<LocationSettingsResult>? = null
    val LOCATION = 0x1//LOCATION Setting
    val GPS_SETTINGS = 0x7//GPS Setting
    val RESULT_CODE = 101//GPS Setting


    private val INTERVAL = 1000 * 5L
    private val DELAY = 5000L


    internal var orderSwitchedAndAssignedFromRemoteData: OrderSwitchedAndAssignedFromRemoteData? =
        null
    internal var orderCancelledFromRemoteData: OrderCancelledFromRemoteData? = null
    internal var orderSwitchingFromRemoteData: OrderSwitchFromRemoteData? = null
    internal var isLogoutFromServer: String? = null
    internal var orderStuck: String? = null

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // fixing portrait mode problem for SDK 26 if using windowIsTranslucent = true
        /*if (Build.VERSION.SDK_INT == 26) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        */
        setSupportActionBar(binding.lytappbar.toolbar)


        //var bundle =intent.extras
        //driverName= bundle.getString("DRIVER_NAME")
        //tvDriverName.text = driverName

        preferenceHelper = PreferenceHelper(this@MainActivity)
        language = preferenceHelper!!.language



        if (language === Language.ENGLISH) {
            // setLangLocal("en");
            //Log.e("Test","Language in Eng =>"+language);
            LogUtils.error(LogUtils.TAG, "Language in Eng =>" + language)

            binding.lytslidingdrawer.tvEng.setBackgroundResource(R.drawable.lan_round_bg)
            binding.lytslidingdrawer.tvEng.setTextColor(Color.parseColor("#FFFFFF")) // white
            binding.lytslidingdrawer.tvAr.setBackgroundResource(R.drawable.rounded_rectangle)
            binding.lytslidingdrawer.tvAr.setTextColor(Color.parseColor("#000000")) // black

        } else if (language === Language.ARABIC) {
            //setLangLocal("ar");
            //Log.e("Test","Language in Ar =>"+language);
            LogUtils.error(LogUtils.TAG, "Language in Ar =>" + language)
            binding.lytslidingdrawer.tvEng.setBackgroundResource(R.drawable.rounded_rectangle)
            binding.lytslidingdrawer.tvEng.setTextColor(Color.parseColor("#000000")) // black
            binding.lytslidingdrawer.tvAr.setBackgroundResource(R.drawable.lan_round_bg)
            binding.lytslidingdrawer.tvAr.setTextColor(Color.parseColor("#FFFFFF")) // white
        } else {
            //setLangLocal("en");
            preferenceHelper!!.language = Language.ENGLISH
            EventBus.getDefault().post(LanguageSelectedEvent(Language.ENGLISH))
            // Log.e("Test Lang","Language in Eng (default) =>"+language);
            LogUtils.error(LogUtils.TAG, "Language in Eng (default) =>" + language)

            binding.lytslidingdrawer.tvEng.setBackgroundResource(R.drawable.lan_round_bg)
            binding.lytslidingdrawer.tvEng.setTextColor(Color.parseColor("#FFFFFF")) // white
            binding.lytslidingdrawer.tvAr.setBackgroundResource(R.drawable.rounded_rectangle)
            binding.lytslidingdrawer.tvAr.setTextColor(Color.parseColor("#000000")) // black
        }


        binding.lytslidingdrawer.tvEngLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                language = preferenceHelper!!.language

                if (language === Language.ARABIC) {
                    preferenceHelper!!.language = Language.ENGLISH

                    EventBus.getDefault().post(LanguageSelectedEvent(Language.ENGLISH))

                    language = preferenceHelper!!.language
                    // Log.e("langauage","is : =>"+language);
                    LogUtils.debug(LogUtils.TAG, "langauage is : =>" + language)
                    val i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName())
                    if (i != null)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    finish()
                    startActivity(i)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.language_note),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        })

        binding.lytslidingdrawer.tvArLayout.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {

                if (language === Language.ENGLISH) {

                    preferenceHelper!!.language = Language.ARABIC
                    EventBus.getDefault().post(LanguageSelectedEvent(Language.ARABIC))

                    language = preferenceHelper!!.language
                    // Log.e("langauage","is : =>"+language);
                    LogUtils.debug(LogUtils.TAG, "langauage is : =>" + language)
                    val i = getBaseContext().getPackageManager()
                        .getLaunchIntentForPackage(getBaseContext().getPackageName())
                    if (i != null)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    finish()
                    startActivity(i)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.language_note),
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        })


        binding.lytslidingdrawer.tvAppVersion.setText(getString(R.string.nav_header_app_version) + "" + BuildConfig.VERSION_NAME)


        /////set driver image here

        preferenceHelper?.let { preferenceHelper ->
            preferenceHelper.loggedInUser?.let { loggedInUser ->
                loggedInUser.driverImage?.let {
                    LogUtils.error(
                        LogUtils.TAG,
                        "DRIVER IMAGE URL =>" + preferenceHelper.loggedInUser!!.driverImage
                    )

                    //var driverURL="http://".plus(preferenceHelper!!.loggedInUser!!.driverImage)
                    // LogUtils.error(LogUtils.TAG, "DRIVER IMAGE driverURL =>" + driverURL)

                    Glide.with(binding.lytslidingdrawer.lytheader.driverImageView.context)
                        .load(preferenceHelper.loggedInUser!!.driverImage)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)///don't cache
                        //.placeholder(R.drawable.ic_profile)
                        .error(R.drawable.ic_image_preview)///if url exist but images not available on server
                        .into(binding.lytslidingdrawer.lytheader.driverImageView)

                }

            }

        }


        ////////////////S   Testing crash report
        //Custom crash logs :
        // FirebaseCrash.log("Activity created");
        //Generate Crash Report :
        // FirebaseCrash.logcat(Log.ERROR, LogUtils.TAG, "NPE caught");
        ////Testing crash
        ///If you’re using our Crashlytics.getInstance().crash() method for testing purposes, make sure it’s not in the onCreate method of your launch Activity
        // throw RuntimeException("This is a crash")
        //FirebaseCrash.report( Exception("My first Firebase non-fatal error on Android"))

        //Fabric.with(this,  Crashlytics());
        ///to your Application class, for test crash report use:
        //Crashlytics.getInstance().crash()
        //or report non-fatals use:
        //Crashlytics.log("Your log")
        //Crashlytics.logException(Throwable("This your not-fatal name"))
        //////////////E


        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.lytappbar.toolbar as Toolbar?,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout!!.addDrawerListener(drawerToggle!!)


        drawerToggle!!.syncState()

        //nav_view .setNavigationItemSelectedListener(this)


        //// Current Order fragment load here
        binding.lytslidingdrawer.navigationDrawerOrderLayout.setOnClickListener {
            if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
                (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                loadYourOrdersTab()


            } else {
                NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)

            }


        }

        /// Scan Order
        binding.lytslidingdrawer.navigationDrawerScanYourOrderLayout.setOnClickListener {

            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE

            // Handle the order history
            // Toast.makeText(this@MainActivity, "Scan Orders!", Toast.LENGTH_LONG).show();
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            shouldDisplayHomeUp(false)///don't show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.scanned_orders)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, ScannedOrderFragment.newInstance()).commit()

        }


        ////Individual  Order History
        binding.lytslidingdrawer.navigationDrawerOrderIndividualOrderLayout.setOnClickListener {

            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE

            // Handle the order history
            //Toast.makeText(this@MainActivity, "Individual  order history!", Toast.LENGTH_LONG).show();
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            shouldDisplayHomeUp(false)///don't show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.individual_orders)
            //    supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, IndividualOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commit()
            //supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, IndividualOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_container,
                IndividualOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)
            ).commit()

        }

        //// Bulk Order History
        binding.lytslidingdrawer.navigationDrawerOrderBulkReportLayout.setOnClickListener {

            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE

            // Handle the order history
            Toast.makeText(this@MainActivity, "Bulk order history!", Toast.LENGTH_LONG).show()
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            shouldDisplayHomeUp(false)///don't show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.bulk_orders)
            //    supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, BulkOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commit()
            //supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, BulkOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commitAllowingStateLoss()
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_container,
                BulkOrderHistoryFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)
            ).commit()

        }


//        navigation_drawer_order_invoice_layout.setOnClickListener {
//
//             binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility = View.GONE
//
//            // Handle the order history
//            Toast.makeText(this@MainActivity, "Invoice!", Toast.LENGTH_LONG).show();
//            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
//
//            shouldDisplayHomeUp(false)///don't show back icon in action bar
//            if (supportActionBar != null)
//                supportActionBar!!.setTitle(R.string.invoice)
//            //    supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, InvoiceFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commit()
//            //supportFragmentManager!!.beginTransaction().replace(R.id.frame_container, InvoiceFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).addToBackStack("My Orders").commitAllowingStateLoss()
//            supportFragmentManager.beginTransaction().replace(R.id.frame_container, InvoiceFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)).commit()
//
//        }


        //////////////////////////////////////
        ///below will handle if fragment in back stack is less than 1 then it will display drawer else will display back icon
        ///// in fragment also it will handle back icon
        /*  supportFragmentManager.addOnBackStackChangedListener(object : FragmentManager.OnBackStackChangedListener {
              override fun onBackStackChanged() {
                  if (supportFragmentManager.backStackEntryCount > 1) {
                      drawerToggle!!.setDrawerIndicatorEnabled(false)
                      supportActionBar!!.setDisplayHomeAsUpEnabled(true)// show back button
                      toolbar.setNavigationOnClickListener { onBackPressed() }
                  } else {
                      //show hamburger
                      drawerToggle!!.setDrawerIndicatorEnabled(true)
                      supportActionBar!!.setDisplayHomeAsUpEnabled(false)
                      drawerToggle!!.syncState()
                      toolbar.setNavigationOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
                  }
              }
          })*/
        //////////////////////////////////////


        binding.lytslidingdrawer.navigationDrawerContactUsLayout.setOnClickListener {

            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
            shouldDisplayHomeUp(false)///don't show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.contact_us_title)
            supportFragmentManager.beginTransaction()
                .replace(R.id.frame_container, ContactUsFragment.newInstance()).commit()


        }

        binding.lytslidingdrawer.navigationDrawerLogoutLayout.setOnClickListener {

            var titleMsg: String
            var message: String
            var positiveButtonMsg: String
            var negativeButtonMsg: String

            titleMsg = getString(R.string.logout_title)
            message = getString(R.string.logout_messege)
            positiveButtonMsg = getString(R.string.logout_no)
            negativeButtonMsg = getString(R.string.logout_yes)



            OoOAlertDialog.Builder(this@MainActivity)
                .setTitle(titleMsg)
                .setTitleColor(R.color.color_them)
                .setMessage(message)
                .setMessageColor(R.color.black)
                .setAnimation(Animation.POP)
                .setPositiveButton(positiveButtonMsg, null)
                .setCancelable(false)
                .setNegativeButton(negativeButtonMsg, OnClickListener() {


                    if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                        //Call here LOGOUT web service go to login screen
                        driverOnlineOfflineStatusPost(
                            preferenceHelper!!.lastUsername!!,
                            OGoConstant.LOGOUT,
                            true,
                            false
                        )


                    } else {
                        NetworkUtil.getInstance(this@MainActivity)
                            .showCustomNetworkError(this@MainActivity)

                    }


                }).build()


        }


        val loginUserInfo = preferenceHelper!!.loggedInUser

        if (loginUserInfo == null) run {


            val i = Intent(this@MainActivity, LoginActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()


        } else {

            driverId = preferenceHelper!!.loggedInUser!!.id
            binding.lytslidingdrawer.lytheader.tvDriverMob.text =
                preferenceHelper!!.loggedInUser!!.phone
            binding.lytslidingdrawer.lytheader.tvDriverName.text =
                preferenceHelper!!.loggedInUser!!.name
            binding.lytslidingdrawer.lytheader.tvDriverEmail.text =
                preferenceHelper!!.loggedInUser!!.emailId
            //tvDriverType.text = preferenceHelper!!.loggedInUser!!.vehicleType

            if (preferenceHelper!!.loggedInUser!!.vehicleType!!.equals("VAN", true)) {
                Glide.with(this@MainActivity).load(getImage("ic_van"))
                    .into(binding.lytslidingdrawer.lytheader.imageViewDriverTyp)

            } else if (preferenceHelper!!.loggedInUser!!.vehicleType!!.equals("BIKE", true)) {

                Glide.with(this@MainActivity).load(getImage("ic_bike"))
                    .into(binding.lytslidingdrawer.lytheader.imageViewDriverTyp)


            } else if (preferenceHelper!!.loggedInUser!!.vehicleType!!.equals("CAR", true)) {
                Glide.with(this@MainActivity).load(getImage("ic_car"))
                    .into(binding.lytslidingdrawer.lytheader.imageViewDriverTyp)

            }

            ///save vehicle type here in share preference
            preferenceHelper!!.vehicleType = preferenceHelper!!.loggedInUser!!.vehicleType


            shouldDisplayHomeUp(false)
            setDrawerState(true)




            orderCancelledFromRemoteData =
                this.intent.getSerializableExtra(MyFirebaseMessagingService.CANCEL_ORDER) as? OrderCancelledFromRemoteData
            orderSwitchingFromRemoteData =
                this.intent.getSerializableExtra(MyFirebaseMessagingService.SWITCH_ORDER) as? OrderSwitchFromRemoteData
            orderSwitchedAndAssignedFromRemoteData =
                this.intent.getSerializableExtra(MyFirebaseMessagingService.SWITCHED_ORDER_ASSIGNING) as? OrderSwitchedAndAssignedFromRemoteData
            isLogoutFromServer =
                this.intent.getStringExtra(MyFirebaseMessagingService.LOGOUT_FROM_SERVER)
            orderStuck = this.intent.getStringExtra(MyFirebaseMessagingService.ORDER_STUCK)



            if (NetworkUtil.getInstance(this@MainActivity).isOnline) {

                if (isLogoutFromServer != null && isLogoutFromServer.equals(OGoConstant.LOGOUT_FROM_SERVER)) {
                    driverOnlineOfflineStatusPost(
                        preferenceHelper!!.lastUsername!!,
                        OGoConstant.LOGOUT,
                        false,
                        true
                    )


                } else if (orderStuck != null && orderStuck.equals(OGoConstant.ORDER_STUCK)) {
                    // call web service only if order is stuck
                    stuckOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!)

                } else {


                    ///////Here whether its accepted order or not just call web service to get current driver assigned order and proceed accordingly
                    receiveCurrentOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!)


                }


            } else {
                NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)


            }

            //NetworkStateChangeUtil.checkInternetConnectivity(this@MainActivity,javaClass.simpleName)
            startSyncingIntentService()


        }


        // show here driver shift time
//        binding.lytslidingdrawer.lytheader.driverImageView.setOnClickListener {
//
//
//
//            val shiftStartedAt= TimeUtils.getFormatedDateTime(preferenceHelper!!.loggedInUser!!.shiftFromTime!!, TimeUtils.readFormate, TimeUtils.writeFormat)
//            val shiftEndAt= TimeUtils.getFormatedDateTime(preferenceHelper!!.loggedInUser!!.shiftToTime!!, TimeUtils.readFormate, TimeUtils.writeFormat)
//
//            //val shiftStartedAt= TimeUtils.getFormatedDateTime("09:30:00", TimeUtils.readFormate, TimeUtils.writeFormat)
//            //val shiftEndAt= TimeUtils.getFormatedDateTime("23:00:00", TimeUtils.readFormate, TimeUtils.writeFormat)
//
//
//
//            val builder = CFAlertDialog.Builder(this@MainActivity)
//                    .setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
//                    .setTitle("Your shift time.")
//                    .setCancelable(false)
//                    .setMessage(""+ shiftStartedAt + " - " + shiftEndAt+ "" )
//
//                    .addButton("Ok got it.", -1, getResources().getColor(R.color.color_them), CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.END, { dialog, which->
//                        //Toast.makeText(this@MainActivity, "Ok.", Toast.LENGTH_SHORT).show()
//
//
//                        dialog.dismiss()
//
//                    })
//            builder.show()
//
//
//
//
//        }


        ////once    binding.lytslidingdrawer.toggleGoOnlineButton clicked , handle request here
        binding.lytslidingdrawer.toggleGoOnlineButton.setOnClickListener {

            // if (language == Language.ENGLISH)
            if (binding.lytslidingdrawer.toggleGoOnlineButton.isChecked()) {


                goOnline()
//                ApiCall.getInstance(this@MainActivity).loginPostToGetNewShiftTime(this@MainActivity,preferenceHelper!!.lastUsername!!,preferenceHelper!!.lastPassword!!,preferenceHelper!!
//                        .fcmToken!!,BuildConfig.VERSION_NAME)
//                Looper.myLooper()?.let {
//                    Handler(it).postDelayed({
//                        // YOUR CODE after duration finished
//                        shiftTimeOnline()
//
//                    }, 1 * 1000)
//                }


            } else {

                // goOffline()

                ApiCall.getInstance(this@MainActivity).loginPostToGetNewShiftTime(
                    this@MainActivity,
                    preferenceHelper!!.lastUsername!!,
                    preferenceHelper!!.lastPassword!!,
                    preferenceHelper!!.fcmToken!!,
                    BuildConfig.VERSION_NAME
                )
                Looper.myLooper()?.let {
                    Handler(it).postDelayed({
                        // YOUR CODE after duration finished
                        shiftTimeOffline()

                    }, OGoConstant.DELAY_TWO_SEC)
                }

            }


        }


        binding.lytappbar.lytcontent.bottomNavigation.topNavigationConstraint.setNavigationChangeListener { view, position ->
            //navigation changed, do something here
            // Toast.makeText(this@MainActivity, "position:"+position, Toast.LENGTH_LONG).show()

            when (position) {
                OGoConstant.YOUR_ORDER_TAB ->
                    loadYourOrdersTab()


                OGoConstant.NEARBY_ORDER_TAB ->
                    loadNearByOrdersTab()


            }


        }


    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            // R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onResume() {
        super.onResume()

        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        ////Keep live connect with real time fire base databse
        // keepRealTimeDatabaseAlive()

        /////registering here will call subscribe method  when respective event gets trigger or posted
        if (!EventBus.getDefault().isRegistered(this@MainActivity)) {

            EventBus.getDefault().register(this@MainActivity)
            LogUtils.error(LogUtils.TAG, "registering event bus for " + this@MainActivity)

        } else {

            LogUtils.error(LogUtils.TAG, "already registered event bus for " + this@MainActivity)

        }


        ////add status to fire base
        UpdateRealTimeDriverApplicationStatus.updateRealTimeDriverApplicationStatus(
            this@MainActivity,
            OGoConstant.ACTIVE
        )
        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit()
            .putBoolean("isActive", true).apply()

        // //Ask for location permission

        if (PermissionUtility.checkPermissionLocation(this@MainActivity)) {

            ///////////////////////////////////////S
            //Ask for location permission
            //askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, LOCATION)
            client = GoogleApiClient.Builder(this@MainActivity)
                .addApi(LocationServices.API)
                .build()


            ////Make sure GPS is ON
            ///Ask to turn on GPS after getting location permission
            askForGPS()
            ///////////////////////////////////////E


            // Toast.makeText(this@MainActivity, "Location permission granted", Toast.LENGTH_SHORT).show()
            runLocationService(true)
            runLocationServicePACI(true)


        }


        //// check Version from firebase config file and proceed accordingly
        CheckUpdateFromFireBaseConfig.checkAndShowUpdateAvailableAlert(this@MainActivity)


    }

    override fun onPause() {

        super.onPause()


        UpdateRealTimeDriverApplicationStatus.updateRealTimeDriverApplicationStatus(
            this@MainActivity,
            OGoConstant.BACKGROUND
        )
        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit()
            .putBoolean("isActive", false).apply()

        ///these below two line will disable landscape mode ,check onResume() also
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR


    }

    override fun onDestroy() {
        // Log.e("TAG", "ON DESTORY CALLED");
        LogUtils.error(LogUtils.TAG, "ON DESTORY CALLED")

        ///You're calling unregister() in onStop()/onPause , so you do not receive events when MainActivity is in the background.This way you will receive in background also
        if (EventBus.getDefault().isRegistered(this@MainActivity))
            EventBus.getDefault().unregister(this@MainActivity)

        ////Make sure online status is maintained before closing application


        LogUtils.debug(LogUtils.TAG, "preferenceHelper" + preferenceHelper!!.driverOnlineStatus)

        //////////////////////S
        //dont stop here even app is in background let service run
        //stopService(locationServiceIntent)
        //LogUtils.error(LogUtils.TAG, "Location Service stop in MainActivity->onDestroy()")
        //////////////////////E


        // Write logic here if application is killed forcefully and/or app exit and/or removed via task manager and/or via swiped out
        /// make driver offline
        //goOffline()

        ///Another way to save value in share preference
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit()
            .putBoolean("isActive", false).apply()

        super.onDestroy()


    }

    override fun onBackPressed() {

        if ((binding.drawerLayout)!!.isDrawerOpen(GravityCompat.START)) {
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }

        LogUtils.error(LogUtils.TAG, "BACK PRESSED IN MainActivity.")
    }


    /////here control will come when location permission granted
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            ////////////////////Case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            PermissionUtility.MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    runLocationService(true)
                    runLocationServicePACI(true)
                } else {


                    Toast.makeText(
                        this@MainActivity,
                        "Permission Denied (LOCATION), Please allow to proceed !",
                        Toast.LENGTH_LONG
                    ).show();
                }


            }


        }
    }


    fun shouldDisplayHomeUp(canback: Boolean) {
        //Enable Up button only  if there are entries in the back stack
        //boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
        setDrawerState(!canback)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(canback)

    }

    fun setDrawerState(isEnabled: Boolean) {
        if (isEnabled) {
            binding.drawerLayout!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            // chaned to STATE_IDLE from  LOCK_MODE_UNLOCKED
            drawerToggle!!.onDrawerStateChanged(DrawerLayout.STATE_IDLE)
            drawerToggle!!.setDrawerIndicatorEnabled(true)
            drawerToggle!!.syncState()

        } else {
            (binding.drawerLayout)!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            drawerToggle!!.onDrawerStateChanged(DrawerLayout.STATE_SETTLING)
            drawerToggle!!.setDrawerIndicatorEnabled(false)
            if (supportActionBar != null)
                supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerToggle!!.setToolbarNavigationClickListener(View.OnClickListener { onSupportNavigateUp() })
            drawerToggle!!.syncState()
        }
    }


    /*  *//*
  * web service call via retrofit ,
  * This method will driver ONLINE Status
  * *//*


    private fun driverOnlineStatusPost(userName: String, driverOnlineStatus: String) {
        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postDriverOnlineStatusNew(userName, driverOnlineStatus).enqueue(object : Callback<DriverOnlineStatusResp> {
            override fun onResponse(call: Call<DriverOnlineStatusResp>, response: Response<DriverOnlineStatusResp>) {

                // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showDriverOnlineStatusResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postDriverOnlineStatus response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        // showProgress(false)

                        //Toast.makeText(this@NavigationActivity, "Toggle button is ON", Toast.LENGTH_LONG).show();
                        ///snack bar online
                        displaySnackBar(getString(R.string.online), OGoConstant.SHORT)
                        ////Make sure GPS is ON
                        ///Ask to turn on GPS after getting location permission
                        askForGPS()
                        //set state to checked
                          binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(true);
                         binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.END)
                        ///close drawer
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                        navigation_drawer_go_online_detail.setText(getString(R.string.navigation_drawer_go_online_desc))



                    } else {


                        Toast.makeText(this@MainActivity, response.body()!!.message, Toast.LENGTH_LONG).show();

                    }


                }
            }

            override fun onFailure(call: Call<DriverOnlineStatusResp>, t: Throwable) {

                LogUtils.error("TAG", "Unable to submit postDriverOnlineStatus to API.")
                Toast.makeText(this@MainActivity, " Unable to submit postDriverOnlineStatus to API.!", Toast.LENGTH_LONG).show();

                // showProgress(false)


            }
        })


    }


    *//*
* web service call via retrofit ,
* This method will driver OFFLINE Status
* *//*


    private fun driverOfflineStatusPost(userName: String, driverOnlineStatus: String) {
        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postDriverOnlineStatusNew(userName, driverOnlineStatus).enqueue(object : Callback<DriverOnlineStatusResp> {
            override fun onResponse(call: Call<DriverOnlineStatusResp>, response: Response<DriverOnlineStatusResp>) {

                // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showDriverOnlineStatusResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(LogUtils.TAG, "postDriverOfflineStatus response.body()!!.message.toString() =>" + response.body()!!.message.toString())
                        // showProgress(false)

                        //Toast.makeText(this@NavigationActivity, "Toggle button is OFF", Toast.LENGTH_LONG).show();
                        ///snack bar offline
                        displaySnackBar(getString(R.string.offline), OGoConstant.INDEFINITE)
                        //set state to unchecked
                          binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(false);
                         binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.START)
                        ///close drawer
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)


                        navigation_drawer_go_online_detail.setText(getString(R.string.navigation_drawer_go_online_description))

                    } else {


                        Toast.makeText(this@MainActivity, response.body()!!.message, Toast.LENGTH_LONG).show();

                    }


                }
            }

            override fun onFailure(call: Call<DriverOnlineStatusResp>, t: Throwable) {

                LogUtils.error("TAG", "Unable to submit postDriverOfflineStatus to API.")
                Toast.makeText(this@MainActivity, " Unable to submit postDriverOfflineStatus to API.!", Toast.LENGTH_LONG).show();

                // showProgress(false)


            }
        })


    }

    fun showDriverOnlineStatusResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }*/

    /*
* web service call via retrofit ,
* This method to make driver online/offline/logout
* */


    private fun driverOnlineOfflineStatusPost(
        userName: String,
        driverOnlineStatus: String,
        isLogoutFromUser: Boolean,
        isLogoutFromServer: Boolean
    ) {
        LogUtils.error(
            LogUtils.TAG,
            "driverOnlineOfflineStatusPost driverOnlineStatus  =>" + driverOnlineStatus
        )

        if (isLogoutFromServer) {
            ///logout directly
            logoutPost(userName)

        } else if (isLogoutFromUser) {

            ///logout directly
            //logoutPost(userName)

            ////First call API and get new shift time
            ApiCall.getInstance(this@MainActivity).loginPostToGetNewShiftTime(
                this@MainActivity,
                preferenceHelper!!.lastUsername!!,
                preferenceHelper!!.lastPassword!!,
                preferenceHelper!!.fcmToken!!,
                BuildConfig.VERSION_NAME
            )

            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    // YOUR CODE after duration finished

                    //// don't allow user to logout till shift time arrived
                    shiftTimeLogin(userName)

                    //// This section is logout
                    // logoutPost(userName)


                }, OGoConstant.DELAY_TWO_SEC)
            }


        } else {

            //// This section is online / offline
            if (apiPostService == null)
                apiPostService = ApiPostUtils.apiPostService

            apiPostService!!.postDriverOnlineStatusNew(userName, driverOnlineStatus)
                .enqueue(object : Callback<DriverOnlineStatusResp> {

                    override fun onResponse(
                        call: Call<DriverOnlineStatusResp>,
                        response: Response<DriverOnlineStatusResp>
                    ) {

                        // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())

                        if (response.isSuccessful) {

                            // driverOnlineOfflineStatusResponse(response.body()!!.toString())

                            if (response.body()!!.status.toString().equals("true", true)) {

                                LogUtils.error(
                                    LogUtils.TAG,
                                    "driverOnlineOfflineStatusPost response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                                )
                                // showProgress(false)

                                if (driverOnlineStatus.equals(OGoConstant.ONLINE, true)) {
                                    makeOnline()
                                } else {
                                    makeOffline(false)
                                }


                            } else {

                                Toast.makeText(
                                    this@MainActivity,
                                    response.body()!!.message,
                                    Toast.LENGTH_LONG
                                ).show();

                            }


                        }
                    }

                    override fun onFailure(call: Call<DriverOnlineStatusResp>, t: Throwable) {

                        LogUtils.error(
                            "TAG",
                            "Unable to submit driverOnlineOfflineStatusPost to API."
                        )
                        Toast.makeText(
                            this@MainActivity,
                            " Unable to submit driverOnlineOfflineStatusPost to API.!",
                            Toast.LENGTH_LONG
                        ).show();

                        // showProgress(false)


                    }
                })


        }


    }

    fun driverOnlineOfflineStatusResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScanningOrderCLicked(event: ScanningOrderEvent) {

        if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
            //// Call here Scanning
            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE

            // Handle the order history
            // Toast.makeText(this@MainActivity, "Scan Orders!", Toast.LENGTH_LONG).show();
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            shouldDisplayHomeUp(true)/// show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.scanning_orders)
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_container,
                ScanningOrderFragment.newInstance(preferenceHelper!!.loggedInUser!!.emailId!!)
            ).commit()


        } else {
            NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)
        }

    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onScannedOrderAccept(scannedOrderEvent: ScannedOrderEvent) {

        if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
            //// Call here Scanning
            binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
                View.GONE

            // Handle the order history
            // Toast.makeText(this@MainActivity, "Scan Orders!", Toast.LENGTH_LONG).show();
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            shouldDisplayHomeUp(true)/// show back icon in action bar
            if (supportActionBar != null)
                supportActionBar!!.setTitle(R.string.scanned_orders)
            supportFragmentManager.beginTransaction().replace(
                R.id.frame_container,
                ScannedOrderFragment.newInstance(scannedOrderEvent.scannedOrder)
            ).commit()


        } else {
            NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)
        }

    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoSenderClicked(event: GoSenderEvent) {

        Toast.makeText(this, R.string.back_to_sender_title, Toast.LENGTH_SHORT).show()

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.back_to_sender_title)
        val orderEntityJson = Gson().toJson(event.getOrderEntity())

        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, OrderSenderMapFragment.newInstance(orderEntityJson))
            .addToBackStack("Sender Directions").commit()

    }

    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoBackToSenderClicked(event: GoBackToSenderEvent) {

        Toast.makeText(this, R.string.back_to_sender_title, Toast.LENGTH_SHORT).show()

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.back_to_sender_title)
        val orderEntityJson = Gson().toJson(event.getOrderEntity())

        supportFragmentManager.beginTransaction().replace(
            R.id.frame_container,
            OrderBackToSenderMapFragment.newInstance(orderEntityJson)
        ).addToBackStack("Back To Sender Directions ").commit()

    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoReceiverClicked(event: GoReceiverEvent) {

        Toast.makeText(this, R.string.receiver_title, Toast.LENGTH_SHORT).show()

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.receiver_title)
        val orderEntityJson = Gson().toJson(event.getOrderEntity())
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, OrderReceiverMapFragment.newInstance(orderEntityJson))
            .addToBackStack("Receiver Directions").commit()

    }


    // Order cancel from server / remote / backend


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderCancelledFromRemote(event: OrderCancelledFromRemoteEvent) {

        cancelOrder(event)


    }


    // Order Switching  from server / remote / backend


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderSwitchingFromRemote(event: OrderSwitchFromRemoteEvent) {

        switchingOrder(event)


    }


    // Order Switched from server / remote / backend and assigned to driver


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderSwitchedAndAssignedFromRemote(event: OrderSwitchedAndAssignedFromRemoteEvent) {

        switchedOrderAssigned(event)


    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGoRatingClicked(event: GoRatingEvent) {

        Toast.makeText(this, R.string.order_rating, Toast.LENGTH_SHORT).show()

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.order_rating)
        val orderEntityJson = Gson().toJson(event.getOrderEntity())
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, OrdersRatingFragment.newInstance(orderEntityJson))
            .addToBackStack("Order Rating").commit()

    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrdersFragmentRequested(event: ListOrdersEvent) {

        ///first remove all fragment from backs stack ( clear all fragments )
        removeAllFragments(supportFragmentManager)

        shouldDisplayHomeUp(false)///don't show back icon in action bar
//        if (supportActionBar != null)
//            supportActionBar!!.setTitle(R.string.orders)
//            supportFragmentManager.beginTransaction().replace(R.id.frame_container, OrdersFragment.newInstance()).commit()

        receiveCurrentOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!)

    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onApplicationUpdateRequested(event: ApplicationUpdateEvent) {


        if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
            //Call here LOGOUT web service go to login screen
            driverOnlineOfflineStatusPost(
                preferenceHelper!!.lastUsername!!,
                OGoConstant.LOGOUT,
                false,
                true
            )

        } else {
            NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)

        }


    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onLocationUpdated(event: LocationChangedEvent) {
        lat = event.getLat()
        lng = event.getLng()

        /////////////////////////////////////////////////////////
        ////put last LAT LONG in share preferences used during updating driver status
        /////////////////////////////////////////////////////////

        //preferenceHelper!!.lastLat = event.getLat().toString()
        //preferenceHelper!!.lastLong = event.getLng().toString()

        //Log.i("Test=> "," DriverLocation lat in MainActivity => " +  lat);
        //Log.i("Test=> "," DriverLocation lng in MainActivity => " +  lng);


    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onShowSenderDirection(event: ShowSenderDirectionEvent) {

        LogUtils.error(LogUtils.TAG, "onShowSenderDirection in MainActivity>>>>")
        SenderDirectionTask().executeOnExecutor(
            AsyncTask.THREAD_POOL_EXECUTOR,
            event.lat,
            event.lon
        )


    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderDetailsClicked(event: OrderDetailsEvent) {
        Toast.makeText(this, R.string.order_details, Toast.LENGTH_SHORT).show()
        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.order_details)
        val orderEntityJson = Gson().toJson(event.orderDetailsEntity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, OrderDetailsFragment.newInstance(orderEntityJson))
            .addToBackStack("Order Detail").commit()
    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOrderDetailHistoryClicked(event: OrderHistoryDetailEvent) {
        //Toast.makeText(this, R.string.order_history_details, Toast.LENGTH_SHORT).show()
        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.order_history_details)
        val orderEntityJson = Gson().toJson(event.orderHistoryEntity)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, OrderHistoryDetailFragment.newInstance(orderEntityJson))
            .addToBackStack("Order Detail").commit()
    }


    // only  Subscribe  is not enough here if your message event is post on UI or toast ( here in this case posting in MyFirebaseMessagingService)
    //@Subscribe
    // This below  method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onInternetStatusChange(event: InternetEvent) {

        if (event.internetStatus == OGoConstant.INTERNET_ON) {
            /// connected to internet
            Glide.with(this@MainActivity).load(getImage("ic_connected_to_internet"))
                .into(binding.lytslidingdrawer.lytheader.ivInternetStatusLogo)
            binding.lytslidingdrawer.lytheader.tvInternetStatusMsg.text =
                getString(R.string.connected_to_internet)

        } else {
            // no internet connection
            Glide.with(this@MainActivity).load(getImage("ic_not_connected_to_internet"))
                .into(binding.lytslidingdrawer.lytheader.ivInternetStatusLogo)
            binding.lytslidingdrawer.lytheader.tvInternetStatusMsg.text =
                getString(R.string.not_connected_to_internet)


        }
    }


    private fun startServicesWhenOnline() {
        runLocationService(true)
        runLocationServicePACI(true)
        startNearByOrdersQueueService()
        startOrdersQueueService()
    }


    private fun stopServicesWhenOffline() {
        /// first stop location service and then go to new Activity
        runLocationService(false)

        /// first stop location service and then go to new Activity
        runLocationServicePACI(false)


        //// stop Near by order Queue Service also
        if (nearByOrderQueueServiceIntent != null)
            stopService(nearByOrderQueueServiceIntent)

        //// stop  Service also
        if (ordersQueueServiceIntent != null)
            stopService(ordersQueueServiceIntent)


    }


    private fun stopAllServices() {

        /// first stop location service and then go to new Activity
        runLocationService(false)

        //// stop Near by order Queue Service also
        runLocationServicePACI(false)


        //// stop Near by order Queue Service also
        if (nearByOrderQueueServiceIntent != null)
            stopService(nearByOrderQueueServiceIntent)

        //// stop  Service also
        if (ordersQueueServiceIntent != null)
            stopService(ordersQueueServiceIntent)

        //// stop  Service also
        if (syncingIntentServiceIntent != null)
            stopService(syncingIntentServiceIntent)
    }


    //When all permissions available
    private fun runLocationService(serviceIntentFlag: Boolean) {

        val serviceStarted = LocationForegroundService.isLocationForegroundServiceStarted
        /// if service already started or running then only stop it else ignore
        LogUtils.error(LogUtils.TAG, "isLocationForegroundServiceStarted::" + serviceStarted)

        locationServiceIntent = Intent(this@MainActivity, LocationForegroundService::class.java)

        ///if locationServiceIntent is not null then only start service
        locationServiceIntent?.let {

            if (serviceIntentFlag) {
                ///this action will start service
                locationServiceIntent!!.setAction(LocationForegroundService.Constants.ACTION.STARTFOREGROUND_ACTION)

                startLocationActionService()


            } else {
                ///this action will stop service
                locationServiceIntent!!.setAction(LocationForegroundService.Constants.ACTION.STOPFOREGROUND_ACTION)

                if (serviceStarted)
                    startLocationActionService()
            }


        }


    }


    private fun runLocationServicePACI(serviceIntentFlag: Boolean) {

        val serviceStarted = LocationForegroundServicePACI.isLocationForegroundServicePACIStarted
        /// if service already started or running then only stop it else ignore
        LogUtils.error(LogUtils.TAG, "isLocationForegroundServicePACIStarted::" + serviceStarted)

        locationForegroundServicePACIIntent =
            Intent(this@MainActivity, LocationForegroundServicePACI::class.java)

        ///if locationForegroundServicePACIIntent is not null then only start service
        locationForegroundServicePACIIntent?.let {


            if (serviceIntentFlag) {

                ///this action will start service
                locationForegroundServicePACIIntent!!.setAction(LocationForegroundServicePACI.Constants.ACTION.STARTFOREGROUND_ACTION);
                startLocationActionServicePACI()


            } else {

                ///this action will stop service
                locationForegroundServicePACIIntent!!.setAction(LocationForegroundServicePACI.Constants.ACTION.STOPFOREGROUND_ACTION)
                if (serviceStarted)
                    startLocationActionServicePACI()

            }


        }


    }

    //// this method will be use to start and stop service based on action

    fun startLocationActionService() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //// this will start Background  Service
            startService(locationServiceIntent)

            LogUtils.error(
                LogUtils.TAG,
                "${LocationForegroundService.javaClass.canonicalName} in MainActivity -> startLocationActionService() for < Oreo (8.0.0) "
            )

        } else {
            startForegroundService(locationServiceIntent)
            LogUtils.error(
                LogUtils.TAG,
                "${LocationForegroundService.javaClass.canonicalName} in MainActivity -> startLocationActionService() for > Oreo (8.0.0) "
            )
        }

    }

    //// this method will be use to start and stop service based on action

    fun startLocationActionServicePACI() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //// this will start Background  Service
            startService(locationForegroundServicePACIIntent)

            LogUtils.error(
                LogUtils.TAG,
                "${LocationForegroundServicePACI.javaClass.canonicalName} in MainActivity -> startLocationActionServicePACI() for < Oreo (8.0.0) "
            )

        } else {
            startForegroundService(locationForegroundServicePACIIntent)
            LogUtils.error(
                LogUtils.TAG,
                "${LocationForegroundServicePACI.javaClass.canonicalName} in MainActivity -> startLocationActionServicePACI() for > Oreo (8.0.0) "
            )
        }

    }


    fun startNearByOrdersQueueService() {
        nearByOrderQueueServiceIntent =
            Intent(this@MainActivity, NearByOrdersQueueService::class.java)

        ///if nearByOrderQueueServiceIntent is not null then only start service
        nearByOrderQueueServiceIntent?.let {


            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                //// this will start Background  Service
                startService(nearByOrderQueueServiceIntent)
                LogUtils.error(
                    LogUtils.TAG,
                    " ${NearByOrdersQueueService.javaClass.simpleName} started in MainActivity -> startNearByOrdersQueueService() for < Oreo (8.0.0) "
                )

            } else {

                startForegroundService(nearByOrderQueueServiceIntent)
                LogUtils.error(
                    LogUtils.TAG,
                    " ${NearByOrdersQueueService.javaClass.simpleName} started in MainActivity -> startNearByOrdersQueueService() for > Oreo (8.0.0) "
                )


            }


        }


    }


    fun startOrdersQueueService() {


        ordersQueueServiceIntent = Intent(this@MainActivity, OrdersQueueService::class.java)
        ///if ordersQueueServiceIntent is not null then only start service
        ordersQueueServiceIntent?.let {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //// this will start Background  Service
                startService(ordersQueueServiceIntent)

                LogUtils.error(
                    LogUtils.TAG,
                    "${OrdersQueueService.javaClass.simpleName}  started in MainActivity -> startOrdersQueueService() for < Oreo (8.0.0) "
                )

            } else {
                startForegroundService(ordersQueueServiceIntent)
                LogUtils.error(
                    LogUtils.TAG,
                    " ${OrdersQueueService.javaClass.simpleName}  started in MainActivity -> startOrdersQueueService() for > Oreo (8.0.0) "
                )
            }
        }


    }


    fun startSyncingIntentService() {


        syncingIntentServiceIntent = Intent(this@MainActivity, SyncingIntentService::class.java)
        ///if syncingIntentServiceIntent is not null then only start service
        syncingIntentServiceIntent?.let {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                //// this will start Background  Service
                startService(syncingIntentServiceIntent)

                LogUtils.error(
                    LogUtils.TAG,
                    "${SyncingIntentService.javaClass.simpleName}  started in MainActivity -> startSyncingIntentService() for < Oreo (8.0.0) "
                )

            } else {
                startForegroundService(syncingIntentServiceIntent)
                LogUtils.error(
                    LogUtils.TAG,
                    " ${SyncingIntentService.javaClass.simpleName}  started in MainActivity -> startSyncingIntentService() for > Oreo (8.0.0) "
                )
            }
        }


    }


    fun cancelOrder(cancelOrderEvent: OrderCancelledFromRemoteEvent) {

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.app_name)
        val orderEntityJson = Gson().toJson(cancelOrderEvent.getOrderCancelledFromRemoteEntity())

        // LogUtils.error(LogUtils.TAG, "currentOrder!!.orderId =>"+currentOrder!!.orderId)
        LogUtils.error(
            LogUtils.TAG,
            "cancelOrderEvent.orderCancelledFromRemoteEntity.orderId.toString() =>" + cancelOrderEvent.orderCancelledFromRemoteEntity.orderId.toString()
        )

        ///if current order and order id from server is same then finish this order and change status respectively
        if (cancelOrderEvent.orderCancelledFromRemoteEntity.orderId.toString()
                .equals(currentOrder!!.orderId, true)
        ) {

            // vibrate the device
            val vibrator =
                this@MainActivity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            run {
//                val mPlayer = MediaPlayer.create(this@MainActivity, R.raw.order_cancelled)
//                mPlayer.start()

                val svc = Intent(this, BackgroundSoundService::class.java)
                startService(svc)

                LogUtils.error(
                    LogUtils.TAG,
                    "Status => mPlayer.start(); called : Order cancelled from remote !"
                )
            }


            OoOAlertDialog.Builder(this@MainActivity)
                .setTitle("Order Cancelled!")
                .setTitleColor(R.color.color_them)
                .setMessage(getString(R.string.order_cancelled))
                .setMessageColor(R.color.black)
                .setAnimation(Animation.POP)
                .setCancelable(false)
                .setNegativeButton("OK", OnClickListener() {

                    ///make drawer close here if its is already opened else ignore
                    if ((binding.drawerLayout)!!.isDrawerOpen(GravityCompat.START)) {
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                    }

                    /// stop background notification service
                    if (DriverUtilities.isMyServiceRunning(BackgroundSoundService::class.java, this)) {
                        val svc = Intent(this, BackgroundSoundService::class.java)
                        stopService(svc)
                    }

                    ////created new api
                    cancelOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!.toString())

                    //..old api
                    //cancelOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!,cancelOrderEvent.orderCancelledFromRemoteEntity.orderId!!,OGoConstant.CANCEL)
                    Toast.makeText(
                        this@MainActivity,
                        "Your order has been cancelled from remote.",
                        Toast.LENGTH_SHORT
                    ).show()


                    Looper.myLooper()?.let {
                        Handler(it).postDelayed({
                            // YOUR CODE after duration finished
                            EventBus.getDefault().post(ListOrdersEvent())
                        }, OGoConstant.DELAY)
                    }


                }).build()


        }


    }


    ///////Web Service call to update order status
//    fun cancelOrderPost(userNameStr: String, orderId: String,orderStatus: String) {
//        LogUtils.error(LogUtils.TAG, "cancelOrderPost userNameStr=>" + userNameStr)
//         LogUtils.error(LogUtils.TAG, "cancelOrderPost orderId=>" + orderId)
//        LogUtils.error(LogUtils.TAG, "cancelOrderPost orderStatus=>" + orderStatus)
//
//
//        apiPostService= ApiPostUtils.apiPostService
//        apiPostService!!.postCancelOrder(userNameStr,orderId,orderStatus).enqueue(object : Callback<OrderCancelledResp> {
//
//            override fun onResponse(call: Call<OrderCancelledResp>, response: Response<OrderCancelledResp>) {
//
//                LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())
//
//                if (response.isSuccessful) {
//
//                    showOrderCancelledResponse(response.body()!!.toString())
//
//                    if (response.body()!!.status.toString().equals("true", true)) {
//                        LogUtils.error(LogUtils.TAG, "postCancelOrder response.body()!!.message.toString() =>" + response.body()!!.message.toString())
//                        //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
//                        Toast.makeText(this@MainActivity, "order cancelled successfully! ", Toast.LENGTH_SHORT).show()
//
//    preferenceHelper!!.orderStatus = OGoConstant.CANCEL
//    UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(this@MainActivity, OGoConstant.CANCEL)
//
//
//
//                    } else {
//
//                        Toast.makeText(this@MainActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
//
//                    }
//
//
//                }
//            }
//
//            override fun onFailure(call: Call<OrderCancelledResp>, t: Throwable) {
//
//                if (t is IOException) {
//                    Toast.makeText(this@MainActivity, "this is an actual network failure :( inform the user and possibly retry", Toast.LENGTH_SHORT).show();
//                    // logging probably not necessary
//                }
//                else {
//                    Toast.makeText(this@MainActivity, "conversion issue! big problems :(", Toast.LENGTH_SHORT).show();
//                    // todo log to some central bug tracking service
//                    Toast.makeText(this@MainActivity, " Unable to submit postUpdateOrderStatus to API.!", Toast.LENGTH_LONG).show();
//                }
//
//                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API."+t.printStackTrace())
//                LogUtils.error("TAG", "Unable to submit postUpdateOrderStatus to API.")
//
//                // showProgress(false)
//
//            }
//        })
//
//
//    }
//
//    fun showOrderCancelledResponse(response: String) {
//        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
//    }


    ///////Web Service call to update order status and cancel order
    fun cancelOrderPost(userNameStr: String) {
        LogUtils.error(LogUtils.TAG, "cancelOrderPost userNameStr=>" + userNameStr)

        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postCancelOrder(userNameStr)
            .enqueue(object : Callback<CancelOrderResponse> {

                override fun onResponse(
                    call: Call<CancelOrderResponse>,
                    response: Response<CancelOrderResponse>
                ) {

                    LogUtils.error(
                        LogUtils.TAG,
                        "response.raw().toString() =>" + response.raw().toString()
                    )

                    if (response.isSuccessful) {

                        showCancelOrderResponseonse(response.body()!!.toString())

                        if (response.body()!!.status.toString().equals("true", true)) {
                            LogUtils.error(
                                LogUtils.TAG,
                                "postCancelOrder response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                            )
                            //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            Toast.makeText(
                                this@MainActivity,
                                "order cancelled successfully! ",
                                Toast.LENGTH_SHORT
                            ).show()

                            preferenceHelper!!.orderStatus = OGoConstant.CANCEL
                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                                this@MainActivity,
                                OGoConstant.CANCEL
                            )


                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }


                    }
                }

                override fun onFailure(call: Call<CancelOrderResponse>, t: Throwable) {

                    if (t is IOException) {
                        Toast.makeText(
                            this@MainActivity,
                            "this is an actual network failure :( inform the user and possibly retry",
                            Toast.LENGTH_SHORT
                        ).show();
                        // logging probably not necessary
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "conversion issue! big problems :(",
                            Toast.LENGTH_SHORT
                        ).show();
                        // todo log to some central bug tracking service
                        Toast.makeText(
                            this@MainActivity,
                            " Unable to submit cancelOrderPost to API.!",
                            Toast.LENGTH_LONG
                        ).show();
                    }

                    LogUtils.error(
                        "TAG",
                        "Unable to submit cancelOrderPost to API." + t.printStackTrace()
                    )
                    LogUtils.error("TAG", "Unable to submit cancelOrderPost to API.")

                    // showProgress(false)

                }
            })


    }

    fun showCancelOrderResponseonse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    fun switchingOrder(switchingOrderEvent: OrderSwitchFromRemoteEvent) {

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.switching_order)
        val orderEntityJson = Gson().toJson(switchingOrderEvent.orderSwitchFromRemoteEntity)

        // LogUtils.error(LogUtils.TAG, "currentOrder!!.orderId =>"+currentOrder!!.orderId)
        LogUtils.error(
            LogUtils.TAG,
            "switchingOrderEvent.orderSwitchFromRemoteEntity.orderId.toString() =>" + switchingOrderEvent.orderSwitchFromRemoteEntity!!.orderId.toString()
        )

        ///if current order and order id from server is same then finish this order and change status respectively
        if (switchingOrderEvent.orderSwitchFromRemoteEntity!!.orderId.toString()
                .equals(currentOrder!!.orderId, true)
        ) {

            // vibrate the device
            val vibrator =
                this@MainActivity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            run {
                val mPlayer = MediaPlayer.create(this@MainActivity, R.raw.order_cancelled)
                mPlayer.start()

                LogUtils.error(
                    LogUtils.TAG,
                    "Status => mPlayer.start(); called : Order cancelled from remote !"
                )
            }


            OoOAlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.switching_order))
                .setTitleColor(R.color.color_them)
                .setMessage(getString(R.string.switching_order_message))
                .setMessageColor(R.color.black)
                .setAnimation(Animation.POP)
                .setCancelable(false)
                .setNegativeButton("OK", OnClickListener() {

                    ///make drawer close here if its is already opened else ignore
                    if ((binding.drawerLayout)!!.isDrawerOpen(GravityCompat.START)) {
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                    }


                    //// web call api

                    switchingOrderPost(
                        preferenceHelper!!.loggedInUser!!,
                        currentOrder!!.orderId!!,
                        preferenceHelper!!.lastLat!!,
                        preferenceHelper!!.lastLong!!
                    )


                    Toast.makeText(
                        this@MainActivity,
                        R.string.switching_order_message,
                        Toast.LENGTH_SHORT
                    ).show()

                    Looper.myLooper()?.let {
                        Handler(it).postDelayed({
                            // YOUR CODE after duration finished
                            EventBus.getDefault().post(ListOrdersEvent())
                        }, OGoConstant.DELAY)
                    }


                }).build()


        }


    }


    ///////Web Service call to witch order
//    @Field("email_id") userName: String,
//    @Field("order_id") orderId: String,
//    @Field("loct_latt") loctLat: String,
//    @Field("loct_long") loctLong: String,
//    @Field("vehicle_type") vehicleType: String
    /// some more fields

    fun switchingOrderPost(
        loggedInUser: LoginRespData,
        orderId: String,
        lastLat: String?,
        lastLong: String?
    ) {
        LogUtils.error(LogUtils.TAG, "switchingOrderPost userNameStr=>" + loggedInUser.emailId)

        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postSwitchingOrder(
            loggedInUser!!.emailId!!,
            loggedInUser!!.name!!,
            loggedInUser!!.phone!!,
            orderId,
            lastLat!!,
            lastLong!!,
            loggedInUser!!.vehicleType!!,
            loggedInUser!!.vehicle_no!!,
            loggedInUser!!.cid!!
        ).enqueue(object : Callback<OrderSwitchingResp> {

            override fun onResponse(
                call: Call<OrderSwitchingResp>,
                response: Response<OrderSwitchingResp>
            ) {

                LogUtils.error(
                    LogUtils.TAG,
                    "response.raw().toString() =>" + response.raw().toString()
                )

                if (response.isSuccessful) {

                    showOrderSwitchingResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {
                        LogUtils.error(
                            LogUtils.TAG,
                            "switchingOrderPost response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                        )
                        //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                        Toast.makeText(
                            this@MainActivity,
                            "Order Switched Successfully! ",
                            Toast.LENGTH_SHORT
                        ).show()

                        preferenceHelper!!.orderStatus = OGoConstant.NO_ORDER
                        UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                            this@MainActivity,
                            OGoConstant.NO_ORDER
                        )


                    } else {

                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()

                    }


                }
            }

            override fun onFailure(call: Call<OrderSwitchingResp>, t: Throwable) {

                if (t is IOException) {
                    Toast.makeText(
                        this@MainActivity,
                        "this is an actual network failure :( inform the user and possibly retry",
                        Toast.LENGTH_SHORT
                    ).show();
                    // logging probably not necessary
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "conversion issue! big problems :(",
                        Toast.LENGTH_SHORT
                    ).show();
                    // todo log to some central bug tracking service
                    Toast.makeText(
                        this@MainActivity,
                        " Unable to submit switchingOrderPost to API.!",
                        Toast.LENGTH_LONG
                    ).show();
                }

                LogUtils.error(
                    "TAG",
                    "Unable to submit switchingOrderPost to API." + t.printStackTrace()
                )
                LogUtils.error("TAG", "Unable to submit switchingOrderPost to API.")

                // showProgress(false)

            }
        })


    }

    fun showOrderSwitchingResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    /// here switched order will be assiged

    fun switchedOrderAssigned(switchedOrderAssignedEvent: OrderSwitchedAndAssignedFromRemoteEvent) {

        shouldDisplayHomeUp(false)///don't show back icon in action bar
        if (supportActionBar != null)
            supportActionBar!!.setTitle(R.string.switched_order_assiged)
        val orderEntityJson =
            Gson().toJson(switchedOrderAssignedEvent.orderSwitchedAndAssignedFromRemoteEntity)

        // LogUtils.error(LogUtils.TAG, "currentOrder!!.orderId =>"+currentOrder!!.orderId)
        LogUtils.error(
            LogUtils.TAG,
            "switchedOrderAssignedEvent.orderSwitchedAndAssignedFromRemoteEntity.orderId.toString() =>" + switchedOrderAssignedEvent.orderSwitchedAndAssignedFromRemoteEntity!!.orderId.toString()
        )


        /// here order is not yet assigned its being assigned , order will be assigned after api call
        // if(true)

        ///if current order and order id from server is same then finish this order and change status respectively
        if (switchedOrderAssignedEvent.orderSwitchedAndAssignedFromRemoteEntity!!.orderId.toString()
                .equals(currentOrder!!.orderId, true)
        ) {

            // vibrate the device
            val vibrator =
                this@MainActivity!!.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(100)
            run {
                val mPlayer = MediaPlayer.create(this@MainActivity, R.raw.order_cancelled)
                mPlayer.start()

                LogUtils.error(
                    LogUtils.TAG,
                    "Status => mPlayer.start(); called : Order switched and assigned from remote !"
                )
            }


            OoOAlertDialog.Builder(this@MainActivity)
                .setTitle(getString(R.string.switched_order_assiged))
                .setTitleColor(R.color.color_them)
                .setMessage(getString(R.string.switched_order_assigned_message))
                .setMessageColor(R.color.black)
                .setAnimation(Animation.POP)
                .setCancelable(false)
                .setNegativeButton("OK", OnClickListener() {

                    ///make drawer close here if its is already opened else ignore
                    if ((binding.drawerLayout)!!.isDrawerOpen(GravityCompat.START)) {
                        (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)
                    }


                    //// web call api

                    switchedOrderAssignedPost(
                        preferenceHelper!!.loggedInUser!!.emailId!!,
                        currentOrder!!.orderId!!
                    )


                    Toast.makeText(
                        this@MainActivity,
                        R.string.switched_order_assiged,
                        Toast.LENGTH_SHORT
                    ).show()

                    Looper.myLooper()?.let {
                        Handler(it).postDelayed({
                            // YOUR CODE after duration finished
                            EventBus.getDefault().post(ListOrdersEvent())
                        }, OGoConstant.DELAY)
                    }


                }).build()


        }


    }


    ///////Web Service call to witch order
//    @Field("email_id") userName: String,
//    @Field("order_id") orderId: String,

    fun switchedOrderAssignedPost(emailId: String, orderId: String) {
        LogUtils.error(LogUtils.TAG, "switchedOrderAssignedPost userNameStr =>" + emailId)

        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postSwitchedOrderAssigned(emailId, orderId)
            .enqueue(object : Callback<OrderSwitchedAndAssignedResp> {

                override fun onResponse(
                    call: Call<OrderSwitchedAndAssignedResp>,
                    response: Response<OrderSwitchedAndAssignedResp>
                ) {

                    LogUtils.error(
                        LogUtils.TAG,
                        "response.raw().toString() =>" + response.raw().toString()
                    )

                    if (response.isSuccessful) {

                        showOrderSwitchedAndAssignedResponse(response.body()!!.toString())

                        if (response.body()!!.status.toString().equals("true", true)) {

                            LogUtils.error(
                                LogUtils.TAG,
                                "switchedOrderAssignedPost response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                            )
                            //Toast.makeText(mActivity, response.body()!!.message, Toast.LENGTH_LONG).show()
                            Toast.makeText(
                                this@MainActivity,
                                "Order Assigned Successfully! ",
                                Toast.LENGTH_SHORT
                            ).show()

                            receiveCurrentOrderPost(emailId)


                        } else {

                            Toast.makeText(
                                this@MainActivity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()

                        }


                    }
                }

                override fun onFailure(call: Call<OrderSwitchedAndAssignedResp>, t: Throwable) {

                    if (t is IOException) {
                        Toast.makeText(
                            this@MainActivity,
                            "this is an actual network failure :( inform the user and possibly retry",
                            Toast.LENGTH_SHORT
                        ).show();
                        // logging probably not necessary
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "conversion issue! big problems :(",
                            Toast.LENGTH_SHORT
                        ).show();
                        // todo log to some central bug tracking service
                        Toast.makeText(
                            this@MainActivity,
                            " Unable to submit switchedOrderAssignedPost to API.!",
                            Toast.LENGTH_LONG
                        ).show();
                    }

                    LogUtils.error(
                        "TAG",
                        "Unable to submit switchedOrderAssignedPost to API." + t.printStackTrace()
                    )
                    LogUtils.error("TAG", "Unable to submit switchedOrderAssignedPost to API.")

                    // showProgress(false)

                }
            })


    }

    fun showOrderSwitchedAndAssignedResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    private inner class SenderDirectionTask : AsyncTask<String, Void, Void>() {
        internal var locationResponse: LocationSender? = null

        override fun doInBackground(vararg strings: String): Void? {
            val senderLat = strings[0]
            val senderLong = strings[1]

            locationResponse!!.lat = senderLat
            locationResponse!!.lon = senderLong


            return null
        }

        override fun onPostExecute(aVoid: Void) {
            super.onPostExecute(aVoid)

            if (locationResponse != null) {
                if (locationResponse!!.lat != null && locationResponse!!.lon != null) {
                    val resLat = java.lang.Double.parseDouble(locationResponse!!.lat!!)
                    val resLon = java.lang.Double.parseDouble(locationResponse!!.lon!!)
                    val intent = Intent(
                        android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=$resLat,$resLon")
                    )
                    startActivity(intent)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.no_sender_location),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } else {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.no_sender_location),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    private fun removeAllFragments(fragmentManager: FragmentManager) {
        ///if we take condition > 0 , app will close here itself
        while (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate()

        }
    }


    private fun displaySnackBar(snackbarMessage: String, snackBarDuration: Int) {


        when (snackBarDuration) {

            1 -> {

                ////Snackbar setAction
                ///Just point to any View inside the Activity's XML. You can give an id to the root viewGroup
                val snack =
                    Snackbar.make(binding.drawerLayout, snackbarMessage, Snackbar.LENGTH_INDEFINITE)
                snack.setAction(getString(R.string.dismiss), View.OnClickListener {
                    // executed when DISMISS is clicked
                    //System.out.println("Snackbar Set Action - OnClick.")
                    snack.dismiss()
                })
                snack.show()


            }

            2 -> {
                ////Snackbar setAction
                ///Just point to any View inside the Activity's XML. You can give an id to the root viewGroup
                val snack =
                    Snackbar.make((binding.drawerLayout)!!, snackbarMessage, Snackbar.LENGTH_LONG)
                snack.setAction(getString(R.string.dismiss), View.OnClickListener {
                    // executed when DISMISS is clicked
                    //System.out.println("Snackbar Set Action - OnClick.")
                    snack.dismiss()
                })
                snack.show()

            }

            3 -> {
                ////Snackbar setAction
                ///Just point to any View inside the Activity's XML. You can give an id to the root viewGroup
                val snack =
                    Snackbar.make((binding.drawerLayout)!!, snackbarMessage, Snackbar.LENGTH_SHORT)
                snack.setAction(getString(R.string.dismiss), View.OnClickListener {
                    // executed when DISMISS is clicked
                    //System.out.println("Snackbar Set Action - OnClick.")
                    snack.dismiss()
                })
                snack.show()
            }


        }

    }


    /*
    * web service call via retrofit
    * Get current assigned order here
    * */
    private fun receiveCurrentOrderPost(userNameStr: String) {
        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postReceiveCurrentOrder(userNameStr)
            .enqueue(object : Callback<ReceiveCurrentOrderResponse> {
                override fun onResponse(
                    call: Call<ReceiveCurrentOrderResponse>,
                    response: Response<ReceiveCurrentOrderResponse>
                ) {

                    //LogUtils.error("TvCloud>>>", "response.raw().toString() =>" + response.raw().toString())

                    if (response.isSuccessful) {

                        showReceiveCurrentOrderResponse(response.body()!!.toString())


                        if (response.body()!!.status.toString().equals("true", true)) {

                            /////// update current order status here in share preference
                            /// value is not coming from server hence taken as hard coded

                            //preferenceHelper!!.orderStatus  = OGoConstant.DRIVER_ACCEPTED

                            preferenceHelper!!.orderStatus =
                                response.body()!!.data!!.get(0)!!.orderStatusInt
                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                                this@MainActivity,
                                response.body()!!.data!!.get(0).orderStatusInt.toString()
                            )


                            LogUtils.error(
                                LogUtils.TAG,
                                "postReceiveCurrentOrder response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                            )
                            // showProgress(false)
                            LogUtils.error(
                                LogUtils.TAG,
                                "response.body()!!.data!!.get(0) : " + response.body()!!.data!!.get(
                                    0
                                )
                            )
                            ///make sure at least one order is present
                            if (response.body()!!.data!! != null && response.body()!!.data!!.size > 0) {


                                ////make busy status
                                preferenceHelper!!.busyStatus = OGoConstant.BUSY

                                ////here send accepted order ( received current order) to fragment and display it accordingly
                                LogUtils.error(
                                    LogUtils.TAG,
                                    "MainActivity response.body()!!.data.get(0).orderId!!=> orderId >:  " + response.body()!!.data!!.get(
                                        0
                                    ).orderId!!
                                )
                                LogUtils.error(
                                    LogUtils.TAG,
                                    "MainActivity  response.body()!!.data!!.get(0)!!.senderName!! >:  " + response.body()!!.data!!.get(
                                        0
                                    ).senderName!!
                                )
                                LogUtils.error(
                                    LogUtils.TAG,
                                    "MainActivity  response.body()!!.data!!.size >:  " + response.body()!!.data!!.size
                                )
                                currentOrder = response.body()!!.data!!.get(0)!!

                                ///....below value is pushing in fir base database on request of backend developer
                                ///if order exist then save this value to push in fire base
                                //// here instead of cid we are updating diff (nonCid ) as request by backend developer (sumeet )
                                ////while accepting order we are updating cid
                                preferenceHelper!!.fireBase_e5 =
                                    response.body()!!.data!!.get(0).nonCid


                                ////make driver online
                                goOnline()


                                if (supportActionBar != null)
                                    supportActionBar!!.setTitle(R.string.orders)

                                val fragment = OrdersFragment()
                                val b = Bundle()
                                b.putSerializable("DISPLAY_ACCEPTED_ORDER", currentOrder!!)
                                fragment.setArguments(b)
                                val fragmentManager = supportFragmentManager

                                ///with action bar load fragment
                                //fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(fragment.javaClass.getName()).commit()
                                fragmentManager.beginTransaction()
                                    .replace(R.id.frame_container, fragment)
                                    .addToBackStack(fragment.javaClass.getName())
                                    .commitAllowingStateLoss()



                                if (((orderCancelledFromRemoteData != null) && orderCancelledFromRemoteData!!.orderId.toString()
                                        .equals(currentOrder!!.orderId, true))
                                ) {

                                    //LogUtils.error(LogUtils.TAG, "currentOrder!!.orderId =>"+currentOrder!!.orderId)
                                    //LogUtils.error(LogUtils.TAG, "orderCancelledFromRemoteData!!.orderId.toString() =>"+orderCancelledFromRemoteData!!.orderId.toString())

                                    cancelOrder(
                                        OrderCancelledFromRemoteEvent(
                                            orderCancelledFromRemoteData
                                        )
                                    )

                                } else if (currentOrder!!.orderStatusInt.toString()
                                        .equals(OGoConstant.CANCEL)
                                ) {


                                    ///converting map to jason
                                    val gson = Gson()
                                    //val jsonOrder = gson.toJson(remoteMessage.data)

                                    ////converting  MAP to POJO model class ( current order to cancel order )
                                    var orderCancelledFromRemoteData: OrderCancelledFromRemoteData? =
                                        null
                                    val jsonElement = gson.toJsonTree(currentOrder)
                                    orderCancelledFromRemoteData = gson.fromJson(
                                        jsonElement,
                                        OrderCancelledFromRemoteData::class.java
                                    )
                                    cancelOrder(
                                        OrderCancelledFromRemoteEvent(
                                            orderCancelledFromRemoteData
                                        )
                                    )

                                } else if (((orderSwitchingFromRemoteData != null) && orderSwitchingFromRemoteData!!.orderId.toString()
                                        .equals(currentOrder!!.orderId, true))
                                ) {
                                    //// this section is for switching order
                                    switchingOrder(
                                        OrderSwitchFromRemoteEvent(
                                            orderSwitchingFromRemoteData!!
                                        )
                                    )


                                } else if (currentOrder!!.switchOrderStatus.toString()
                                        .equals(OGoConstant.SWITCHING_ORDER)
                                ) {


                                    ///converting map to jason
                                    val gson = Gson()
                                    //val jsonOrder = gson.toJson(remoteMessage.data)

                                    ////converting  MAP to POJO model class ( current order to cancel order )
                                    var orderSwitchFromRemoteData: OrderSwitchFromRemoteData? = null
                                    val jsonElement = gson.toJsonTree(currentOrder)
                                    orderSwitchFromRemoteData = gson.fromJson(
                                        jsonElement,
                                        OrderSwitchFromRemoteData::class.java
                                    )

                                    switchingOrder(
                                        OrderSwitchFromRemoteEvent(
                                            orderSwitchFromRemoteData
                                        )
                                    )

                                } else if (((orderSwitchedAndAssignedFromRemoteData != null) && orderSwitchedAndAssignedFromRemoteData!!.orderId.toString()
                                        .equals(currentOrder!!.orderId, true))
                                ) {
                                    switchedOrderAssigned(
                                        OrderSwitchedAndAssignedFromRemoteEvent(
                                            orderSwitchedAndAssignedFromRemoteData!!
                                        )
                                    )

                                } else if (currentOrder!!.switchOrderStatus!!.equals(OGoConstant.ASSIGNING_ORDER)) {
                                    ///converting map to jason
                                    val gson = Gson()
                                    //val jsonOrder = gson.toJson(remoteMessage.data)

                                    ////converting  MAP to POJO model class ( current order to cancel order )
                                    var orderSwitchedAndAssignedFromRemoteDataModel: OrderSwitchedAndAssignedFromRemoteData? =
                                        null
                                    val jsonElement = gson.toJsonTree(currentOrder)
                                    orderSwitchedAndAssignedFromRemoteDataModel = gson.fromJson(
                                        jsonElement,
                                        OrderSwitchedAndAssignedFromRemoteData::class.java
                                    )

                                    switchedOrderAssigned(
                                        OrderSwitchedAndAssignedFromRemoteEvent(
                                            orderSwitchedAndAssignedFromRemoteDataModel
                                        )
                                    )


                                }


                            } else {

                                /// make here current running order null
                                currentOrder = null
                                UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                                    this@MainActivity,
                                    OGoConstant.NO_ORDER
                                )


                            }


                        } else {


                            ///// display here empty fragment and add some welcome text
                            if (supportActionBar != null)
                                supportActionBar!!.setTitle(R.string.orders)
                            supportFragmentManager.beginTransaction()
                                .replace(R.id.frame_container, WelcomeFragment.newInstance())
                                .commitAllowingStateLoss()

                            Toast.makeText(
                                this@MainActivity,
                                response.body()!!.message,
                                Toast.LENGTH_LONG
                            ).show()

                            /// make here current running order null
                            currentOrder = null

                            UpdateRealTimeDriverOrderStatus.updateRealTimeDriverOrderStatus(
                                this@MainActivity,
                                OGoConstant.NO_ORDER
                            )


                        }


                        LogUtils.error(
                            LogUtils.TAG,
                            "preferenceHelper!!.driverOnlineStatus###=>" + preferenceHelper!!.driverOnlineStatus
                        )

                        /////put last status
                        if (preferenceHelper!!.driverOnlineStatus.equals(
                                OGoConstant.ONLINE,
                                true
                            )
                        ) {

                            goOnline()


                        } else {

                            goOffline()


                        }


                    }
                }

                override fun onFailure(call: Call<ReceiveCurrentOrderResponse>, t: Throwable) {

                    LogUtils.error("TAG", "Unable to submit postReceiveCurrentOrder to API.")
                    Toast.makeText(
                        this@MainActivity,
                        " Unable to submit postReceiveCurrentOrder to API.!",
                        Toast.LENGTH_LONG
                    ).show();

                }
            })


    }

    fun showReceiveCurrentOrderResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


    fun goOnline() {


        ////Check here device has internet connection or not
        if (NetworkUtil.getInstance(this@MainActivity).isOnline) {
            ////first update in fire base and then only update via api

            driverOnlineOfflineStatusPost(
                preferenceHelper!!.lastUsername!!,
                OGoConstant.ONLINE,
                false,
                false
            )


        } else {

            //set state to unchecked
            binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(false);
            binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.START)

            NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)

        }


    }

    private fun makeOnline() {

        //Toast.makeText(this@NavigationActivity, "Toggle button is ON", Toast.LENGTH_LONG).show();
        ///snack bar online
        displaySnackBar(getString(R.string.online), OGoConstant.SHORT)
        ////Make sure GPS is ON
        ///Ask to turn on GPS after getting location permission
        askForGPS()
        //set state to checked
        binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(true);
        binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.END)
        ///close drawer
        binding.drawerLayout!!.closeDrawer(GravityCompat.START)
        binding.lytslidingdrawer.navigationDrawerGoOnlineDetail!!.setText(getString(R.string.navigation_drawer_go_online_desc))
        UpdateRealTimeDriverOnlineStatus.updateDriverOnlineStatus(
            this@MainActivity,
            OGoConstant.ONLINE
        )

        ///Once online then start  services here
        startServicesWhenOnline()


    }

    private fun goOffline() {

        ////Check here device has internet connection or not
        if (NetworkUtil.getInstance(this@MainActivity).isOnline) {

            ////preferenceHelper!!.loggedInUser!!.emailId!!
            driverOnlineOfflineStatusPost(
                preferenceHelper!!.lastUsername!!,
                OGoConstant.OFFLINE,
                false,
                false
            )


        } else {

            //set state to checked
            binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(true);
            binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.END)
            NetworkUtil.getInstance(this@MainActivity).showCustomNetworkError(this@MainActivity)


        }


    }


    private fun makeOffline(isLogout: Boolean) {
        if (currentOrder != null && isLogout == false) {
            // if order exist force user to go online
            goOnline()

        } else {
            preferenceHelper!!.driverOnlineStatus = OGoConstant.OFFLINE

            //Toast.makeText(this@NavigationActivity, "Toggle button is OFF", Toast.LENGTH_LONG).show();
            ///snack bar offline
            displaySnackBar(getString(R.string.offline), OGoConstant.INDEFINITE)
            //set state to unchecked
            binding.lytslidingdrawer.toggleGoOnlineButton.setChecked(false);
            binding.lytslidingdrawer.toggleGoOnlineButtonLayout.setHorizontalGravity(Gravity.START)
            ///close drawer
            (binding.drawerLayout)!!.closeDrawer(GravityCompat.START)

            binding.lytslidingdrawer.navigationDrawerGoOnlineDetail.setText(getString(R.string.navigation_drawer_go_online_description))
            UpdateRealTimeDriverOnlineStatus.updateDriverOnlineStatus(
                this@MainActivity,
                OGoConstant.OFFLINE
            )

            ///Once offline then stop  services here
            stopServicesWhenOffline()


        }


    }


    fun getImage(imageName: String): Int {

        return resources.getIdentifier(imageName, "drawable", packageName)
    }


    /*
  * LOGOUT web service call via retrofit
  *
  *
  * email_id:john@ogo.delivery
  *
  *
  * */


    private fun logoutPost(userNameStr: String) {

        ////////////////////////////////
        ///loader animation
        binding.lytappbar.lytcontent.loadingLayoutAnim.visibility = View.VISIBLE
        val loader = CircularSticksLoader(
            this@MainActivity, 80, 90f, 40f,
            ContextCompat.getColor(this, R.color.white),
            ContextCompat.getColor(this, R.color.colorLoader),
            ContextCompat.getColor(this, R.color.white)
        )
            .apply {

            }
        binding.lytappbar.lytcontent.loadingLayoutAnim.addView(loader)
        ////////////////////////////////


        apiPostService = ApiPostUtils.apiPostService

        apiPostService!!.postLogout(userNameStr).enqueue(object : Callback<LogoutResp> {
            override fun onResponse(call: Call<LogoutResp>, response: Response<LogoutResp>) {

                // LogUtils.error(LogUtils.TAG, "response.raw().toString() =>" + response.raw().toString())
                if (response.isSuccessful) {

                    showLogoutResponse(response.body()!!.toString())

                    if (response.body()!!.status.toString().equals("true", true)) {


                        makeOffline(true)
                        var isSucesssfullFirebasePush =
                            UpdateRealTimeDriverApplicationStatus.updateRealTimeDriverApplicationStatus(
                                this@MainActivity,
                                OGoConstant.LOGOUT
                            )
                        LogUtils.error(
                            LogUtils.TAG,
                            "isSucesssfullFirebasePush ===>" + isSucesssfullFirebasePush
                        )

                        if (isSucesssfullFirebasePush) {
                            Toast.makeText(this@MainActivity, "Logout success!", Toast.LENGTH_LONG)
                                .show()
                        }

                        stopAllServices()
                        LogUtils.error(
                            LogUtils.TAG,
                            "postLogout response.body()!!.message.toString() =>" + response.body()!!.message.toString()
                        )

                        val i = Intent(this@MainActivity, LoginActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(i)
                        // close this activity
                        finish()

                    } else {

                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show();

                    }


                }
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.lytappbar.lytcontent.loadingLayoutAnim.visibility = View.GONE
                }, 250L)
            }

            override fun onFailure(call: Call<LogoutResp>, t: Throwable) {

                LogUtils.error("TAG", "Unable to submit postLogout to API.")
                Toast.makeText(
                    this@MainActivity,
                    " Unable to submit postLogout to API.!",
                    Toast.LENGTH_LONG
                ).show();


            }
        })


    }

    fun showLogoutResponse(response: String) {
        LogUtils.error("TAG>>>> showLogoutResponse -> RESPONSE >>>>", response)
    }


    ///First ask for permission
    ///Ask user for location permission
    private fun askForPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) !== PackageManager.PERMISSION_GRANTED
        ) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this@MainActivity,
                    permission
                )
            ) {
                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf<String>(permission),
                    requestCode
                )
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf<String>(permission),
                    requestCode
                )
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    //// This method will generate dialog box if GPS is not enable and ask user to enable it.
    private fun askForGPS() {
        mLocationRequest = LocationRequest.create()
        mLocationRequest!!.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mLocationRequest!!.setInterval(30 * 1000)
        mLocationRequest!!.setFastestInterval(5 * 1000)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest!!)
        builder.setAlwaysShow(true)

        ///deprecated
//        result = LocationServices.SettingsApi.checkLocationSettings(client, builder.build())
//        result!!.setResultCallback(object:ResultCallback<LocationSettingsResult> {
//            override fun onResult(result:LocationSettingsResult) {
//                val status = result.getStatus()
//                when (status.getStatusCode()) {
//                    LocationSettingsStatusCodes.SUCCESS -> {}
//                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try
//                    {
//                        status.startResolutionForResult(this@MainActivity, GPS_SETTINGS)
//                    }
//                    catch (e: IntentSender.SendIntentException) {
//                    }
//                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
//                }
//            }
//        })


        ////Check result in  onActivityResult in RESULT_CODE
        ///latest
        val task = LocationServices.getSettingsClient(this).checkLocationSettings(builder.build())
        task.addOnCompleteListener(object : OnCompleteListener<LocationSettingsResponse> {
            override fun onComplete(task: Task<LocationSettingsResponse>) {
                try {
                    val response = task.getResult(ApiException::class.java)
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                } catch (exception: ApiException) {
                    when (exception.getStatusCode()) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                val resolvable = exception as ResolvableApiException
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                    this@MainActivity,
                                    RESULT_CODE
                                )
                            } catch (e: Throwable) {
                                // Ignore the error.
                            } catch (e: Throwable) {
                                // Ignore, should be an impossible error.
                            }

                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {}
                    }// Location settings are not satisfied. However, we have no way to fix the
                    // settings so we won't show the dialog.
                }
            }
        })


        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
            val states = LocationSettingsStates.fromIntent(data)
            when (requestCode) {
                RESULT_CODE -> when (resultCode) {
                    Activity.RESULT_OK ->
                        // All required changes were successfully made
                        Toast.makeText(
                            this@MainActivity,
                            " " + states.isLocationPresent(),
                            Toast.LENGTH_SHORT
                        ).show()

                    Activity.RESULT_CANCELED ->
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(
                            this@MainActivity,
                            " " + getString(R.string.cancel),
                            Toast.LENGTH_SHORT
                        ).show()

                    else -> {}
                }
            }
        }


    }


    private fun loadYourOrdersTab() {
        Toast.makeText(this@MainActivity, "Your Orders", Toast.LENGTH_LONG).show()
        binding.drawerLayout!!.closeDrawer(GravityCompat.START)
        binding.lytappbar.lytcontent.bottomNavigation.BubbleNavigationmainLayout.visibility =
            View.VISIBLE
        receiveCurrentOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!)
    }


    private fun loadNearByOrdersTab() {
        //Toast.makeText(this@MainActivity, "Your Near by Orders : To Do!", Toast.LENGTH_LONG).show()
        //supportFragmentManager.beginTransaction().replace(R.id.frame_container, NearByOrdersFragment.newInstance()).addToBackStack("Near By Orders").commit()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, NearByOrdersFragment.newInstance())
            .commitAllowingStateLoss()


    }


///////############# during shift time going offline is not allowed


    private fun shiftTimeOffline() {
        if (preferenceHelper!!.loggedInUser!!.shiftFromTime == null || preferenceHelper!!.loggedInUser!!.shiftFromTime == "" || preferenceHelper!!.loggedInUser!!.shiftToTime == null || preferenceHelper!!.loggedInUser!!.shiftToTime == "") {
            /// can go offline
            goOffline()
        } else if (TimeUtils.isValidShiftDateAndTime(
                preferenceHelper!!.loggedInUser!!.shiftFromTime,
                preferenceHelper!!.loggedInUser!!.shiftToTime!!
            )
        ) {
            ///can not go offline during shift timing
            showShiftTimeDialogue(".You can not go offline without admin permission.")
            goOnline()

        } else {
            /// can go offline
            goOffline()

        }

    }


    ///////############# shift time login and logout

    private fun shiftTimeOnline() {


        if (preferenceHelper!!.loggedInUser!!.shiftFromTime == null || preferenceHelper!!.loggedInUser!!.shiftFromTime == "" || preferenceHelper!!.loggedInUser!!.shiftToTime == null || preferenceHelper!!.loggedInUser!!.shiftToTime == "") {
            goOffline()
            val builder = CFAlertDialog.Builder(this@MainActivity)
                .setDialogStyle(CFAlertDialog.CFAlertStyle.NOTIFICATION)
                .setTitle("ALERT")
                .setCancelable(false)
                .setMessage("Your working shift is not available today.Please make sue you are on duty to go online.")
                .addButton(
                    "Ok got it.",
                    -1,
                    getResources().getColor(R.color.color_them),
                    CFAlertDialog.CFAlertActionStyle.POSITIVE,
                    CFAlertDialog.CFAlertActionAlignment.END,
                    { dialog, which ->
                        //Toast.makeText(this@MainActivity, "Ok.", Toast.LENGTH_SHORT).show()


                        dialog.dismiss()

                    })
            builder.show()


        } else {
            // val isValidShiftTime = TimeUtils.isTimeBetween(preferenceHelper!!.loggedInUser!!.shiftFromTime!!,preferenceHelper!!.loggedInUser!!.shiftToTime!!,currentTime)

            if (TimeUtils.isValidShiftDateAndTime(
                    preferenceHelper!!.loggedInUser!!.shiftFromTime,
                    preferenceHelper!!.loggedInUser!!.shiftToTime!!
                )
            ) {
                goOnline()

            } else {

                goOffline()
                val builder = CFAlertDialog.Builder(this@MainActivity)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle("ALERT")
                    .setCancelable(false)
                    .setMessage("Your working shift hours has been finished.Please wait for next shift to go online.")
                    .addButton(
                        "Ok got it.",
                        -1,
                        getResources().getColor(R.color.color_them),
                        CFAlertDialog.CFAlertActionStyle.POSITIVE,
                        CFAlertDialog.CFAlertActionAlignment.END,
                        { dialog, which ->
                            //Toast.makeText(this@MainActivity, "Ok.", Toast.LENGTH_SHORT).show()


                            dialog.dismiss()

                        })
                builder.show()


            }

        }

    }


    private fun shiftTimeLogin(userName: String) {

        if (preferenceHelper!!.loggedInUser!!.shiftFromTime != null || preferenceHelper!!.loggedInUser!!.shiftToTime != null) {
            if (TimeUtils.isValidShiftDateAndTime(
                    preferenceHelper!!.loggedInUser!!.shiftFromTime,
                    preferenceHelper!!.loggedInUser!!.shiftToTime!!
                )
            ) {

                showShiftTimeDialogue(".You can not logout without admin permission.")


            } else {
                logoutPost(userName)

            }

        } else {
            logoutPost(userName)

        }


    }


    private fun showShiftTimeDialogue(msg: String) {
        val shiftStartedAt = TimeUtils.getFormatedDateTime(
            preferenceHelper!!.loggedInUser!!.shiftFromTime!!,
            TimeUtils.readFormate,
            TimeUtils.writeFormat
        )
        val shiftEndAt = TimeUtils.getFormatedDateTime(
            preferenceHelper!!.loggedInUser!!.shiftToTime!!,
            TimeUtils.readFormate,
            TimeUtils.writeFormat
        )

        val builder = CFAlertDialog.Builder(this@MainActivity)
            .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
            .setTitle("Your shift time")
            .setCancelable(false)
            .setMessage("" + shiftStartedAt + " - " + shiftEndAt + " " + msg)
            .addButton(
                "Ok got it.",
                -1,
                getResources().getColor(R.color.color_them),
                CFAlertDialog.CFAlertActionStyle.POSITIVE,
                CFAlertDialog.CFAlertActionAlignment.END,
                { dialog, which ->
                    //Toast.makeText(this@MainActivity, "Ok.", Toast.LENGTH_SHORT).show()


                    dialog.dismiss()

                })
        builder.show()
    }


    /*
* web service call via retrofit
* Get current assigned order here
* */
    private fun stuckOrderPost(userNameStr: String) {
        apiPostService = ApiPostUtils.apiPostService
        apiPostService!!.postStuckOrder(userNameStr).enqueue(object : Callback<StuckOrderResp> {
            override fun onResponse(
                call: Call<StuckOrderResp>,
                response: Response<StuckOrderResp>
            ) {

                //LogUtils.error("TvCloud>>>", "response.raw().toString() =>" + response.raw().toString())

                if (response.isSuccessful) {

                    showStuckOrderResponse(response.body()!!.toString())


                    if (response.body()!!.status.toString().equals("true", true)) {

                        ///////Here whether its accepted order or not just call web service to get current driver assigned order and proceed accordingly
                        receiveCurrentOrderPost(preferenceHelper!!.loggedInUser!!.emailId!!)
                        NetworkStateChangeUtil.checkInternetConnectivity(
                            this@MainActivity,
                            javaClass.simpleName
                        )

                        startServicesWhenOnline()


                    } else {

                        Toast.makeText(
                            this@MainActivity,
                            response.body()!!.message,
                            Toast.LENGTH_LONG
                        ).show()


                    }


                }
            }

            override fun onFailure(call: Call<StuckOrderResp>, t: Throwable) {

                LogUtils.error("TAG", "Unable to submit postStuckOrder to API.")
                Toast.makeText(
                    this@MainActivity,
                    " Unable to submit postStuckOrder to API.!",
                    Toast.LENGTH_LONG
                ).show();

            }
        })


    }

    fun showStuckOrderResponse(response: String) {
        LogUtils.error("TAG>>>>RESPONSE>>>>", response)
    }


}



