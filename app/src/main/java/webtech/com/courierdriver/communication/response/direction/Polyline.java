package webtech.com.courierdriver.communication.response.direction;

/*
Created by ​
Hannure Abdulrahim


on 1/13/2019.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Polyline {

    @SerializedName("points")
    @Expose
    private String points;

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("points", points).toString();
    }

}