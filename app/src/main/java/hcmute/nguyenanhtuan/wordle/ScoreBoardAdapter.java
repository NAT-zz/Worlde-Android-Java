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

        TextView userName;
        LinearLayout ll_bg;
        ll_bg = view.findViewById(R.id.ll_bg);
        userName = view.findViewById(R.id.tv_userName);

        int num = getRandom(users.size());
        if (num == 1){
        } else if (num == 2){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_2));
        } else if (num == 3){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_3));
        }else if (num == 4){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_4));
        }else if (num == 5){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_5));
        }else if (num == 6){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_6));
        }else if (num == 7){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_7));
        }else if (num == 8){
            ll_bg.setBackground(ContextCompat.getDrawable(scoreBoardActivity, R.drawable.gradient_8));
        }

        userName.setText(users.get(i).getName().toString());
        userName.setAnimation(animation);

        return null;
    }
}
