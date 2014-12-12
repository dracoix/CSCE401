package mainapp;

import java.io.Serializable;

public class ScoreEntry implements Comparable<ScoreEntry>, Serializable {

    // From Homework
    
    private final String name;
    private final int score;

    public ScoreEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return this.name;
    }

    public Integer getScore() {
        return this.score;
    }

    @Override
    public int compareTo(ScoreEntry o) {
        return 0 - this.getScore().compareTo(o.getScore());
    }
    
    @Override
    public String toString()
    {
        return this.name + "  \t._._._._._._._._._.\t" + this.score;
    }

}
