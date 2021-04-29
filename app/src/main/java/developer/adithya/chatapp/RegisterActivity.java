package developer.adithya.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private Button next;
    private EditText Phone ;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser fUser;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private static final String TAG = "RegisterActivity";
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        next = findViewById(R.id.button);
        Phone = findViewById(R.id.editTextPhone);
        firebaseAuth = FirebaseAuth.getInstance();
        fUser = firebaseAuth.getCurrentUser();
        progressBar = findViewById(R.id.progressBar);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String PhoneNum = "+91" + Phone.getText().toString();
                Log.d(TAG, "onClick: " + PhoneNum);

                if (PhoneNum.length() == 13) {
                    SendOTP(PhoneNum);
                }else Phone.setError("Invalid Phone number");

            }
        });

    }

    private void SendOTP(String phoneNum) {
        progressBar.setVisibility(View.VISIBLE);
        Phone.setEnabled(false);
        next.setEnabled(false);

        Log.d(TAG, "SendOTP:" + phoneNum );

        Thread manageOtp = new Thread(){
            public void run(){
                Log.d(TAG, "run: thread started" );
                PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // The SMS verification code has been sent to the provided phone number, we
                        // now need to ask the user to enter the code and then construct a credential
                        // by combining the code with a verification ID.

                        // Save verification ID and resending token so we can use them later
                        mVerificationId = verificationId;
                        mResendToken = token;
                        // TODO: Enable Resend OTP

                        // UpdateUI
                        Intent i = new Intent(RegisterActivity.this , RegisterActivity2.class);
                        i.putExtra("VerificationID" , verificationId);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // This callback will be invoked in two situations:
                        // 1 - Instant verification. In some cases the phone number can be instantly
                        //     verified without needing to send or enter a verification code.
                        // 2 - Auto-retrieval. On some devices Google Play services can automatically
                        //     detect the incoming verification SMS and perform verification without
                        //     user action.



                        Log.d(TAG, "onCodeSent:" + phoneAuthCredential);

                    }


                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // This callback is invoked in an invalid request for verification is made,
                        // for instance if the the phone number format is not valid.
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(RegisterActivity.this, "ERROR: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        progressBar.setVisibility(View.INVISIBLE);
                        Phone.setEnabled(true);
                        next.setEnabled(true);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {

                            // Invalid request
                            // ...

                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                            Toast.makeText(RegisterActivity.this, "An Error Has Occured Please try again later", Toast.LENGTH_LONG).show();
                        }

                    }
                };

                PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNum, 60L, TimeUnit.SECONDS, RegisterActivity.this, mCallbacks );
            }

        };

        manageOtp.run();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (fUser != null){
            Intent i = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

    }
}