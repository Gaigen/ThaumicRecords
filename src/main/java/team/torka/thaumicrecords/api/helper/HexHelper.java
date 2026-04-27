package team.torka.thaumicrecords.api.helper;


// From thaumcraft.common.lib.utils.HexUtils
// I'll replace it later with cube coordinates
public class HexHelper {
    static final int[][] NEIGHBOURS = new int[][]{{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};

    public static Hex getRoundedHex(double qq, double rr) {
        return getRoundedCubicHex(qq, rr, -qq - rr).toHex();
    }

    public static CubicHex getRoundedCubicHex(double xx, double yy, double zz) {
        int rx = (int) Math.round(xx);
        int ry = (int) Math.round(yy);
        int rz = (int) Math.round(zz);
        double x_diff = Math.abs((double) rx - xx);
        double y_diff = Math.abs((double) ry - yy);
        double z_diff = Math.abs((double) rz - zz);
        if (x_diff > y_diff && x_diff > z_diff) {
            rx = -ry - rz;
        } else if (y_diff > z_diff) {
            ry = -rx - rz;
        } else {
            rz = -rx - ry;
        }

        return new CubicHex(rx, ry, rz);
    }

    public static class Hex {
        public int q = 0;
        public int r = 0;

        public Hex(int q, int r) {
            this.q = q;
            this.r = r;
        }

        public CubicHex toCubicHex() {
            return new CubicHex(this.q, this.r, -this.q - this.r);
        }

        public Pixel toPixel(int size) {
            return new Pixel((double) size * (double) 1.5F * (double) this.q,
                    (double) size * Math.sqrt(3.0F) * ((double) this.r + (double) this.q / (double) 2.0F));
        }

        public Hex getNeighbour(int direction) {
            int[] d = HexHelper.NEIGHBOURS[direction];
            return new Hex(this.q + d[0], this.r + d[1]);
        }

        public boolean equals(Hex h) {
            return h.q == this.q && h.r == this.r;
        }

        public String toString() {
            return this.q + ":" + this.r;
        }
    }

    public static class CubicHex {
        public int x;
        public int y;
        public int z;

        public CubicHex(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Hex toHex() {
            return new Hex(this.x, this.z);
        }
    }

    public static class Pixel {
        public double x;
        public double y;

        public Pixel(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public Hex toHex(int size) {
            double qq = 0.6666666666666666 * this.x / (double) size;
            double rr = (0.3333333333333333 * Math.sqrt(3.0F) * -this.y - 0.3333333333333333 * this.x) / (double) size;
            return HexHelper.getRoundedHex(qq, rr);
        }
    }
}
