package whackamole;

import java.util.ArrayList;
import java.util.Iterator;
import static whackamole.BetterRandom.rnd;
import static whackamole.CalibCore.calibrate;
import static whackamole.CalibCore.getOffsetX;
import static whackamole.CalibCore.getOffsetY;
import static whackamole.RenderCore.SCREEN_HEIGHT;
import static whackamole.RenderCore.SCREEN_WIDTH;

public class EngineCore {

    public static final int MASTER_FPS = 30;
    public static final int MASTER_FRAME_TIME = 1000 / MASTER_FPS;

    public static double WINDOW_Y, WINDOW_X;
    public static double MOVE_Y, MOVE_X;
    public static boolean MOUSE_DOWN;
    public static boolean APP_EXIT;
    public static boolean MOUSE_MOVE;
    
    public static BetterPoint2D REAL_MOUSE_POS = new BetterPoint2D();
    public static BetterPoint2D FUZZ_MOUSE_POS = new BetterPoint2D();

    public static double MASTER_MOUSE_X;
    public static double MASTER_MOUSE_Y;

    public static double MASTER_POWER_BAR;

    public static int GRID = 4;

    public static final ArrayList<MoleObject> MOLES = new ArrayList<>();

    public static boolean[] FILLED_SLOTS = new boolean[GRID * GRID];
    public static BetterPoint2D[] SLOT_POSITIONS = new BetterPoint2D[GRID * GRID];

    public static void prepEngineCore() {
        double x = SCREEN_WIDTH / (GRID * 2);
        double y = SCREEN_HEIGHT / (GRID * 2);
        int i = 0;

        for (int j = 0; j < GRID; j++) {
            for (int k = 0; k < GRID; k++) {
                SLOT_POSITIONS[i] = new BetterPoint2D((k * x * 2) + x, (j * y * 2) + y);
                i++;
            }
        }

//        SLOT_POSITIONS[0] = new BetterPoint2D(x, y);
//        SLOT_POSITIONS[1] = new BetterPoint2D(x * 3, y);
//        SLOT_POSITIONS[2] = new BetterPoint2D(x * 5, y);
//
//        SLOT_POSITIONS[3] = new BetterPoint2D(x, y * 3);
//        SLOT_POSITIONS[4] = new BetterPoint2D(x * 3, y * 3);
//        SLOT_POSITIONS[5] = new BetterPoint2D(x * 5, y * 3);
//
//        SLOT_POSITIONS[6] = new BetterPoint2D(x, y * 5);
//        SLOT_POSITIONS[7] = new BetterPoint2D(x * 3, y * 5);
//        SLOT_POSITIONS[8] = new BetterPoint2D(x * 5, y * 5);
    }

    public static void ADD_NEW_MOLE() {
        if (MOLES.size() >= GRID * GRID) {
            return;
        }
        boolean unfilled = true;
        int try_slot;
        while (unfilled) {
            try_slot = (int) Math.abs(rnd() % (GRID * GRID));
            if (FILLED_SLOTS[try_slot] == false) {
                FILLED_SLOTS[try_slot] = true;
                unfilled = false;
                MOLES.add(new MoleObject(try_slot, SLOT_POSITIONS[try_slot]));
            }
        }
    }

    public static void engineTick() {
        REAL_MOUSE_POS.set(MASTER_MOUSE_X, MASTER_MOUSE_Y);
        FUZZ_MOUSE_POS.MidpointAndSet(REAL_MOUSE_POS);
        MOVE_X = FUZZ_MOUSE_POS.getX();
        MOVE_Y = FUZZ_MOUSE_POS.getY();
        if (MOUSE_MOVE) {
            calibrate(MOVE_X, MOVE_Y, SCREEN_WIDTH, SCREEN_HEIGHT);
        }
        MOUSE_MOVE = false;
        Iterator<MoleObject> itr = MOLES.iterator();
        MoleObject m;
        while (itr.hasNext()) {
            m = itr.next();
            m.tick();
            if (m.collided(getOffsetX(MOVE_X, SCREEN_WIDTH), getOffsetY(MOVE_Y, SCREEN_HEIGHT))) {
                if (MOUSE_DOWN) {
                    m.bop();
                }
            }
            if (m.garbage()) {
                itr.remove();
            }
        }
    }

}
