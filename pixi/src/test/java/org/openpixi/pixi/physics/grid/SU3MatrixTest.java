package org.openpixi.pixi.physics.grid;


import org.apache.commons.math3.Field;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexField;
import org.apache.commons.math3.linear.Array2DRowFieldMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.junit.Assert;
import org.junit.Test;

public class SU3MatrixTest {


	private final double accuracy = 1.E-13;

	@Test
	public void SU() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			/*
				Create a random matrix.
			 */
			SU3Matrix m1 = createRandomSU3Matrix();

			/*
				Test determinant
			 */
			double[] d = m1.det();
			Assert.assertArrayEquals(new double[]{1,0}, d, accuracy);

			/*
				Test if U U* is I
			 */
			SU3Matrix m2 = (SU3Matrix) m1.mult(m1.adj());
			Array2DRowFieldMatrix<Complex> s0 = convertToMatrix(m2);
			Field<Complex> field = ComplexField.getInstance();
			Array2DRowFieldMatrix<Complex> s1 = new Array2DRowFieldMatrix<Complex>(field, 3, 3);
			s1.setEntry(0, 0, new Complex(1.0, 0.0));
			s1.setEntry(1, 1, new Complex(1.0, 0.0));
			s1.setEntry(2, 2, new Complex(1.0, 0.0));
			compareMatrices(s0, s1);
		}
	}

	@Test
	public void testAdditionAndSubtraction() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			SU3Matrix a = createRandomSU3Matrix();
			SU3Matrix b = createRandomSU3Matrix();

			Array2DRowFieldMatrix<Complex> aMatrix = convertToMatrix(a);
			Array2DRowFieldMatrix<Complex> bMatrix = convertToMatrix(b);

			/*
				Do the addition.
			 */
			SU3Matrix c = (SU3Matrix) a.add(b);
			Array2DRowFieldMatrix<Complex> cMatrix = (Array2DRowFieldMatrix<Complex>) aMatrix.add(bMatrix).copy();

			/*
				Compare results.
			 */
			Array2DRowFieldMatrix<Complex> cMatrix2 = convertToMatrix(c);
			compareMatrices(cMatrix, cMatrix2);

			/*
				Do the subtraction.
			 */
			c = (SU3Matrix) a.sub(b);
			cMatrix = (Array2DRowFieldMatrix<Complex>) aMatrix.subtract(bMatrix).copy();

			/*
				Compare results.
			 */
			cMatrix2 = convertToMatrix(c);
			compareMatrices(cMatrix, cMatrix2);
		}
	}

	@Test
	public void testMultiplication() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			SU3Matrix a = createRandomSU3Matrix();
			SU3Matrix b = createRandomSU3Matrix();

			Array2DRowFieldMatrix<Complex> aMatrix = convertToMatrix(a);
			Array2DRowFieldMatrix<Complex> bMatrix = convertToMatrix(b);

			/*
				Do the multiplication.
			 */
			SU3Matrix c = (SU3Matrix) a.mult(b);
			Array2DRowFieldMatrix<Complex> cMatrix = (Array2DRowFieldMatrix<Complex>) aMatrix.multiply(bMatrix).copy();

			/*
				Compare results.
			 */
			Array2DRowFieldMatrix<Complex> cMatrix2 = convertToMatrix(c);

			compareMatrices(cMatrix, cMatrix2);
		}
	}

	@Test
	public void testScalarMultiplication() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			/*
				Create a random matrix.
			 */
			SU3Matrix m1 = createRandomSU3Matrix();
			Array2DRowFieldMatrix<Complex> m2 = convertToMatrix(m1);

			/*
				Multiply with real scalar.
			 */
			double rand = Math.random() - 0.5;

			m1 = (SU3Matrix) m1.mult(rand);
			m2 = (Array2DRowFieldMatrix<Complex>) m2.scalarMultiply(new Complex(rand));

			/*
				Compare results.
			 */
			Array2DRowFieldMatrix<Complex> m3 = convertToMatrix(m1);
			compareMatrices(m2, m3);
		}
	}

	@Test
	public void testAlgebraGroupElements() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			/*
				Create a random matrix.
			 */
			SU3Matrix m1 = createRandomSU3Matrix();
			Array2DRowFieldMatrix<Complex> m2 = convertToMatrix(m1);

			double[] values = ((SU3Field) m1.getAlgebraElement()).get();
			SU3Matrix tempMatrix = new SU3Matrix(new double[]{values[0],values[1],values[2],values[1],values[4],values[5],values[2],values[5],values[8],0,values[3],values[6],-values[3],0,values[7],-values[6],-values[7],0});
			// mm1 is algebra element expressed as Array2DRowFieldMatrix
			Array2DRowFieldMatrix<Complex> mm1 = convertToMatrix(tempMatrix);

			SU3Matrix m3 = (SU3Matrix) m1.getAlgebraElement().getLinkExact();
			// m4 is group element again, which should be same as m2
			Array2DRowFieldMatrix<Complex> m4 = convertToMatrix(m3);

			Array2DRowFieldMatrix<Complex> arg = (Array2DRowFieldMatrix<Complex>) mm1.scalarMultiply(new Complex(0,1));

			/*
				Exponentiate i*mm1; should be original SU3Matrix m2
			 */
			Field<Complex> field = ComplexField.getInstance();
			// initialize summand to 3x3 identity matrix
			Array2DRowFieldMatrix<Complex> summand = new Array2DRowFieldMatrix<Complex>(field, 3, 3);
			summand.setEntry(0, 0, new Complex(1,0));
			summand.setEntry(1, 1, new Complex(1,0));
			summand.setEntry(2, 2, new Complex(1,0));
			Array2DRowFieldMatrix<Complex> mm2 = (Array2DRowFieldMatrix<Complex>) summand.copy();
			// power series
			for (int i = 1; i <= 100; i++) {
				summand = summand.multiply(arg);
				summand = (Array2DRowFieldMatrix<Complex>) summand.scalarMultiply(new Complex(1./i,0));
				mm2 = mm2.add(summand);
			}

			compareMatrices(m2,mm2);
			compareMatrices(m2,m4);
		}
	}

	@Test
	public void testConjugation() {
		int numberOfTests = 10;
		for (int t = 0; t < numberOfTests; t++) {
			/*
				Create a random matrix.
			 */
			SU3Matrix m1 = createRandomSU3Matrix();
			Array2DRowFieldMatrix<Complex> m2 = convertToMatrix(m1);

			/*
				Apply hermitian conjugation.
			 */
			m1 = (SU3Matrix) m1.adj();
			m2 = (Array2DRowFieldMatrix<Complex>) m2.transpose();
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 3; j++) {
					Complex v = m2.getEntry(i, j).conjugate();
					m2.setEntry(i, j, v);
				}
			}

			/*
				Compare results.
			 */
			Array2DRowFieldMatrix<Complex> m3 = convertToMatrix(m1);
			compareMatrices(m2, m3);
		}
	}

	private double[] proj(double[] v1, double[] v2) {
		double[] prod = new double[2];
		prod[0] = v1[0]*v2[0]+v1[1]*v2[1]+v1[2]*v2[2]+v1[3]*v2[3]+v1[4]*v2[4]+v1[5]*v2[5];
		prod[1] = v1[0]*v2[3]+v1[1]*v2[4]+v1[2]*v2[5]-v1[3]*v2[0]-v1[4]*v2[1]-v1[5]*v2[2];
		double[] out = new double[6];
		out[0] = prod[0]*v1[0] - prod[1]*v1[3];
		out[1] = prod[0]*v1[1] - prod[1]*v1[4];
		out[2] = prod[0]*v1[2] - prod[1]*v1[5];
		out[3] = prod[0]*v1[3] + prod[1]*v1[0];
		out[4] = prod[0]*v1[4] + prod[1]*v1[1];
		out[5] = prod[0]*v1[5] + prod[1]*v1[2];
		return out;
	}

	// uses Gram-Schmidt on random matrix and then normalizes to set determinant
	public SU3Matrix createRandomSU3Matrix() {
		double[] v1 = new double[6];
		double[] v2 = new double[6];
		double[] v3 = new double[6];
		for (int i = 0; i < 6; i++) {
			v1[i] = Math.random() - .5;
			v2[i] = Math.random() - .5;
			v3[i] = Math.random() - .5;
		}
		double norm = v1[0]*v1[0]+v1[1]*v1[1]+v1[2]*v1[2]+v1[3]*v1[3]+v1[4]*v1[4]+v1[5]*v1[5];
		norm = Math.sqrt(norm);
		for (int i = 0; i < 6; i++) {
			v1[i] /= norm;
		}
		double[] projVector;
		projVector = proj(v1,v2);
		for (int i = 0; i < 6; i++) {
			v2[i] -= projVector[i];
		}
		norm = v2[0]*v2[0]+v2[1]*v2[1]+v2[2]*v2[2]+v2[3]*v2[3]+v2[4]*v2[4]+v2[5]*v2[5];
		norm = Math.sqrt(norm);
		for (int i = 0; i < 6; i++) {
			v2[i] /= norm;
		}
		projVector = proj(v1, v3);
		for (int i = 0; i < 6; i++) {
			v3[i] -= projVector[i];
		}
		projVector = proj(v2,v3);
		for (int i = 0; i < 6; i++) {
			v3[i] -= projVector[i];
		}
		norm = v3[0]*v3[0]+v3[1]*v3[1]+v3[2]*v3[2]+v3[3]*v3[3]+v3[4]*v3[4]+v3[5]*v3[5];
		norm = Math.sqrt(norm);
		for (int i = 0; i < 6; i++) {
			v3[i] /= norm;
		}
		SU3Matrix u = new SU3Matrix(new double[]{v1[0],v1[1],v1[2],v2[0],v2[1],v2[2],v3[0],v3[1],v3[2],v1[3],v1[4],v1[5],v2[3],v2[4],v2[5],v3[3],v3[4],v3[5]});
		double[] d = u.det();
		double r, th;
		r = Math.pow(d[0]*d[0] + d[1]*d[1],-1./6);
		th = -Math.atan2(d[1], d[0])/3;
		d[0] = r*Math.cos(th);
		d[1] = r*Math.sin(th);
		for (int i = 0; i < 9; i++) {
			double ure, uim;
			ure = u.get(i);
			uim = u.get(i+9);
			u.set(i,ure*d[0]-uim*d[1]);
			u.set(i+9,ure*d[1]+uim*d[0]);
		}
		return u;
	}

	private void compareMatrices(Array2DRowFieldMatrix<Complex> a, Array2DRowFieldMatrix<Complex> b) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Assert.assertEquals(a.getEntry(i, j).getReal(),
						b.getEntry(i, j).getReal(),
						accuracy);
				Assert.assertEquals(a.getEntry(i, j).getImaginary(),
						b.getEntry(i, j).getImaginary(),
						accuracy);
			}
		}
	}

	private Array2DRowFieldMatrix<Complex> convertToMatrix(LinkMatrix arg) {
		SU3Matrix input = (SU3Matrix) arg;

		Field<Complex> field = ComplexField.getInstance();
		Array2DRowFieldMatrix<Complex> output = new Array2DRowFieldMatrix<Complex>(field, 3, 3);

		output.setEntry(0, 0, new Complex(input.get(0),input.get(9)));
		output.setEntry(0, 1, new Complex(input.get(1),input.get(10)));
		output.setEntry(0, 2, new Complex(input.get(2),input.get(11)));
		output.setEntry(1, 0, new Complex(input.get(3),input.get(12)));
		output.setEntry(1, 1, new Complex(input.get(4),input.get(13)));
		output.setEntry(1, 2, new Complex(input.get(5),input.get(14)));
		output.setEntry(2, 0, new Complex(input.get(6),input.get(15)));
		output.setEntry(2, 1, new Complex(input.get(7),input.get(16)));
		output.setEntry(2, 2, new Complex(input.get(8),input.get(17)));

		return output;
	}
}
