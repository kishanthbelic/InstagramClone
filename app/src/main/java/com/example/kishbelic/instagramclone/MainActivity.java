package com.example.kishbelic.instagramclone;

import android.content.Context;
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

public class MainActivity extends AppCompatActivity {

    EditText username_id, pass_id;
    Button login_id;

    TextView changeModeText;

    private FirebaseAuth FireAuth;
    private FirebaseUser FireUser;

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
        FireUser = FireAuth.getCurrentUser();


        //temporary
        if (FireUser!=null){
            FireAuth.signOut();
        }

        //changemodeText function
         changeModeText= (TextView)findViewById(R.id.changeModeText);
        changeMode(changeModeText);







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

                    Toast.makeText(MainActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                        Log.i("tagFire","success man");

                }
                else
                {
                    Log.i("tagFire","failed  man");
                }

            }
        });


        }


        //for sign in method
        if (login_id.getText().toString().equals("Sign In")) {

            Log.i("tagFire","Sign In clicked");

            if (FireUser == null) {
                FireAuth.signInWithEmailAndPassword(username_id.getText().toString(), pass_id.getText().toString()).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FireUser = FireAuth.getCurrentUser();


                            Log.i("tagFire", "User " + FireUser.getEmail() + "Logged in successfully");
                            Toast.makeText(MainActivity.this, "User " + FireUser.getEmail() + "Logged in successfully", Toast.LENGTH_SHORT).show();

                            if (FireUser.isEmailVerified()) {
                                Log.i("tagFire", "Email verified");
                            }

                        } else {

                            Toast.makeText(MainActivity.this, "Login failed check your username and password", Toast.LENGTH_SHORT).show();
                            Log.i("tagFire", "Login failed check your username and password");
                        }


                    }
                });
            } else {
                Log.i("tagFire", "User " + FireUser.getEmail() + " Logged in already");
            }
        }

    }
}
