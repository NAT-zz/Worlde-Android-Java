package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import io.github.muddz.styleabletoast.StyleableToast;

public class NewMode1 extends AppCompatActivity {

    // Keys
    String[] keyArray = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Z", "X", "C", "V", "B", "N", "M"};

    // dialog
    Dialog dialogResult, dialogHelp;

    // coordinate & user's current score
    int col_count = 1, error_count = 0, score_count = 0;

    // the user's guess and the answer
    String preWord = "";
    String trueWord = "";

    // firebase
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
        ImageButton helper = (ImageButton) findViewById(R.id.btn_menuhelp);
        TextView scoreNum = (TextView) findViewById(R.id.tv_scorenum);

        // inti game data
        dataInit();

        // add words to the list
        Collections.addAll(wordArray, "LOVE", "NICE", "BAKE", "WEST", "RICE", "TIDE",
                                                "LIST", "RACE", "HOPE", "NEED", "HUGE", "BEST",
                                                "GOOD", "ZERO", "TREE", "CUTE", "NINE", "EXIT",
                                                "STAY", "COME", "COLD", "FIVE");
        // shuffle the list to make it looks like random
        Collections.shuffle(wordArray);
        // set up the game
        setUp();

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
        // helper-onclick logic
        helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the helper dialog
                showHelper(null);
            }
        });
        // enter-onclick logic
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print the current word to log
                Log.d("preword: ", preWord);

                // get the return value after checking the user's guess
                int result = checkWordValid();
                // if it's the last letter
                if (col_count == 5)
                {
                    // if the guess is wrong
                    if (result == 0) {
                        // pop a message
                        StyleableToast.makeText(NewMode1.this, "Not in word list", Toast.LENGTH_SHORT, R.style.not_in_word_list).show();
                        // handle the error
                        handleError();
                        // reset the boxes
                        makeEmptyBox();
                    }
                    // the it's correct
                    else if (result == 1){
                        // pop a message
                        StyleableToast.makeText(NewMode1.this, "NICE", Toast.LENGTH_SHORT, R.style.you_win).show();
                        // increase score
                        score_count++;
                        scoreNum.setText(String.valueOf(score_count));
                        // reset the guessed word
                        preWord="";

                        // reset the boxes
                        makeEmptyBox();
                        // reset the keys
                        clearkey();
                        // set up new game
                        setUp();
                    }
                    // if the guess is wrong
                    else if (result == 2) {
                        StyleableToast.makeText(NewMode1.this, "WRONG", Toast.LENGTH_SHORT, R.style.finish_the_word).show();
                        handleError();
                        makeEmptyBox();
                    }
                    // reset "y" coordinate
                    col_count = 1;
                }
                // pop a message if the word is not finished
                else StyleableToast.makeText(NewMode1.this, "Finish the word", Toast.LENGTH_SHORT, R.style.finish_the_word).show();
            }
        });
        // delete-onclick logic
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if it's not the first letter
                if (col_count != 1)
                    col_count--;
                // get the box id
                String genboxId = "tv_col" + col_count;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                // remove last letter
                if (preWord != null && preWord.length() > 0) {
                    preWord = preWord.substring(0, preWord.length() - 1);
                }
                // reset the box
                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setText("");
            }
        });
    }
    // show the helper popup
    private void showHelper(View v){
        // init dialog helper
        dialogHelp = new Dialog(this);
        dialogHelp.setContentView(R.layout.customhelper1);

        // mapping
        TextView closeHelper = (TextView) dialogHelp.findViewById(R.id.tv_closehelper1);

        // closehelper-onclick logic
        closeHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogHelp.dismiss();
            }
        });
        // make border transparent
        dialogHelp.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the helper dialge
        dialogHelp.show();
    }
    // show the result popup
    private void showResult(View v){
        // init dialog helper
        dialogResult = new Dialog(this);
        dialogResult.setContentView(R.layout.customresult);

        // mapping
        TextView closeResult = (TextView) dialogResult.findViewById(R.id.tv_closeresult);
        TextView myScore = (TextView) dialogResult.findViewById(R.id.tv_myscore);
        Button tryAgain = (Button) dialogResult.findViewById(R.id.btn_tryagain);
        Button mainGame = (Button) dialogResult.findViewById(R.id.btn_maingame);
        
        myScore.setText(String.valueOf(score_count));

        // closeresult-onclick logic
        closeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close the dialog
                dialogResult.dismiss();
            }
        });
        // tryAgain-onclick logic
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // close the dialog
                dialogResult.dismiss();
                // restart the game
                recreate();
            }
        });
        // mainGame-onclick logic
        mainGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // end the activity
                finish();
                // to main game mode
                startActivity(new Intent(NewMode1.this, MainActivity.class));
            }
        });
        
        // make border transparent
        dialogResult.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the helper dialge
        dialogResult.show();
    }
    // error handler
    private void handleError(){
        // increase error_count by 1
        ++error_count;
        // gen check id
        String gencheckId = "img_check" + error_count;
        int getcheckId = getResources().getIdentifier(gencheckId, "id", getPackageName());
        ImageView thisCheck = (ImageView) findViewById(getcheckId);

        // change background color of check image to rea
        thisCheck.setBackgroundColor(getResources().getColor(R.color.red));

        // if the chances run out
        if (error_count == 3){
            showResult(null);
        }
    }
    // reset keys handler
    private void clearkey(){
        for(int i=1;i<8;i++)
        {
            // generate key id
            String genkeyId =  "btn_" + i;
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            Button thisKey = (Button) findViewById(getkeyId);

            // set background-color to default
            thisKey.setBackgroundColor(getResources().getColor(R.color.default_key));
        }
    }
    // reset boxes handler
    private void makeEmptyBox(){
        for (int i=0;i<4;i++) {
            // generate box id
            String genboxId = "tv_col" + (i+1);
            int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

            // reset the box
            TextView thisBox = (TextView) findViewById(getboxID);
            thisBox.setText("");
        }
    }
    // check the guessed word
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
                preWord = "";
                return 2;
            }
        }
    }
    // set up the game
    private void setUp(){
        // get word
        if (wordCount < wordArray.size()) {
            trueWord = wordArray.get(wordCount);
            wordCount++;
        }

        // split string into array
        String[] letterArray = new String[7];
        for(int i=0;i<4;i++){
            letterArray[i] = String.valueOf(trueWord.charAt(i));
        }

        int n = keyArray.length, n1 = 4;
        // shuffle the key array
        Collections.shuffle(Arrays.asList(keyArray));
        // add more 3 letter into the array
        for(int i=0;i<n;i++){
            if (!Arrays.asList(letterArray).contains(keyArray[i])){
                letterArray[n1] = keyArray[i];
                ++n1;

                if(n1==7)
                    break;
            }
        }

        // shuffle the letter list to make it look like random :)
        Collections.shuffle(Arrays.asList(letterArray));
        // print the current guessed word into log
        Log.w("this word letter", letterArray.toString());

        // set up key
        for (int i=1;i<8;i++)
        {
            // generate the key's id
            String genkeyId =  "btn_" + i;
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            Button thisKey = (Button) findViewById(getkeyId);

            // set the letter of the key
            thisKey.setText(letterArray[i-1]);
        }

    }
    // init the data
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
    // keys-onlic logic
    private void keyOnclick(String x){
        // if it's not the last letter
        if(col_count != 5) {
            //generate box id
            String genboxId = "tv_col" + col_count;
            int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

            // set the box with the clicked key
            TextView thisBox = (TextView) findViewById(getboxID);
            thisBox.setText(x);

            // add the letter of the clicked key to preWord
            preWord += x;
            // increase the col
            ++col_count;
        }
    }
}