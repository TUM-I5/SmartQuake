package de.ferienakademie.smartquake.model;

import java.util.List;

/**
 * Created by alex on 21.09.16.
 */
public class Material {
    protected double E = 0;   //Young's modulus
    protected double A = 0;   //cross section
    protected double I = 0;   //moment of inertia
    protected double EA = 0;  //
    protected double EI = 0;  //rigidity
    protected double h = 0;   //height of beam (input)
    protected double b = 0;   //width of beam (input)

    protected double rho = 0;
    protected double alpha = 0;   //alpha for mass matrix
    //may have to change zeroes

    //constructor
    public Material(double b, double h ,double E, double rho, double alpha){
        this.b=b;
        this.h=h;
        this.E=E;
        this.A=b*h;
        this.I=b*h*h*h/12.;
        this.EA=E*A;
        this.EI=E*I;
        this.rho = rho;
        this.alpha = alpha;

    }

    public Material(){
        this(0.1, 0.1, 210e9, 7860, 0.005); //SI-Units - use this (steel) for creating standard beam.
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

    public double getb(){
        return b;
    }

    public double geth(){
        return h;
    }

    public void setNewProperties(double b, double h){ //necessary, if b and h are changed - changes all relevant properties
        this.b = b;
        this.h = h;
        A = b*h;
        I = b*h*h*h/12.;
        EA = E*A; //update
        EI = E*I; //update
    }

    public void setA(double a) {  A = a; }

    public void setI(double i) {  I = i; }

    public void setEA(double EA) {  this.EA = EA; }

    public void setEI(double EI) {  this.EI = EI;  }

    public void setE(double e) {  E = e;  }

    public void setRho(double rho)  { this.rho = rho;}

    public void setAlpha(double alpha)  {this.alpha = alpha;}

    /*
    public double getI() { return I;   }
    public double getEA() { return EA; }
    public double getEI() {  return EI;  }
    public double getRho() { return rho; }
    public double getAlpha() { return alpha;}
    */
}
