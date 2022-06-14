package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

public class ScoreBoardActivity extends AppCompatActivity {

    ListView listView;
    Animation animation;

    ArrayList<User> users = new ArrayList<>();

    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        ListView leaderBoard = (ListView) findViewById(R.id.lv_listview);

        getUserData();
        Log.d("users", users.toString());
//        sortUser();

        ScoreBoardAdapter adapter = new ScoreBoardAdapter(this, users);
        animation = AnimationUtils.loadAnimation(this, R.anim.animation1);
        leaderBoard.setAdapter(adapter);

        leaderBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(ScoreBoardActivity.this, ""+ users.get(i), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void getUserData() {
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        Log.d("snapshot", );
                        User thisUser = postSnapshot.getValue(User.class);
                        if (thisUser == null) {
                            Log.d("error", "getting error");
                        } else
                            users.add(thisUser);
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void sortUser(){

    }
}