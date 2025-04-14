package webtech.com.courierdriver.utilities

import webtech.com.courierdriver.constants.OGoConstant
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws

/*
Created by ​
Hannure Abdulrahim


on 3/19/2019.
 */
object TimeUtils
{
    val readFormate = "HH:mm:ss"
    val writeFormat = "hh:mm a"

    fun isTimeBetween(startTime: String, endTime: String, currentTime: String): Boolean {
        val lCurrentTime = parseTimeStringToSeconds(currentTime)
        val lstartTime = parseTimeStringToSeconds(startTime)
        val lEndTime = parseTimeStringToSeconds(endTime)

        return if ((lstartTime - lCurrentTime) * (lEndTime - lCurrentTime) * (lstartTime - lEndTime) > 0) {
            true
        } else {
            false
        }
    }



    // given time string (hours:minutes:seconds, or mm:ss, return number of seconds.
    fun parseTimeStringToSeconds(str: String): Long {
        try {
            return parseTime(str)
        } catch (nfe: NumberFormatException) {
            return 0
        }

    }




    // given: mm:ss or hh:mm:ss or hhh:mm:ss, return number of seconds.
    // bad input throws NumberFormatException.
    // bad includes:  "", null, :50, 5:-4
    @Throws(NumberFormatException::class)
    fun parseTime(str: String?): Long {
        if (str == null)
            throw NumberFormatException("parseTimeString null str")
        if (str.isEmpty())
            throw NumberFormatException("parseTimeString empty str")

        var h = 0
        val m: Int
        val s: Int
        val units = str.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        assert(units.size == 2 || units.size == 3)
        when (units.size) {
            2 -> {
                // mm:ss
                m = Integer.parseInt(units[0])
                s = Integer.parseInt(units[1])
            }

            3 -> {
                // hh:mm:ss
                h = Integer.parseInt(units[0])
                m = Integer.parseInt(units[1])
                s = Integer.parseInt(units[2])
            }

            else -> throw NumberFormatException("parseTimeString failed:$str")
        }
        if (m < 0 || m > 60 || s < 0 || s > 60 || h < 0)
            throw NumberFormatException("parseTimeString range error:$str")
        return (h * 3600 + m * 60 + s).toLong()
    }


//receiving time as 23:30:00.
// This method will convert it to the format of 12 hours.
// i.e I want output something like 11:30 PM (after converting 23:30:00).

    fun getFormatedDateTime(dateStr:String, strReadFormat:String, strWriteFormat:String):String {
        var formattedDate = dateStr
        val readFormat = SimpleDateFormat(strReadFormat, Locale.getDefault())
        val writeFormat = SimpleDateFormat(strWriteFormat, Locale.getDefault())
        var date:Date? = null
        try
        {
            date = readFormat.parse(dateStr)
        }
        catch (e: ParseException) {}
        if (date != null)
        {
            formattedDate = writeFormat.format(date)
        }
        return formattedDate
    }


    ///input in string formate
    ///06/23/2021 18:07:01
    //MM/dd//yyyy HH:mm:ss
    //output in DateObJect

    fun getDateFormattedDateObject(dateStr:String):Date?
    {
        val dateFormat = SimpleDateFormat(
                "HH:mm:ss",Locale.US)

        try {

            val dateObject = dateFormat.parse(dateStr)
            return dateObject
        }catch (e :ParseException) {
            e.printStackTrace()
            return null
        }


    }




    fun isDateInBetweenIncludingEndPoints(shiftStartDateTime: Date?, shiftEndDateTime: Date?, dateTimeNow: Date): Boolean {
        return !(dateTimeNow.before(shiftStartDateTime) || dateTimeNow.after(shiftEndDateTime))
    }


    fun isValidShiftDateAndTime(shiftFromTime:String?,shiftToTime:String?): Boolean {
        var isShiftDateTimeAvailable = false
        var isValidShiftDateTime = false
        isShiftDateTimeAvailable = !( shiftFromTime.isNullOrEmpty() || shiftToTime.isNullOrEmpty() || shiftFromTime.equals(OGoConstant.DUTRY_OFF) || shiftToTime.equals(OGoConstant.DUTRY_OFF))

        if(isShiftDateTimeAvailable)
        {
            ///from server shift from time and shift to time
            ///06/23/2021 18:07:01
            //MM/dd/yyyy HH:mm:ss
            val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US)
            val nowDate = Date()
            // val currentDateTimeStr = sdf.format(nowDate)

            //// get All date object here
            // val currentDateTimeObject =  TimeUtils.getDateFormattedDateObject(currentDateTimeStr)
            val shiftFromDateTimeObject =  getDateFormattedDateObject(shiftFromTime!!)
            val shiftToDateTimeObject = getDateFormattedDateObject(shiftToTime!!)


            LogUtils.error(LogUtils.TAG, "currentDateTimeObject:" +nowDate.toString())
            LogUtils.error(LogUtils.TAG, "shiftFromDateTimeObject:" +shiftFromDateTimeObject.toString())
            LogUtils.error(LogUtils.TAG, "shiftToDateTimeObject:" +shiftToDateTimeObject.toString())

            isValidShiftDateTime  = isDateInBetweenIncludingEndPoints( shiftFromDateTimeObject,shiftToDateTimeObject,nowDate)


            LogUtils.error(LogUtils.TAG, "isValidShiftDateTime:" +isValidShiftDateTime)

            return isValidShiftDateTime

            // val isValidShiftTime = TimeUtils.isTimeBetween(preferenceHelper!!.loggedInUser!!.shiftFromTime!!,preferenceHelper!!.loggedInUser!!.shiftToTime!!,currentTime)

        }
        return isShiftDateTimeAvailable



    }



    //

    fun getFormattedDateAndTime():String {


        val MMddHHmmFormat = SimpleDateFormat("MM-dd HH:mm", Locale.US) // 19-08-31 01:55
        //SimpleDateFormat called with pattern
        return  MMddHHmmFormat.format(Calendar.getInstance().getTime())

//        val yyMMddHHmmFormat = SimpleDateFormat("yy-MM-dd HH:mm", Locale.US) // 19-08-31 01:55
//        //SimpleDateFormat called with pattern
//        return  yyMMddHHmmFormat.format(Calendar.getInstance().getTime())


//                    val yyyMMddHHmmssFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US) // 2018-08-31 01:55:22
//                    //SimpleDateFormat called with pattern
//                    return  yyyMMddHHmmssFormat.format(Calendar.getInstance().getTime())
                 //SimpleDateFormat called without pattern
                //return SimpleDateFormat().format(Calendar.getInstance().getTime())


    }



    ///used for PACI while feeding api param
    //yyyy-MM-dd HH:mm:ss
    fun getPACIFormattedDateAndTime():String {
        val MMddHHmmFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US) // 2020-07-27 08:50:16
        //SimpleDateFormat called with pattern
        return MMddHHmmFormat.format(Calendar.getInstance().getTime())
    }









}
