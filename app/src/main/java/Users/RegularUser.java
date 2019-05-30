package Users;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import Models.Recipe;
import Register.signUp;

public class RegularUser implements user, Parcelable {

    private String firstName, lastName, email;
    private String yearOfBirth;
    private String userClassification;
    private  boolean isKosher;
    private List<String> allergies;
    private List<String> dislikes;
    private List<String> top5FavMeal;
    private List<String> top10FavIngredients;
    private boolean isPremium;

    public Set<String> getRecipeHistory() {
        return recipeHistory;
    }

    public void setRecipeHistory(Set<String> recipeHistory) {
        this.recipeHistory = new HashSet<>(recipeHistory);
    }

    private Set<String> recipeHistory;
    private int userID;


    ////////////////Constructor////////////

    public RegularUser(String firstName, String lastName, String email, String yearOfBirth) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.yearOfBirth = yearOfBirth;
        isKosher = true;
        allergies = new ArrayList<>();
        dislikes = new ArrayList<>();
        top5FavMeal = new ArrayList<>();
        top10FavIngredients = new ArrayList<>();
        this.userID = signUp.userID++;
    }


    public RegularUser() {}


    public int getUserID() {
        return userID;
    }

    public boolean getKosher() {
        return isKosher;
    }

    public void setKosher(boolean kosher) {
        isKosher = kosher;
    }



    public String getFirstName() {
        return firstName;
    }



    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    public String getLastName() {
        return lastName;
    }



    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public String getEmail() {
        return email;
    }



    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }



    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }



    public List<String> getAllergies() {
        return allergies;
    }



    public void setAllergies(List allergies) {
        this.allergies = allergies;
    }



    public List<String> getTop5FavMeal() {
        return top5FavMeal;
    }



    public void setTop5FavMeal(List<String> top5FavMeal) {
        this.top5FavMeal = top5FavMeal;
    }



    public List<String> getTop10FavIngredients() {
        return top10FavIngredients;
    }



    public void setTop10FavIngredients(List<String> top10FavIngredients) {
        this.top10FavIngredients = top10FavIngredients;
    }



    public String getUserClassification() {
        return userClassification;
    }



    public void setUserClassification(String userClassification) {
        this.userClassification = userClassification;
    }



    public List<String> getDislikes() { return dislikes; }



    public void setDislikes(List<String> dislikes){ this.dislikes = dislikes; }



    @Override
    public boolean appendAllergies(String allergic) {
        return false;
    }



    @Override
    public void deleteUser() {

    }



    public RegularUser(Parcel in){
        readFromParcel(in);
    }



    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public RegularUser createFromParcel(Parcel in ) {
            return new RegularUser( in );
        }

        public RegularUser[] newArray(int size) {
            return new RegularUser[size];
        }
    };



    @Override
    public int describeContents() {
        return 0;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(yearOfBirth);
    }



    private void readFromParcel(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        yearOfBirth = in.readString();
    }
}
