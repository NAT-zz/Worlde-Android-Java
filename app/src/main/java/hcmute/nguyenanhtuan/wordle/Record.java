package hcmute.nguyenanhtuan.wordle;

public class Record {

    private int played;
    private int winCount;
    private int looseCount;
    private int currentStreak;

    private int firstWin;
    private int secondWin;
    private int thirdWin;
    private int fourthWin;

    public Record() {}

    public Record(int played, int winCount, int looseCount, int currentStreak, int firstWin, int secondWin, int thirdWin, int fourthWin) {
        this.played = played;
        this.winCount = winCount;
        this.looseCount = looseCount;
        this.currentStreak = currentStreak;
        this.firstWin = firstWin;
        this.secondWin = secondWin;
        this.thirdWin = thirdWin;
        this.fourthWin = fourthWin;
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

    public int getFirstWin() {
        return firstWin;
    }

    public void setFirstWin(int firstWin) {
        this.firstWin = firstWin;
    }

    public int getSecondWin() {
        return secondWin;
    }

    public void setSecondWin(int secondWin) {
        this.secondWin = secondWin;
    }

    public int getThirdWin() {
        return thirdWin;
    }

    public void setThirdWin(int thirdWin) {
        this.thirdWin = thirdWin;
    }

    public int getFourthWin() {
        return fourthWin;
    }

    public void setFourthWin(int fourthWin) {
        this.fourthWin = fourthWin;
    }
}
