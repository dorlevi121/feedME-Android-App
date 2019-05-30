package Register;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.dor.testfeedme.R;
import Users.RegularUser;

import Database.Server;

public class signUp extends AppCompatActivity implements View.OnClickListener {

    private EditText email_SignUp, password_SignUp, firstName_SignUp, lastName_SignUp, yearOfBirth_SignUp;
    private Button signUpButton;
    private boolean isExists = false;
    public static int userID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        email_SignUp = findViewById(R.id.signUp_email);
        password_SignUp = findViewById(R.id.signUp_password);
        firstName_SignUp = findViewById(R.id.signUp_firstName);
        lastName_SignUp = findViewById(R.id.signUp_lastName);
        yearOfBirth_SignUp = findViewById(R.id.signUp_yearOfBirth);
        signUpButton = findViewById(R.id.signUp_button);
        signUpButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.signUp_button:
                if (ValidateFields()) {
                    //Check if email exists
                    Server.The().checkIfEmailExists(email_SignUp.getText().toString(), new OnEmailCheckListener() {
                        @Override
                        public void onSucess(Boolean emailExist) {

                            if (!emailExist) {
                                email_SignUp.setError(getString(R.string.email_exist));
                                email_SignUp.requestFocus();
                                isExists = false;
                            } else{
                                isExists = true;

                            }
                        }
                    });
                    if(isExists)
                    goToEntrySurvey();
                }

             break;
        }
    }

    private void goToEntrySurvey() {
        Intent entrySurveyActivity = new Intent(signUp.this, EntrySurveyText.class);
        RegularUser user = new RegularUser(firstName_SignUp.getText().toString(),
                lastName_SignUp.getText().toString(),
                email_SignUp.getText().toString(),
                yearOfBirth_SignUp.getText().toString());

        //Add new user to firebase
        Server.The().registerNewUser(user, password_SignUp.getText().toString());

        //Full register
        entrySurveyActivity.putExtra("newUser", user);
        startActivity(entrySurveyActivity);
    }


    private Boolean ValidateFields() {
        Boolean rc = true;
        final String firstName = firstName_SignUp.getText().toString();
        final String lastName = lastName_SignUp.getText().toString();
        final String email = email_SignUp.getText().toString();
        final String password = password_SignUp.getText().toString();
        final String yearOfBirth = yearOfBirth_SignUp.getText().toString();

        if (!firstName.matches("[a-zA-Z]+")) {
            String msg;
            if (firstName.isEmpty()) {
                msg = getString(R.string.requiredError);
            } else {
                msg = getString(R.string.onlyLettersError);
            }
            firstName_SignUp.setError(msg);
            firstName_SignUp.requestFocus();
            rc = false;
        } else if (!lastName.matches("[a-zA-Z]+")) {
            String msg;
            if (lastName.isEmpty()) {
                msg = getString(R.string.requiredError);
            } else {
                msg = getString(R.string.onlyLettersError);
            }
            lastName_SignUp.setError(msg);
            lastName_SignUp.requestFocus();
            rc = false;
        } else if (yearOfBirth.length() != 4) {
            yearOfBirth_SignUp.setError(getString(R.string.short_tearOfBirth));
            yearOfBirth_SignUp.requestFocus();
            rc = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            email_SignUp.setError(getString(R.string.email_required));
            email_SignUp.requestFocus();
            rc = false;
        } else if (password.length() < 6) {
            password_SignUp.setError(getString(R.string.short_password));
            password_SignUp.requestFocus();
            rc = false;
        }

        return rc;
    }


}