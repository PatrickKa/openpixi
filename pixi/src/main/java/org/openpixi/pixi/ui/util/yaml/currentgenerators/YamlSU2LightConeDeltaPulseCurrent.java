package org.openpixi.pixi.ui.util.yaml.currentgenerators;

import org.openpixi.pixi.physics.Settings;
import org.openpixi.pixi.physics.fields.currentgenerators.SU2LightConeDeltaPulseCurrent;

import java.util.List;

/**
 * Yaml wrapper for the SU2LightConeDeltaPulseCurrent CurrentGenerator.
 */
public class YamlSU2LightConeDeltaPulseCurrent {

	/**
	 * Direction of the current.
	 */
	public Integer direction;

	/**
	 * Starting point for the current pulse on the grid.
	 */
	public List<Double> location;

	/**
	 * Direction of the current in color space.
	 */
	public List<Double> aColor;

	/**
	 * Magnitude of the current.
	 */
	public Double a;

	/**
	 * Orientation of the current.
	 */
	public Integer v;

	/**
	 * Switch for the temporal axial gauge.
	 */
	public Integer tempGauge;

	/**
	 * Checks input for errors.
	 *
	 * @param settings Settings class. Important: numberOfDimensions and numberOfColors must be defined.
	 * @return Returns true if everything looks alright.
	 */
	public boolean checkConsistency(Settings settings) {
		if (direction >= settings.getNumberOfDimensions()) {
			System.out.println("SU2LightConeDeltaPulseCurrent: direction index exceeds the dimensions of the system.");
			return false;
		}

		if (location.size() != settings.getNumberOfDimensions()) {
			System.out.println("SU2LightConeDeltaPulseCurrent: location vector does not have the right dimensions.");
			return false;
		}

		int numberOfComponents = settings.getNumberOfColors() * settings.getNumberOfColors() - 1;
		if (aColor.size() != numberOfComponents) {
			System.out.println("SU2LightConeDeltaPulseCurrent: aColor vector does not have the right dimensions.");
			return false;
		}

		if (v == 0) {
			System.out.println("SU2LightConeDeltaPulseCurrent: Orientation has to be non-zero.");
			return false;
		}
		return true;
	}

	/**
	 * Returns an instance of SU2LightConeDeltaPulseCurrent according to the parameters in the YAML file.
	 *
	 * @return Instance of SU2LightConeDeltaPulseCurrent.
	 */
	public SU2LightConeDeltaPulseCurrent getCurrentGenerator() {
		int numberOfDimensions = location.size();
		int numberOfComponents = aColor.size();
		boolean gauge;
		if(tempGauge != 0) {
			gauge = true;
		} else {
			gauge = false;
		}
        /*
			I'm sure this can be improved. I don't know how to convert a ArrayList<Double> into a double[] quickly, so
            I do it manually.
         */

		double[] locationArray = new double[numberOfDimensions];
		double[] aColorArray = new double[numberOfComponents];

		for (int i = 0; i < numberOfDimensions; i++) {
			locationArray[i] = location.get(i);
		}

		for (int c = 0; c < numberOfComponents; c++) {
			aColorArray[c] = aColor.get(c);
		}

		return new SU2LightConeDeltaPulseCurrent(direction, locationArray, aColorArray, a, v, gauge);
	}
}
