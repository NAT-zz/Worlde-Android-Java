package hcmute.nguyenanhtuan.wordle;

public class User {
    // user basic info
    private String name;
    private String email;
    private String password;
    private String age;

    // user's data statistics
    private int played;
    private int winCount;
    private int looseCount;
    private int currentStreak;

    public User(){}

    public User(String name, String email, String password, String age, int played, int winCount, int looseCount, int currentStreak) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.played = played;
        this.winCount = winCount;
        this.looseCount = looseCount;
        this.currentStreak = currentStreak;
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

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLooseCount() {
        return looseCount;
    }

    public void setLooseCount(int looseCount) {
        this.looseCount = looseCount;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }
}
