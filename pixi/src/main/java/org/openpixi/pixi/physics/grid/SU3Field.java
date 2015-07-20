package org.openpixi.pixi.physics.grid;

public class SU3Field extends YMField {


	public SU3Field() {

		v = new double[9];

		for (int i = 0; i < 9; i++) {
			v[i] = 0;
		}
	}

	public SU3Field(double[] values) {
		
		v = new double[9];

		for (int i = 0; i < 9; i++) {
			v[i] = values[i];
		}
		
	}
	
	public void reset () {

		for (int i = 0; i < 9; i++) {
			v[i] = 0;
		}
		
	}
	
	public YMField add (YMField a) {
		
		SU3Field b = new SU3Field();
		for (int i = 0; i < 9; i++) {
			b.v[i] = v[i]+a.v[i];
		}
		return b;
		
	}
	
	public void addequate (YMField a) {

		for (int i = 0; i < 9; i++) {
			v[i] += a.v[i];
		}
		
	}
	
	public YMField sub (YMField a) {
		
		SU3Field b = new SU3Field();
		for (int i = 0; i < 9; i++) {
			b.v[i] = v[i]-a.v[i];
		}
		return b;
		
	}
	
	public void set (int j, double value) {
		
		v[j] = value;
		
	}
	
	public double get (int j) {
		
		double b = v[j];
		return b;
		
	}

	public double[] get () {

		return v;

	}

	/**
	 * @return 2 tr(A^2)
	 */
	public double square () {
		return 2*(v[0]*v[0]+v[4]*v[4]+v[8]*v[8]+2*(v[1]*v[1]+v[2]*v[2]+v[3]*v[3]+v[5]*v[5]+v[6]*v[6]+v[7]*v[7]));
		
	}
	
	public YMField mult (double number) {

		SU3Field b = new SU3Field();
		for (int i = 0; i < 9; i++) {
			b.v[i] = v[i]*number;
		}
		return b;

	}

	public void set (YMField a) {

		for (int i = 0; i < 9; i++) {
			v[i] = a.v[i];
		}

	}
	
	public void addfour (YMField a, YMField b, YMField c, YMField d) {

		for (int i = 0; i < 9; i++) {
			v[i] = a.get(i)+b.get(i)+c.get(i)+d.get(i)+this.get(i);
		}
			
	}

	public void FieldFromForwardPlaquette (LinkMatrix a, LinkMatrix b, LinkMatrix c, LinkMatrix d) {}

	public void FieldFromBackwardPlaquette (LinkMatrix a, LinkMatrix b, LinkMatrix c, LinkMatrix d) {}

	/**
	 * Normalizes (complex) vector in place
	 * Vector is stored as three real components followed by three imag. components
	 * @param vector to be normalized
	 */
	private void normalize(double[] vector) {
		double norm = 0;
		for (int i = 0; i < 6; i++) {
			norm += vector[i] * vector[i];
		}
		norm = Math.sqrt(norm);
		for (int i = 0; i < 6; i++) {
			vector[i] /= norm;
		}
	}

