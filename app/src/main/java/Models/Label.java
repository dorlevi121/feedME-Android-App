package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Label implements Serializable
{
    String key;
    String value;

    public Label(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    public Label()
    {
        this.key = "";
        this.value = "";
    }
    public String getKey()
    {
        return key;
    }

    public void setKey(String type)
    {
        this.key = type;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }


}
