package de.ferienakademie.smartquake.model;

/**
 * Created by alex on 21.09.16.
 */
public class Material {

    public static Material STEEL = new Material();
    public static Material WOOD = new Material(0.1,0.1,12e9,600,0.005); // Source: Holzbau, wikipedia
    public static Material CONCRETE = new Material(0.1,0.1,32e9,2400,0.005); // Source: Betonbau, wikipedia
    public static Material BAMBOO = new Material(0.1,0.1,19e9,1000,0.005);
    public static Material SOMETHING = new Material(); //TODO Add values and more materials!


    protected double YoungsModulus = 0;   //Young's modulus
    protected double AreaOfCrossSection = 0;   //cross section
    protected double MomentOfInertia = 0;   //moment of inertia
    protected double AxialStiffnessOfBar = 0;  //
    protected double BendingStiffnessOfBeam = 0;  //rigidity
    protected double HeightOfBeam = 0;   //height of beam (input)
    protected double BreadthOfBeam = 0;   //width of beam (input)
    protected double MassPerLength = 0;
    protected double DampingCoefficient = 0;

    protected double Density = 0;     //density of material
    protected double alpha = 0;   //alpha for mass matrix
    //may have to change zeroes

    //constructor
    public Material(double BreadthOfBeam, double HeightOfBeam, double YoungsModulus, double Density, double alpha){
        this.BreadthOfBeam = BreadthOfBeam;
        this.HeightOfBeam = HeightOfBeam;
        this.YoungsModulus = YoungsModulus;
        this.AreaOfCrossSection = BreadthOfBeam * HeightOfBeam;
        this.MomentOfInertia = BreadthOfBeam * HeightOfBeam * HeightOfBeam * HeightOfBeam /12.;
        this.AxialStiffnessOfBar = YoungsModulus * AreaOfCrossSection;
        this.BendingStiffnessOfBeam = YoungsModulus * MomentOfInertia;
        this.Density = Density;
        this.alpha = alpha;
        this.MassPerLength = Density * AreaOfCrossSection;
        this.DampingCoefficient =10;

    }

    public Material() {
        this(0.00123, 0.00123, 2.1*10e8, 7.85, 0.005); //SI-Units - use this (steel) for creating standard  (10cm x 10cm) beam.
    }


    public void setNewProperties(double b, double h) { //necessary, if BreadthOfBeam and HeightOfBeam are changed - changes all relevant properties
        this.BreadthOfBeam = b;
        this.HeightOfBeam = h;
        AreaOfCrossSection = b*h;
        MomentOfInertia = b*h*h*h/12.;
        AxialStiffnessOfBar = YoungsModulus * AreaOfCrossSection; //update
        BendingStiffnessOfBeam = YoungsModulus * MomentOfInertia; //update
    }

    public void setYoungsModulus(double youngsModulus){
        YoungsModulus = youngsModulus;
        AxialStiffnessOfBar = YoungsModulus * AreaOfCrossSection; //update
        BendingStiffnessOfBeam = YoungsModulus * MomentOfInertia; //update
    }

    public void setDensity(double density){
        this.Density = density;
    }

    public void setAlpha(double alpha){this.alpha = alpha;
    }

    public double getBreadthOfBeam(){return BreadthOfBeam;}
    public double getHeightOfBeam(){return HeightOfBeam;}
    public double getAreaOfCrossSection(){return AreaOfCrossSection;}
    public double getDampingCoefficient() {return DampingCoefficient;}
    public double getMassPerLength(){return MassPerLength;}
    public double getMomentOfInertia(){return MomentOfInertia;}
    public double getAxialStiffnessOfBar(){return AxialStiffnessOfBar;}
    public double getBendingStiffnessOfBeam(){return BendingStiffnessOfBeam;}
    public double getDensity(){return Density;}
    public double getAlpha(){return alpha;}

    /*
    public Material(String test){
        if (test.contentEquals("testmat")) {
            this.AreaOfCrossSection = 10;
            this.YoungsModulus = 10e7;
            this.MomentOfInertia = 10;
            this.Density = 7860;
        }
    }
    */  // Not necessary

    //TODO Also include beams with non-quadratic cross sections.

}
