package API;

import java.util.ArrayList;
import java.util.List;

import Models.Ingredient;
import Models.Label;

public class RecipeConfig
{
    List<String> ingredients;
    List<String> topTenIngredients;
    List<Label> labels;
    List<String> allergies;
    List<String> dislikes;

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Label> getLabels() {
        return labels;
    }

    public void setLabels(List<Label> labels) {
        this.labels = labels;
    }

    public List<String> getAllergies() {
        return allergies;
    }

    public void setAllergies(List<String> allergies) {
        this.allergies = allergies;
    }

    public List<String> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<String> dislikes) {
        this.dislikes = dislikes;
    }

    public List<String> getTopTenIngredients() {
        return topTenIngredients;
    }

    public void setTopTenIngredients(List<String> topTenIngredients) {
        this.topTenIngredients = topTenIngredients;
    }

    public  RecipeConfig(List<String> topTenIngredients)
    {
        this.topTenIngredients = new ArrayList<>(topTenIngredients);
        //this.labels = new ArrayList<>();
        this.allergies = new ArrayList<>();
        this.dislikes = new ArrayList<>();
        this.ingredients = new ArrayList<>();
    }
}
