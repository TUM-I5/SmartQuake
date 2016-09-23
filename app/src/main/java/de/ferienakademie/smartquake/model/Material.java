package de.ferienakademie.smartquake.model;

import java.util.List;

/**
 * Created by alex on 21.09.16.
 */
public class Material {

    public static Material STEEL = new Material();
    public static Material WOOD = new Material(0.1,0.1,12e9,600,0.005); // Source: Holzbau, wikipedia
    public static Material CONCRETE = new Material(0.1,0.1,32e9,2400,0.005); // Source: Betonbau, wikipedia
    public static Material BAMBOO = new Material(0.1,0.1,19e9,1000,0.005);
    public static Material SOMETHING = new Material(); //TODO Add values and more materials!


    protected double E = 0;   //Young's modulus
    protected double A = 0;   //cross section
    protected double I = 0;   //moment of inertia
    protected double EA = 0;  //
    protected double EI = 0;  //rigidity
    protected double h = 0;   //height of beam (input)
    protected double b = 0;   //width of beam (input)
    protected double m= 0;
    protected double c= 0;

    protected double rho = 0;     //density of material
    protected double alpha = 0;   //alpha for mass matrix
    //may have to change zeroes

    //constructor
    public Material(double b, double h, double E, double rho, double alpha){
        this.b=b;
        this.h=h;
        this.E=E;
        this.A=b*h;
        this.I=b*h*h*h/12.;
        this.EA=E*A;
        this.EI=E*I;
        this.rho = rho;
        this.alpha = alpha;
        this.m=rho*A;
        this.c=10;

    }

    public Material() {
        this(0.1, 0.1, 210e9, 7860, 0.005); //SI-Units - use this (steel) for creating standard  (10cm x 10cm) beam.
    }

    public void setNewProperties(double b, double h) { //necessary, if b and h are changed - changes all relevant properties
        this.b = b;
        this.h = h;
        A = b*h;
        I = b*h*h*h/12.;
        EA = E*A; //update
        EI = E*I; //update
    }

    public void setE(double e){
        E = e;
        EA = E*A; //update
        EI = E*I; //update
    }

    public void setRho(double rho){
        this.rho = rho;
    }

    public void setAlpha(double alpha){this.alpha = alpha;
    }

    public double getb(){return b;}
    public double geth(){return h;}
    public double getA(){return A;}
    public double getC() {return c;}
    public double getM(){return m;}
    public double getI(){return I;}
    public double getEA(){return EA;}
    public double getEI(){return EI;}
    public double getRho(){return rho;}
    public double getAlpha(){return alpha;}

    /*
    public Material(String test){
        if (test.contentEquals("testmat")) {
            this.A = 10;
            this.E = 10e7;
            this.I = 10;
            this.rho = 7860;
        }
    }
    */  // Not necessary

    //TODO Also include beams with non-quadratic cross sections.

}