	/**
	 * Calculates the algebra element by first eigendecomposing into UDU* and then finding log D
	 * WARNING: This decomposition only works for SU(3) matrices due to certain optimizations
	 * @return coefficients to be fed into SU3Field to give algebra element
	 */
	private double[] groupElementDecompositionMethod() {
		// trace of matrix squared, using square method
		double trSq = square() / 2;

		// real determinant
		double det;
		det = -v[2]*v[2]*v[4]+v[6]*(2*v[3]*v[5]-v[4]*v[6]+2*v[1]*v[7])+2*v[2]*(v[1]*v[5]-v[3]*v[7])-
				(v[1]*v[1]+v[3]*v[3])*v[8]-v[0]*(v[5]*v[5]+v[7]*v[7]-v[4]*v[8]);

		// coefficients in reduced cubic equation
		// \lambda = X
		// X^3 - X * 1/2 tr(U^2) - det(U) == 0
		double linTerm;
		linTerm = -trSq/2;

		// cubic is now in form X^3 + p X + q == 0
		// transform to W^6 + q W^3 - 1/27 p^3 == 0
		// then W^3 == (-q + sqrt(q^2 + 4/27 p^3))/2
		// (pick positive solution)
		// preRad is always real here

		double preRad, radRe, radIm;
		preRad = det*det + Math.pow(linTerm,3)*4/27;
		if (preRad >= 0) {
			radRe = Math.sqrt(preRad);
			radIm = 0;
		}
		else {
			radRe = 0;
			radIm = Math.sqrt(-preRad);
		}

		// convert W^3 to polar
		double preOmegaRe, preOmegaIm, r, th;
		preOmegaRe = (det + radRe)/2;
		preOmegaIm = radIm/2;
		th = Math.atan2(preOmegaIm, preOmegaRe);
		r = Math.pow(preOmegaRe * preOmegaRe + preOmegaIm * preOmegaIm, 1. / 6);

		// three angles of cube roots of W^3
		double th1, th2, th3;
		th1 = (th + 0*Math.PI)/3;
		th2 = (th + 2*Math.PI)/3;
		th3 = (th + 4*Math.PI)/3;

		// the end is near!
		// X_i = W_i - p / (3 W_i)
		// then \lambda_i = X_i
		// this gives us the real eigenvalues to high precision
		// (these matrices are hermitian, so eigenvalues better be real)
		//
		double phase1, phase2, phase3;
		phase1 = r * Math.cos(th1) - (linTerm*Math.cos(th1))/(3*r);
		phase2 = r * Math.cos(th2) - (linTerm*Math.cos(th2))/(3*r);
		phase3 = r * Math.cos(th3) - (linTerm*Math.cos(th3))/(3*r);

		double test = r * Math.sin(th3) + (linTerm*Math.sin(th3))/(3*r);

		// now use eigenvalues to compute orthonormal eigenvectors
		// (U - \lambda_i)(U - \lambda_j) has columns that are eigenvectors for the remaining eigenvalue \lambda_k
		// we use this result, but we only need one column so we can avoid doing the full multiplication
		// optimized result computed in Mathematica, of course

		// get one eigenvector for each value
		// normalize vectors in place
		// not yet tested
		double[] vector1, vector2, vector3;

		vector1 = new double[6];
		vector1[0] = v[0]*v[0]+v[1]*v[1]+v[2]*v[2]+v[3]*v[3]+v[6]*v[6]+phase2*phase3-v[0]*(phase2+phase3);
		vector1[1] = v[0]*v[1]+v[2]*v[5]+v[6]*v[7]+v[1]*(v[4]-phase2-phase3);
		vector1[2] = v[0]*v[2]+v[1]*v[5]-v[3]*v[7]+v[2]*v[8]-v[2]*phase2-v[2]*phase3;
		vector1[3] = 0;
		vector1[4] = -v[0]*v[3]-v[5]*v[6]+v[2]*v[7]+v[3]*(phase2+phase3-v[4]);
		vector1[5] = -v[3]*v[5]-v[0]*v[6]-v[1]*v[7]-v[6]*v[8]+v[6]*phase2+v[6]*phase3;

		vector2 = new double[6];
		vector2[0] = v[0]*v[0]+v[1]*v[1]+v[2]*v[2]+v[3]*v[3]+v[6]*v[6]+phase1*phase3-v[0]*(phase1+phase3);
		vector2[1] = v[0]*v[1]+v[2]*v[5]+v[6]*v[7]+v[1]*(v[4]-phase1-phase3);
		vector2[2] = v[0]*v[2]+v[1]*v[5]-v[3]*v[7]+v[2]*v[8]-v[2]*phase1-v[2]*phase3;
		vector2[3] = 0;
		vector2[4] = -v[0]*v[3]-v[5]*v[6]+v[2]*v[7]+v[3]*(phase1+phase3-v[4]);
		vector2[5] = -v[3]*v[5]-v[0]*v[6]-v[1]*v[7]-v[6]*v[8]+v[6]*phase1+v[6]*phase3;

		vector3 = new double[6];
		vector3[0] = v[0]*v[0]+v[1]*v[1]+v[2]*v[2]+v[3]*v[3]+v[6]*v[6]+phase1*phase2-v[0]*(phase1+phase2);
		vector3[1] = v[0]*v[1]+v[2]*v[5]+v[6]*v[7]+v[1]*(v[4]-phase1-phase2);
		vector3[2] = v[0]*v[2]+v[1]*v[5]-v[3]*v[7]+v[2]*v[8]-v[2]*phase1-v[2]*phase2;
		vector3[3] = 0;
		vector3[4] = -v[0]*v[3]-v[5]*v[6]+v[2]*v[7]+v[3]*(phase1+phase2-v[4]);
		vector3[5] = -v[3]*v[5]-v[0]*v[6]-v[1]*v[7]-v[6]*v[8]+v[6]*phase1+v[6]*phase2;

		normalize(vector1);
		normalize(vector2);
		normalize(vector3);

		// take log of eigenvalue matrix
		double value1Re,value1Im, value2Re,value2Im, value3Re,value3Im;
		value1Re = Math.cos(phase1);
		value1Im = Math.sin(phase1);
		value2Re = Math.cos(phase2);
		value2Im = Math.sin(phase2);
		value3Re = Math.cos(phase3);
		value3Im = Math.sin(phase3);

		// multiply U exp(D) U* to get algebra element
		// exp(D) is just a (complex) diagonal matrix so multipication is included in construction of U
		SU3Matrix UEeD = new SU3Matrix(new double[]{vector1[0]*value1Re-vector1[3]*value1Im,vector2[0]*value2Re-vector2[3]*value2Im,vector3[0]*value3Re-vector3[3]*value3Im,
													vector1[1]*value1Re-vector1[4]*value1Im,vector2[1]*value2Re-vector2[4]*value2Im,vector3[1]*value3Re-vector3[4]*value3Im,
													vector1[2]*value1Re-vector1[5]*value1Im,vector2[2]*value2Re-vector2[5]*value2Im,vector3[2]*value3Re-vector3[5]*value3Im,
													vector1[0]*value1Im+vector1[3]*value1Re,vector2[0]*value2Im+vector2[3]*value2Re,vector3[0]*value3Im+vector3[3]*value3Re,
													vector1[1]*value1Im+vector1[4]*value1Re,vector2[1]*value2Im+vector2[4]*value2Re,vector3[1]*value3Im+vector3[4]*value3Re,
													vector1[2]*value1Im+vector1[5]*value1Re,vector2[2]*value2Im+vector2[5]*value2Re,vector3[2]*value3Im+vector3[5]*value3Re});
		SU3Matrix UAdj = new SU3Matrix(new double[]{ vector1[0], vector1[1], vector1[2],
													 vector2[0], vector2[1], vector2[2],
													 vector3[0], vector3[1], vector3[2],
													-vector1[3],-vector1[4],-vector1[5],
													-vector2[3],-vector2[4],-vector2[5],
													-vector3[3],-vector3[4],-vector3[5]});

		return ((SU3Matrix) UEeD.mult(UAdj)).get();
	}



	/**
	 * Essentially just using exp(I v) ~ 1 + I v
	 */
	public LinkMatrix getLink () {
		double[] values = new double[]{1,-v[3],-v[6],v[3],1,-v[7],v[6],v[7],1,v[0],v[1],v[2],v[1],v[4],v[5],v[2],v[5],v[8]};
		return new SU3Matrix(values);
	}
	
	public LinkMatrix getLinkExact () {
		return new SU3Matrix(groupElementDecompositionMethod());
	}

}
