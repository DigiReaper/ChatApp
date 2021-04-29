package developer.adithya.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

import developer.adithya.chatapp.databinding.ActivityMainBinding;
import developer.adithya.chatapp.fragments.CallsFragment;
import developer.adithya.chatapp.fragments.ChatsFragment;
import developer.adithya.chatapp.fragments.UsersFragment;


public class    MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fUser;
    private DatabaseReference dbUsers;
    private ViewPager2 ViewPager;
    private TabLayout TabLayout ;
    private String[] titles = new String[]{"Chats", "Users" , "Calls"};
    private ActivityMainBinding binding;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        dbUsers = FirebaseDatabase.getInstance().getReference("Users");
        ViewPager  = findViewById(R.id.viewpager);
        TabLayout = findViewById(R.id.tabLayout);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        init();


    }


    private void init() {
        // removing toolbar elevation
        getSupportActionBar().setElevation(0);

        binding.viewpager.setAdapter(new ViewPagerFragmentAdapter(this));

        // attaching tab mediator
        new TabLayoutMediator(binding.tabLayout, binding.viewpager,
                (tab, position) -> tab.setText(titles[position])).attach();
    }

    private class ViewPagerFragmentAdapter extends FragmentStateAdapter {

        public ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new ChatsFragment();
                case 1:
                    return new UsersFragment();
                case 2:
                    return new CallsFragment();
            }
            return new ChatsFragment();
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout :{
                firebaseAuth.signOut();
                startActivity(new Intent(MainActivity.this , RegisterActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                );
                finish();
                return true;
            }
            case R.id.Profile :{
                startActivity(new Intent(MainActivity.this , ProfileActivity.class));
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckStatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        CheckStatus("offline");
    }
    private void CheckStatus(String status){
        HashMap<String , Object> map = new HashMap<>();
        map.put("status" , status);

        dbUsers.child(fUser.getUid()).updateChildren(map);
    }


}