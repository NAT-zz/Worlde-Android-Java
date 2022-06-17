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

    //views
    EditText email;
    Button resetPassword;
    ProgressBar progressBar;

    //firebase
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //mapping
        email = (EditText) findViewById(R.id.et_emailreset);
        resetPassword = (Button) findViewById(R.id.btn_reset);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        mAuth = FirebaseAuth.getInstance();

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rsPassword();
            }
        });
    }
    private void rsPassword(){
        // get field value
        String strEmail = email.getText().toString().trim();

        // check if email is empty?
        if (strEmail.isEmpty()){
            email.setError("Email is missing");
            email.requestFocus();
            return;
        }
        // check if email is valid?
        if (!Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()){
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(strEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgotPasswordActivity.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(ForgotPasswordActivity.this, "Oops! Please try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}