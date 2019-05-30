package FeedMeButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dor.testfeedme.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import API.RecipeConfig;
import API.Utilities;
import Database.Client;
import Database.GetDataFromFirebase;
import Database.GetRecipesFromDatabase;
import Database.Server;
import Models.DownloadImageTask;
import Models.Recipe;
import Register.OnSwipeTouchListener;
import Users.RegularUser;

public class GenerateSuggestionsActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle aToggle;
    private String userEmail;
    private RegularUser userDetails;
    private NavigationView navigationView;
    private Button feedMeBtn, addIngBtn, updateBTN;
    private List<String> ingredients, chosenIng;
    private List<Recipe> recipesToChooseFrom;
    private int currRecipeIndex;
    private ImageView im;
    private DownloadImageTask imageViewHandler;
    private TextView tv;
    private boolean initialized, isUpdate, isFinishLoadingUser;
    LinearLayout search, searchHeadline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_suggestions);

        chosenIng = new ArrayList<>();
        findViewById(R.id.feedMeBtn).setEnabled(false);
        //get user email from MainActivity
        Bundle data = getIntent().getExtras();
        userEmail = data.getString("userEmail");

        getUserDetails();
        initialized = true;
    }


    private void getUserDetails() {

        Client.The().getUserFromDatabase(userEmail, new GetDataFromFirebase() {
            @Override
            public void onCallback(RegularUser user) {
                userDetails = user;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Initialize FeedME button and Premium search
                        InitializeButtonListener();

                        //Initialize Side menu
                        addFullNameToHeaderMenu();
                        InitializeSideBarMenu();

                        //Initialize search for premium user
                        initializePremium();

                        handleSpinnerEnd();
                    }
                });

            }
        });

    }

    private void handleSpinnerEnd(){
        findViewById(R.id.startupLinearLayout).setVisibility(View.GONE);
        findViewById(R.id.mainLinearLayout).setVisibility(View.VISIBLE);
    }


    //Initialize FeedME button and Premium search
    private void InitializeButtonListener() {
        feedMeBtn = findViewById(R.id.feedMeBtn);
        feedMeBtn.setEnabled(true);
        feedMeBtn.setOnClickListener(this);
        //Initialize all the ingredients for Premium user search
        initializeIngAdding();
    }


    //Initialize all the ingredients for Premium user search
    private void initializeIngAdding()
    {
        //Initialize all the ingredients to initialize List<String>
        GetAllIngredients();

        addIngBtn = findViewById(R.id.addIngBtn);
        addIngBtn.setOnClickListener(this);
    }



    //Initialize all the ingredients to initialize List<String>
    private void GetAllIngredients(){
        setUtilitiesContext();
        this.ingredients = Utilities.getIngredients();
    }



    private void setUtilitiesContext(){
        Utilities.setContext(this);
    }



    /////////////////////// Premium User - search bu ingredients ///////////////////////


    //Add ingredient that premium user selected to chosenIng List
    private void addIng() {
        String s = ((AutoCompleteTextView)findViewById(R.id.IngSearch)).getText().toString();
        if(!s.equals(""))
            chosenIng.add(s);
    }




    //Initialize search for premium user
    private void initializePremium()
    {
        search = findViewById(R.id.SearchLinearLayout);
        searchHeadline = findViewById(R.id.SearchHeadlineLinearLayout);
        if(!userDetails.isPremium())
        {
            search.setVisibility(View.GONE);
            searchHeadline.setVisibility(View.GONE);
        }
        //Initialize search by ingredients for premium user
        else
            InitializeTextViewListeners();

    }



    //Initialize search by ingredients for premium user
    private void InitializeTextViewListeners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                this.ingredients);

        ((AutoCompleteTextView)findViewById(R.id.IngSearch)).setAdapter(adapter);
        //Add all the ingredients to complete text search
        InitializeTextViewListener(R.id.IngSearch, R.id.addIngBtn);

    }



    //Add all the ingredients to complete text search
    private void InitializeTextViewListener(int tvId, final int btnId){

        ((AutoCompleteTextView)findViewById(tvId)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0 ||
                        !ingredients.contains(s.toString().trim())){
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



    /////////////////////// END Premium User - search bu ingredients ///////////////////////





    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void HandleUserChooseRecipe() {
        if (recipesToChooseFrom == null || recipesToChooseFrom.size() == 0){
            if (!userDetails.getUserClassification().equals("Regular")){
                Client.The().getVegetarianRecipes(new GetRecipesFromDatabase() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                    @Override
                    public void onCallbackRecipes(List<Recipe> recipes) {
                        Utilities.recipes = recipes;
                        getRecipesByConfig();
                        StartShowingRecipes();
                    }
                });
            }
            else {
                getRecipesByConfig();
                StartShowingRecipes();
            }
        }
        else {
            StartShowingRecipes();
        }
    }

    private void getRecipesByConfig(){
        RecipeConfig config = new RecipeConfig(userDetails.getTop10FavIngredients());
        Log.i("TOP INGREDIENTS: ", userDetails.getTop10FavIngredients().toString());
        if(chosenIng.size() > 0)
        {
            config.setIngredients(chosenIng);
        }
        if (userDetails.getAllergies().size() > 0){
            config.setAllergies(userDetails.getAllergies());
        }
        if (userDetails.getDislikes().size() > 0){
            config.setDislikes(userDetails.getDislikes());
        }
        recipesToChooseFrom = Utilities.findRecpiesByUserPreferences(config);
        if (recipesToChooseFrom.size() < 20){
            recipesToChooseFrom = Utilities.recipes;
        }
        Log.i("SIZE:", (String.valueOf(recipesToChooseFrom.size())));
    }



    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void StartShowingRecipes() {
        setContentView(R.layout.layout_choose_recipe);
        InitializeSideBarMenu();
        addFullNameToHeaderMenu();
        im = findViewById(R.id.imageView);
        im.setOnTouchListener(new OnSwipeTouchListener(GenerateSuggestionsActivity.this) {
            public void onSwipeRight() {
                HandleChooseBtn();
            }
            public void onSwipeLeft() {
                generateNewRecipe();
            }
        });
        showNewRecipe();
    }

    private void showNewRecipe() {
        if (currRecipeIndex < recipesToChooseFrom.size() - 1){
            imageViewHandler = new DownloadImageTask(im);
            Recipe currRec = recipesToChooseFrom.get(currRecipeIndex);
            currRecipeIndex++;
            try {
                Bitmap res = imageViewHandler.execute(currRec.getImgUrl()).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tv = findViewById(R.id.chooseRecipeImageTitle);
            tv.setText(currRec.getName());
        }
        else{
            currRecipeIndex = 0;
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.feedMeBtn:
                HandleUserChooseRecipe();
                break;

            case R.id.addIngBtn:
                addIng();
                ((AutoCompleteTextView)findViewById(R.id.IngSearch)).setText("");
        }
    }

    private void HandleChooseBtn() {
        Intent showRecipeIntent = new Intent(GenerateSuggestionsActivity.this,
                ShowRecipeActivity.class);
        Recipe curr = recipesToChooseFrom.get(currRecipeIndex - 1);
        userDetails.getRecipeHistory().add(curr.getName());
        Server.The().UpdateUserRecipeHistory(userEmail, userDetails.getRecipeHistory());
        showRecipeIntent.putExtra("currRecipe", curr);
        showRecipeIntent.putExtra("currUser", userDetails);
        startActivity(showRecipeIntent);
    }

    private void generateNewRecipe(){
        showNewRecipe();
    }




    //////////// Side Menu ////////////////

    private void InitializeSideBarMenu() {
        drawerLayout = findViewById(R.id.drawer);
        aToggle = new ActionBarDrawerToggle(this,drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(aToggle);
        aToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // declaring the NavigationView
        navigationView = findViewById(R.id.menuLayout);
        // assigning the listener to the NavigationView
        navigationView.setNavigationItemSelectedListener(this);
    }



    private void addFullNameToHeaderMenu(){
        NavigationView navigationView = findViewById(R.id.menuLayout);
        View headerView = navigationView.inflateHeaderView(R.layout.header_menu);

        TextView emailMenu = headerView.findViewById(R.id.email_menu);
        emailMenu.setText(userDetails.getEmail());

        TextView fullnameMenu = headerView.findViewById(R.id.name_menu);
        fullnameMenu.setText(userDetails.getFirstName() + " " + userDetails.getLastName());

        TextView classifictionMenu = headerView.findViewById(R.id.classifiction_menu);
        classifictionMenu.setText(userDetails.getUserClassification());
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(aToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }



    public boolean onNavigationItemSelected(MenuItem menuItem) {

        menuItem.setChecked(true);

        switch (menuItem.getItemId()) {
            case R.id.profile:
                profileMenu();
                break;
            case R.id.recipeHistory:
                // do you click actions for the second selection
                break;
            case R.id.share:
                // do you click actions for the third selection
                break;
            case R.id.setting:
                settingMenu();
                break;
            case R.id.logOut:
                logOutMenu();
                break;
        }

        return false;

    }



    private void profileMenu(){
        setContentView(R.layout.profile_menu);

        TextView emailProfile = findViewById(R.id.email_profile);
        emailProfile.setText(userDetails.getEmail());

        TextView fullnameProfile = findViewById(R.id.name_profile);
        fullnameProfile.setText(userDetails.getFirstName() + " " + userDetails.getLastName());

        TextView premium = findViewById(R.id.Premium);
        String text = (userDetails.isPremium()) ? "Premium" : "Premium Not Actived";
        premium.setText(text);

        TextView classifictionProfile = findViewById(R.id.classificrion_profile);
        classifictionProfile.setText(userDetails.getUserClassification());

        TextView yearOfBirthProfile = findViewById(R.id.yearOfBirth_profile);
        yearOfBirthProfile.setText(userDetails.getYearOfBirth());
    }



    private void logOutMenu(){
        Client.The().logOutUser();
        Intent intent = new Intent(GenerateSuggestionsActivity.this, MainActivity.MainActivity.class);
        finish();
        startActivity(intent);

    }



    private void settingMenu(){
        setContentView(R.layout.setting_menu);

        final EditText emailSetting = findViewById(R.id.setting_email);
        emailSetting.setText(userDetails.getEmail());

        final EditText firstNameSetting = findViewById(R.id.setting_firstName);
        firstNameSetting.setText(userDetails.getFirstName());

        final EditText lastNameSetting = findViewById(R.id.setting_lastName);
        lastNameSetting.setText(userDetails.getLastName());

        final EditText yearOfBirthSetting = findViewById(R.id.setting_yearOfName);
        yearOfBirthSetting.setText(userDetails.getYearOfBirth());

        EditText classificationSetting = findViewById(R.id.setting_classification);
        classificationSetting.setText(userDetails.getUserClassification());

        EditText dislikeSetting = findViewById(R.id.setting_dislike);
        if(!userDetails.getDislikes().isEmpty()){
            String s = userDetails.getDislikes().get(0);
            for(int i = 1; i<userDetails.getDislikes().size(); i++)
                s = s + ", " + userDetails.getDislikes().get(i);
            dislikeSetting.setText(s);
        }
        else
            dislikeSetting.setText("No Dislike chooses");

        TextView allergiesSetting = findViewById(R.id.setting_allergies);
        if(!userDetails.getAllergies().isEmpty()){
            String t = userDetails.getAllergies().get(0);
            for(int i = 1; i<userDetails.getAllergies().size(); i++)
                t = t + ", " + userDetails.getAllergies().get(i);
            allergiesSetting.setText(t);
        }
        else
            allergiesSetting.setText("No Allergies chooses");

        isUpdate = false;

        updateBTN = findViewById(R.id.setting_updateButton);
        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!userDetails.getFirstName().equals(firstNameSetting.getText().toString())) {

                    if (!firstNameSetting.getText().toString().matches("[a-zA-Z]+")) {
                        String msg;
                        if (firstNameSetting.getText().toString().isEmpty()) {
                            msg = getString(R.string.requiredError);
                        } else {
                            msg = getString(R.string.onlyLettersError);
                        }
                        firstNameSetting.setError(msg);
                        firstNameSetting.requestFocus();
                    }

                    else{
                        userDetails.setFirstName(firstNameSetting.getText().toString());
                        isUpdate = true;
                    }

                }



                if (!userDetails.getLastName().equals(lastNameSetting.getText().toString())) {

                    if (!lastNameSetting.getText().toString().matches("[a-zA-Z]+")) {
                        String msg;
                        if (lastNameSetting.getText().toString().isEmpty()) {
                            msg = getString(R.string.requiredError);
                        } else {
                            msg = getString(R.string.onlyLettersError);
                        }
                        lastNameSetting.setError(msg);
                        lastNameSetting.requestFocus();
                    }

                    else{
                        userDetails.setFirstName(lastNameSetting.getText().toString());
                        isUpdate = true;
                    }

                }




                if (!userDetails.getYearOfBirth().equals(yearOfBirthSetting.getText().toString())) {
                    if (yearOfBirthSetting.getText().toString().length() != 4) {
                        yearOfBirthSetting.setError(getString(R.string.short_tearOfBirth));
                        yearOfBirthSetting.requestFocus();
                        isUpdate = false;
                    } else {
                        userDetails.setYearOfBirth(yearOfBirthSetting.getText().toString());
                        isUpdate = true;
                    }
                }

                if(isUpdate){
                    Server.The().updateUserDetails(emailSetting.getText().toString(), userDetails);
                    setContentView(R.layout.activity_generate_suggestions);
                    onBackPressed();
                }

            }

        });
    }

    ////////////End Side Menu ////////////////




    @Override
    public void onBackPressed() {
        setContentView(R.layout.activity_generate_suggestions);
        InitializeButtonListener();
        InitializeSideBarMenu();
        addFullNameToHeaderMenu();
        initializePremium();
        InitializeTextViewListeners();
        handleSpinnerEnd();
    }
}