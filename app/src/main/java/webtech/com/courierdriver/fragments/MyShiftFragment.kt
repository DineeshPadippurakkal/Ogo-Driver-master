package webtech.com.courierdriver.fragments

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import webtech.com.courierdriver.R
import webtech.com.courierdriver.utilities.PreferenceHelper
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A simple [Fragment] subclass.
 * Use the [MyShiftFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyShiftFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var shiftAdapter: ShiftAdapter
    var preferenceHelper: PreferenceHelper? = null
    var mActivity: Activity? = null
    lateinit var progressBar:ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_sift, container, false)
        recyclerView = view.findViewById(R.id.rvShifts)
        progressBar = view.findViewById(R.id.progressBar)
        preferenceHelper = PreferenceHelper(requireContext())

        recyclerView.layoutManager = LinearLayoutManager(context)
        shiftAdapter = ShiftAdapter(emptyList())
        recyclerView.adapter = shiftAdapter
        fetchData()

        return view
    }

    private fun fetchData() {
        var emailId = ""
        preferenceHelper?.let { preferenceHelper ->
            preferenceHelper.loggedInUser?.let { loginResposeData ->
                emailId = preferenceHelper!!.loggedInUser!!.emailId.toString()
            }
        }
        val url = "https://ogo.delivery:8888/api/driver/shift_time_list"
        val params = HashMap<String, String>()
        params["email_id"] = emailId
        val jsonObject = (params as Map<*, *>?)?.let { JSONObject(it) }

        progressBar.visibility = View.VISIBLE
        val queue: RequestQueue = Volley.newRequestQueue(activity)
        val request: JsonObjectRequest = object : JsonObjectRequest(Request.Method.POST,
            url,
            jsonObject,
            Response.Listener { response ->
                try {
                    progressBar.visibility = View.GONE
                    if(response.getString("status")=="true") {
                        val shifts = parseShifts(response)
                        shiftAdapter = ShiftAdapter(shifts)
                        recyclerView.adapter = shiftAdapter
                        Toast.makeText(activity, response.getString("message"),Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(activity, response.getString("message"),Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    progressBar.visibility = View.GONE
                    e.printStackTrace()
                    println("params--> ${e.toString()}")
                }
            },
            Response.ErrorListener { error ->
                progressBar.visibility = View.GONE
                Log.e("TAG", "RESPONSE IS $error")
                Toast.makeText(activity, "Fail to get response", Toast.LENGTH_SHORT).show()
            }) {
        }
        queue.add(request)
    }

    private fun parseShifts(response: JSONObject): List<Shift> {
        val shifts = mutableListOf<Shift>()
        val dataArray = response.optJSONArray("data")
        if (dataArray != null) {
            for (i in 0 until dataArray.length()) {
                val shiftObj = dataArray.optJSONObject(i)
                val shiftFromTime = shiftObj.optString("shift_from_time")
                val shiftToTime = shiftObj.optString("shift_to_time")
                val currentDay = shiftObj.optString("current_day")
                if(shiftFromTime!="DUTY_OFF") {
                    val formattedFromTime = convertTimeFormat(shiftFromTime)
                    val formattedToTime = convertTimeFormat(shiftToTime)
                    shifts.add(Shift(formattedFromTime, formattedToTime, currentDay))
                }else{
                    shifts.add(Shift("DUTY_OFF", "DUTY_OFF", currentDay))
                }
            }
        }
        return shifts
    }

    data class Shift(
        val shiftFromTime: String,
        val shiftToTime: String,
        val currentDay: String
    )

    companion object {
        fun newInstance(): MyShiftFragment {
            return MyShiftFragment()
        }
    }

    class ShiftAdapter(private val shifts: List<Shift>) : RecyclerView.Adapter<ShiftAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val dayTextView: TextView = itemView.findViewById(R.id.dayTextView)
            val shiftTimeTextView: TextView = itemView.findViewById(R.id.timeTextView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_shifts, parent, false)
            return ViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val shift = shifts[position]
            holder.dayTextView.text = shift.currentDay
            if(shift.shiftFromTime!="DUTY_OFF") {
                holder.shiftTimeTextView.text = "${shift.shiftFromTime} - ${shift.shiftToTime}"
            }else{
                holder.shiftTimeTextView.text = "DUTY OFF"
                holder.shiftTimeTextView.setTextColor(Color.RED)
            }
        }

        override fun getItemCount(): Int {
            return shifts.size
        }
    }

    override fun onResume() {
        super.onResume()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.your_shift))



    }

    override fun onPause() {
        super.onPause()
        mActivity!!.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR

    }
    /////////////////////////////////////E


    override fun onAttach(context: Context) {
        super.onAttach(context)


        /// for new version > M
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Code here

            //first convert context into Activity ,since context is also Activity
            val activity = context as? Activity
            mActivity = activity





        }
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)


        /// for old  version < M
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here

            mActivity = activity

        }
    }

    override fun onDetach() {
        super.onDetach()
        (mActivity as AppCompatActivity).getSupportActionBar()!!.setTitle(getString(R.string.your_shift))


    }


    fun convertTimeFormat(timeString: String): String {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm aa", Locale.getDefault())

        try {
            val time = inputFormat.parse(timeString)
            return outputFormat.format(time)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}