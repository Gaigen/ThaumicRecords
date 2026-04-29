package team.torka.thaumicrecords.api.helper;


public class CubeCoordinateHelper {
    public static final int[][] DIRECTIONS = {{1, -1, 0}, {1, 0, -1}, {0, 1, -1}, {-1, 1, 0}, {-1, 0, 1}, {0, -1, 1}};

    public record CubeHex(int x, int y, int z) {
        public CubeHex {
            if (x + y + z != 0) {
                y = -x - z;
            }
        }

        public ScreenPos toPixel(float size) {
            return new ScreenPos(size * 1.5 * this.x, size * Math.sqrt(3.0) * (this.z + this.x / 2.0));
        }

        public CubeHex getNeighbour(int direction) {
            int[] d = DIRECTIONS[direction % 6];
            return new CubeHex(x + d[0], y + d[1], z + d[2]);
        }

        public String toKey() {
            return this.x + "," + this.z;
        }

        public static CubeHex fromKey(String key) {
            try {
                String[] parts = key.split(",");
                int x = Integer.parseInt(parts[0]);
                int z = Integer.parseInt(parts[1]);
                return new CubeHex(x, -x - z, z);
            } catch (Exception e) {
                return new CubeHex(0, 0, 0);
            }
        }
    }

    public record ScreenPos(double x, double y) {
    }

    public static CubeHex pixelToCube(double px, double py, float size) {
        double q = (2.0 / 3.0 * px) / size;
        double r = (-1.0 / 3.0 * px + Math.sqrt(3.0) / 3.0 * py) / size;
        double s = -q - r;
        int rx = (int) Math.round(q);
        int ry = (int) Math.round(r);
        int rz = (int) Math.round(s);
        double x_diff = Math.abs(rx - q);
        double y_diff = Math.abs(ry - r);
        double z_diff = Math.abs(rz - s);
        if (x_diff > y_diff && x_diff > z_diff) {
            rx = -ry - rz;
        } else if (y_diff > z_diff) {
            ry = -rx - rz;
        } else {
            rz = -rx - ry;
        }
        return new CubeHex(rx, ry, rz);
    }

    public static int getDistance(CubeHex a, CubeHex b) {
        return (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z)) / 2;
    }
}
