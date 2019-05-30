package Users;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.example.dor.testfeedme.R;

import java.util.List;

import API.Utilities;
import Database.Client;
import Database.GetRecipeFromDatabase;
import MainActivity.MainActivity;
import Models.Recipe;

public class GuestActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout search;
    List<String> recipeNames;
    RegularUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);
        initializeRecipeSearch();
        InitializeTextViewListeners();
        user = new RegularUser();
    }

    private String getRecipeName() {
        String s = ((AutoCompleteTextView) findViewById(R.id.RecipeSearch)).getText().toString();
        return s;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearButton:
                Client.The().getRecipeByName(getRecipeName(), new GetRecipeFromDatabase() {
                    @Override
                    public void onCallbackRecipe(Recipe recipe) {
                        Intent ShowRecipeActivity = new Intent(GuestActivity.this, FeedMeButton.ShowRecipeActivity.class);
                        Recipe curr = recipe;
                        ShowRecipeActivity.putExtra("currRecipe", curr);
                        ShowRecipeActivity.putExtra("currUser",user);
                        ((AutoCompleteTextView)findViewById(R.id.RecipeSearch)).setText("");
                        startActivity(ShowRecipeActivity);
                    }
                });
                break;
        }
    }
        private void setUtilitiesContext()
        {
            Utilities.setContext(this);
        }
        private void initializeRecipeSearch ()
        {
            setUtilitiesContext();
            recipeNames = Utilities.getRecipes();

            search = findViewById(R.id.linearButton);
            search.setOnClickListener(this);
        }

        private void InitializeTextViewListeners () {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line,
                    this.recipeNames);

            ((AutoCompleteTextView) findViewById(R.id.RecipeSearch)).setAdapter(adapter);
            InitializeTextViewListener(R.id.RecipeSearch, R.id.linearButton);

        }
        private void InitializeTextViewListener ( int tvId, final int btnId){

            ((AutoCompleteTextView) findViewById(tvId)).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().trim().length() == 0 ||
                            !recipeNames.contains(s.toString().trim())) {
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
    @Override
    public void onBackPressed()
    {
        Intent main = new Intent(GuestActivity.this, MainActivity.class);
        startActivity(main);
    }
    }

