package Register;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dor.testfeedme.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import API.Utilities;
import Database.Client;
import Database.GetRecipesFromDatabase;
import Database.InternetConnection;
import Database.Server;
import MainActivity.MainActivity;
import Models.DownloadImageTask;
import Models.Ingredient;
import Models.Recipe;
import Users.RegularUser;

public class EntrySurveyText extends AppCompatActivity implements View.OnClickListener {

    private List<String> allergies;
    private List<String> dislikes;
    private List<String> Ingredients;
    private List<String> recipes = new ArrayList<>();
    private List<Recipe> chosenRecipes = new ArrayList<>();
    private List<Recipe> chunckOfTenRecipes = new ArrayList<>();
    private boolean isKosher = true;
    private String FoodType = "Regular";
    private DownloadImageTask imageViewHandler;
    private int currRecipeIndex = 0;
    private final int MAXIMUM_CHOSEN_RECIPES = 10;
    private RegularUser newUser;
    private Button addAllergyBtn;
    private Button addDislikeBtn;
    private Button nextBtn;
    private Button likeBtn;
    private Button passBtn;
    private Button continueBtn;
    private RadioGroup KosherRadioGroup;
    private RadioGroup VeganRadioGroup;
    private ImageView im;
    private TextView tv;
    private CheckBox premium;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_survey_text);

        findViewById(R.id.AllergiesLinearLayout).setLayoutDirection(LinearLayout.LAYOUT_DIRECTION_LTR);
        findViewById(R.id.DislikesLinearLayout).setLayoutDirection(LinearLayout.LAYOUT_DIRECTION_LTR);

        InitializeListeners();

        allergies = new ArrayList<>();
        dislikes = new ArrayList<>();
        //getAllRecipes();
        //loadTenRecipes();
        Bundle data = getIntent().getExtras();
        newUser= data.getParcelable("newUser");


    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.addAllergyBtn:
                AddAllergy();
                ((AutoCompleteTextView)findViewById(R.id.IngredientSearch)).setText("");
                break;

            case R.id.addDisklikeBtn:
                addDislike();
                ((AutoCompleteTextView)findViewById(R.id.DislikesSearch)).setText("");
                break;

            case R.id.nextBtn:
                checkForVegan();
                break;

            case R.id.continueBtn:
                //check if is internet connection
                if(!InternetConnection.isNetworkNetworkAvailable(EntrySurveyText.this))
                    Toast.makeText(EntrySurveyText.this, "No Internet connection",Toast.LENGTH_SHORT).show();
                else
                CompleteRegistration();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void checkForVegan(){
        if (FoodType != "Regular"){
            Client.The().getVegetarianRecipes(new GetRecipesFromDatabase() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onCallbackRecipes(List<Recipe> recipes) {
                    Utilities.recipes = recipes;
                    goToImageSurvey();
                }
            });
        }
        else if(Utilities.RecipeMode == "Vegetarian"){
            Client.The().getAllRecipes(new GetRecipesFromDatabase() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onCallbackRecipes(List<Recipe> recipes) {
                    Utilities.recipes = recipes;
                    goToImageSurvey();
                }
            });
        }
        else {
            goToImageSurvey();
        }
    }

    private void CompleteRegistration() {
        newUser.setAllergies(allergies);
        newUser.setDislikes(dislikes);
        newUser.setTop5FavMeal(GetTop5Recipes());
        newUser.setTop10FavIngredients(GetTop10Ingreds());
        newUser.setUserClassification(FoodType);
        newUser.setKosher(isKosher);
        boolean isPremium = (premium.isChecked()) ? true : false;
        newUser.setPremium(isPremium);
        Server.The().completeRegister(newUser);
        Toast.makeText(EntrySurveyText.this, getString(R.string.register_success), Toast.LENGTH_LONG).show();
        Intent login = new Intent(EntrySurveyText.this, MainActivity.class);
        startActivity(login);
    }

    private List<String> GetTop10Ingreds() {
        List<String> rc = new ArrayList<>();
        HashMap<Ingredient, Integer> ingredsCount = new HashMap<>();
        List<Ingredient> ingreds = new ArrayList<>();
        for (Recipe rec : chosenRecipes){
            ingreds.addAll(rec.getIngredients());
        }
        for (Ingredient ingred : ingreds){
            if (ingredsCount.containsKey(ingred)){
                ingredsCount.put(ingred, ingredsCount.get(ingred) + 1);
            }
            else {
                ingredsCount.put(ingred, 1);
            }
        }
        ingredsCount = sortMap(ingredsCount);
        int counter = 0;
        for (Ingredient in : ingredsCount.keySet()){
            if (counter < 10){
                rc.add(in.getKey());
            }
            counter++;
        }

        return rc;
    }

    private HashMap<Ingredient, Integer> sortMap(HashMap<Ingredient, Integer> map) {
        List<Map.Entry<Ingredient, Integer>> capitalList = new LinkedList<>(map.entrySet());

        Collections.sort(capitalList, new Comparator<Map.Entry<Ingredient, Integer>>() {
            @Override
            public int compare(Map.Entry<Ingredient, Integer> o1, Map.Entry<Ingredient, Integer> o2) {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        Collections.reverse(capitalList);

        HashMap<Ingredient, Integer> result = new HashMap<>();
        for (Map.Entry<Ingredient, Integer> entry : capitalList)
        {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    private List<String> GetTop5Recipes() {
        List<String> rc = new ArrayList<>();
        for (int i = 0; i < 5; i++){
            rc.add(chosenRecipes.get(i).getName());
        }
        return rc;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeListeners(){
        GetAllIngredients();

        InitializeTextViewListeners();

        addAllergyBtn = findViewById(R.id.addAllergyBtn);
        addAllergyBtn.setOnClickListener(this);

        addDislikeBtn = findViewById(R.id.addDisklikeBtn);
        addDislikeBtn.setOnClickListener(this);

        InitializeCheckboxListener();

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);

        InitializeRadioGroupsListeners();
    }

    private void InitializeCheckboxListener() {
        premium = findViewById(R.id.premiumCheckBox);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeRadioGroupsListeners() {
        InitializeKosherRadioGroup();
        InitializeVeganRadioGroup();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeVeganRadioGroup() {
        VeganRadioGroup = findViewById(R.id.VeganRadioGroup);
        VeganRadioGroup.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        VeganRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                FoodType = rb.getText().toString();
                if (((RadioButton)findViewById(R.id.radio_regular)).getError() != null){
                    ((RadioButton)findViewById(R.id.radio_regular)).setError(null);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeKosherRadioGroup() {
        KosherRadioGroup = findViewById(R.id.KosherRadioGroup);
        KosherRadioGroup.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        KosherRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = findViewById(checkedId);
                isKosher = rb.getText().toString() == "Kosher" ? true : false;
                if (((RadioButton)findViewById(R.id.radio_kosher)).getError() != null){
                    ((RadioButton)findViewById(R.id.radio_kosher)).setError(null);
                }
            }
        });
    }

    private void InitializeTextViewListener(int tvId, final int btnId){

        ((AutoCompleteTextView)findViewById(tvId)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0 ||
                        !Ingredients.contains(s.toString().trim())){
                    findViewById(btnId).setEnabled(false);
                } else {
                    findViewById(btnId).setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void InitializeTextViewListeners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                this.Ingredients);

        ((AutoCompleteTextView)findViewById(R.id.IngredientSearch)).setAdapter(adapter);
        InitializeTextViewListener(R.id.IngredientSearch, R.id.addAllergyBtn);

        ((AutoCompleteTextView)findViewById(R.id.DislikesSearch)).setAdapter(adapter);
        InitializeTextViewListener(R.id.DislikesSearch, R.id.addDisklikeBtn);

    }

    private void addDislike() {
        String s = ((AutoCompleteTextView)findViewById(R.id.DislikesSearch)).getText().toString();
        if(!s.equals(""))
            dislikes.add(s);
    }

    private void HandleLikedRecipe() {
        chosenRecipes.add(Utilities.recipes.get(currRecipeIndex));
        generateNewRecipe();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void generateNewRecipe() {
        if (chosenRecipes.size() < MAXIMUM_CHOSEN_RECIPES){
            currRecipeIndex++;
            Recipe currRec = Utilities.recipes.get(currRecipeIndex);
            imageViewHandler = new DownloadImageTask(im);
            try {
                //Bitmap res = imageViewHandler.execute(recipes.get(currRecipeIndex).getImgUrl()).get();
                Bitmap res = imageViewHandler.execute(currRec.getImgUrl()).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tv.setText(currRec.getName());
            return;
        }
        setContentView(R.layout.last_registration_layout);

        continueBtn = findViewById(R.id.continueBtn);
        continueBtn.setOnClickListener(this);
    }

    private void AddAllergy() {
        String s = ((AutoCompleteTextView)findViewById(R.id.IngredientSearch)).getText().toString();
        if(!s.equals(""))
            allergies.add(s);
    }

    private void GetAllIngredients(){
        setUtilitesContext();
        this.Ingredients = Utilities.getIngredients();
    }

    private void setUtilitesContext(){
        Utilities.setContext(this);
    }

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void goToImageSurvey(){
        Random rand = new Random();
        currRecipeIndex = rand.nextInt(Utilities.recipes.size() - 1);
        setContentView(R.layout.image_survey_layout);
        im = findViewById(R.id.imageView);
        im.setOnTouchListener(new OnSwipeTouchListener(EntrySurveyText.this) {
            public void onSwipeRight() {
                HandleLikedRecipe();
            }
            public void onSwipeLeft() {
                generateNewRecipe();
            }
        });
        imageViewHandler = new DownloadImageTask(im);

        Recipe currRec = Utilities.recipes.get(currRecipeIndex);

        try {
            Bitmap res = imageViewHandler.execute(currRec.getImgUrl()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tv = findViewById(R.id.imageTitle);
        tv.setText(currRec.getName());

    }


}
