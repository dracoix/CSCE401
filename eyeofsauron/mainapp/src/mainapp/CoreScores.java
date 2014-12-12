package mainapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CoreScores {

    private static int CURRENT_SCORE;
    private static ArrayList<ScoreEntry> SCORES = new ArrayList<>();

    private static void SAVE_SCORES() {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            File f = new File("scores.dat");
            fos = new FileOutputStream(f, false);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(SCORES);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CoreEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CoreEngine.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fos.close();
            } catch (IOException ex) {
                Logger.getLogger(CoreEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private static void LOAD_SCORES() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File f = new File("scores.dat");
            if (!f.exists()) {
                SAVE_SCORES();
            }
            fis = new FileInputStream(f);
            ois = new ObjectInputStream(fis);
            SCORES = (ArrayList<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            SCORES = new ArrayList<>();
            Logger.getLogger(CoreEngine.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(CoreEngine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static void init()
    {
        LOAD_SCORES();
    }
    
    public static int getCurrentScore()
    {
        return CURRENT_SCORE;
    }
    
    public static int count()
    {
        return SCORES.size();
    }
    
    public static String getPrinted(int index)
    {
        if (index >= SCORES.size()) return "";
        return SCORES.get(index).toString();
    }
    
    public static void modifyScoreBy(int amount)
    {
        CURRENT_SCORE += amount;
    }
    public static void addNewScoreEntry(String name) {
        SCORES.add(new ScoreEntry(name, CURRENT_SCORE));
        CURRENT_SCORE = 0;
        Collections.sort(SCORES);
        SAVE_SCORES();
    }

    private static class ScoreEntry implements Comparable<ScoreEntry>, Serializable {

        // Modified from score homework assignment
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
        public String toString() {
            return this.name + "  \t._._._._._._._._._.\t" + this.score;
        }

    }

}
