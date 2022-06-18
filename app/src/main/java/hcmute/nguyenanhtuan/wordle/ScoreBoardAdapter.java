package hcmute.nguyenanhtuan.wordle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;

public class ScoreBoardAdapter extends BaseAdapter {
    // init socreboard activity
    ScoreBoardActivity scoreBoardActivity;
    // init usres array
    ArrayList<User> users;
    // init animation
    Animation animation;

    // adapter's constructer with arguments
    public ScoreBoardAdapter(ScoreBoardActivity scoreBoardActivity, ArrayList<User> users) {
        this.scoreBoardActivity = scoreBoardActivity;
        this.users = users;
    }

    // get the user's list size
    @Override
    public int getCount() {
        return users.size();
    }

    // get index of the current user
    @Override
    public Object getItem(int i) {
        return i;
    }

    // get id of current user
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // inflate the item for each user
        view = LayoutInflater.from(scoreBoardActivity).inflate(R.layout.score_item_layout, viewGroup, false);
        // load the animation for each item
        animation = AnimationUtils.loadAnimation(scoreBoardActivity, R.anim.animation1);

        // views
        TextView userName, pos, score;

        // mapping
        userName = view.findViewById(R.id.tv_userName);
        pos = view.findViewById(R.id.tv_pos);
        score = view.findViewById(R.id.tv_score);

        // set content
        userName.setText(users.get(i).getName().toString());
        pos.setText(String.valueOf(i+1));
        score.setText(users.get(i).getScore().toString());

        // set animation
        userName.setAnimation(animation);
        score.setAnimation(animation);

        return view;
    }
}
