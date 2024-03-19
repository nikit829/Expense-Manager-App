package com.example.budgetbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Registration_Activity extends AppCompatActivity
{
    private EditText rEmail;
    private EditText rpass;

    private EditText rCfmPass;
    private Button regbtn;
    private TextView rsignin;
    //firebase
    private FirebaseAuth rAuth;
    private ProgressDialog rDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Registration();

        rAuth=FirebaseAuth.getInstance();
        rDialog=new ProgressDialog(this);
    }
    private void Registration()
    {
        rEmail = findViewById(R.id.email_reg);
        rpass = findViewById(R.id.password_reg);
        rCfmPass = findViewById(R.id.confirm_password);
        regbtn = findViewById(R.id.button_reg);
        rsignin = findViewById(R.id.signin);

        regbtn.setOnClickListener(new View.OnClickListener()
        {
//           @Override
           public void onClick(View view)
           {
               String email=rEmail.getText().toString();
               String pass=rpass.getText().toString();
               String cfmpass=rCfmPass.getText().toString();

               if(TextUtils.isEmpty(email))
                   rEmail.setError("Email required");

               if(TextUtils.isEmpty(pass))
                   rpass.setError("Password required");

               if(TextUtils.isEmpty(cfmpass))
                   rCfmPass.setError("Password required");

               if(!cfmpass.equals(pass))
                   rCfmPass.setError("Password and confirm password do not match");

               else {
                   rDialog.setMessage("Processing..");
                   rDialog.show();
                   rAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()){
                               rDialog.dismiss();
                               Toast.makeText(getApplicationContext(),"Registration Successful", Toast.LENGTH_SHORT).show();
                               startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                           }
                           else{
                               rDialog.dismiss();
                               Toast.makeText(getApplicationContext(),"Registration Failed"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                           }
                       }
                   });
               }
           }
        });

        //Login Activity
        rsignin.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
    });
    }
}