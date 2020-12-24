package com.example.myblog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import javax.security.auth.login.LoginException;

public class MainActivity extends AppCompatActivity {
        private RecyclerView mBlogList;
        private DatabaseReference dbBlog, dbUsers, LastDb;

        private ProgressDialog mProgress;
        private Query mQuery;
        //NOTIFICATION ----------------------------------------
        private int messageCount = 0;
        private static Uri alarmSound;
        private final long[] pattern = {100, 300, 300, 300};
        private NotificationManager mNotificationManager;
        String  __APPKEY = "SECONDAPP";
        private String lastIdReference;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            dbBlog = FirebaseDatabase.getInstance().getReference().child("Blogs");
            mQuery = dbBlog.orderByChild("date").startAt("01-01-2010 12:12:12");
            dbUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            ActionBar actionBar = getSupportActionBar();

            LastDb = FirebaseDatabase.getInstance().getReference().child("Last_post");
            LastDb.keepSynced(true);
            //progress
            mProgress = new ProgressDialog(this);

            mBlogList = (RecyclerView)findViewById(R.id.blog_list);
            mBlogList.setHasFixedSize(true);
            mBlogList.setLayoutManager(new LinearLayoutManager(this));
            //NOTIFICATION ----------------------------------------------------------------------------
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            LastDb.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    lastIdReference = snapshot.getValue().toString();
                    Log.i("TAG", snapshot.getValue().toString());
                    if(!__APPKEY.equals("MAINAPP")){
                        notif();
                        Log.i("data","dataadded");
                    }
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    private void notif() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MainActivity.this);

        mBuilder.setContentTitle("My Blog");
        mBuilder.setContentText("Ada berita baru...jangan ketinggalan");
        mBuilder.setTicker("My Blog - berita terbaru");
        mBuilder.setSmallIcon(R.drawable.ic_myapp);
        mBuilder.setAutoCancel(true);
//        mBuilder.setNumber(++messageCount);
        mBuilder.setSound(alarmSound);
        mBuilder.setVibrate(pattern);

        Intent i = new Intent(MainActivity.this, DetailActivity.class);
        i.putExtra("notificationId", 111);
        i.putExtra("message", lastIdReference);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DetailActivity.class);

        stackBuilder.addNextIntent(i);

        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);

        mNotificationManager.notify(111, mBuilder.build());
    }

    private void fireSearch(String search){
//        "\uf8ff");
        //Query fireQuery = dbBlog.orderByChild("search").startAt(search + "\uf8ff");
        Query fireQuery = dbBlog.orderByChild("search").startAt(search).endAt(search+"\uf88ff");

        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                fireQuery) {
            @Override
            protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                final String postKey = getRef(i).getKey();

                blogViewHolder.setTitle(blog.getTitle());
                blogViewHolder.setDescription(blog.getDescription());
                blogViewHolder.setDate(blog.getDate());
                blogViewHolder.setImage(getApplicationContext(),blog.getImage());
                //Onclick card
                blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                        intent.putExtra("blog_id",postKey);
                        startActivity(intent);
                    }
                });
            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);
    }

        @Override
        protected void onStart() {
            super.onStart();

            FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                    Blog.class,
                    R.layout.blog_row,
                    BlogViewHolder.class,
                    mQuery) {
                @Override
                protected void populateViewHolder(BlogViewHolder blogViewHolder, Blog blog, int i) {
                    final String postKey = getRef(i).getKey();

                    blogViewHolder.setTitle(blog.getTitle());
                    blogViewHolder.setDescription(blog.getDescription());
                    blogViewHolder.setDate(blog.getDate());
                    blogViewHolder.setImage(getApplicationContext(),blog.getImage());
                    //Onclick card
                    blogViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                            intent.putExtra("blog_id",postKey);
                            startActivity(intent);
                        }
                    });
                }
            };
            mBlogList.setAdapter(firebaseRecyclerAdapter);
        }
        public static class BlogViewHolder extends RecyclerView.ViewHolder{
            View mView;
            public BlogViewHolder(@NonNull View itemView) {
                super(itemView);
                mView = itemView;
            }

            public void setTitle(String title){
                TextView postTitle = (TextView)mView.findViewById(R.id.post_title);
                postTitle.setText(title);
            }

            public void setDescription(String desc){
                TextView postDesc = (TextView)mView.findViewById(R.id.post_text);
                postDesc.setText(desc);
            }
            public void setDate(String date){
                TextView textView3 = (TextView)mView.findViewById(R.id.textView3);
                textView3.setText(date);
            }

            public void setImage(Context ctx, String image){
                ImageView imageView = (ImageView)mView.findViewById(R.id.post_image);
                Picasso.with(ctx).load(image).into(imageView);
            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fireSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                fireSearch(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            //select actoin to button add
            if(item.getItemId() == R.id.action_tentang){
                startActivity(new Intent(MainActivity.this,AboutActivity.class));
            }
            return super.onOptionsItemSelected(item);
        }
    }