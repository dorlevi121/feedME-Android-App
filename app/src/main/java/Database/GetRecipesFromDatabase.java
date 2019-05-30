package Database;

import java.util.List;

import Models.Recipe;

public interface GetRecipesFromDatabase
{
    void onCallbackRecipes(List<Recipe> recipes);
}
