package com.example.todo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class todo_AddUsername extends AppCompatActivity {

    private ImageView todoIconStatic;
    private EditText username;
    private TextView btnDone;
    private TextView btn_sign_out;
    private static final int MY_REQUEST_CODE=7117;
    List<AuthUI.IdpConfig> providers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_username);

        btn_sign_out = findViewById(R.id.signout);
        btn_sign_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(todo_AddUsername.this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                btn_sign_out.setEnabled(false);
                                showSignInOptions();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(todo_AddUsername.this,""+e.getMessage(),Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        // init provider
        providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(), // Email Builder
                new AuthUI.IdpConfig.PhoneBuilder().build(), // Phone Builder
                //Facebook Builder
                new AuthUI.IdpConfig.GoogleBuilder().build() //Google Builder
        );
        showSignInOptions();

        isUserAlreadyRegistered();

        assignUIcomponents();

        //elements slide in from off-screen
        introAnimation();

        buttonListeners();
    }

    private void showSignInOptions() {
        startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(providers).setTheme(R.style.globalTheme).setLogo(R.drawable.todo_intro_icon)
                        .build(),MY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MY_REQUEST_CODE)
        {

            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                //show email on toast
                Toast.makeText(this,""+user.getEmail(),Toast.LENGTH_SHORT).show();
                btn_sign_out.setEnabled(true);
            }
            else
            {
                Toast.makeText(this,""+response.getError().getMessage(),Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void isUserAlreadyRegistered(){

        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String username = "";
        if(!prefs.getString("username", "").equals("")){
            Intent intent = new Intent(todo_AddUsername.this, todo_HomePage.class);
            startActivity(intent);
            finish();
        }
    }

    private void assignUIcomponents() {
        todoIconStatic = findViewById(R.id.add_username_todo_icon_static);
        username = findViewById(R.id.add_username_input);
        btnDone = findViewById(R.id.add_username_done_btn);
    }

    private void introAnimation() {
        Animation slide_in_from_top = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_top);
        Animation slide_in_from_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_left);
        Animation slide_in_from_bottom = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_from_bottom);

        todoIconStatic.startAnimation(slide_in_from_top);
        btnDone.startAnimation(slide_in_from_bottom);
        username.startAnimation(slide_in_from_left);
    }

    private void buttonListeners() {
        //button(textView) to submit username pressed.
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!username.getText().toString().equals("")) {
                    SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                    prefs.edit().putString("username", username.getText().toString().trim()).apply();
                    Intent intent = new Intent(todo_AddUsername.this, todo_HomePage.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        //do nothing
        finish();
    }

}