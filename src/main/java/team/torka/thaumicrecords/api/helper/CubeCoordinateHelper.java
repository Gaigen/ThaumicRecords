package team.torka.thaumicrecords.api.helper;


public class CubeCoordinateHelper {
    public static final int[][] DIRECTIONS = {{1, 0}, {1, -1}, {0, -1}, {-1, 0}, {-1, 1}, {0, 1}};

    public record CubeHex(int x, int z) {

        public int y() {
            return -x - z;
        }

        public ScreenPos toPixel(float size) {
            return new ScreenPos(size * 1.5 * this.x, size * Math.sqrt(3.0) * (this.z + this.x / 2.0));
        }

        public CubeHex getNeighbor(int direction) {
            int[] d = DIRECTIONS[Math.floorMod(direction, 6)];
            return new CubeHex(x + d[0], z + d[1]);
        }

        public String toKey() {
            return this.x + "," + this.z;
        }

        public static CubeHex fromKey(String key) {
            try {
                String[] parts = key.split(",");
                int x = Integer.parseInt(parts[0]);
                int z = Integer.parseInt(parts[1]);
                return new CubeHex(x, z);
            } catch (Exception e) {
                return new CubeHex(0, 0);
            }
        }
    }

    public record ScreenPos(double x, double y) {
    }

    public static CubeHex pixelToCube(double px, double py, float size) {
        double fx = (2.0 / 3.0 * px) / size;
        double fz = (-1.0 / 3.0 * px + Math.sqrt(3.0) / 3.0 * py) / size;
        double fy = -fx - fz;
        int rx = (int) Math.round(fx);
        int rz = (int) Math.round(fz);
        int ry = (int) Math.round(fy);
        double x_diff = Math.abs(rx - fx);
        double z_diff = Math.abs(rz - fz);
        double y_diff = Math.abs(ry - fy);
        if (x_diff > z_diff && x_diff > y_diff) {
            rx = -rz - ry;
        } else if (z_diff > y_diff) {
            rz = -rx - ry;
        }
        return new CubeHex(rx, rz);
    }
}
