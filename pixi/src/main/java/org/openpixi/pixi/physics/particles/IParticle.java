package org.openpixi.pixi.physics.particles;
import java.awt.Color;


public interface IParticle
{
	//----------------------------------------------------------------------------------------------
	// GETTERS
	//----------------------------------------------------------------------------------------------

	/*
			GETTERS
	 */

	/*
		Particle position, velocity and acceleration getters
		Note: in a relativistic context 'velocity' refers to the relativistic velocity usually denoted as 'u'.
	 */

	double getPosition(int d);
	double getPrevPosition(int d);
	double getVelocity(int d);
	double getAcceleration(int d);

    double[] getPosition();
    double[] getPrevPosition();
    double[] getVelocity();
    double[] getAcceleration();

	/*
		Getters for fields at particle position
	 */

	double getE(int d, int c);
	double getF(int i, int j, int c);

	/*
		Getters for particle properties
	 */

	double  getCharge(int c);
	double  getMass();
    int     getNumberOfColors();
    int     getNumberOfDimensions();
    int     getNumberOfComponents();

	/*
		Getters for display properties
	 */

	double getRadius();
	Color getColor();

	/*
			SETTERS
	 */

	/*
		Setters for particle position, velocity and acceleration
	 */

	void setPosition(int d, double x);
	void addPosition(int d, double x);

	void setPrevPosition(int d, double x);
	void addPrevPosition(int d, double x);

	void setVelocity(int d, double v);
	void addVelocity(int d, double v);

	void setAcceleration(int d, double a);
	void addAcceleration(int d, double a);


	/*
		Setters for fields at particle position
	 */

	void setE(int d, int c, double E);
	void setF(int i, int j, int c, double F);

	/*
		Setters for particle properties
	 */

	void setNumberOfColors(int numberOfColors);
	void setNumberOfDimensions(int numberOfDimensions);
	void setCharge(int c, double q);
	void setMass(double m);

	/*
		Setters for display properties
	 */

	void setRadius(double r);
	void setColor(Color color);


	//----------------------------------------------------------------------------------------------
	// UTILITY METHODS
	//----------------------------------------------------------------------------------------------

	void storePosition();

	IParticle copy();

	String toString();
}