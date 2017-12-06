package com.nutrifit.tipper.nutrifit.Model;

/**
 * Created by tipper on 11/24/17.
 */

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String gender;
    private String fitnessGoals;
    private float caloriesToBurnPerDay;
    private float foodCalories;
    private float exerciseCalories;

    public User(String firstName, String lastName, String email, String password, String gender, String fitnessGoals, float caloriesToBurnPerDay, float foodCalories, float exerciseCalories) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.fitnessGoals = fitnessGoals;
        this.caloriesToBurnPerDay = caloriesToBurnPerDay;
        this.foodCalories = foodCalories;
        this.exerciseCalories = exerciseCalories;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCaloriesToBurnPerDay(float caloriesToBurnPerDay) {
        this.caloriesToBurnPerDay = caloriesToBurnPerDay;
    }

    public float getFoodCalories() {
        return foodCalories;
    }

    public void setFoodCalories(float foodCalories) {
        this.foodCalories = foodCalories;
    }

    public float getExerciseCalories() {
        return exerciseCalories;
    }

    public void setExerciseCalories(float exerciseCalories) {
        this.exerciseCalories = exerciseCalories;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFitnessGoals() {
        return fitnessGoals;
    }

    public void setFitnessGoals(String fitnessGoals) {
        this.fitnessGoals = fitnessGoals;
    }

    public float getCaloriesToBurnPerDay() {
        return caloriesToBurnPerDay;
    }

    public void setCaloriesToBurnPerDay(int caloriesToBurnPerDay) {
        this.caloriesToBurnPerDay = caloriesToBurnPerDay;
    }


}
