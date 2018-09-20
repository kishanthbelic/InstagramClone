package com.example.kishbelic.instagramclone;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText username_id, pass_id;
    Button login_id;

    private FirebaseAuth FireAuth;
    private FirebaseUser FireUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username_id = (EditText) findViewById(R.id.username_id);
        pass_id = (EditText) findViewById(R.id.pass_id);
        login_id = (Button) findViewById(R.id.login_id);


        FireAuth = FirebaseAuth.getInstance();
        FireUser = FireAuth.getCurrentUser();


        if (FireUser!=null){
            FireAuth.signOut();
        }


        /*FireAuth.createUserWithEmailAndPassword("kishanthprab@gmail.com","baby123").addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {

                        Log.i("Fire","success man");

                }
                else
                {
                    Log.i("Fire","failed  man");
                }

            }
        });*/










    }



    public void signInClicked(View view){

        if (FireUser == null) {
            FireAuth.signInWithEmailAndPassword(username_id.getText().toString(), pass_id.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {

                        FireUser = FireAuth.getCurrentUser();


                        Log.i("tagFire", "User "+FireUser.getEmail() +"Logged in successfully");

                        if (FireUser.isEmailVerified()){
                            Log.i("tagFire", "Email verified");
                        }

                    }
                    else {

                        Log.i("tagFire", "Login failed check your username and password");
                    }



                }
            });
        }
        else {
            Log.i("tagFire", "User " + FireUser.getEmail() +" Logged in already");
        }


    }
}
