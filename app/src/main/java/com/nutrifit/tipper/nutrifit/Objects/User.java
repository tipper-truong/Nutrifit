package com.nutrifit.tipper.nutrifit.Objects;

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
    private int caloriesToBurnPerDay;

    public User(String firstName, String lastName, String email, String password, String gender, String fitnessGoals, int caloriesToBurnPerDay) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.fitnessGoals = fitnessGoals;
        this.caloriesToBurnPerDay = caloriesToBurnPerDay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getCaloriesToBurnPerDay() {
        return caloriesToBurnPerDay;
    }

    public void setCaloriesToBurnPerDay(int caloriesToBurnPerDay) {
        this.caloriesToBurnPerDay = caloriesToBurnPerDay;
    }


}
