package android.example.videocallapp.Activities;

import android.example.videocallapp.R;
import android.example.videocallapp.Models.User;
import android.example.videocallapp.Adapters.UsersAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private FirebaseFirestore database;
    private FirebaseDatabase mDatabase;
    private ArrayList<User>users;
    private UsersAdapter usersAdapter;
    private RecyclerView recyclerView;
    private MenuItem menuItem;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflating the layout
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        //Getting reference  of layout varibles
        recyclerView  = view.findViewById(R.id.recyclerView);

        //Getting reference  of firebase varibles
        database = FirebaseFirestore.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        //Creating an array list object
        users = new ArrayList<>();

        //Creating an custom adapter object
        usersAdapter = new UsersAdapter(container.getContext() , users);
        //setting the adapter
        recyclerView.setAdapter(usersAdapter);

        //Here we get reference to database and then to specific child "Users"
        mDatabase.getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                users.clear();
                for(DataSnapshot snapshot1 : snapshot.getChildren()){
                    User user = snapshot1.getValue(User.class);
                    if(!user.getUid().equals(FirebaseAuth.getInstance().getUid()))
                        //Adding users to a list and then using them to populate with help of adapter
                        users.add(user);
                }
                //Notifying the adapter to populate the data
                usersAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}