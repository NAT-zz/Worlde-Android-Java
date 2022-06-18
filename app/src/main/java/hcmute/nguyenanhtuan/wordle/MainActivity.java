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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import io.github.muddz.styleabletoast.StyleableToast;

public class MainActivity extends AppCompatActivity {

    // coordinate of current box
    int row_count = 1;
    int col_count = 1;

    // views
    TextView enter, delete;
    Dialog dialog, dialogHelp, dialogMenu;
    ImageButton statistic, replay, help, menu;

    // Keys
    String[] keyArray = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                        "A", "S", "D", "F", "G", "H", "J", "K", "L",
                        "Z", "X", "C", "V", "B", "N", "M"};
    // firebase
    private FirebaseDatabase db;
    private DatabaseReference databaseReference;
    // firebase authentication
    private FirebaseAuth mAuth;
    // the current user
    private FirebaseUser currentUser;
    // id of the current user
    private String userID;

    // current user mapping the User class
    User thisUser;

    // the word in the current row
    String preWord="";
    // list of words
    ArrayList<String> wordArray = new ArrayList<>();
    // the answer
    String trueWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // mapping views with variables
        mapping();
        // init data
        dataInit();

        // keys on click logic
        for(int i=0;i<keyArray.length;i++)
        {
            // generate key id
            String genkeyId =  "tv_" + keyArray[i];
            // get the id but in int
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            // get the key
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
        // menu-onclick logic
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the menu pop-up
                showMenu(null);
            }
        });
        // helper-onclick logic
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the helper pop-up
                showHelper(null);
            }
        });
        // statistic-onclick logic
        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the helper pop-up
                showPopUp(null);
            }
        });
        // replay-onclick logic
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            // restart the activity
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

                // get the return value after checking
                int result = checkWordValid();
                // if it's the last letter
                if (col_count == 6)
                {
                    if (result == 0) {
                        // pop a message if the word is not in the word list
                        StyleableToast.makeText(MainActivity.this, "Not in word list", Toast.LENGTH_SHORT, R.style.not_in_word_list).show();
                    }
                    // if player guess the correct word
                    else if (result == 1){
                        // pop a message if if's not the last row
                        if (row_count < 5)
                            StyleableToast.makeText(MainActivity.this, "FANSTACTIC", Toast.LENGTH_SHORT, R.style.you_win).show();
                        else
                            StyleableToast.makeText(MainActivity.this, "PHEW", Toast.LENGTH_SHORT, R.style.you_win).show();

                        // set the user's record with the current row
                        if(row_count == 1)
                            thisUser.getRecord().setFirstWin(thisUser.getRecord().getFirstWin()+1);
                        else if (row_count == 2)
                            thisUser.getRecord().setSecondWin(thisUser.getRecord().getSecondWin()+1);
                        else if (row_count == 3)
                            thisUser.getRecord().setThirdWin(thisUser.getRecord().getThirdWin()+1);
                        else if (row_count == 4)
                            thisUser.getRecord().setFourthWin(thisUser.getRecord().getFourthWin()+1);
                        else if (row_count == 5)
                            thisUser.getRecord().setFifthWin(thisUser.getRecord().getFifthWin()+1);
                        else
                            thisUser.getRecord().setSixthWin(thisUser.getRecord().getSixthWin()+1);

                        // set the user's record about play-times, current streak and win-times
                        thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
                        thisUser.getRecord().setCurrentStreak(thisUser.getRecord().getCurrentStreak()+1);
                        thisUser.getRecord().setWinCount(thisUser.getRecord().getWinCount()+1);
                        // update the record to firebase
                        updateRecord();
                    }
                    // the word is in the word list but not correct
                    else if (result == 2) {
                        // if it's not the last row
                        if (row_count != 6)
                            row_count++;
                        else {
                            // pop the answer if the last try run out
                            StyleableToast.makeText(MainActivity.this, trueWord, Toast.LENGTH_LONG, R.style.the_word).show();

                            // set the user record (the player lost)
                            thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
                            thisUser.getRecord().setCurrentStreak(0);
                            thisUser.getRecord().setLooseCount(thisUser.getRecord().getLooseCount()+1);
                            // update the record to firebase
                            updateRecord();
                        }
                        // increase the "y" coordinate
                        col_count = 1;
                    }
                }
                // pop a message if not
                else StyleableToast.makeText(MainActivity.this, "Finish the word", Toast.LENGTH_LONG, R.style.finish_the_word).show();
            }
        });
        // delete-onclick logic
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if the "y" coordinate is not the first
                if (col_count != 1)
                    col_count--;
                // get the box id
                String genboxId = "tv_row" + row_count + "_" + col_count;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                // remove last letter
                if (preWord != null && preWord.length() > 0) {
                    preWord = preWord.substring(0, preWord.length() - 1);
                }

                // reset the current box
                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setText("");
            }
        });
    }
    // mapping views with variables
    private void mapping(){
        enter = (TextView) findViewById(R.id.tv_enter);
        delete = (TextView) findViewById(R.id.tv_del);

        menu = (ImageButton) findViewById(R.id.btn_menu);
        statistic = (ImageButton) findViewById(R.id.btn_statistics);
        replay = (ImageButton) findViewById(R.id.btn_replay);
        help = (ImageButton) findViewById(R.id.btn_help);
    }
    @Override
    protected void onStart() {
        super.onStart();
        // init the firsebse instance
        mAuth = FirebaseAuth.getInstance();
        // get the current loged-in user
        currentUser = mAuth.getCurrentUser();

        // if no user is loged-in
        if (currentUser==null) {
            // end the activity & to login
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        else{
            // get the current user's id
            userID = currentUser.getUid();
            // print the user's email to log
            Log.d("user", currentUser.getEmail());

            // get users collection on firebase
            databaseReference = FirebaseDatabase.getInstance().getReference("Users");
            // get the user data with the id
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
    // init game data
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
    // check the current word
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
    // update user record
    private void updateRecord(){
        // get the Users collection on firebase
        databaseReference = db.getReference("Users");

        // get the record data of the current user
        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("record")
                .setValue(thisUser.getRecord());
    }
    // show the menu popup
    private void showMenu(View v) {
        // init dialog menu
        dialogMenu = new Dialog(this);
        dialogMenu.setContentView(R.layout.custommenu);

        // mapping
        TextView closeMenu = (TextView) dialogMenu.findViewById(R.id.tv_closemenu);
        Button cont = (Button) dialogMenu.findViewById(R.id.btn_cont);
        Button score = (Button) dialogMenu.findViewById(R.id.btn_score);
        Button newMode = (Button) dialogMenu.findViewById(R.id.btn_newMode);
        Button logOut = (Button) dialogMenu.findViewById(R.id.btn_logout);

        // newmode-onclick logic
        newMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, NewMode1.class));
            }
        });
        // scoreboard-onclick logic
        score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ScoreBoardActivity.class));
            }
        });
        // closemenu-onclick logic
        closeMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMenu.dismiss();
            }
        });
        // continue-onclick logic
        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMenu.dismiss();
            }
        });
        // logout-onclick logic
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                dialogMenu.dismiss();

                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
        
        // make border transparent
        dialogMenu.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // show the helper dialge
        dialogMenu.show();
    }
    // show the helper popup
    private void showHelper(View v){
        // init dialog helper
        dialogHelp = new Dialog(this);
        dialogHelp.setContentView(R.layout.customhelper);

        // mapping
        TextView closeHelper = (TextView) dialogHelp.findViewById(R.id.tv_closehelper);

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
    // set data for pie chart
    private void pieChartData(){
        // mapping piechart
        PieChart pieChart = dialog.findViewById(R.id.piechart);
        // set value for pie chart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        // get total guess
        int totalGuess = thisUser.getRecord().getFirstWin() + thisUser.getRecord().getSecondWin()
                + thisUser.getRecord().getThirdWin() + thisUser.getRecord().getFourthWin()
                + thisUser.getRecord().getFifthWin() + thisUser.getRecord().getSixthWin();

        // calculate the ratio with each row's win
        float first = (thisUser.getRecord().getFirstWin()/ (float) totalGuess) * 100;
        float second = (thisUser.getRecord().getSecondWin()/ (float) totalGuess) * 100;
        float third = (thisUser.getRecord().getThirdWin()/ (float) totalGuess) * 100;
        float fourth = (thisUser.getRecord().getFourthWin()/ (float) totalGuess) * 100;
        float fifth = (thisUser.getRecord().getFifthWin()/ (float) totalGuess) * 100;
        float sixth = (thisUser.getRecord().getSixthWin()/ (float) totalGuess) * 100;

        // init pie chart entry
        PieEntry pieEntry = new PieEntry(first, "First");
        // add values in array list
        pieEntries.add(pieEntry);

        pieEntry = new PieEntry(second, "Second");
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(third, "Third");
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(fourth, "Fourth");
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(fifth, "Fifth");
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(sixth, "Sixth");
        pieEntries.add(pieEntry);

        // init pie data set
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Guess");
        // set colors
        pieDataSet.setColors( Color.rgb(193, 37, 82),
                            Color.rgb(255, 102, 0),
                            Color.rgb(245, 199, 0),
                            Color.rgb(106, 150, 31),
                            Color.rgb(179, 100, 53),
                            Color.rgb(219, 9, 198));
        // set pie data
        pieChart.setData(new PieData(pieDataSet));
        // set animation
        pieChart.animateXY(2000, 2000);
        // hide description
        pieChart.getDescription().setTextColor(R.color.white);
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
        played.setText(String.valueOf(thisUser.getRecord().getPlayed()));
        loose.setText(String.valueOf(thisUser.getRecord().getLooseCount()));
        win.setText(String.valueOf(thisUser.getRecord().getWinCount()));
        streak.setText(String.valueOf(thisUser.getRecord().getCurrentStreak()));
        pieChartData();

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