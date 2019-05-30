
package Database;

import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import API.Utilities;
import Models.Ingredient;
import Models.IngredientLine;
import Models.Instructions;
import Models.Label;
import Models.Recipe;
import Users.RegularUser;

public class Client {


    private DatabaseReference db;
    private RegularUser user;
    private String email;
    private List<String> topIngreds = new ArrayList<>();
    private List<String> topMeals = new ArrayList<>();
    private List<String> allergies = new ArrayList<>();
    private List<String> dislikes = new ArrayList<>();
    private Set<String> recipeHistory = new HashSet<>();
    private String classificiation;
    private boolean isKosher;
    private Recipe recipe;
    private String recipeName;
    private List<Recipe> recipes = new ArrayList<>();
    private boolean ingreds, meals, alregs, dlikes, history, premium;
    private boolean isPremium;
    private static Client _instance;

    private Client(){
        db = FirebaseDatabase.getInstance().getReference();
        this.user = new RegularUser();
    }

    public static Client The(){
        if (_instance == null){
            _instance = new Client();
        }
        return _instance;
    }

    private void clearUser(){
        this.user = new RegularUser();
    }

    private void getTop5Meals(String email) {
        db.child("Users").child(email.replace(".", "|")).child("top5Meal")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> topMealsIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : topMealsIter){
                                topMeals.add(dss.getValue().toString());
                            }
                        }
                        meals = true;
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getAllergies(String email) {
        db.child("Users").child(email.replace(".", "|")).child("Allergies")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> allergiesIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : allergiesIter){
                                allergies.add(dss.getValue().toString());
                            }
                        }
                        alregs = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getDislikes(String email) {
        db.child("Users").child(email.replace(".", "|")).child("Dislikes")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> dislikesIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : dislikesIter){
                                dislikes.add(dss.getValue().toString());
                            }
                        }
                        dlikes = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getTop10Ingredients(String email) {
        db.child("Users").child(email.replace(".", "|")).child("Top10Ingredients")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> topIngredsIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : topIngredsIter){
                                topIngreds.add(dss.getValue().toString());
                            }
                        }
                        ingreds = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getPremium(String email) {
        db.child("Users").child(email.replace(".", "|")).child("Details")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> detailsIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : detailsIter){
                                if((dss.getKey().toString().equals("premium")))
                                {
                                    String tmp = dss.getValue().toString();
                                    isPremium = tmp.equals("true");
                                }
                            }
                        }
                        premium = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void getRecipeHistory(String email) {
        db.child("Users").child(email.replace(".", "|")).child("RecipeHistory")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Iterable<DataSnapshot> historyIter = dataSnapshot.getChildren();
                            for (DataSnapshot dss : historyIter){
                                recipeHistory.add(dss.getValue().toString());
                            }
                        }
                        history = true;
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    public void getRecipeByName(String recipeName, final GetRecipeFromDatabase callback){
        db.child("Recipes").child(recipeName).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    callback.onCallbackRecipe(createRecipeFromSnapShot(dataSnapshot));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error
            }
        });
    }

    public void getVegetarianRecipes(final GetRecipesFromDatabase callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.child("Recipes").child("Vegetarian").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dss : dataSnapshot.getChildren()){
                                recipes.add(createRecipeFromSnapShot(dss));
                            }
                            Utilities.RecipeMode = "Vegetarian";
                            callback.onCallbackRecipes(recipes);
                            clearRecipes();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }


    public void getAllRecipes(final GetRecipesFromDatabase callback){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                db.child("Recipes").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            for (DataSnapshot dss : dataSnapshot.getChildren()){
                                recipes.add(createRecipeFromSnapShot(dss));
                            }
                            Utilities.RecipeMode = "Regular";
                            callback.onCallbackRecipes(recipes);
                            clearRecipes();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void clearRecipes() {
        this.recipes = new ArrayList<>();
    }

    private Recipe createRecipeFromSnapShot(DataSnapshot dss){
        String name = dss.child("name").getValue(String.class);
        String img = dss.child("imgUrl").getValue(String.class);
        List<Label> labels = new ArrayList<>();
        List<Ingredient> ingredients = new ArrayList<>();
        List<IngredientLine> ingredientLines = new ArrayList<>();
        List<Instructions> instructions = new ArrayList<>();
        for (DataSnapshot labelDss : dss.child("labels").getChildren()){
            labels.add(labelDss.getValue(Label.class));
        }
        for (DataSnapshot ingredDss : dss.child("ingredients").getChildren()){
            ingredients.add(ingredDss.getValue(Ingredient.class));
        }
        for (DataSnapshot ingredLineDss : dss.child("ingredientLine").getChildren()){
            ingredientLines.add(ingredLineDss.getValue(IngredientLine.class));
        }
        for (DataSnapshot instrucDss : dss.child("instructions").getChildren()){
            instructions.add(instrucDss.getValue(Instructions.class));
        }
        return new Recipe(name, img, ingredients, labels, instructions, ingredientLines);
    }

    public void getUserFromDatabase(final String email, final GetDataFromFirebase myCallback){
        clearUser();
        db.child("Users").child(email.replace(".", "|")).child("Details")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            user = dataSnapshot.getValue(RegularUser.class);
                            getAllergies(email);
                            getDislikes(email);
                            getTop5Meals(email);
                            getTop10Ingredients(email);
                            getRecipeHistory(email);
                            getPremium(email);
                            AsyncTask.execute(new Runnable() {
                                @Override
                                public void run() {
                                    while (!ingreds || !meals || !alregs || !dlikes || !history || !premium) { }
                                    user.setTop10FavIngredients(topIngreds);
                                    user.setTop5FavMeal(topMeals);
                                    user.setAllergies(allergies);
                                    user.setDislikes(dislikes);
                                    user.setRecipeHistory(recipeHistory);
                                    user.setPremium(isPremium);
                                    myCallback.onCallback(user);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    public void logOutUser(){
        FirebaseAuth.getInstance().signOut();
    }
}