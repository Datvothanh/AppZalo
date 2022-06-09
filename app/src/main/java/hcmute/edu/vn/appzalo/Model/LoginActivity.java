package hcmute.edu.vn.appzalo.Model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.appzalo.R;

public class LoginActivity extends AppCompatActivity {
    private EditText  PhoneNumber;
    private Button btnregis;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PhoneNumber=findViewById(R.id.inputPhonenumber);
        btnregis = findViewById(R.id.btnRegister);

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = PhoneNumber.getText().toString();
                if(phoneNumber.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter your phone number", Toast.LENGTH_SHORT).show();
                }
                else {
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+84"+phoneNumber, 60, TimeUnit.SECONDS, LoginActivity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                            signInUnser(phoneAuthCredential);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e) {
                            Log.d(TAG, "onVerificationFailed: "+ e.getLocalizedMessage());
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationId ,@NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                            super.onCodeSent( verificationId , forceResendingToken);

                            Dialog dialog  = new Dialog(LoginActivity.this);
                            dialog.setContentView(R.layout.verify_popup);
                            EditText etVerifyCode = dialog.findViewById(R.id.etVerifyOTP);
                            Button btnVerifyCode = dialog.findViewById(R.id.btnVerifyOTP);
                            btnVerifyCode.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                 String verificationCode = etVerifyCode.getText().toString();
                                 if(verificationId.isEmpty()) return;
                                 PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,verificationCode );
                                 signInUnser(credential);
                                }
                            });

                            dialog.show();
                        }
                    });
                }
            }
        });
    }

    private void signInUnser(PhoneAuthCredential credential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            startActivity(new Intent(LoginActivity.this , MainActivity.class));
                            finish();
                        }else {
                            Log.d(TAG , "onComplete:" + task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

}