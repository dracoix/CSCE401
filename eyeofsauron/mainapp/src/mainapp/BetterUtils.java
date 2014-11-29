/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

/**
 *
 * @author Expiscor
 */
public class BetterUtils {

    public static long time() {
        return System.currentTimeMillis();
    }

    public static final clsRandom Random = new clsRandom();
    
    public static final class clsRandom {

        private static long rnd() {
            return mg2003(proc() ^ mg2003(proc()));
        }
        
        private static long proc()
        {
            return  mg2003(System.nanoTime() * System.currentTimeMillis());
        }

        private static long mg2003(long x) {
            x ^= x >>> 12;
            x ^= x << 25;
            x ^= x >>> 27;

            return x * 2685821657736338717L;
        }

        public long nextLong() {
            return rnd();
        }
        
        public int nextInt()
        {
            return (int) rnd();
        }
        
        public int nextInt(int m)
        {
            return Math.abs(nextInt()) % m;
        }
        
        public double nextDouble()
        {
            return Math.abs((double) rnd() / ((double)Long.MAX_VALUE));
        }
        
        public float nextFloat()
        {
            return (float) nextDouble();
        }
    }
}
