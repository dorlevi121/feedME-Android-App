package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Instructions implements Serializable
{
    private String key;
    public Instructions (String key)
    {
        this.key = key;
    }
    public Instructions()
    {
        key = "";
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


}
