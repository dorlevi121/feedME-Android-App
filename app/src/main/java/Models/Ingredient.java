package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Ingredient implements Serializable
{
    String key;

    public Ingredient(String key)
    {
        this.key = key;
    }

    public Ingredient()
    {
        this.key = "";
    }
    public String getKey()
    {
        return key;
    }

    public void setKey(String type)
    {
        this.key = type;
    }


}
