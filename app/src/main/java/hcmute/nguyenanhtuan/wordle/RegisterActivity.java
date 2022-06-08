package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText fullName, age, email, password;
    TextView banner;
    Button register;
    ProgressBar progressBar;

    // firebase
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mapping();
        mAuth = FirebaseAuth.getInstance();

        // element onclick
        register.setOnClickListener(this);
        banner.setOnClickListener(this);

    }
    // map elements with ids
    private void mapping(){
        fullName = (EditText) findViewById(R.id.et_fullname);
        age = (EditText) findViewById(R.id.et_age);
        email = (EditText) findViewById(R.id.et_email);
        password = (EditText) findViewById(R.id.et_password);

        banner = (TextView) findViewById(R.id.tv_banner);
        register = (Button) findViewById(R.id.btn_register);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }
    // empty field
    private void makeEmpty(){
        fullName.setText("");
        age.setText("");
        email.setText("");
        password.setText("");
    }
    // register new user logic
    private void registerUser() {
        // get input field
        String str_fullName = fullName.getText().toString().trim();
        String str_age = age.getText().toString().trim();
        String str_email = email.getText().toString().trim();
        String str_password = password.getText().toString().trim();

        // raise error and request again if field is missing ?
        if(str_fullName.isEmpty()) {
            fullName.setError("Full name is missing");
            fullName.requestFocus();
            return;
        }
        if(str_age.isEmpty()) {
            age.setError("Age is missing");
            age.requestFocus();
            return;
        }

        if(str_email.isEmpty()) {
            email.setError("Email address is missing");
            email.requestFocus();
            return;
        }
        // check email is in correct form
        if (!Patterns.EMAIL_ADDRESS.matcher(str_email).matches()) {
            email.setError("Please provide a valid email!");
            email.requestFocus();
            return;
        }

        if (str_password.isEmpty()) {
            password.setError("Password is missing");
            password.requestFocus();
            return;
        }
        if (str_password.length() < 6) {
            password.setError("Password length must contain at least 6 characters");
            password.requestFocus();
            return;
        }

        // show progress bar
        progressBar.setVisibility(View.VISIBLE);
        // create user with email and password with to firebase
        mAuth.createUserWithEmailAndPassword(str_email, str_password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if user is created successfully
                        if (task.isSuccessful()){
                            // init new User
                            User user = new User(str_fullName, str_age, str_email);

                            // also add user to realtime database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // is user is added
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                                    }
                                    else{
                                        Toast.makeText(RegisterActivity.this, "Register failed, please try again!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        // if not
                        }else {
                            Toast.makeText(RegisterActivity.this, "Register failed, please try again!", Toast.LENGTH_LONG).show();
                        }
                        makeEmpty();
                        // hide progressbar
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }
    // button-onclick logic
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // return to login when banner is clicked
            case R.id.tv_banner:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.btn_register:
                registerUser();
                break;
        }
    }
}