package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
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
import com.github.mikephil.charting.utils.ColorTemplate;
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
    Dialog dialog, dialogHelp, dialogMenu;
    ImageButton statistic, replay, help, menu;

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
        // menu-onclick logic
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu(null);
            }
        });
        // helper-onclick logic
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHelper(null);
            }
        });
        // statistic-onclick logic
        statistic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(null);
            }
        });
        // replay-onclick logic
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
                        StyleableToast.makeText(MainActivity.this, "Not in word list", Toast.LENGTH_LONG, R.style.not_in_word_list).show();
                    }
                    else if (result == 1){
                        if (row_count < 5)
                            StyleableToast.makeText(MainActivity.this, "FANSTACTIC", Toast.LENGTH_LONG, R.style.you_win).show();
                        else
                            StyleableToast.makeText(MainActivity.this, "PHEW", Toast.LENGTH_LONG, R.style.you_win).show();

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

                        thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
                        thisUser.getRecord().setCurrentStreak(thisUser.getRecord().getCurrentStreak()+1);
                        thisUser.getRecord().setWinCount(thisUser.getRecord().getWinCount()+1);
                        updateRecord();
                    }
                    else if (result == 2) {
                        if (row_count != 6)
                            row_count++;
                        else {
                            StyleableToast.makeText(MainActivity.this, trueWord, Toast.LENGTH_LONG, R.style.the_word).show();

                            thisUser.getRecord().setPlayed(thisUser.getRecord().getPlayed()+1);
                            thisUser.getRecord().setCurrentStreak(0);
                            thisUser.getRecord().setLooseCount(thisUser.getRecord().getLooseCount()+1);
                            updateRecord();
                        }
                        col_count = 1;
                    }
                }
                else StyleableToast.makeText(MainActivity.this, "Finish the word", Toast.LENGTH_LONG, R.style.finish_the_word).show();
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

        menu = (ImageButton) findViewById(R.id.btn_menu);
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
    // update user record
    private void updateRecord(){
        databaseReference = db.getReference("User");

        databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("record")
                .setValue(thisUser.getRecord());
    }
    // show the menu popup
    private void showMenu(View v) {
        // init dialog menu
        dialogMenu = new Dialog(this);
        dialogMenu.setContentView(R.layout.custommenu);

        // mapping
        TextView closeHelper = (TextView) dialogMenu.findViewById(R.id.tv_closehelper);
        Button cont = (Button) dialogMenu.findViewById(R.id.btn_cont);
        Button score = (Button) dialogMenu.findViewById(R.id.btn_score);
        Button saved = (Button) dialogMenu.findViewById(R.id.btn_saved);
        Button newMode = (Button) dialogMenu.findViewById(R.id.btn_newMode);
        Button logOut = (Button) dialogMenu.findViewById(R.id.btn_logout);

        // closemenu-onclick logic
        closeHelper.setOnClickListener(new View.OnClickListener() {
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
        PieChart pieChart = findViewById(R.id.piechart);
        // set value for pie chart
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        // get total guess
        int totalGuess = thisUser.getRecord().getFirstWin() + thisUser.getRecord().getSecondWin()
                + thisUser.getRecord().getThirdWin() + thisUser.getRecord().getFourthWin()
                + thisUser.getRecord().getFifthWin() + thisUser.getRecord().getSixthWin();

        float first = ((float) (thisUser.getRecord().getFirstWin()/totalGuess)) * 100;
        float second = ((float) (thisUser.getRecord().getSecondWin()/totalGuess)) * 100;
        float third = ((float) (thisUser.getRecord().getThirdWin()/totalGuess)) * 100;
        float fourth = ((float) (thisUser.getRecord().getFourthWin()/totalGuess)) * 100;
        float fifth = ((float) (thisUser.getRecord().getFifthWin()/totalGuess)) * 100;
        float sixth = ((float) (thisUser.getRecord().getSixthWin()/totalGuess)) * 100;

        // init pie chart entry
        PieEntry pieEntry = new PieEntry(1, first);
        // add values in array list
        pieEntries.add(pieEntry);

        pieEntry = new PieEntry(2, second);
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(3, third);
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(4, fourth);
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(5, fifth);
        pieEntries.add(pieEntry);
        pieEntry = new PieEntry(6, sixth);
        pieEntries.add(pieEntry);

        // init pie data set
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Guess count");
        // set colors
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        // set pie data
        pieChart.setData(new PieData(pieDataSet));
        // set animation
        pieChart.animateXY(2000, 2000);
        // hide description
        // pieChart.getDescription().setEnabled(false);
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