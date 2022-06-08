package hcmute.nguyenanhtuan.wordle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    TextView register;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mapping();

        register.setOnClickListener(this);
    }
    // map elements with ids
    private void mapping(){
        register = (TextView) findViewById(R.id.tv_register);
    }
    // button-onclick logic
    @Override
    public void onClick(View v){
        switch (v.getId()){
            // to register
            case R.id.tv_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }
}