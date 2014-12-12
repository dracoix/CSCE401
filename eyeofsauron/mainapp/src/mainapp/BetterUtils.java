package mainapp;

public class BetterUtils {

    public static final clsRandom Random = new clsRandom(); //Easy Access

    public static final long time() {
        // Easy access for snap times
        return System.currentTimeMillis();
    }

    public static final double rand() {
        //Better than Math.random() and java.util.Random
        return Random.nextDouble();
    }

    public static final class clsRandom {
        // Light-Weight Randomizer from Clover Jacket
        // Uses M.G. 2003 XORShift Method
        // Uses Rathbun D. 2014 Clover salting method (non-crypto, simplified)

        private static long rnd() {
            // Clover salted hash
            // Hash = F(G() XOR F(G()))
            return mg2003(proc() ^ mg2003(proc()));
        }

        private static long proc() {
            // Clover salt
            // G() = F(X * Salt)
            return mg2003(System.nanoTime() * System.currentTimeMillis());
        }

        private static long mg2003(long x) {
            // M.G. 2003 Xorshift seeded psuedo randomizer

            x ^= x >>> 12;
            x ^= x << 25;
            x ^= x >>> 27;

            return x * 2685821657736338717L;
        }

        public long nextLong() {
            return rnd();
        }

        public int nextInt() {
            return (int) rnd();
        }

        public int nextInt(int m) {
            //Positive Bounded
            return Math.abs(nextInt()) % m;
        }

        public double nextDouble() {
            return Math.abs((double) rnd() / ((double) Long.MAX_VALUE));
        }

        public float nextFloat() {
            return (float) nextDouble();
        }
    }
}
