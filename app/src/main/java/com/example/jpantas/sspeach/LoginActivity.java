package com.example.jpantas.sspeach;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import MAIN_CLASSES.User;

public class LoginActivity extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    ArrayList<String> Friends;
    private FirebaseAuth mAuth;
    String name, email, uid;
    Uri photourl;
    DatabaseReference mRef;
    User DBuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        loginButton = findViewById(R.id.loginfacebook);
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        DBuser = new User();

        Intent intent = getIntent();

        //check if user is already logged in
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        //after logging out
        else if (TextUtils.equals(intent.getStringExtra("code"), "logout")) {
            loginButton.performClick();
        }
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                String text = "Logged in Successfully ";
                Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Failed Login", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Log.v("NAMASTE", error.getCause().toString());
                Toast.makeText(getApplicationContext(), "Facebook Exception", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("NAMASTE", "handleFacebookAccessToken:" + token.getToken());

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("NAMASTE", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Name, email address, and profile photo Url
                            name = user.getDisplayName();
                            email = user.getEmail();
                            uid = user.getUid();
                            photourl = user.getPhotoUrl();

                            Log.d("NAMASTE photourl", photourl.toString());
                            Log.d("NAMASTE name", name);
                            Log.d("NAMASTE email", email);
                            Log.d("NAMASTE", name + " " + email + " " + email.split("@")[0]);

                            //Log.d("NAMASTE uid", mRef.child(uid).toString() + " 1: " + mRef.child(uid).getKey() + " 2: " + mRef.child(uid).getRef());

                            if (user != null) {
                                //if user already exists in firebase DB
                                mRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            //TODO remove this part eventually
                                            Log.d("NAMASTE photourl", photourl.toString()+"?height=100&width=100");
                                            DBuser.setEmail(email);
                                            DBuser.setUri(photourl.toString()+"?height=100&width=100");
                                            DBuser.setName(name);
                                            DBuser.setDevice_token(FirebaseInstanceId.getInstance().getToken().toString());
                                            mRef.child(uid).setValue(DBuser);
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            //Create user in firebase database
                                            DBuser.setEmail(email);
                                            DBuser.setUri(photourl.toString()+"?height=100&width=100");
                                            DBuser.setName(name);
                                            DBuser.setDevice_token(FirebaseInstanceId.getInstance().getToken().toString());
                                            mRef.child(uid).setValue(DBuser);

                                            // The user's ID, unique to the Firebase project. Do NOT use this value to
                                            // authenticate with your backend server, if you have one. Use
                                            // FirebaseUser.getIdToken() instead.
                                            Intent intent = new Intent(LoginActivity.this, InviteActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("NAMASTE", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

}
