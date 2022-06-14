package hcmute.nguyenanhtuan.wordle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class ScoreBoardAdapter extends BaseAdapter {
    ScoreBoardActivity scoreBoardActivity;
    ArrayList<User> users;
    Animation animation;

    public ScoreBoardAdapter(ScoreBoardActivity scoreBoardActivity, ArrayList<User> users) {
        this.scoreBoardActivity = scoreBoardActivity;
        this.users = users;
    }

    private static int getRandom(int max){
        return (int) (Math.random()*max);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(scoreBoardActivity).inflate(R.layout.score_item_layout, viewGroup, false);
        animation = AnimationUtils.loadAnimation(scoreBoardActivity, R.anim.animation1);

        TextView userName, pos, score;
        LinearLayout ll_bg;

        // mapping
        ll_bg = view.findViewById(R.id.ll_bg);
        userName = view.findViewById(R.id.tv_userName);
        pos = view.findViewById(R.id.tv_pos);
        score = view.findViewById(R.id.tv_score);

        // set content
        userName.setText(users.get(i).getName().toString());
        pos.setText(i+1);
        score.setText(users.get(i).getScore().toString());

        // set animation
        userName.setAnimation(animation);
        score.setAnimation(animation);

        return view;
    }
}
