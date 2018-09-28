package com.example.kishbelic.instagramclone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PostActivity extends AppCompatActivity {

    private FirebaseUser FireUser;
    private DatabaseReference UsersRef;
    private StorageReference StoreRef,ImagesRef;

    ArrayList<String> imagesArray;

    ImageView imageView;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        final String userMail = getIntent().getStringExtra("userMail");

        imagesArray = new ArrayList<String>();


        //firebase
        FireUser = FirebaseAuth.getInstance().getCurrentUser();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        StoreRef = FirebaseStorage.getInstance().getReference();

        linearLayout = findViewById(R.id.LinearLayoutId);

        setTitle((CharSequence) userMail+"'s feed");


        imageView = new ImageView(getApplicationContext());

        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        imageView.setPadding(0,5,0,0);



        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                imagesArray.clear();
                for (DataSnapshot userids : dataSnapshot.getChildren()){

                    if (userids.child("mail").getValue().equals(userMail)){

                        //imagesArray.add(userids.child("images"))

                        Log.i("tagFire",""+userids.child("images").getChildren());

                        if (userids.hasChild("images")){

                            for (DataSnapshot imagesSnap : userids.child("images").getChildren()){
                                imagesArray.add(imagesSnap.getKey());

                                Log.i("tagFire",""+imagesSnap.getKey());
                            }
                            retrieveImages(userids.getKey(),imagesArray.get(0));
                        }

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("tagFire","error "+databaseError);
            }
        });





    }

    public void retrieveImages(String userId,String imageName)  {

        ImagesRef = StoreRef.child("/images/"+userId+"/"+imageName);

        Log.i("tagFire","userID " +userId);
        Log.i("tagFire","imagename "+imageName);


        final long ONE_MEGABYTE = 1024 * 1024;
        ImagesRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                //Uri imageUri = Uri.parse(bytes.toString());

                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
               imageView.setImageBitmap(bitmap);
                linearLayout.addView(imageView);
                //linearLayout.addView(imageView);
                Log.i("tagFire","Success ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("tagFire","error "+exception);
            }
        });


    }

}
