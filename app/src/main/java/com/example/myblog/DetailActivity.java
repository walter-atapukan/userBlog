package com.example.myblog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    //to save postKey value
    private  String postKey = null, postKey1;
    //db reference
    private DatabaseReference dbBlog, dbBlogID;

    TextView detailDescription, updateTitle, teksPenulis;
    ImageView imageView;
    private ProgressDialog mProgress;
    private String log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        //progress
        mProgress = new ProgressDialog(this);
        //Get string
        postKey1 = getIntent().getExtras().getString("blog_id");
        postKey = receiveData();
        if (postKey == null){
            postKey = postKey1;
        }

        //get compenent with id
        detailDescription = (TextView)findViewById(R.id.detailDescription);
        imageView = (ImageView)findViewById(R.id.detailImage) ;
        teksPenulis = (TextView)findViewById(R.id.textPenulis);
        //set text to scrollable
        detailDescription.setMovementMethod(new ScrollingMovementMethod());

        dbBlog = FirebaseDatabase.getInstance().getReference().child("Blogs");
          if (postKey1 == null){
              dbBlogID = dbBlog.child(postKey);
          }else{
              dbBlogID = dbBlog.child(postKey1);
          }

//        Linkify.addLinks(receiveData(),Linkify.ALL);
        dbBlog.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(postKey)) {
                    dbBlogID.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                            String postTitle = snapshot.child("title").getValue().toString();
                                String postDesc = snapshot.child("description").getValue().toString();
                                String postImage = snapshot.child("image").getValue().toString();
                                String tanggal = snapshot.child("date").getValue().toString();
                                String penulis = snapshot.child("penulis").getValue().toString();
                                setTitle(postTitle);
                                detailDescription.setText(postDesc);
                                teksPenulis.setText(penulis+ ", "+tanggal);
                                Picasso.with(DetailActivity.this).load(postImage).into(imageView);
                            }   }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else{
                    Toast.makeText(DetailActivity.this, "Blog tidak ada", Toast.LENGTH_LONG).show();

                    Intent loginIntent = new Intent(DetailActivity.this, MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String receiveData() {
        String message = "";
        int id = 0;
        Bundle extras = getIntent().getExtras();
        if(extras == null){
            message = "error";
        }else{
            id = extras.getInt("notificationId");
            message = extras.getString("message");
        }
        return message;
    }

}