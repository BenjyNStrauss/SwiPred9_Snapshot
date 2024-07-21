package org.liblinear;

public class ParameterSearchResult {

    private final double bestC;
    private final double bestScore;
    private final double bestP;

    public ParameterSearchResult(double bestC, double bestScore, double bestP) {
        this.bestC = bestC;
        this.bestScore = bestScore;
        this.bestP = bestP;
    }

    public double getBestC() {
        return bestC;
    }

    public double getBestScore() {
        return bestScore;
    }

    public double getBestP() {
        return bestP;
    }
}
