package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class NewMode1 extends AppCompatActivity {

    // button
//    Button submit;
//    Button delete;

    // coordinate
    int col_count = 1;

    // answer
    String preWord = "";
    String trueWord = "";

    // database
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;

    // list of words
    ArrayList<String> wordArray = new ArrayList<>();
    int wordCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_mode1);

        // mapping
        Button submit = (Button) findViewById(R.id.btn_submit);
        Button delete = (Button) findViewById(R.id.btn_del);
        ImageButton restart = (ImageButton) findViewById(R.id.btn_restart);
        dataInit();
        getWord();

        // keys on click
        for(int i=1;i<8;i++)
        {
            // generate key id
            String genkeyId =  "btn_" + i;
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            Button thisKey = (Button) findViewById(getkeyId);

            // set onclick logic to each key
            thisKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keyOnclick(thisKey.getText().toString().trim());
                }
            });
        }
        // restart-onclick logic
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });
        // enter-onclick logic
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print the current word to log
                Log.d("preword: ", preWord);

                int result = checkWordValid();
                if (col_count == 6)
                {
                    if (result == 0) {
                        StyleableToast.makeText(NewMode1.this, "Not in word list", Toast.LENGTH_LONG, R.style.not_in_word_list).show();
                    }
                    else if (result == 1){
                        StyleableToast.makeText(NewMode1.this, "NICE", Toast.LENGTH_LONG, R.style.you_win).show();

//                        if(row_count == 1)
//                            thisUser.getRecord().setFirstWin(thisUser.getRecord().getFirstWin()+1);
//                        else if (row_count == 2)
//                            thisUser.getRecord().setSecondWin(thisUser.getRecord().getSecondWin()+1);
//                        else if (row_count == 3)
//                            thisUser.getRecord().setThirdWin(thisUser.getRecord().getThirdWin()+1);
//                        else if (row_count == 4)
//                            thisUser.getRecord().setFourthWin(thisUser.getRecord().getFourthWin()+1);
//                        else if (row_count == 5)
//                            thisUser.getRecord().setFifthWin(thisUser.getRecord().getFifthWin()+1);
//                        else
//                            thisUser.getRecord().setSixthWin(thisUser.getRecord().getSixthWin()+1);
//
//                        thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
//                        thisUser.getRecord().setCurrentStreak(thisUser.getRecord().getCurrentStreak()+1);
//                        thisUser.getRecord().setWinCount(thisUser.getRecord().getWinCount()+1);
//                        updateRecord();
                    }
                    else if (result == 2) {
                        col_count = 1;
//                            thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
//                            thisUser.getRecord().setCurrentStreak(0);
//                            thisUser.getRecord().setLooseCount(thisUser.getRecord().getLooseCount()+1);
//                            updateRecord();
                    }
                }
                else StyleableToast.makeText(NewMode1.this, "Finish the word", Toast.LENGTH_LONG, R.style.finish_the_word).show();
                makeEmpty();
            }
        });
        // delete-onclick logic
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (col_count != 1)
                    col_count--;
                // get the box id
                String genboxId = "tv_col" + col_count;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setText("");
            }
        });
    }
    private void makeEmpty(){
        for (int i=0;i<4;i++) {
            String genboxId = "tv_col" + (i+1);
            int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

            TextView thisBox = (TextView) findViewById(getboxID);
            thisBox.setText("");
        }
    }
    private int checkWordValid() {
        // if the answer is corrent
        if (preWord.equals(trueWord)) {
            return 1;
        }
        else {
            // if the word is not in the current array
            if (!wordArray.contains(preWord)) {
                preWord = "";
                return 0;
            }
            else{
                for(int i=0;i<4;i++){
                    //get box
                    String genboxId = "tv_col" + (i+1);
                    int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());
                    TextView thisBox = (TextView) findViewById(getboxID);

                    //get key
//                    String genkeyId =  "tv_" + preWord.substring(i, i+1);
//                    int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
//                    Button thisKey = (Button) findViewById(getkeyId);

                    // if the letter is at the right place
                    if (trueWord.charAt(i) == preWord.charAt(i)) {
                        // change the background color of the box and the key to green
                        thisBox.setBackgroundColor(getResources().getColor(R.color.green));
//                        thisKey.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    // if the letter is not in the answer
                    else {
                        // change the background color of the box and the key to brighter_dark
                        thisBox.setBackgroundColor(getResources().getColor(R.color.birghter_dark));
                    }
                }
                preWord = "";
                return 2;
            }
        }
    }
    private void getWord(){
        // get word
        trueWord = wordArray.get(wordCount);
        wordCount++;
    }
    private void dataInit(){
        // get Word data
        db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference("Word2");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // loop over the word list
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    // add word to current array
                    wordArray.add(postSnapshot.getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Failed to read value.", error.toException().toString());
            }
        });
    }
    private void keyOnclick(String x){
        //generate box id
        if(col_count != 5) {
            String genboxId = "tv_col" + col_count;
            int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

            TextView thisBox = (TextView) findViewById(getboxID);
            thisBox.setText(x);

            preWord += x;
            ++col_count;
        }
    }
}