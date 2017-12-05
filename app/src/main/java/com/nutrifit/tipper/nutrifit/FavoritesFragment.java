package com.nutrifit.tipper.nutrifit;
        import android.content.ContentResolver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.annotation.Nullable;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentTransaction;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.Toolbar;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.gson.Gson;
        import com.nutrifit.tipper.nutrifit.Database.DatabaseHandler;
        import com.nutrifit.tipper.nutrifit.Model.Recipe;
        import com.nutrifit.tipper.nutrifit.Model.User;
        import com.squareup.picasso.Picasso;

        import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    ArrayList<Recipe> listitems = new ArrayList<Recipe>();
    RecyclerView MyRecyclerView;
    private DatabaseHandler db;
    public static final String USER = "USER";
    public static final String RECIPE = "RECIPE";
    private User user;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHandler(getActivity());
        user = getUserData();
        initializeList();
        getActivity().setTitle("Search for Healthy Recipes");
    }

    public static FavoritesFragment newInstance()
    {
        FavoritesFragment fFragment = new FavoritesFragment();
        return fFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_card, container, false);
        MyRecyclerView = (RecyclerView) view.findViewById(R.id.cardView);
        MyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager MyLayoutManager = new LinearLayoutManager(getActivity());
        MyLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        if (listitems.size() > 0 & MyRecyclerView != null) {
            MyRecyclerView.setAdapter(new MyAdapter(listitems));
        }
        MyRecyclerView.setLayoutManager(MyLayoutManager);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        private ArrayList<Recipe> list;

        public MyAdapter(ArrayList<Recipe> Data) {
            list = Data;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            // create a new view
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycle_items, parent, false);
            MyViewHolder holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            holder.titleTextView.setText(list.get(position).getRecipeName());
            Picasso.with(getActivity()).load(list.get(position).getImageUrl()).into(holder.coverImageView);
            holder.likeImageView.setTag(R.drawable.ic_liked);

            holder.likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    int id = (int)holder.likeImageView.getTag();
                    if( id == R.drawable.ic_liked){

                        holder.likeImageView.setTag(R.drawable.ic_like);
                        holder.likeImageView.setImageResource(R.drawable.ic_like);
                        db.deleteRecipe(list.get(position));
                        Toast.makeText(getActivity(),holder.titleTextView.getText()+" removed from favorites",Toast.LENGTH_SHORT).show();

                    } else{

                        holder.likeImageView.setTag(R.drawable.ic_liked);
                        holder.likeImageView.setImageResource(R.drawable.ic_liked);
                        db.addRecipe(list.get(position));
                        Toast.makeText(getActivity(),holder.titleTextView.getText()+" added to favorites",Toast.LENGTH_SHORT).show();


                    }

                }
            });


            holder.infoImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveRecipeData(getActivity(), list.get(position));
                    Fragment selectedFragment = RecipeDetailsFragment.newInstance();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer, selectedFragment);
                    transaction.commit();
                    /*Intent i = new Intent(getActivity(), RecipeDetailsActivity.class);
                    getActivity().startActivity(i);*/

                }
            });



        }

       @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public ImageView coverImageView;
        public ImageView likeImageView;
        public ImageView infoImageView;

        public MyViewHolder(View v) {
            super(v);
            titleTextView = (TextView) v.findViewById(R.id.titleTextView);
            coverImageView = (ImageView) v.findViewById(R.id.coverImageView);
            likeImageView = (ImageView) v.findViewById(R.id.likeImageView);
            infoImageView = (ImageView) v.findViewById(R.id.infoImageView);
        }
    }

    private void saveRecipeData(Context context, Recipe recipe)
    {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = context.getSharedPreferences(RECIPE, Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String recipeObj = gson.toJson(recipe);

        editor.putString(RECIPE, recipeObj);
        editor.commit();
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

    public void initializeList() {
        listitems.clear();
        try {
            listitems = db.getAllRecipes(user.getId());
        } catch (NullPointerException e) {
            Log.v("Error", "User hasn't added favorite recipes yet");
            e.printStackTrace();
        }

    }
}
