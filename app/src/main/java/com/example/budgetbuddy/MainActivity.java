package com.example.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText lEmail;
    private EditText lPass;
    private Button lBtn;
    private TextView forgotPass;
    private TextView reg;
    private ProgressDialog lDialog;
    private FirebaseAuth lAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(lAuth.getCurrentUser()!=null){
//            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
//        }
        Login();

        //firebase
        lDialog= new ProgressDialog(this);
        lAuth=FirebaseAuth.getInstance();
    }
    private void Login(){
        lEmail = findViewById(R.id.email_login);
        lPass = findViewById(R.id.password_login);
        lBtn = findViewById(R.id.button_login);
        forgotPass = findViewById(R.id.forget_password);
        reg = findViewById(R.id.signup);

        lBtn.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                String email=lEmail.getText().toString();
                String pass=lPass.getText().toString();

                if(TextUtils.isEmpty(email))
                {
                    lEmail.setError("Email required");
                }
                if(TextUtils.isEmpty(pass))
                {
                    lPass.setError("Password required");
                }
                else{
                    lDialog.setMessage("Processing..");
                    lDialog.show();
                    lAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                lDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                            else{
                                lDialog.dismiss();
                                Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        //Forgot Password
        forgotPass.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), ResetActivity.class));
            }
        });

        //Registration activity
        reg.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), Registration_Activity.class));
            }
        });
    }
}