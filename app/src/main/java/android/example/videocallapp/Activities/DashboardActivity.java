package android.example.videocallapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.example.videocallapp.R;
import android.os.Bundle;

import me.ibrahimsn.lib.OnItemSelectedListener;

public class DashboardActivity extends AppCompatActivity {

    android.example.videocallapp.databinding.ActivityDashboardBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        binding = android.example.videocallapp.databinding.ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        FragmentTransaction groupcallTrans = getSupportFragmentManager().beginTransaction();
        groupcallTrans.replace(R.id.frameLayout , new VideocallFragment());
        groupcallTrans.commit();

        binding.bottomNav.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public boolean onItemSelect(int i) {
                FragmentTransaction Transaction = getSupportFragmentManager().beginTransaction();
                switch (i){
                    case 0:
                        Transaction.replace(R.id.frameLayout , new VideocallFragment());
                        break;
                    case 1:
                        Transaction.replace(R.id.frameLayout , new ProfileFragment());
                        break;
                    case 2:
                        Transaction.replace(R.id.frameLayout , new ChatFragment());
                        break;
                    case 3:
                        Transaction.replace(R.id.frameLayout , new GroupChatFragment());
                        break;
                }
                Transaction.commit();
                return false;
            }
       });

    }
}