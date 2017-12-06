package com.nutrifit.tipper.nutrifit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.gson.Gson;
import com.nutrifit.tipper.nutrifit.Model.User;

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Context pContext;
    private Button logOutButton;
    public static final String USER = "USER";
    private User user;
    private TextView name;
    private TextView email;
    private TextView gender;
    private TextView goal;
    private TextView foodCalories;
    private TextView exerciseCaloriesBurned;
    private TextView calPerDay;
    private TextView currCalIntake;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public static ProfileFragment newInstance()
    {
        ProfileFragment pFragment = new ProfileFragment();
        return pFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        pContext = profileView.getContext();

        user = getUserData();

        name = (TextView) profileView.findViewById(R.id.profileName);
        email = (TextView) profileView.findViewById(R.id.emailProfile);
        gender = (TextView) profileView.findViewById(R.id.genderProfile);
        goal = (TextView) profileView.findViewById(R.id.goalProfile);
        calPerDay = (TextView) profileView.findViewById(R.id.caloriesPerDay);
        currCalIntake = (TextView) profileView.findViewById(R.id.caloriesIntake);
        foodCalories = (TextView) profileView.findViewById(R.id.foodCalories);
        exerciseCaloriesBurned = (TextView) profileView.findViewById(R.id.exerciseCaloriesBurn);

        initializeUserProfileData();

        logOutButton = (Button) profileView.findViewById(R.id.logOutButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearSessionUser(); // clear session user
                LoginManager.getInstance().logOut(); // log out facebook
                Intent i = new Intent(getActivity(), SignInActivity.class);
                getActivity().startActivity(i);
            }
        });
        return profileView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void initializeUserProfileData()
    {
        name.setText(user.getFirstName() + " " + user.getLastName());
        email.setText(user.getEmail());
        gender.setText(user.getGender());
        goal.setText(user.getFitnessGoals());
        foodCalories.setText(String.valueOf(user.getFoodCalories()));
        exerciseCaloriesBurned.setText(String.valueOf(user.getExerciseCalories()));
        setProfileFitnessGoals(user.getGender(), user.getFitnessGoals());
        currCalIntake.setText(String.valueOf(user.getCaloriesToBurnPerDay()));
    }

    private void setProfileFitnessGoals(String gender, String selectedFitnessGoals)
    {

        if(gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Lose Weight")) {
            calPerDay.setText("1500");
        } else if (gender.equalsIgnoreCase("female") && selectedFitnessGoals.equals("Gain Weight")) {
            calPerDay.setText("2000");
        } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Lose Weight")) {
            calPerDay.setText("2000");
        } else if (gender.equalsIgnoreCase("male") && selectedFitnessGoals.equals("Gain Weight")) {
            calPerDay.setText("2500");
        }

    }

    private User getUserData()
    {
        SharedPreferences settings;
        settings = getActivity().getSharedPreferences(USER, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String userObj = settings.getString(USER, null);
        User retUser = gson.fromJson(userObj, User.class);
        return retUser;
    }

    private void clearSessionUser()
    {
        SharedPreferences preferences = getActivity().getSharedPreferences(USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            editor.clear();
            editor.commit();
        } catch(NullPointerException e) {
            // else it's a facebook login, don't clear session for user
            e.printStackTrace();
        }
    }
}
