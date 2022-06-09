package hcmute.nguyenanhtuan.wordle;

public class User {
    // user basic info
    private String name;
    private String email;
    private String password;
    private String age;

    // user's data statistics
    private int played;
    private int winRate;
    private int maxStreak;

    public User(){}

    public User(String name, String email, String password, String age, int played, int winRate, int maxStreak) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.played = played;
        this.winRate = winRate;
        this.maxStreak = maxStreak;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }

    public int getMaxStreak() {
        return maxStreak;
    }

    public void setMaxStreak(int maxStreak) {
        this.maxStreak = maxStreak;
    }
}
