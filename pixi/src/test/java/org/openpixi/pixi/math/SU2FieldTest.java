package org.openpixi.pixi.math;


import org.junit.Assert;
import org.junit.Test;
import org.openpixi.pixi.math.SU2AlgebraElement;
import org.openpixi.pixi.math.SU2GroupElement;

public class SU2FieldTest {


	private final double accuracy = 1.E-14;

	@Test
	public void testGetterAndSetter() {
		// Create random SU2 field.
		double[] vec = new double[3];
		double square = 0.0;
		for (int i = 0; i < 3; i++) {
			vec[i] = Math.random() - 0.5;
			square += vec[i] * vec[i];
		}


		// We construct two fields. One from the constructor and another using setter methods.
		SU2AlgebraElement firstField = new SU2AlgebraElement(vec[0], vec[1], vec[2]);
		SU2AlgebraElement secondField = new SU2AlgebraElement();
		for (int i = 0; i < 3; i++) {
			secondField.set(i, vec[i]);
		}

		// Now we test if the values have been set correctly.
		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(firstField.get(i), vec[i], accuracy);
			Assert.assertEquals(secondField.get(i), vec[i], accuracy);
		}

		// Here the square method is tested.
		Assert.assertEquals(firstField.square(), square, accuracy);
		Assert.assertEquals(secondField.square(), square, accuracy);
	}

	@Test
	public void testAdditionAndSubtraction() {

		// Prepare some variables.
		double[] aVec = new double[3];
		double[] bVec = new double[3];
		double[] rVec1 = new double[3];
		double[] rVec2 = new double[3];

		for (int i = 0; i < 3; i++) {
			aVec[i] = Math.random() - 0.5;
			bVec[i] = Math.random() - 0.5;
		}

		for (int i = 0; i < 3; i++) {
			rVec1[i] = aVec[i] + bVec[i];
			rVec2[i] = aVec[i] - bVec[i];
		}

		// Create two fields.
		SU2AlgebraElement a = new SU2AlgebraElement();
		SU2AlgebraElement b = new SU2AlgebraElement();
		for (int i = 0; i < 3; i++) {
			a.set(i, aVec[i]);
			b.set(i, bVec[i]);
		}


		// Use add and sub methods.
		SU2AlgebraElement r1 = (SU2AlgebraElement) a.add(b);
		SU2AlgebraElement r2 = (SU2AlgebraElement) a.sub(b);


		// Compare results.
		for (int i = 0; i < 3; i++) {
			Assert.assertEquals(r1.get(i), rVec1[i], accuracy);
			Assert.assertEquals(r2.get(i), rVec2[i], accuracy);
		}
	}

	@Test
	public void testConversionToMatrixAndBack() {

		for(int t = 0; t < 10; t++) {
			// Create random SU2 field.
			double[] vec = new double[3];
			double scaling = 1.0;
			for (int i = 0; i < 3; i++) {
				vec[i] = (Math.random() - 0.5) * scaling;
			}
			SU2AlgebraElement firstField = new SU2AlgebraElement(vec[0], vec[1], vec[2]);
		

			// Transform to a SU2 matrix exactly and in linear approximation.
			SU2GroupElement matSimple = (SU2GroupElement) firstField.getLinearizedLink();
			SU2GroupElement matExact = (SU2GroupElement) firstField.getLink();


			// Transform back to a SU2 field exactly and in linear approximation (proj).
			SU2AlgebraElement fieldSimple = (SU2AlgebraElement) matSimple.proj();
			SU2AlgebraElement fieldExact = (SU2AlgebraElement) matExact.getAlgebraElement();
		

			// Compare results.
			for (int i = 0; i < 3; i++) {
				double simpleRelDifference = Math.abs((fieldSimple.get(i) - firstField.get(i)) / firstField.get(i));
				double exactRelDifference = Math.abs((fieldExact.get(i) - firstField.get(i)) / firstField.get(i));
				//System.out.println(simpleRelDifference + ", " + exactRelDifference);
				Assert.assertEquals(0.0, simpleRelDifference, accuracy);
				Assert.assertEquals(0.0, exactRelDifference, accuracy);
			}
		}
		
	}

	@Test
	public void testScalarMultiplication() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {

			// Create a random field.
			double[] vec1 = new double[3];
			double[] vec2 = new double[3];
			double value = Math.random() - 0.5;
			for (int i = 0; i < 3; i++) {
				vec1[i] = Math.random() - 0.5;
				vec2[i] = value*vec1[i];
			}
			SU2AlgebraElement firstField = new SU2AlgebraElement(vec1[0], vec1[1], vec1[2]);

			// Multiply with a scalar.
			SU2AlgebraElement secondField = (SU2AlgebraElement) firstField.mult(value);

			// Compare results.
			for (int i = 0; i < 3; i++) {
				Assert.assertEquals(secondField.get(i), vec2[i], accuracy);
			}


		}
	}

	@Test
	public void testDimensions(){
		SU2AlgebraElement A = new SU2AlgebraElement();
		Assert.assertEquals(A.getNumberOfColors(), 2);
		Assert.assertEquals(A.getAdjointDimension(), 3);
	}


}
