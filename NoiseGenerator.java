import java.util.Random;

public class NoiseGenerator {
    private int[] p = new int[512];

    public NoiseGenerator(double seed)
    {
        int[] permutation = new int[256];
        for (int i = 0; i < 256; i++) permutation[i] = i;

        Random random = new Random((long)seed);
        for (int i = 255; i > 0; i--) {
            int index = random.nextInt(i + 1);
            int temp = permutation[i];
            permutation[i] = permutation[index];
            permutation[index] = temp;
        }
        for (int i = 0; i < 512; i++) p[i] = permutation[i & 255];
    }

    public double noise(double x, double y, int octaves)
    {
        double value = 0, amplitude = 1, frequency = 1, max = 0;
        for (int i = 0; i < octaves; i++) {
            value += perlin(x * frequency, y * frequency) * amplitude;
            max += amplitude;
            amplitude *= 0.5;
            frequency *= 2;
        }
        return value / max;
    }

    private double perlin(double x, double y)
    {
        int X = ((int) Math.floor(x)) & 255;
        int Y = ((int) Math.floor(y)) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x), v = fade(y);
        int aa = p[p[X] + Y], ab = p[p[X] + Y + 1];
        int ba = p[p[X + 1] + Y], bb = p[p[X + 1] + Y + 1];
        return lerp(v, lerp(u, grad(aa, x, y), grad(ba, x - 1, y)), lerp(u, grad(ab, x, y - 1), grad(bb, x - 1, y - 1)));
    }

    private double fade(double t) { return t * t * t * (t * (t * 6 - 15) + 10); }
    private double lerp(double t, double a, double b) { return a + t * (b - a); }
    private double grad(int hash, double x, double y) {
        int h = hash & 7;
        double u = h < 4 ? x : y, v = h < 4 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }
}