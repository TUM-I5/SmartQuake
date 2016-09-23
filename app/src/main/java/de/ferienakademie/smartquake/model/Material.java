package de.ferienakademie.smartquake.model;

/**
 * Created by alex on 21.09.16.
 */
public class Material {
    private double E = 0;   //Young's modulus
    private double A = 0;   //cross section
    private double I = 0;   //moment of inertia
    private double EA = 0;  //
    private double EI = 0;  //rigidity
    private double h = 0;   //height of beam (input)
    private double b = 0;   //width of beam (input)



    //constructor
    public Material(double b, double h ,double E){
        this.b=b;
        this.h=h;
        this.A=b*h;
        this.E=E;
        this.I=b*h*h*h/12.;
        this.EA=E*A;
        this.EI=E*I;

    }

    public Material(String test){
        if (test.contentEquals("testmat")) {
            this.A = 10;
            this.E = 10e7;
            this.I = 10;
        }
    }
    public double getA() {
        return A;
    }

    public void setNewAandI(double b, double h){ //necessary, if b and h are changed - changes all relevant properties
        A = b*h;
        I = b*h*h*h/12.;
        EA = E*A;
        EI = E*I;
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

    public void setE(double e) {
        E = e;
    }
}
