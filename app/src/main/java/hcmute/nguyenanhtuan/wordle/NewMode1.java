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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.regex.Pattern;

import io.github.muddz.styleabletoast.StyleableToast;

public class NewMode1 extends AppCompatActivity {

    // Key
    String[] keyArray = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
            "A", "S", "D", "F", "G", "H", "J", "K", "L",
            "Z", "X", "C", "V", "B", "N", "M"};

    // dialog
    Dialog dialogResult;

    // coordinate
    int col_count = 1, error_count = 0, score_count = 0;

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
        TextView scoreNum = (TextView) findViewById(R.id.tv_scorenum);

        dataInit();
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
        // enter-onclick logic
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // print the current word to log
                Log.d("preword: ", preWord);

                int result = checkWordValid();
                if (col_count == 4)
                {
                    if (result == 0) {
                        StyleableToast.makeText(NewMode1.this, "Not in word list", Toast.LENGTH_SHORT, R.style.not_in_word_list).show();
                        handleError();
                        makeEmptyBox();
                    }
                    else if (result == 1){
                        StyleableToast.makeText(NewMode1.this, "NICE", Toast.LENGTH_SHORT, R.style.you_win).show();
                        makeEmptyBox();
                        clearkey();
                        setUp();
                    }
                    else if (result == 2) {
                        col_count = 1;
                        handleError();
                        makeEmptyBox();
                    }
                }
                else StyleableToast.makeText(NewMode1.this, "Finish the word", Toast.LENGTH_SHORT, R.style.finish_the_word).show();
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
    // show the result popup
    private void showResult(View v){
        // init dialog helper
        dialogResult = new Dialog(this);
        dialogResult.setContentView(R.layout.custommenu);

        // mapping
        TextView closeResult = (TextView) dialogResult.findViewById(R.id.tv_closemenu);
        TextView myScore = (TextView) dialogResult.findViewById(R.id.tv_myscore);
        Button tryAgain = (Button) dialogResult.findViewById(R.id.btn_tryagain);
        Button mainGame = (Button) dialogResult.findViewById(R.id.btn_maingame);
        
        myScore.setText(String.valueOf(score_count));

        // closeresult-onclick logic
        closeResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.dismiss();
            }
        });
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogResult.dismiss();
                recreate();
            }
        });
        mainGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                startActivity(new Intent(NewMode1.this, MainActivity.class));
            }
        });
        
        // make border transparent
        dialogResult.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the helper dialge
        dialogResult.show();
    }
    // error - logic
    private void handleError(){
        // increase error_count by 1
        ++error_count;
        if (error_count == 4){
            showResult(null);
        }
        else {
            // gen check id
            String gencheckId = "img_check" + error_count;
            int getcheckId = getResources().getIdentifier(gencheckId, "id", getPackageName());
            ImageView thisCheck = (ImageView) findViewById(getcheckId);

            // change background color of check image to rea
            thisCheck.setBackgroundColor(getResources().getColor(R.color.red));
        }
    }
    private void clearkey(){
        for(int i=1;i<8;i++)
        {
            // generate key id
            String genkeyId =  "btn_" + i;
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            Button thisKey = (Button) findViewById(getkeyId);

            thisKey.setBackgroundColor(getResources().getColor(R.color.default_key));
        }
    }
    private void makeEmptyBox(){
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
                        thisBox.setBackgroundColor(getResources().getColor(R.color.red));
                    }
                }
                preWord = "";
                return 2;
            }
        }
    }
    private void setUp(){
        // get word
        trueWord = wordArray.get(wordCount);
        wordCount++;

        // split string into array
        String[] letterArray = {};
        for(int i=0;i<4;i++){
            letterArray[i] = trueWord.substring(i, i+1);
        }

        // add more 3 letter into the array
        int n = keyArray.length, count = 0;
        for(int i=0;i<n;i++){
            if (!Arrays.asList(keyArray).contains(keyArray[i])){
                letterArray[++n] = keyArray[i];
                count++;

                if(count==4)
                    break;
            }
        }

        // suffle the letter list to make it look like random :)
        Collections.shuffle(Arrays.asList(letterArray));
        Log.d("this word letter", letterArray.toString());

        // set up key
        for (int i=1;i<8;i++)
        {
            String genkeyId =  "btn_" + i;
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            Button thisKey = (Button) findViewById(getkeyId);

            thisKey.setText(letterArray[i-1]);
        }

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