package laffelrog.jumprangecalc;

class JumpRangeCalculator {

    private double lastJumpRange;
    private double lastDistanceSag;

    public double calcOptJumpRange(double jumpRange, double distanceSag) {
        this.lastJumpRange = jumpRange;
        this.lastDistanceSag = distanceSag;

        int noJumps = (int)(1000 / jumpRange);
        double jumpDistance = noJumps * jumpRange;
        double optimalDistance = jumpDistance - noJumps/4 - distanceSag*2;

        return optimalDistance;
    }

    public double getLastJumpRange() {
        return lastJumpRange;
    }

    public double getLastDistanceSag() {
        return lastDistanceSag;
    }
}
