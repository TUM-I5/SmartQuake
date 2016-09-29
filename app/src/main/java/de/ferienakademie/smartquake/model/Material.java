package de.ferienakademie.smartquake.model;

/**
 * Created by alex on 21.09.16.
 */
public class Material {

    public static Material STEEL = new Material(0.01, 0.01, 2.1e11, 7850, 0.005, "rectangular", 400e6); //SI-Units - use this (steel) for creating standard  (10cm x 10cm) beam.
    public static Material STEEL2 = new Material(0.02978, 0.02978, 2.1e11, 7850, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL3 = new Material(0.2978, 0.2978, 2.1e11, 7850, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL4 = new Material(0.05956, 0.05956, 2.1e11, 7850, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL5 = new Material(0.03347, 0.03347, 2.1e11, 7850, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL6 = new Material(0.03347, 0.03347, 2.1e11, 0.00001, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL7 = new Material(0.03347, 0.03347, 2.1e11, 0.00001, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!
    public static Material STEEL8 = new Material(0.03347/0.39806080e-1/9.0112327300/0.9962406530, 0.03347/0.39806080e-1/9.0112327300/0.9962406530, 2.1e11, 7850, 0.005, "rectangular", 400e6); // for cantilever beam !!! DO NOT CHANGE !!!



    public static Material STEEL_I_SHAPED = new Material(0.001, 0.001, 2.1e11, 7850, 0.005, "I-shaped beam", 400e6);
    public static Material WOOD = new Material(0.1,0.1,12e9,600,0.005, "rectangular", 60);// Source: Holzbau, wikipedia
    public static Material WOOD_I_SHAPED = new Material(0.1,0.1,12e9,600,0.005, "I-shaped beam", 60);
    public static Material CONCRETE = new Material(0.1,0.1,32e9,2400,0.005, "rectangular", 20);// Source: Betonbau, wikipedia
    public static Material CONCRETE_I_SHAPED = new Material(0.1,0.1,32e9,2400,0.005, "I-shaped beam", 20);
    public static Material BAMBOO = new Material(0.1,0.1,19e9,1000,0.005, "rectangular", 350);

    public String shape;
    //shape = "rectangular" ;  // Can be changed for using other than rectangular cross-sections.

    protected double YoungsModulus = 0;   //Young's modulus
    protected double AreaOfCrossSection = 0;   //cross section
    protected double MomentOfInertia = 0;   //moment of inertia
    protected double AxialStiffnessOfBar = 0;  //
    protected double BendingStiffnessOfBeam = 0;  //rigidity
    protected double HeightOfBeam = 0;   //height of beam (input)
    protected double BreadthOfBeam = 0;   //width of beam (input)
    protected double MassPerLength = 0;
    protected double tensileStrength = 0; //stress when beam breaks: Steel: 400 N/m²; Wood: 60 N/m²; Concrete: 20 N/m²; Bamboo 350 N/m²

    protected double Density = 0;     //density of material
    protected double alpha = 0;   //alpha for mass matrix
    //may have to change zeroes

    //constructor - The I-shaped beam only uses the 240 profile from DIN 1025; source: https://www.bauforumstahl.de/upload/documents/profile/querschnittswerte/I.pdf
    public Material(double BreadthOfBeam, double HeightOfBeam, double YoungsModulus, double Density, double alpha, String shape, double tensileStrenght){
        this.BreadthOfBeam = BreadthOfBeam;
        this.HeightOfBeam = HeightOfBeam;
        this.YoungsModulus = YoungsModulus;
        this.AreaOfCrossSection = BreadthOfBeam * HeightOfBeam;
        this.MomentOfInertia = BreadthOfBeam * HeightOfBeam * HeightOfBeam * HeightOfBeam /12.;

        this.Density = Density;
        this.alpha = alpha;
        this.MassPerLength = Density * AreaOfCrossSection;
        this.tensileStrength = tensileStrenght;

        if (shape.equals("I-shaped beam")) { // SI-Units
            this.BreadthOfBeam = 0.106;
            this.HeightOfBeam = 0.24;
            this.AreaOfCrossSection = 46.1 * 0.0001;
            this.MomentOfInertia = 4250 * 0.00000001;
        }

        this.AxialStiffnessOfBar = YoungsModulus * AreaOfCrossSection;
        this.BendingStiffnessOfBeam = YoungsModulus * MomentOfInertia;
    }

    public void setNewProperties(double b, double h) { //necessary, if BreadthOfBeam and HeightOfBeam are changed - changes all relevant properties - only useful for rectangular cross-sections.
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

    //Most of this is only useful for rectangular cross-sections.

    public double getBreadthOfBeam(){return BreadthOfBeam;}
    public double getHeightOfBeam(){return HeightOfBeam;}
    public double getAreaOfCrossSection(){return AreaOfCrossSection;}
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
