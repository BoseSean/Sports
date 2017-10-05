package org.team7.sports;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference databaseRef;
    private TextInputLayout Email;
    private TextInputLayout Username;
    private TextInputLayout Password;
    private Button RegisterBtn;
    // TODO: change this depreciated Progressdialog to ProgressBar
    private ProgressDialog RegisterProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();



        Email = (TextInputLayout) findViewById(R.id.reg_email);
        Username = (TextInputLayout) findViewById(R.id.reg_username);
        Password = (TextInputLayout) findViewById(R.id.reg_password);
        RegisterBtn = (Button) findViewById(R.id.reg_register_btn);

        RegisterProgress = new ProgressDialog(this);

        RegisterBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: add exception handlling on null input of each fields,
                // TODO: add validation and handle invalide
                String email = Email.getEditText().getText().toString();
                String username = Username.getEditText().getText().toString();
                String password = Password.getEditText().getText().toString();

                if(!TextUtils.isEmpty((email)) || !TextUtils.isEmpty(username) || !TextUtils.isEmpty(password)){
                    RegisterProgress.setTitle(R.string.registering);
                    RegisterProgress.setCanceledOnTouchOutside(false);
                    RegisterProgress.show();
                    registerNewUser(email, username, password);
                }

            }
        });
    }

    private void registerNewUser(String email, final String username, String password){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser currentUse = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = currentUse.getUid();

                    databaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                    HashMap<String, String> userMap = new HashMap();
                    userMap.put("name", username);
                    userMap.put("image", "default");
                    databaseRef.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()){
                                    RegisterProgress.dismiss();
                                    Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(mainIntent);
                                    finish();

                                } else {
                                    Toast.makeText(RegisterActivity.this,R.string.register_failed, Toast.LENGTH_LONG).show();
                                    RegisterProgress.dismiss();
                                }
                        }
                    });
                } else {
                    // TODO: add detailed reason for registration failure.
                    Toast.makeText(RegisterActivity.this,R.string.register_failed, Toast.LENGTH_LONG).show();
                    RegisterProgress.dismiss();
                }
            }
        });
    }
}
