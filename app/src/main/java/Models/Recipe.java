package Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Users.RegularUser;

public class Recipe implements Serializable
{
    String name;
    String imgUrl;
    List<IngredientLine> ingredientLine;
    List<Ingredient> ingredients;
    List<Label> labels;
    List<Instructions> instructions;
    public Recipe()
    {
        this.name = "";
        this.imgUrl = "";
        this.ingredients = new ArrayList<>();
        this.labels = new ArrayList<>();
        this.instructions = new ArrayList<>();
        this.ingredientLine = new ArrayList<>();
    }

    public Recipe(String name, String imgUrl, List<Ingredient> ingredients, List<Label> labels, List<Instructions> instructions, List<IngredientLine> ingredientLine)
    {
        this.name = name;
        this.imgUrl = imgUrl;
        this.ingredients = ingredients;
        this.labels = labels;
        this.instructions = instructions;
        this.ingredientLine = ingredientLine;
    }


    public String getImgUrl()
    {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl)
    {
        this.imgUrl = imgUrl;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public List<Ingredient> getIngredients()
    {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients)
    {
        ingredients = ingredients;
    }

    public List<Label> getLabels()
    {
        return labels;
    }

    public void setLabels(List<Label> labels)
    {
        labels = labels;
    }

    public List<Instructions> getInstructions()
    {
        return instructions;
    }

    public List<IngredientLine> getIngredientLine() {
        return ingredientLine;
    }

    public void setIngredientLine(List<IngredientLine> ingredientLine) {
        this.ingredientLine = ingredientLine;
    }

    public void setInstructions(List<Instructions> instructions)
    {
        this.instructions = instructions;
    }

}
