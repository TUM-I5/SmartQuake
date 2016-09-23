package de.ferienakademie.smartquake.model;

/**
 * Created by alex on 21.09.16.
 */
public class Material {
    private double E = 0;
    private double A = 0;
    private double I = 0;
    private double EA = 0;
    private double EI = 0;
    private double rho = 0;
    private double alpha = 0;   //alpha for mass matrix
    //may have to change zeroes

    //constructor
    public Material(double A,double E,double I,double rho,double alpha){
        this.A=A;
        this.E=E;
        this.I=I;
        this.EA=E*A;
        this.EI=E*I;
        this.rho = rho;
        this.alpha = alpha;

    }

    public Material(String test){
        if (test.contentEquals("testmat")) {
            this.A = 10;
            this.E = 10e7;
            this.I = 10;
            this.rho = 7860;
        }
    }
    public double getA() {
        return A;
    }

    public void setA(double a) {
        A = a;
    }

    public double getI() {
        return I;
    }

    public void setI(double i) {
        I = i;
    }

    public double getEA() {
        return EA;
    }

    public void setEA(double EA) {
        this.EA = EA;
    }

    public double getEI() {
        return EI;
    }

    public void setEI(double EI) {
        this.EI = EI;
    }

    public double getE() {
        return E;
    }

    public void setE(double e) {  E = e;  }

    public double getRho() { return rho; }

    public void setRho(double rho)  { this.rho = rho;}

    public double getAlpha() { return alpha;}

    public void setAlpha()  {this.alpha = alpha;}
}
