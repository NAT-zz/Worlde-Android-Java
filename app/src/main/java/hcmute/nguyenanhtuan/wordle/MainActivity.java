package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    // coordinate
    int row_count = 1;
    int col_count = 1;

    // view
    TextView enter, delete;
    Dialog dialog;
    ImageButton statistic, replay, help;

    // Key
    String[] keyArray = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                        "A", "S", "D", "F", "G", "H", "J", "K", "L",
                        "Z", "X", "C", "V", "B", "N", "M"};
    //firebase
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    // firebase authentication
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String userID;

    //user
    User thisUser;

    // word in a row
    String preWord="";
    // list of words
    ArrayList<String> wordArray = new ArrayList<>();
    // the answer
    String trueWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapping();
        dataInit();

        // keys on click
        for(int i=0;i<keyArray.length;i++)
        {
            // generate key id
            String genkeyId =  "tv_" + keyArray[i];
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            TextView thisKey = (TextView) findViewById(getkeyId);

            int finalI = i;
            // set onclick logic to each key with the provided letter
            thisKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keyOnclick(keyArray[finalI]);
                }
            });
        }
        // statistic-onclick logic
        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(null);
            }
        });
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        // enter-onclick logic
        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print the current word to log
                Log.d("preword: ", preWord);

                int result = checkWordValid();
                if (col_count == 6)
                {
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Not in word list", Toast.LENGTH_SHORT).show();
                        StyleableToast.makeText(MainActivity.this, "Not in word list", Toast.LENGTH_LONG, R.style.not_in_word_list).show();
                    }
                    else if (result == 1){
                        Toast.makeText(MainActivity.this, "You Win", Toast.LENGTH_SHORT).show();
                    }
                    else if (result == 2) {
                        if (row_count != 6)
                            row_count++;
                        else
                            Toast.makeText(MainActivity.this, "You failed", Toast.LENGTH_SHORT).show();
                        col_count = 1;
                    }
                }
                else Toast.makeText(MainActivity.this, "Finish the word", Toast.LENGTH_SHORT).show();
            }
        });
        // delete-onclick logic
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (col_count != 1)
                    col_count--;
                // get the box id
                String genboxId = "tv_row" + row_count + "_" + col_count;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setText("");
            }
        });
    }
    private void mapping(){
        enter = (TextView) findViewById(R.id.tv_enter);
        delete = (TextView) findViewById(R.id.tv_del);

        statistic = (ImageButton) findViewById(R.id.btn_statistics);
        replay = (ImageButton) findViewById(R.id.btn_replay);
        help = (ImageButton) findViewById(R.id.btn_help);
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser==null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            userID = currentUser.getUid();
            Log.d("user", currentUser.getEmail());

            // get users collection
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    thisUser = snapshot.getValue(User.class);
                    if (thisUser == null){
                        Log.d("error", "getting error");
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
    private void dataInit(){
        // get Word data
        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Word");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // loop over the word list
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    // add word to current array
                    wordArray.add(postSnapshot.getValue().toString());
                }
                // get a random int
                int random = new Random().nextInt(wordArray.size());
                // get word with the int above
                trueWord = wordArray.get(random);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed to read value.", error.toException().toString());
            }
        });
    }
    private int checkWordValid() {
        // if the answer is corrent
        if (preWord.equals(trueWord)) {
            for (int i=1;i<=5;i++)
            {
                // get the box id
                String genboxId = "tv_row" + row_count + "_" + i;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                // set the color for the box
                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setBackgroundColor(getResources().getColor(R.color.green));
            }
            return 1;
        }
        else {
            // if the word is not in the current array
            if (!wordArray.contains(preWord)) {
                preWord = "";
                return 0;
            }
            else{
                for(int i=0;i<5;i++){
                    //get box
                    String genboxId = "tv_row" + row_count + "_" + (i+1);
                    int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());
                    TextView thisBox = (TextView) findViewById(getboxID);

                    //get key
                    String genkeyId =  "tv_" + preWord.substring(i, i+1);
                    int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
                    TextView thisKey = (TextView) findViewById(getkeyId);

                    // if the letter is at the right place
                    if (trueWord.charAt(i) == preWord.charAt(i)) {
                        // change the background color of the box and the key to green
                        thisBox.setBackgroundColor(getResources().getColor(R.color.green));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    // if the letter is in the answer
                    else if (trueWord.contains(preWord.substring(i, i+1))) {
                        // change the background color of the box and the key to yellow
                        thisBox.setBackgroundColor(getResources().getColor(R.color.yellow));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.yellow));
                    }
                    // if the letter is not in the answer
                    else {
                        // change the background color of the box and the key to brighter_dark
                        thisBox.setBackgroundColor(getResources().getColor(R.color.birghter_dark));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.birghter_dark));
                    }
                }
                preWord = "";
                return 2;
            }
        }
    }
    // show the statistic popup
    private void showPopUp(View v) {
        // init dialog
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custompopup);

        // mapping
        TextView close = (TextView) dialog.findViewById(R.id.tv_close);;
        TextView played = (TextView) dialog.findViewById(R.id.tv_playednum);
        TextView win = (TextView) dialog.findViewById(R.id.tv_winnum);
        TextView loose = (TextView) dialog.findViewById(R.id.tv_loosenum);
        TextView streak = (TextView) dialog.findViewById(R.id.tv_streaknum);

        // show user record
        played.setText(((java.lang.Integer) thisUser.getPlayed()));
        win.setText(((java.lang.Integer) thisUser.getWinCount()));
        loose.setText(((java.lang.Integer) thisUser.getLooseCount()));
        streak.setText(((java.lang.Integer) thisUser.getCurrentStreak()));

        // close-onclick logic
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        // make border transparent
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the dialog
        dialog.show();
    }
    // key-onclick logic
    private void keyOnclick(String x){
        //generate box id
        if(col_count != 6) {
            String genboxId = "tv_row" + row_count + "_" + col_count;
            int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

            TextView thisBox = (TextView) findViewById(getboxID);
            thisBox.setText(x);

            preWord += x;
            ++col_count;
        }
    }
}