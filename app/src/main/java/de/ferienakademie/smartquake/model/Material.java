package de.ferienakademie.smartquake.model;

/**
 * Created by alex on 21.09.16.
 */
public class Material {
    private double E = 0;
    private double A = 0;
    private double I = 0;

    //constructor
    public Material(double A,double E,double I){
        this.A=A;
        this.E=E;
        this.I=I;
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



    public double getE() {
        return E;
    }

    public void setE(double e) {
        E = e;
    }
}
