package developer.adithya.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class RegisterActivity2 extends AppCompatActivity {
    private OtpTextView otp_view;
    private Button verify ;
    private static final String TAG = "RegisterActivity2";
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth firebaseAuth;
    public FirebaseUser fUser;
    private TextView ErrorText;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        otp_view = findViewById(R.id.otp_view);
        verify = findViewById(R.id.button);
        firebaseAuth = FirebaseAuth.getInstance();
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        ErrorText = findViewById(R.id.textView13);


        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        otp_view.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // invoked when user types something in the Otpbox

            }
            @Override
            public void onOTPComplete(String otp) {
                // invoked when user has entered the OTP fully.

                verifyOTP(otp);
                Log.d(TAG, "onOTPComplete: " + otp);
            }
        });

//        otpTextView.getOtpListener();  // retrieves the current OTPListener (null if nothing is set)
//        otpTextView.requestFocusOTP();	//sets the focus to OTP box (does not open the keyboard)
//        otpTextView.setOTP(otpString);	// sets the entered otpString in the Otp box (for case when otp is retreived from SMS)
//        otpTextView.getOTP();	// retrieves the OTP entered by user (works for partial otp input too)
//        otpTextView.showSuccess();	// shows the success state to the user (can be set a bar color or drawable)
//        otpTextView.showError();	// shows the success state to the user (can be set a bar color or drawable)
//        otpTextView.resetState();	// brings the views back to default state (the state it was at input)
    }

    private void verifyOTP(String otp) {

        Intent i = getIntent();
        if (i != null){
            mVerificationId = i.getStringExtra("VerificationID");
            Log.d(TAG, "verifyOTP: " + mVerificationId);
        }else {
            Log.d(TAG, "verifyOTP: NPE");
        }



        PhoneAuthCredential mCredential = PhoneAuthProvider.getCredential(mVerificationId , otp);

        firebaseAuth.signInWithCredential(mCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        otp_view.showSuccess();
                        ErrorText.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(RegisterActivity2.this , RegisterActivity3.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK)
                        );
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        otp_view.showError();
                        otp_view.resetState();
                        if (e instanceof FirebaseAuthInvalidCredentialsException){
                            Toast.makeText(RegisterActivity2.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                            otp_view.showError();
                            otp_view.resetState();
                            ErrorText.setVisibility(View.VISIBLE);

                        }
                    }
                });
    }

}