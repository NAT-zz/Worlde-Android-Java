package hcmute.nguyenanhtuan.wordle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreBoardActivity extends AppCompatActivity {

    // init animation
    Animation animation;
    // init list of users
    ArrayList<User> users = new ArrayList<>();

    // get firebase's instance
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

        // mapping
        ListView leaderBoard = (ListView) findViewById(R.id.lv_listview);
        // get users data from firebase
        getUserData();

        // init adapter
        ScoreBoardAdapter adapter = new ScoreBoardAdapter(this, users);
        // load animation
        animation = AnimationUtils.loadAnimation(this, R.anim.animation1);
        // set adapter for listView
        leaderBoard.setAdapter(adapter);

        // items on-click logic
        leaderBoard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // pop a message of clicked's user's info
                Toast.makeText(ScoreBoardActivity.this, ""+ users.get(i), Toast.LENGTH_SHORT).show();
            }
        });

    }
    // get users data from firebase
    private void getUserData() {
        // get the Users's collection
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        // map the user with class
                        User thisUser = new User(
                                userSnapshot.getValue(User.class).getName().toString(),
                                null, null, null, userSnapshot.getValue(User.class).getRecord());
                        // calculater the score for this user
                        thisUser.setScore();
                        // add this user to list
                        users.add(thisUser);
                    }
                // sort the users ascendent with the score
                Collections.sort(users, new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return t1.getScore().compareTo(user.getScore());
                    }
                });
                }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}