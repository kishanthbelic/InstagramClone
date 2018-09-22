package com.example.kishbelic.instagramclone;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.UpdateLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText username_id, pass_id;
    Button login_id;

    TextView changeModeText;

    private FirebaseAuth FireAuth;
    private FirebaseUser FireUser;

    private DatabaseReference UserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //login page contents
        username_id = (EditText) findViewById(R.id.username_id);
        pass_id = (EditText) findViewById(R.id.pass_id);
        login_id = (Button) findViewById(R.id.login_id);


        //firebase
        FireAuth = FirebaseAuth.getInstance();
        //FireUser = FireAuth.getCurrentUser();

        //changemodeText function
         changeModeText= (TextView)findViewById(R.id.changeModeText);
        changeMode(changeModeText);

        FireUser = FireAuth.getCurrentUser();

        if (FireUser!=null){
            GoToHomeActivity();
        }


        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");

    }



    public void changeMode(View view) {

        changeModeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (login_id.getText().toString().equals("Sign In"))
                {
                    login_id.setText("Sign Up");
                    changeModeText.setText("or Signin");
                }
                else {

                    login_id.setText("Sign In");
                    changeModeText.setText("or Signup");
                }


            }
        });

    }


    public void signInClicked(View view){




        if (username_id.getText().toString().equals("") || pass_id.getText().toString().equals("")){

            Toast.makeText(this, "Username and password are required", Toast.LENGTH_SHORT).show();
            return;
        }


        //for sign up method
        if (login_id.getText().toString().equals("Sign Up")) {

            Log.i("tagFire","Sign up clicked");
            Log.i("tagFire",pass_id.getText().length()+"");

            //validation

            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
            if (!username_id.getText().toString().matches(emailPattern))
            {
              Toast.makeText(getApplicationContext(),"Invalid email address", Toast.LENGTH_SHORT).show();
              return;
            }

            if (pass_id.getText().length() < 6)
            {
                Toast.makeText(getApplicationContext(),"Password must be 6 characters long", Toast.LENGTH_SHORT).show();
                return;
            }


            FireAuth.createUserWithEmailAndPassword(username_id.getText().toString(),pass_id.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful())
                {

                    createUserInformation();

                    FireUser = null;
                    FirebaseAuth.getInstance().signOut();
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Failed to create account Sorry bro", Toast.LENGTH_SHORT).show();
                    Log.i("tagFire","Failed to create account Sorry bro");
                }

            }
        });


        }


        //for sign in method
        if (login_id.getText().toString().equals("Sign In")) {

            Log.i("tagFire","Sign In clicked");

            FireUser = FirebaseAuth.getInstance().getCurrentUser();


            if (FireUser == null) {
                FireAuth.signInWithEmailAndPassword(username_id.getText().toString(), pass_id.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FireUser = FireAuth.getCurrentUser();


                            Log.i("tagFire", "User " + FireUser.getEmail() + " has Logged in successfully");
                            GoToHomeActivity();
                            Toast.makeText(getApplicationContext(), "User " + FireUser.getEmail() + "Logged in successfully", Toast.LENGTH_LONG).show();


                            if (FireUser.isEmailVerified()) {
                                Log.i("tagFire", "Email verified");
                            }

                        } else {

                            Toast.makeText(MainActivity.this, "Login failed check your username and password", Toast.LENGTH_LONG).show();
                            Log.i("tagFire", "Login failed check your username and password");
                        }


                    }
                });
            } else {
                
                GoToHomeActivity();
                Log.i("tagFire", "User " + FireUser.getEmail() + " Logged in already");
            }
        }

    }


    public void createUserInformation(){

            FireUser = FireAuth.getCurrentUser();
            HashMap UserMap = new HashMap<>();

            UserMap.put("mail",FireUser.getEmail());


            UserRef.child(FireUser.getUid()).updateChildren(UserMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if (task.isSuccessful())
                    {

                        Toast.makeText(MainActivity.this, "Bro , Your Account Created Successfully", Toast.LENGTH_LONG).show();
                        Log.i("tagFire","Bro , Your Account Created Successfully");


                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Failed to create account Sorry bro", Toast.LENGTH_SHORT).show();
                        Log.i("tagFire","Failed to create account Sorry bro");
                    }

                }

            });


    }

public void GoToHomeActivity(){
    Intent intent = new Intent(getApplicationContext(),HomePage.class);

    startActivity(intent);

}

}
