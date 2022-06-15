package hcmute.nguyenanhtuan.wordle;

public class User {
    // user basic info
    private String name;
    private String email;
    private String password;
    private String age;

    private Record record;
    private Float score;

    public User(){}

    public User(String name, String email, String password, String age, Record record) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.age = age;
        this.record = record;
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

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public void setScore(){
        if (this.getRecord().getPlayed() == 0)
            this.score = (float) 0;
        else
            this.score = (this.getRecord().getWinCount() / (float) this.getRecord().getPlayed()) * 100;
    }
    public Float getScore() {
        return score;
    }
}
