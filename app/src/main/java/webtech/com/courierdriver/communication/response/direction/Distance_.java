package webtech.com.courierdriver.communication.response.direction;

/*
Created by ​
Hannure Abdulrahim


on 1/13/2019.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Distance_ {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private Integer value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("text", text).append("value", value).toString();
    }

}