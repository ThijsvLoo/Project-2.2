package com.mygdx.mass.Algorithms;

public class GapRate {

    public int MNG; //upper limit number of gaps
    public int UNG; //manually counted number of gaps
    public int ANG; //algorithm computed number of gaps
    private double GRR; //rate we want to get
    private double OGRR;
    private double AGRR;


    public GapRate() {}

    public double getOGRR() {
        return OGRR = UNG/MNG;
    }

    public double getAGRR() {
        return AGRR = ANG/MNG;
    }

    public double getGRR() {
       return GRR = Math.abs(getAGRR()-getOGRR());
    }

    public void setMNG(int MNG) {
        this.MNG = MNG;
    }

    public void setUNG(int UNG) {
        this.UNG = UNG;
    }

    public void setANG(int ANG) {
        this.ANG = ANG;
    }

}
