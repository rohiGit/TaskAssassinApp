package com.example.taskassassin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterActivity extends AppCompatActivity {

    private EditText userNameText;
    private EditText emailText;
    private EditText passwordText;
    private TextView signInLink;
    private Button mSignUpBtn;
    private ProgressDialog mRegProgress;
    private FirebaseAuth mAuth;
    private boolean hasEmailVerified;
    private DatabaseReference mDatabase;
    int timeRemaining = 150;
    private boolean isButtonClicked = false;
    private Timer t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        userNameText = findViewById(R.id.reg_user_name);
        emailText = findViewById(R.id.email_text);
        passwordText = findViewById(R.id.password_text);
        signInLink = findViewById(R.id.sign_in_link);
        mSignUpBtn = findViewById(R.id.sign_up_btn);
        mRegProgress = new ProgressDialog(this);

        signInLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = new Intent(RegisterActivity.this, SignInActivity.class);
                signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(signInIntent);
            }
        });

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailText.getText().toString();
                String password = passwordText.getText().toString();
                String userName = userNameText.getText().toString();
                if (!TextUtils.isEmpty(userName) || !TextUtils.isEmpty(password) || !TextUtils.isEmpty(email) && !isButtonClicked) {
                    isButtonClicked = true;
                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while we create your account.");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    register_user(userName, email, password);

                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isButtonClicked){
            t.cancel();
            FirebaseAuth.getInstance().getCurrentUser().delete();
            System.out.println("back delete");
        }
        
    }

    private void register_user(final String userName, final String email, final String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    mRegProgress.dismiss();
                    current_user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if (task.isSuccessful()) {
                               System.out.println("verify please");
                               mRegProgress.dismiss();
                               Toast.makeText(getBaseContext(), "Check your email for verification", Toast.LENGTH_LONG).show();

                               t = new Timer();
                               t.schedule(new TimerTask() {
                                   @Override
                                   public void run() {

                                       if (--timeRemaining < 1){
                                           isButtonClicked = false;
                                           t.cancel();
                                           System.out.println("Timeout");
                                           RegisterActivity.this.runOnUiThread(new Runnable() {
                                               public void run() {
                                                   Toast.makeText(RegisterActivity.this, "Timeout", Toast.LENGTH_SHORT).show();
                                               }
                                           });
                                           FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   Toast.makeText(getBaseContext(), "User deleted", Toast.LENGTH_LONG).show();
                                                   System.out.println("User deleted");
                                               }
                                           });
                                       }
                                       mAuth.getCurrentUser().reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                hasEmailVerified = user.isEmailVerified();
                                                if (hasEmailVerified){
                                                    isButtonClicked = false;
                                                    t.cancel();
                                                    System.out.println("timer has stopped");

                                                    String uid = current_user.getUid();
                                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                                                    System.out.println("creating a db node");
                                                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                                                    Log.i("email, pass, uName", email + " " + password + " " + userName);

                                                    final HashMap<String, Object> userMap = new HashMap<>();
                                                    userMap.put("name", userName);
                                                    userMap.put("thumb_image", "default");
                                                    userMap.put("profile_image", "default");
                                                    userMap.put("device_token", deviceToken);
                                                    userMap.put("score", 0);
                                                    userMap.put("completed_task", 0);
                                                    userMap.put("pending_task", 0);
                                                    userMap.put("focused_min", 0);
                                                    mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                mRegProgress.dismiss();

                                                                Intent mainIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                                                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(mainIntent);
                                                                finish();

                                                            }
                                                        }
                                                    });
                                                }

                                           }
                                       });
                                       System.out.println("timer running");
                                   }
                               }, 0, 2000);

                           }

                        }
                    });


                }else {

                    mRegProgress.dismiss();
                    // Toast.makeText(RegisterActivity.this, "Drat! There may be some problem, please check the form and try again.", Toast.LENGTH_LONG).show();
                    String error = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        error = "Weak Password!";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        error = "Invalid Email";
                    } catch (FirebaseAuthUserCollisionException e) {
                        error = "Existing account!";
                    } catch (Exception e) {
                        error = "Unknow error!";
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
