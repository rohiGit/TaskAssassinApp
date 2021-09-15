package com.example.taskassassin;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.swipe.SwipeLayout;
import com.example.taskassassin.Model.Users;
import com.example.taskassassin.ViewHolder.LeaderBoardViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseRecyclerOptions<Users> options;
    private DatabaseReference mUserRef;
    private TextView mUsernameTxt;
    private LinearLayout transparentLayout, leftSlideLayout, rankingSwipeDown;
    private TextView mSwipeUpTxt, mainTxt;
    private CardView focusCard;
    private ImageView swipeImageView;
    private RecyclerView.LayoutManager layoutManager;
    private CardView todoCard;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private Animation fadeIn, fadeOut;
    private int height;
    private FirebaseRecyclerAdapter<Users, LeaderBoardViewHolder> adapter;
    private SwipeLayout swipeLayout,settingSwipe;
    private boolean notCollapsed = true;
    private ImageView settingIcon;
    private VideoView charAnim;
    private boolean isOpenSwipe = false;
    private LinearLayout bronzeLinear, silverLinear, goldLinear;
    private CircleImageView bronzeProfile, silverProfile, goldProfile, userProfile;
    private TextView bronzeScore, silverScore, goldScore, bronzeName, silverName, goldName;
    private RecyclerView rankList;
    private DatabaseReference thumbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

            try {
                mUserRef = FirebaseDatabase.getInstance().getReference();
                mUserRef.keepSynced(true);
                thumbReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid());
                fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                swipeLayout = findViewById(R.id.dragLayout);
                fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                mUsernameTxt = findViewById(R.id.userName_txt);
                focusCard = findViewById(R.id.focus_card);
                charAnim = findViewById(R.id.character_anim);
                mainTxt = findViewById(R.id.maintxt);
                settingIcon = findViewById(R.id.setting_icon);
                transparentLayout = findViewById(R.id.transparent_layout);
                leftSlideLayout = findViewById(R.id.bottom_wrapper);
                todoCard = findViewById(R.id.todo_card);
                userProfile = findViewById(R.id.user_profile_img);
                settingSwipe = findViewById(R.id.setting_swipe_layout);
                swipeImageView = findViewById(R.id.swipe_image);
                slidingUpPanelLayout = findViewById(R.id.sliding_layout);
                mSwipeUpTxt = findViewById(R.id.swipe_up_txt);

                thumbReference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        System.out.println("e2");
                        displayImage();
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                startVideo();
                initRank();
                setRankList();
                System.out.println("e3");
                displayImage();
                settingSwipe.setShowMode(SwipeLayout.ShowMode.PullOut);
                settingSwipe.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(settingIcon, "alpha", 0, 1f);
                        fadeIn.setDuration(800);
                        fadeIn.start();
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {

                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {
                        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(settingIcon, "alpha", 1f, 0f);
                        fadeOut.setDuration(500);
                        fadeOut.start();
                    }

                    @Override
                    public void onClose(SwipeLayout layout) {

                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                    }
                });

                settingIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent settingIntent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(settingIntent);
                    }
                });

                userProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isOpenSwipe) {
                            settingSwipe.open();
                            isOpenSwipe = true;
                        } else {
                            settingSwipe.close();
                            isOpenSwipe = false;
                        }
                    }
                });


                ViewGroup.LayoutParams layoutParams = transparentLayout.getLayoutParams();
                height = layoutParams.height;

                //        mSwipeUp.setOnTouchListener(new OnSwipeTouchListner(HomeActivity.this){
                //            public void onSwipeTop() {
                //
                //                Intent characterIntent = new Intent(HomeActivity.this, CharacterActivity.class);
                //                //characterIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                //                startActivity(characterIntent);
                //                overridePendingTransition(R.anim.slide_up, R.anim.fix_anim);
                //            }
                //        });

                swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            swipeLayout.close();
                            System.out.println(1);
                        }
                        slidingUpPanelLayout.setDragView(null);
                        leftSlideLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                System.out.println(4);
                            }
                        });
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            swipeLayout.close();
                            System.out.println(2);
                        }
                        slidingUpPanelLayout.setDragView(rankingSwipeDown);
                        leftSlideLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                System.out.println(5);
                            }
                        });
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {
                        slidingUpPanelLayout.setDragView(null);
                        System.out.println("close start");
                        slidingUpPanelLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    System.out.println(6);
                                }
                            }
                        });
                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        System.out.println("on close");
                        slidingUpPanelLayout.setDragView(R.id.dragLayout);
                        System.out.println(13);
                        slidingUpPanelLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                System.out.println("Oye4");
                                if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
                                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                                    System.out.println(7);
                                }
                            }
                        });
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                    }
                });
