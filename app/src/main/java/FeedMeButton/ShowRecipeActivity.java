package FeedMeButton;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dor.testfeedme.R;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;

import API.Utilities;
import Models.DownloadImageTask;
import Models.IngredientLine;
import Models.Instructions;
import Models.Label;
import Models.Recipe;
import Register.EntrySurveyText;
import Register.OnSwipeTouchListener;

public class ShowRecipeActivity extends AppCompatActivity {

    private Recipe currRecipe;
    private TextView recipeTitle;
    private LinearLayout labelsLinearLayout;
    private LinearLayout ingredsLinearLayout;
    private LinearLayout instrucstionsLinearLayout;
    private ImageView im;
    private DownloadImageTask imageViewHandler;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);

        Intent i = getIntent();
        currRecipe = (Recipe)i.getSerializableExtra("currRecipe");

        InitializeLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeLayout() {
        InitializeRecipeTitle();

        InitializeLabels();

        InitializeImage();

        InitializeIngredients();

        InitializeInstructions();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void InitializeInstructions() {
        instrucstionsLinearLayout = findViewById(R.id.InstrucstionsLinearLayout);
        if (currRecipe.getInstructions().size() > 0){
            for (Instructions ins : currRecipe.getInstructions()){
                TextView key = new TextView(this);
                key.setText(ins.getKey());
                instrucstionsLinearLayout.addView(key);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeIngredients() {
        ingredsLinearLayout = findViewById(R.id.IngredientsLinearLayout);
        if (currRecipe.getIngredientLine().size() > 0){
            for (IngredientLine il : currRecipe.getIngredientLine()){
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                TextView key = new TextView(this);
                key.setText(il.getValue());
                ll.addView(key);
                TextView divider = new TextView(this);
                divider.setText(" : ");
                ll.addView(divider);
                TextView value = new TextView(this);
                value.setText(il.getKey());
                ll.addView(value);
                ingredsLinearLayout.addView(ll);
            }
        }
    }

    private void InitializeImage() {
        im = findViewById(R.id.imageView);
        imageViewHandler = new DownloadImageTask(im);

        try {
            Bitmap res = imageViewHandler.execute(currRecipe.getImgUrl()).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void InitializeLabels() {
        labelsLinearLayout = findViewById(R.id.LabelsLinearLayout);
        if (currRecipe.getLabels().size() > 0){
            for (Label label : currRecipe.getLabels()){
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                ll.setGravity(Gravity.CENTER);
                TextView key = new TextView(this);
                key.setText(label.getKey());
                ll.addView(key);
                TextView divider = new TextView(this);
                divider.setText(" : ");
                ll.addView(divider);
                TextView value = new TextView(this);
                value.setText(label.getValue());
                ll.addView(value);
                labelsLinearLayout.addView(ll);
            }
        }
    }

    private void InitializeRecipeTitle() {
        recipeTitle = findViewById(R.id.recipeTitle);
        recipeTitle.setText(currRecipe.getName());
    }


}
