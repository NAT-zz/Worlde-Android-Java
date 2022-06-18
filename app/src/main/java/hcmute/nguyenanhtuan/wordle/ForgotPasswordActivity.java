package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    // views
    EditText email;
    Button resetPassword;
    ProgressBar progressBar;

    // firebase authentication
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // mapping
        email = (EditText) findViewById(R.id.et_emailreset);
        resetPassword = (Button) findViewById(R.id.btn_reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        // get instance of firebase authentication
        mAuth = FirebaseAuth.getInstance();

        // resetPassword-onclick logic
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rsPassword();
            }
        });
    }
    private void rsPassword(){
        // get field email value
        String strEmail = email.getText().toString().trim();

        // check if email is empty?
        if (strEmail.isEmpty()){
            // raise error for user
            email.setError("Email is missing");
            // set focus on the email field
            email.requestFocus();
            return;
        }
        // check if email is valid?
        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
            // raise error for user
            email.setError("Please provide valid email!");
            // set focus on the email field
            email.requestFocus();
            return;
        }
        // set visibility of progressBar to visible
        progressBar.setVisibility(View.VISIBLE);
        // send link to user's email for password reset
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            // if link is sent successfully
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    // pop a message
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Oops! Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}