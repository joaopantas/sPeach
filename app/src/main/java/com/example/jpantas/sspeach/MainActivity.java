package com.example.jpantas.sspeach;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.design.widget.BottomNavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import Fragment.AmigosFragment;
import Fragment.ChatsFragment;
import Fragment.GroupsFragment;
import Fragment.InviteAmigosFragment;
import Fragment.PublicChatsFragment;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    //This is our viewPager
    public static ViewPager viewPager;

    //Fragments
    GroupsFragment groupsFragment;
    AmigosFragment amigosFragment;
    ChatsFragment chatsFragment;
    InviteAmigosFragment inviteAmigosFragment;
    PublicChatsFragment publicChatsFragment;
    MenuItem prevMenuItem;

    private FirebaseAuth mAuth;
    Intent prevIntent;
    String name;
    Button logout;
    public static ViewPagerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        Log.d("NAMASTE", mAuth.getUid().toString());


        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //Initializing the bottomNavigationView
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_public_chats:
                                viewPager.setCurrentItem(0);
                                break;
                            case R.id.action_chats:
                                viewPager.setCurrentItem(1);
                                break;
                            case R.id.action_groups:
                                viewPager.setCurrentItem(2);
                                break;
                            case R.id.action_friends:
                                /* Toast.makeText(MainActivity.this, "You need to select two or more friends to start a group", Toast.LENGTH_LONG).show(); */
                                viewPager.setCurrentItem(3);
                                break;
                            case R.id.action_addfriend:
                                viewPager.setCurrentItem(4);

                                break;
                        }
                        return false;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

       logout= findViewById(R.id.logout);
       logout.setEnabled(true);
       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               FirebaseAuth.getInstance().signOut();
               Intent intent = new Intent(MainActivity.this, LoginActivity.class);
               intent.putExtra("code","logout");
               startActivity(intent);
               finish();
           }
       });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        groupsFragment = new GroupsFragment();
        amigosFragment = new AmigosFragment();
        chatsFragment = new ChatsFragment();
        inviteAmigosFragment = new InviteAmigosFragment();
        publicChatsFragment = new PublicChatsFragment();
        adapter.addFragment(publicChatsFragment);
        adapter.addFragment(chatsFragment);
        adapter.addFragment(groupsFragment);
        adapter.addFragment(amigosFragment);
        adapter.addFragment(inviteAmigosFragment);
        viewPager.setAdapter(adapter);
    }
}
