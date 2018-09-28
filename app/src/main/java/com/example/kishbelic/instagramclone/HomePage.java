package com.example.kishbelic.instagramclone;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class HomePage extends AppCompatActivity {


    private FirebaseAuth FireAuth;
    private FirebaseUser FireUser;

    private DatabaseReference UsersRef,userImageRef;
    private StorageReference storeRef;
    private StorageReference ImageRef;




    ListView listView;
    ArrayList<String> UsersList;
    ArrayAdapter arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


        FireAuth = FirebaseAuth.getInstance();
        FireUser = FireAuth.getCurrentUser();

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        storeRef = FirebaseStorage.getInstance().getReference();



        listView = (ListView)findViewById(R.id.listView);
        UsersList = new ArrayList();



        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,UsersList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                Intent intent = new Intent(getApplicationContext(),PostActivity.class);

                intent.putExtra("userMail",listView.getItemAtPosition(i).toString());

                startActivity(intent);


            }
        });


        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UsersList.clear();

                if(dataSnapshot.exists()){


                    for (DataSnapshot child :dataSnapshot.getChildren())
                    {

                        if (!FireUser.getUid().equals(child.getKey())){

                            UsersList.add(child.child("mail").getValue().toString());
                           // Log.i("tagFire",child.getKey()+"");
                        }


                    }

                    arrayAdapter.notifyDataSetChanged();
                }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.i("value","error ");
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.homepage_menu,menu);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId()==R.id.signout_Id){

            Log.i("tagFire","user "+FireAuth.getCurrentUser().getEmail());

            FireAuth.signOut();

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);

            return true;
        }

        if (item.getItemId()==R.id.imageId){


            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,2);
            
            return true;
        }

        return false;
    }

public void saveImage(Uri selectedImage){

        FireUser =FireAuth.getCurrentUser();

        final String randomName = UUID.randomUUID().toString();
        ImageRef = storeRef.child("images/"+ FireUser.getUid()+"/"+randomName);

        userImageRef = UsersRef.child(FireUser.getUid()).child("images");


    final ProgressDialog progressDialog = new ProgressDialog(this);
    progressDialog.setTitle("Uploading...");
    progressDialog.show();
    progressDialog.setCanceledOnTouchOutside(false);

        ImageRef.putFile(selectedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                userImageRef.child(randomName).setValue("image");
                Log.i("tagfire","upload Success ");
                progressDialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Log.i("tagfire","failed upload");
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double val = taskSnapshot.getBytesTransferred();
                progressDialog.setMessage("uploaded "+(int)val+"%");
            }
        });



}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==2 && resultCode==RESULT_OK && data!=null){

            Uri selectedImage = data.getData();
            saveImage(selectedImage);

        }


    }
}
