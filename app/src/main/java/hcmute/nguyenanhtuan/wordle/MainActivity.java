package hcmute.nguyenanhtuan.wordle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    // coordinate
    int row_count = 1;
    int col_count = 1;

    TextView enter;
    TextView delete;

    // Key
    String[] keyArray = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P",
                        "A", "S", "D", "F", "G", "H", "J", "K", "L",
                        "Z", "X", "C", "V", "B", "N", "M"};
    // word in a row
    String preWord;
    // list of word
    ArrayList<String> wordArray;
    // the answer
    String trueWord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enter = (TextView) findViewById(R.id.tv_enter);
        delete = (TextView) findViewById(R.id.tv_del);

        dataInit();

        // keys on click
        for(int i=0;i<keyArray.length;i++)
        {
            // generate key id
            String genkeyId =  "tv_" + keyArray[i];
            int getkeyId = getResources().getIdentifier(genkeyId, "id", getPackageName());
            TextView thisKey = (TextView) findViewById(getkeyId);

            int finalI = i;
            thisKey.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    keyOnclick(keyArray[finalI]);
                }
            });
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d("row", "" + row_count);
//                Log.d("col", "" + col_count);
//                Log.d("check", "" + checkWordValid());
//                Log.d("preword: ", preWord);

                int result = checkWordValid();
                if (col_count == 6)
                {
                    if (result == 0) {
                        Toast.makeText(MainActivity.this, "Not in word list", Toast.LENGTH_SHORT).show();
                    }
                    else if (result == 1){
                        Toast.makeText(MainActivity.this, "You Win", Toast.LENGTH_SHORT).show();
                    }
                    else if (result == 2) {
                        Log.d("in", "co");
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
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (col_count != 1)
                    col_count--;
                String genboxId = "tv_row" + row_count + "_" + col_count;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setText("");
            }
        });
    }
    private void dataInit(){
        preWord = "";
        trueWord = "ALLOW";
        wordArray = new ArrayList<>(Arrays.asList("BREAK", "ABOVE", "BEGIN", "FLASH", "EQUAL",
                "INDEX", "IDEAL", "PRESS", "RAISE", "SHARE"));

    }
    private int checkWordValid() {
        if (preWord.equals(trueWord)) {
            for (int i=1;i<=5;i++)
            {
                String genboxId = "tv_row" + row_count + "_" + i;
                int getboxID = getResources().getIdentifier(genboxId, "id", getPackageName());

                TextView thisBox = (TextView) findViewById(getboxID);
                thisBox.setBackgroundColor(getResources().getColor(R.color.green));
            }
            return 1;
        }
        else {
            if (!Arrays.asList(wordArray).contains(preWord)) {
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

                    if (trueWord.charAt(i) == preWord.charAt(i)) {
                        thisBox.setBackgroundColor(getResources().getColor(R.color.green));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.green));
                    }
                    else if (trueWord.contains(preWord.substring(i, i+1))) {
                        thisBox.setBackgroundColor(getResources().getColor(R.color.yellow));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.yellow));
                    }
                    else {
                        thisBox.setBackgroundColor(getResources().getColor(R.color.birghter_dark));
                        thisKey.setBackgroundColor(getResources().getColor(R.color.birghter_dark));
                    }
                }
                preWord = "";
                return 2;
            }
        }
    }
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