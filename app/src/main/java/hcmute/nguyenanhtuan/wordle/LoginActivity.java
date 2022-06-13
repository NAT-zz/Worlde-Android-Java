package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    // views
    TextView register, forgotPassword;
    Button login;
    EditText email, password;
    ProgressBar progressBar;

    // firebase
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapping();

        // init firebase authentication
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(this);
        login.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);
    }
    // map elements with ids
    private void mapping(){
        forgotPassword = (TextView) findViewById(R.id.tv_forgotpassword);
        register = (TextView) findViewById(R.id.tv_register);

        login = (Button) findViewById(R.id.btn_login);
        email = (EditText) findViewById(R.id.et_emaillogin);
        password = (EditText) findViewById(R.id.et_passwordlogin);

        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
    }
    private void userLogin() {
        // get field value
        String str_email = email.getText().toString().trim();
        String str_password = password.getText().toString().trim();

        // raise error and request again if field is missing ?
        if (str_email.isEmpty()){
            email.setError("Email is missing");
            email.requestFocus();
            return;
        }
        // check email is in correct form
        if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()){
            email.setError("Please provide a valid email");
            email.requestFocus();
            return;
        }

        if (str_password.isEmpty()) {
            password.setError("Password is missing");
            password.requestFocus();
            return;
        }
        // check password's length
        if (str_password.length() < 6) {
            password.setError("Password length must contain at least 6 characters");
            password.requestFocus();
            return;
        }

        // show progress bar
        progressBar.setVisibility(View.VISIBLE);
        // validate email and passwrod with realtime database
        mAuth.signInWithEmailAndPassword(str_email, str_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // if validation is successful
                if (task.isSuccessful()){
                    // for testing
//                    FirebaseUser user = mAuth.getCurrentUser();
//                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful())
//                                Log.d("Flag", "Email verification sent");
//                            else
//                                Log.d("Flag", "Email verification not sent");
//                        }
//                    });
                    // to game
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed! Please check your infomation again", Toast.LENGTH_LONG).show();
                }
//                progressBar.setVisibility(View.GONE);
            }
        });

    }
    // buttons-onclick logic
    @Override
    public void onClick(View v){
        switch (v.getId()){
            // to register
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.btn_login:
                userLogin();
                break;
            case R.id.tv_forgotpassword:
                startActivity(new Intent(this, ForgotPasswordActivity.class));
                break;
        }
    }
}