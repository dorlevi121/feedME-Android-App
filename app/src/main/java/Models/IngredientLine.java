package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class IngredientLine implements Serializable
{
    String key;
    String value;


    public IngredientLine(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public IngredientLine()
    {
        this.key = "";
        this.value = "";
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }


}
