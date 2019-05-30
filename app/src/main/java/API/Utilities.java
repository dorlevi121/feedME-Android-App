package API;

import android.content.Context;
import android.util.Pair;

import Models.Ingredient;
import Models.Label;
import Models.Recipe;

import com.example.dor.testfeedme.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import static java.util.Map.Entry.comparingByValue;


public class Utilities {
    private static Context context;
    private static List<String> ingredients = new ArrayList<>();
    public static boolean ApplicationLoaded = false;
    public static List<Recipe> recipes;
    private static HashMap<Double,Integer> rateMap;
    public static void setContext(Context _context){
        context = _context;
    }
    public static String RecipeMode;

    public static List<String> getIngredients()
    {
        if (ingredients.size() == 0){
            buildIngredientsList();
        }
        return ingredients;
    }

    private static void buildIngredientsList()
    {
        ArrayList<Integer> res = new ArrayList<>();
        for (char ch = 'a'; ch <= 'z'; ch++){
            res.add(context.getResources().getIdentifier(String.valueOf(ch), "raw", context.getPackageName()));
        }

        for (Integer r : res){
            InputStream in = context.getResources().openRawResource(r);
            JSONObject jsonObj = new JSONObject();
            JSONParser jsonParser = new JSONParser();
            try {
                jsonObj = (JSONObject)jsonParser.parse(new InputStreamReader(in, "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Set<String> keys = jsonObj.keySet();
            System.out.println(keys.toString());
            ingredients.addAll(keys);
        }
    }

    public static List<String> getRecipes()
    {
        List<String> listOfRecipes = new ArrayList<>();
        InputStream in = context.getResources().openRawResource(R.raw.recipe_data);
        JSONObject jsonObj;
        JSONParser jsonParser = new JSONParser();
        try {
            jsonObj = (JSONObject)jsonParser.parse(new InputStreamReader(in, "UTF-8"));
            for(Iterator iterator = jsonObj.keySet().iterator(); iterator.hasNext();)
            {
                String key = (String) iterator.next();
                listOfRecipes.add(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return listOfRecipes;
    }

    public static List<Recipe> findRecpiesByUserPreferences(RecipeConfig config)
    {
        List<Recipe> ans = new ArrayList<>();
        rateMap = new HashMap<>();
        for(Recipe recipe : recipes)
        {
            double matchRate = 0;
            if(!config.ingredients.isEmpty())
            {
                matchRate = checkIngredients(recipe, config.ingredients);
                double sum = matchRate * ingredients.size() / recipe.getIngredients().size();
                matchRate += sum;
            }

            if (!config.allergies.isEmpty())
            {
                if (checkAllergies(recipe, config))
                {
                    continue;
                }
            }

            matchRate += checkIngredients(recipe, config.topTenIngredients);
            matchRate /= 1.5;

            if(!config.dislikes.isEmpty())
            {
                matchRate -= checkDislikes(recipe, config);
            }

            if(matchRate >= 0.5)
            {
                ans.add(recipe);
                while (rateMap.containsKey(matchRate))
                {
                    matchRate += 0.001;
                }
                rateMap.put(matchRate,ans.indexOf(recipe));
            }
        }
        ans = sortChosenRecipeList(ans);
        return ans;
    }
    private static List<Recipe> sortChosenRecipeList(List<Recipe> ans)
    {
        List<Recipe> tmp = new ArrayList<>();
        while(!rateMap.isEmpty())
        {
            tmp.add(ans.get(rateMap.get(Collections.max(rateMap.keySet()))));
            rateMap.remove(Collections.max(rateMap.keySet()));
        }
        return tmp;
    }

    private static double checkIngredients(Recipe rec, List<String> ingredients)
    {
        double matchRate = 0;
        for(String configIng : ingredients)
        {
            for(Ingredient ing : rec.getIngredients())
            {
                if(ing.getKey().equals(configIng))
                {
                    matchRate++;
                    continue;
                }
            }
        }
        matchRate /= ingredients.size();
        return matchRate;
    }

    private static boolean checkAllergies(Recipe rec, RecipeConfig con)
    {
        if(!con.allergies.isEmpty())
        {
            for (String configAlle : con.allergies)
            {
                for (Ingredient ing : rec.getIngredients())
                {
                    if (configAlle.equals(ing.getKey()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private  static double checkDislikes(Recipe rec, RecipeConfig con)
    {
        double matchRate = 0;
        for(String dislikes : con.dislikes)
        {
            for(Ingredient ing : rec.getIngredients())
            {
                if(ing.getKey().equals(dislikes))
                {
                    matchRate++;
                    continue;
                }
            }
        }
        if(matchRate > 1)
        {
            return 1;
        }
        matchRate /= con.dislikes.size();
        return matchRate;
    }
//    private static boolean checklabels(Recipe rec, RecipeConfig con)
//    {
//        for(Label configLabel : con.labels)
//        {
//            for (Label label : rec.getLabels())
//            {
//                if(configLabel.getKey().equals("Total Time"))
//                {
//                    if(configLabel.getValue().split(" ")[0]. <= label.getValue().split(" ")[0])
//                    {
//
//                    }
//                }
//            }
//        }
//    }
}