//            slidingUpPanelLayout.getChildAt(1).setOnClickListener(null);

                slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
                    @Override
                    public void onPanelSlide(View panel, float slideOffset) {
                        transparentLayout.setAlpha(1f - slideOffset);
                    }

                    @Override
                    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                        if (newState == SlidingUpPanelLayout.PanelState.EXPANDED && swipeLayout.getOpenStatus() == SwipeLayout.Status.Close) {
                            System.out.println(8);
                            setRankList();
                            slidingUpPanelLayout.setDragView(swipeLayout);
                            System.out.println(10);
                            System.out.println(transparentLayout.getLayoutParams().height);
                        }

                        if (newState == SlidingUpPanelLayout.PanelState.DRAGGING) {

                            System.out.println(transparentLayout.getLayoutParams().height);
                        }
                        if (newState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                            slidingUpPanelLayout.setDragView(swipeLayout);
                            swipeLayout.close();
                            System.out.println(3);
                            notCollapsed = true;
                        }
                    }
                });


                focusCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent focusIntent = new Intent(HomeActivity.this, FocusActivity.class);
                        //focusIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(focusIntent);
                    }
                });

                todoCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent todoIntent = new Intent(HomeActivity.this, TodoActivity.class);
                        //todoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(todoIntent);
                    }
                });
            }catch (Exception e){
                mAuth.signOut();
                Intent signInIntent = new Intent(HomeActivity.this, SignInActivity.class);
                //signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signInIntent);
            }
        }

    private void startVideo() {
        String path = "android.resource://" + getPackageName() + "/" + R.raw.head_twist;
        Uri uri = Uri.parse(path);
        charAnim.setVideoURI(uri);
        charAnim.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                charAnim.start();
            }
        });

        

    }

    private void displayImage() {
        try {

            thumbReference.child("thumb_image").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String url = dataSnapshot.getValue().toString();

                        if (!url.equals("default")) {
                            Glide.with(HomeActivity.this).load(url)
                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                    .into(userProfile);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
            System.out.println("display picture exception");
            mAuth.signOut();
            Intent signInIntent = new Intent(HomeActivity.this, SignInActivity.class);
            //signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signInIntent);
        }

    }

    private void initRank() {
        rankList = findViewById(R.id.ranking_list);
        bronzeName = findViewById(R.id.bronze_name);
        silverName = findViewById(R.id.silver_name);
        goldName = findViewById(R.id.gold_name);
        bronzeLinear = findViewById(R.id.linear_bronze);
        bronzeProfile = findViewById(R.id.bronze_profile);
        bronzeScore = findViewById(R.id.bronze_score);
        rankingSwipeDown = findViewById(R.id.ranking_swipe_down);
        silverLinear = findViewById(R.id.linear_silver);
        silverProfile = findViewById(R.id.silver_profile);
        silverScore = findViewById(R.id.silver_score);
        goldLinear = findViewById(R.id.linear_gold);
        goldProfile = findViewById(R.id.gold_profile);
        goldScore = findViewById(R.id.gold_score);
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        rankList.setLayoutManager(layoutManager);
        rankList.setHasFixedSize(true);

    }

    private void setRankList() {
        System.out.println("Entered func");
        Query query = mUserRef.child("Users").orderByChild("score");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("This is count " + dataSnapshot.getChildrenCount());
                if (dataSnapshot.getChildrenCount() <= 3){
                    System.out.println("Entered rank");

                        if (dataSnapshot.getChildrenCount() == 1){
                            for (DataSnapshot details: dataSnapshot.getChildren()){
                                Users users = details.getValue(Users.class);
                                String name = users.getName();
                                long score = users.getScore();
                                String url = users.getThumb_image();

                                if (!url.equals("default")){
                                    Glide.with(HomeActivity.this).load(url)
                                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                                            .into(goldProfile);
                                }
                                goldLinear.setVisibility(View.VISIBLE);
                                goldScore.setText(""+score);
                                goldName.setText(name);
                                System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                        }
                    }else if (dataSnapshot.getChildrenCount() == 2){
                            int rep = 0;
                            for (DataSnapshot details: dataSnapshot.getChildren()){
                                if (rep == 0){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(silverProfile);
                                    }
                                    silverLinear.setVisibility(View.VISIBLE);
                                    silverScore.setText(""+score);
                                    silverName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                if (rep == 1){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(goldProfile);
                                    }
                                    goldLinear.setVisibility(View.VISIBLE);
                                    goldScore.setText(""+score);
                                    goldName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+url);
                                }
                                rep++;
                            }
                        }else if (dataSnapshot.getChildrenCount() == 3){
                            int rep = 0;
                            for (DataSnapshot details: dataSnapshot.getChildren()){
                                if (rep == 0){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(bronzeProfile);
                                    }
                                    bronzeLinear.setVisibility(View.VISIBLE);
                                    bronzeScore.setText(""+score);
                                    bronzeName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                if (rep == 1){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(silverProfile);
                                    }
                                    silverLinear.setVisibility(View.VISIBLE);
                                    silverScore.setText(""+score);
                                    silverName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                if (rep == 2){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(goldProfile);
                                    }
                                    goldLinear.setVisibility(View.VISIBLE);
                                    goldScore.setText(""+score);
                                    goldName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                rep++;
                            }
                        }
                }else{
                    final int childCount = (int) dataSnapshot.getChildrenCount();
                    Query topQuery = mUserRef.child("Users").orderByChild("score").limitToLast(3);
                    topQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int rep = 0;
                            for (DataSnapshot details: dataSnapshot.getChildren()){
                                if (rep == 0){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(bronzeProfile);
                                    }
                                    bronzeLinear.setVisibility(View.VISIBLE);
                                    bronzeScore.setText(""+score);
                                    bronzeName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                if (rep == 1){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(silverProfile);
                                    }
                                    silverLinear.setVisibility(View.VISIBLE);
                                    silverScore.setText(""+score);
                                    silverName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                if (rep == 2){
                                    Users users = details.getValue(Users.class);
                                    String name = users.getName();
                                    long score = users.getScore();
                                    String url = users.getThumb_image();

                                    if (!url.equals("default")){
                                        Glide.with(HomeActivity.this).load(url)
                                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                .into(goldProfile);
                                    }
                                    goldLinear.setVisibility(View.VISIBLE);
                                    goldScore.setText(""+score);
                                    goldName.setText(name);
                                    System.out.println( "name: "+name+ " score: " +score+ " profile: "+ url);
                                }
                                rep++;
                            }
                            Query query1 = mUserRef.child("Users").orderByChild("score").limitToFirst(childCount - 3);
                            options = new FirebaseRecyclerOptions.Builder<Users>()
                                    .setQuery(query1, Users.class)
                                    .build();

                            adapter = new FirebaseRecyclerAdapter<Users, LeaderBoardViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position, @NonNull Users model) {
                                    if (!model.getThumb_image().equals("default")){

                                            Glide.with(HomeActivity.this).load(model.getThumb_image())
                                                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                                                    .into(holder.rankThumb);

                                    }
                                    holder.rankName.setText(model.getName());
                                    System.out.println("This is Score" + model.getScore());
                                    holder.rankScore.setText("" + model.getScore());
                                    int serialNo = (childCount - 3) - position - 1 + 4;
                                    holder.rankPos.setText(""+serialNo);
                                }

                                @NonNull
                                @Override
                                public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                                    View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_ranking_layout, parent
                                    , false);

                                    return new LeaderBoardViewHolder(itemView);
                                }
                            };

                            adapter.startListening();
                            rankList.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            System.out.println(9);
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            return;
        }
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null){
            adapter.startListening();
        }
        if (currentUser != null){
        setRankList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(currentUser == null) {
            System.out.println("signed out");
            mAuth.signOut();
            Intent signInIntent = new Intent(HomeActivity.this, SignInActivity.class);
            //signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signInIntent);
        }else{
            System.out.println("e1");
            displayImage();
            mUserRef.child("Users").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String userName = dataSnapshot.child("name").getValue().toString();
                    Log.i("name", userName);
                    mUsernameTxt.setText(userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();

            Intent signOutIntent = new Intent(HomeActivity.this, SignInActivity.class);
           // signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signOutIntent);



        }
        if (item.getItemId() == R.id.main_settings_btn) {
//            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
//            startActivity(settingsIntent);

        }
        if (item.getItemId() == R.id.main_all_btn) {
//            Intent settingsIntent = new Intent(MainActivity.this, UserActivity.class);
//            startActivity(settingsIntent);
        }
        return true;
    }
}
