/*
 * OpenPixi - Open Particle-In-Cell (PIC) Simulator
 * Copyright (C) 2012  OpenPixi.org
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.openpixi.pixi.physics;

import org.openpixi.pixi.physics.force.ConstantForce;
import org.openpixi.pixi.physics.particles.Particle;
import org.openpixi.pixi.physics.particles.ParticleFull;
import org.openpixi.pixi.physics.solver.relativistic.LeapFrogRelativistic;
import org.openpixi.pixi.physics.fields.PoissonSolverFFTPeriodic;
import org.openpixi.pixi.physics.fields.EmptyPoissonSolver;
import org.openpixi.pixi.physics.fields.SimpleSolver;
import org.openpixi.pixi.physics.grid.ChargeConservingCIC;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

public class InitialConditions {

	/**Random angle*/
	private static double phi;

	public static Simulation initRandomParticles(int count, double radius) {
		Settings stt = new Settings();

		stt.setTimeStep(0.1);

		//stt.setSpeedOfLight(3);
		// Use maximum speed available by grid
		//stt.setSpeedOfLight(stt.getCellWidth() / stt.getTimeStep());

		stt.setGridCellsX(100);
		stt.setGridCellsY(100);
		stt.setGridCellsZ(100);

		stt.addForce(new ConstantForce());
		stt.setParticleList(
				createRandomParticles(stt.getSimulationWidth(), stt.getSimulationHeight(), stt.getSimulationDepth(),
				stt.getSpeedOfLight(), count, radius));
		stt.setBoundary(GeneralBoundaryType.Periodic);
		//stt.setParticleSolver(new EulerRichardson());
		//stt.setParticleSolver(new BorisRelativistic(stt.getSpeedOfLight()));
		stt.setParticleSolver(new LeapFrogRelativistic(stt.getSpeedOfLight()));
		

		Simulation simulation = new Simulation(stt);
		return simulation;
	}

	/**Creates particles on random positions with random speeds
	 * @param width		maximal x-coodrinate
	 * @param height	maximal y-coordinate
	 * @param maxspeed	maximal particle speed
	 * @param count		number of particles to be created
	 * @param radius	particle radius
	 * @return ArrayList of Particle2D
	 */
	public static ArrayList<Particle> createRandomParticles(double width, double height, double maxspeed, int count, double radius) {

		ArrayList<Particle> particlelist = new ArrayList<Particle>(count);

		for (int k = 0; k < count; k++) {
			Particle p = new ParticleFull();
			p.setX(width * Math.random());
			p.setY(height * Math.random());
			p.setRadius(radius);
			phi = 2 * Math.PI * Math.random();
			p.setVx(maxspeed * Math.cos(phi));
			p.setVy(maxspeed * Math.sin(phi));
			p.setMass(1);
            //overall charge is 0:
			if (k<count/2) {
				p.setCharge(.01);
				p.setColor(Color.red);
			} else {
				p.setCharge(-.01);
				p.setColor(Color.blue);
			}
			particlelist.add(p);
		}

		return particlelist;
	}
	
	public static ArrayList<Particle> createRandomParticles(double width, double height, double depth, double maxspeed, int count, double radius) {

		ArrayList<Particle> particlelist = new ArrayList<Particle>(count);

		for (int k = 0; k < count; k++) {
			Particle p = new ParticleFull();
			p.setX(width * Math.random());
			p.setY(height * Math.random());
			p.setZ(depth * Math.random());
			p.setRadius(radius);
			phi = 2 * Math.PI * Math.random();
			p.setVx(maxspeed * Math.random());
			p.setVy(maxspeed * Math.random());
			p.setVz(maxspeed * Math.random());
			p.setMass(1);
            //overall charge is 0:
			if (k<count/2) {
				p.setCharge(.01);
				p.setColor(Color.red);
			} else {
				p.setCharge(-.01);
				p.setColor(Color.blue);
			}
			particlelist.add(p);
		}

		return particlelist;
	}
	   
    public static Simulation initPair(double charge, double radius) {
	Settings stt = new Settings();

	stt.setTimeStep(0.1);
	stt.setTMax(1000);
	stt.setSpeedOfLight(1);
	stt.setRelativistic(true);
	/*stt.setSimulationWidth(100);
	stt.setSimulationHeight(100);*/
	stt.setGridStep(1);
	stt.setGridCellsX(100);
	stt.setGridCellsY(100);
            stt.setNumOfParticles(2);

	stt.addForce(new ConstantForce());

	stt.setBoundary(GeneralBoundaryType.Periodic);
            stt.setGridSolver(new SimpleSolver());

	for (int k = 0; k < 2; k++) {
		Particle par = new ParticleFull();
		par.setX(stt.getSimulationWidth() * 1/9.0*(k+4));
		par.setY(stt.getSimulationHeight() * 1/2 + stt.getGridStep()*2/4);
		par.setRadius(radius);
		par.setVx(0);//par.setVx(0.1*(1-2*k));
		par.setVy(0);
		par.setMass(1);
		par.setCharge(charge*(1-2*k));
		if (k == 0) {
			par.setColor(Color.red);
		} else {
			par.setColor(Color.blue);
		}
		stt.addParticle(par);
	}
            
            stt.setPoissonSolver(new PoissonSolverFFTPeriodic());
            stt.useGrid(true);
            stt.setInterpolator(new ChargeConservingCIC());
            //stt.setIterations(1);//Testing purposes!!!
            //set to charge conserving CIC; already preset in settings
	Simulation simulation = new Simulation(stt);
	return simulation;
}

    public static Simulation initTwoStream(double charge, double radius, int numpart) {
	Settings stt = new Settings();
	double dnumpart = numpart;

	stt.setTimeStep(0.1);
	stt.setSpeedOfLight(1);
	stt.setRelativistic(true);
	/*stt.setSimulationWidth(100);
	stt.setSimulationHeight(100);*/
	stt.setGridStep(1);
	stt.setGridCellsX(100);
	stt.setGridCellsY(100);
            stt.setNumOfParticles(2*numpart);

	stt.setBoundary(GeneralBoundaryType.Periodic);
            stt.setGridSolver(new SimpleSolver());

	for (int k = 0; k < 2*numpart; k++) {
		Particle par = new ParticleFull();
		if(k < numpart) {par.setX(stt.getSimulationWidth() * 1/dnumpart*k);
		par.setVx(0.1);
		par.setColor(Color.red);
		}
		else {par.setX(stt.getSimulationWidth() * 1/dnumpart*(k-numpart));
		par.setVx(-0.1);
		par.setColor(Color.blue);
		}
		par.setY(stt.getSimulationHeight() * 1/2 );
		par.setRadius(radius);
		par.setVy(0);
		par.setMass(1);
		par.setCharge(-charge);
		stt.addParticle(par);
	}
	stt.setPoissonSolver(new EmptyPoissonSolver());
            stt.useGrid(true);
	Simulation simulation = new Simulation(stt);
	return simulation;
}
    
    public static Simulation initOneTest(double charge, double radius) {
    	Settings stt = new Settings();

    	stt.setTimeStep(0.1);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	stt.setGridStep(1);
    	stt.setGridCellsX(100);
    	stt.setGridCellsY(100);
    	/*
		ConstantForce cf = new ConstantForce();
		cf.ex = -1;
		stt.addForce(cf);
		*/
                stt.setNumOfParticles(1);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());

    		Particle par = new ParticleFull();
    		par.setX(stt.getSimulationWidth() * 1/2);
    		par.setVx(0.1);
    		par.setY(stt.getSimulationHeight() * 1/2 );
    		par.setRadius(radius);
    		par.setVy(0);
    		par.setMass(1);
    		par.setCharge(-charge);
    		par.setColor(Color.red);
    		stt.addParticle(par);

    		stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
    	Simulation simulation = new Simulation(stt);
    	return simulation;
    }
    
    public static Simulation initWeibel(double charge, double radius, int numpart, int numstripes, double speed) {
    	Settings stt = new Settings();
    	double dnumpart = numpart;

    	stt.setTimeStep(0.1);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	/*stt.setSimulationWidth(100);
    	stt.setSimulationHeight(100);*/
    	stt.setGridStep(10);
    	stt.setGridCellsX(10);
    	stt.setGridCellsY(10);
                stt.setNumOfParticles(numpart);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());
                
        double stripeWidth = stt.getSimulationWidth() / numstripes;
        
        if ( (numpart % numstripes) != 0 ) {
        	System.out.println( "Error!! Number of particles and number of stripes don't fit!!");
        	Simulation simulation = new Simulation(stt);
        	return simulation;
        }
        
        Random ranGen = new Random();

    	for (int i = 0; i < numstripes; i++) {
    		for (int k = 0; k < numpart/numstripes; k++) {
    			
    		Particle par = new ParticleFull();
    		par.setY(stt.getSimulationHeight() * 1/dnumpart*numstripes*k);
    		if ( ( (i+1) % 2 ) == 0 ) {
    			par.setVy(speed);
    		} else {
    			par.setVy(-speed);
    		}

    		//par.setX(stt.getSimulationWidth() / numstripes * i + stripeWidth/2 + (Math.random() - 0.5)*stripeWidth );
    		par.setX(stt.getSimulationWidth() / numstripes * i + stripeWidth/2 + ranGen.nextGaussian()*stripeWidth*0.15 );
    		par.setRadius(radius);
    		par.setVx(0);
    		par.setCharge(-charge);
    		par.setMass(1);
    		stt.addParticle(par);
    		
    		}
    	}
    	
    	stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
    	Simulation simulation = new Simulation(stt);
    	/*
    	for (int i = 0; i < stt.getGridCellsY(); i++) {
    		for (int k = 0; k < stt.getGridCellsX(); k++) {

    		simulation.grid.setBz(k, i, 0.3*Math.cos( Math.PI/stt.getSimulationWidth()*numstripes*stt.getGridStep()*(k+1/2)));
    		
    		}
    	}
    	*/
    	return simulation;
    }
    
    public static Simulation initWaveTest(double kx) {
    	Settings stt = new Settings();

    	stt.setTimeStep(0.5);
    	stt.setTMax(100);
    	stt.setSpectrumStep(1);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	/*stt.setSimulationWidth(100);
    	stt.setSimulationHeight(100);*/
    	stt.setGridStep(1);
    	stt.setGridCellsX(40);
    	stt.setGridCellsY(10);
    	stt.setGridCellsZ(10);
    	stt.setNumOfParticles(0);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());
    	
    	stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
    	Simulation simulation = new Simulation(stt); 	

    	for (int i = 0; i < stt.getGridCellsY(); i++) {
    		for (int k = 0; k < stt.getGridCellsX(); k++) {
    			for (int j = 0; j < stt.getGridCellsZ(); j++) {
    				
    				simulation.grid.setEy(k, i, stt.getGridCellsZ()/2, Math.sin( kx*Math.PI/stt.getGridCellsX()*k ));
    	    		simulation.grid.setBz(k, i, stt.getGridCellsZ()/2, Math.sin( kx*Math.PI/stt.getGridCellsX()*k ));
    	    		
    			}
    		}
    	}
    	
    	return simulation;
    }
    
    public static Simulation initPair3D(double charge, double radius) {
    	Settings stt = new Settings();

    	stt.setTimeStep(0.025);
    	stt.setTMax(5000);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	stt.setSpectrumStep(500);
    	/*stt.setSimulationWidth(100);
    	stt.setSimulationHeight(100);*/
    	stt.setGridStep(1);
    	stt.setGridCellsX(40);
    	stt.setGridCellsY(20);
    	stt.setGridCellsZ(20);
                stt.setNumOfParticles(4);

    	stt.addForce(new ConstantForce());

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());

    	for (int k = 0; k < 2; k++) {
    		Particle par = new ParticleFull();
    		par.setX(stt.getSimulationWidth() * 1/100.0*(5*k+25));
    		par.setY(stt.getSimulationHeight() * 1/2 + stt.getGridStep()*0/2);
    		par.setZ(stt.getSimulationDepth() * 1/2 + stt.getGridStep()*0/2);
    		par.setRadius(radius);
    		par.setVx(0.0*(1-2*k));
    		par.setVy(0);
    		par.setVz(0);
    		par.setMass(1);
    		par.setCharge(charge*(1-2*k));
    		if (k == 0) {
    			par.setColor(Color.red);
    		} else {
    			par.setColor(Color.blue);
    		}
    		stt.addParticle(par);
    	}
    	
    	for (int k = 0; k < 2; k++) {
    		Particle par = new ParticleFull();
    		par.setX(stt.getSimulationWidth() * 1/100.0*(5*k+70));
    		par.setY(stt.getSimulationHeight() * 1/2 + stt.getGridStep()*0/2);
    		par.setZ(stt.getSimulationDepth() * 1/2 + stt.getGridStep()*0/2);
    		par.setRadius(radius);
    		par.setVx(-0.0*(1-2*k));
    		par.setVy(0);
    		par.setVz(0);
    		par.setMass(1);
    		par.setCharge(-charge*(1-2*k));
    		if (k == 0) {
    			par.setColor(Color.blue);
    		} else {
    			par.setColor(Color.red);
    		}
    		stt.addParticle(par);
    	}
                
                stt.setPoissonSolver(new PoissonSolverFFTPeriodic());
                //stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
                //stt.setInterpolator(new NearestGridPoint());
                stt.setInterpolator(new ChargeConservingCIC());
                //stt.setIterations(1);//Testing purposes!!!
                //set to charge conserving CIC; already preset in settings
    	Simulation simulation = new Simulation(stt);
    	
    	//DipoleCorrection(simulation);
    	
    	return simulation;
    }
    
    public static Simulation initOneTest3D(double charge, double radius) {
    	Settings stt = new Settings();

    	stt.setTimeStep(0.1);
    	stt.setSpeedOfLight(1);
    	stt.setGridStep(0.5);
    	stt.setGridCellsX(20);
    	stt.setGridCellsY(20);
    	stt.setGridCellsZ(20);
    	stt.setTMax(100);
    	/*
		ConstantForce cf = new ConstantForce();
		cf.bz = -1;
		stt.addForce(cf);
		*/
                stt.setNumOfParticles(1);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());

    		Particle par = new ParticleFull();
    		par.setX(stt.getSimulationWidth() * 50/100);
    		par.setVx(0.1);
    		par.setY(stt.getSimulationHeight() * 50/100 );
    		par.setZ(stt.getSimulationDepth() * 50/100 );
    		par.setRadius(radius);
    		par.setVy(0);
    		par.setVz(0);
    		par.setMass(1);
    		par.setCharge(-charge);
    		par.setColor(Color.red);
    		stt.addParticle(par);

    		stt.setPoissonSolver(new EmptyPoissonSolver());
    		stt.useGrid(true);
    		//stt.setPoissonSolver(new PoissonSolverFFTPeriodic());
    		//stt.setInterpolator(new NearestGridPoint());
    	Simulation simulation = new Simulation(stt);
    	/*for (int i = 0; i < stt.getGridCellsY(); i++) {
    		for (int k = 0; k < stt.getGridCellsX(); k++) {
    			for (int j = 0; j < stt.getGridCellsZ(); j++) {
    				
    				simulation.grid.setEx(k, i, j, -1 );
    				simulation.grid.setEy(k, i, j, -1 );
    	    		simulation.grid.setEz(k, i, j, -1 );
    	    		
    			}
    		}
    	}*/
    	return simulation;
    }
    
    public static Simulation initTwoStream3D(double charge, double radius, int numpart) {
    	Settings stt = new Settings();
    	double dnumpart = numpart;
    	Random ranGen = new Random();

    	stt.setTimeStep(0.1);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	/*stt.setSimulationWidth(100);
    	stt.setSimulationHeight(100);*/
    	stt.setGridStep(5);
    	stt.setGridCellsX(20);
    	stt.setGridCellsY(4);
    	stt.setGridCellsZ(4);
                stt.setNumOfParticles(2*numpart);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());

    	for (int k = 0; k < 2*numpart; k++) {
    		Particle par = new ParticleFull();
    		if(k < numpart) {par.setX(stt.getSimulationWidth() * 1/dnumpart*k);
    		par.setVx(0.2 + ranGen.nextGaussian()*0.001);
    		par.setColor(Color.red);
    		}
    		else {par.setX(stt.getSimulationWidth() * 1/dnumpart*(k-numpart));
    		par.setVx(-0.2 + ranGen.nextGaussian()*0.001);
    		par.setColor(Color.blue);
    		}
    		par.setY(stt.getSimulationHeight() * 1/2 );
    		par.setZ(stt.getSimulationHeight() * 1/2 );
    		par.setRadius(radius);
    		par.setVy(0);
    		par.setVz(0);
    		par.setMass(1);
    		par.setCharge(-charge);
    		stt.addParticle(par);
    	}
    	stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
    	Simulation simulation = new Simulation(stt);
    	return simulation;
    }
    
    public static Simulation initWeibel3D(double charge, double radius, int numpart, int numstripes, double speed) {
    	Settings stt = new Settings();
    	double dnumpart = numpart;

    	stt.setTimeStep(0.1);
    	stt.setSpeedOfLight(1);
    	stt.setRelativistic(true);
    	/*stt.setSimulationWidth(100);
    	stt.setSimulationHeight(100);*/
    	stt.setGridStep(10);
    	stt.setGridCellsX(10);
    	stt.setGridCellsY(10);
    	stt.setGridCellsZ(10);
                stt.setNumOfParticles(numpart);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());
                
        double stripeWidth = stt.getSimulationWidth() / numstripes;
        
        if ( (numpart % numstripes) != 0 ) {
        	System.out.println( "Error!! Number of particles and number of stripes don't fit!!");
        	Simulation simulation = new Simulation(stt);
        	return simulation;
        }
        
        Random ranGen = new Random();

    	for (int i = 0; i < numstripes; i++) {
    		for (int k = 0; k < numpart/numstripes; k++) {
    			
    		Particle par = new ParticleFull();
    		par.setY(stt.getSimulationHeight() * 1/dnumpart*numstripes*k);
    		if ( ( (i+1) % 2 ) == 0 ) {
    			par.setVy(speed);
    		} else {
    			par.setVy(-speed);
    		}

    		//par.setX(stt.getSimulationWidth() / numstripes * i + stripeWidth/2 + (Math.random() - 0.5)*stripeWidth );
    		par.setX(stt.getSimulationWidth() / numstripes * i + stripeWidth/2 + ranGen.nextGaussian()*stripeWidth*0.1 );
    		par.setZ(stt.getSimulationDepth() / 2 + ranGen.nextGaussian()*stripeWidth*0.1 );
    		par.setRadius(radius);
    		par.setVx(0);
    		par.setVz(0);
    		par.setCharge(-charge);
    		par.setMass(1);
    		stt.addParticle(par);
    		
    		}
    	}
    	
    	stt.setPoissonSolver(new EmptyPoissonSolver());
                stt.useGrid(true);
    	Simulation simulation = new Simulation(stt);
    	/*
    	for (int i = 0; i < stt.getGridCellsY(); i++) {
    		for (int k = 0; k < stt.getGridCellsX(); k++) {

    		simulation.grid.setBz(k, i, 0.3*Math.cos( Math.PI/stt.getSimulationWidth()*numstripes*stt.getGridStep()*(k+1/2)));
    		
    		}
    	}
    	*/
    	return simulation;
    }
    
    public static Simulation initInterpolationTest3D(double charge, double radius) {
    	Settings stt = new Settings();

    	stt.setTimeStep(.1);
    	stt.setSpeedOfLight(1);
    	stt.setGridStep(1);
    	stt.setGridCellsX(4);
    	stt.setGridCellsY(4);
    	stt.setGridCellsZ(4);
    	stt.setTMax(1);
    	/*
		ConstantForce cf = new ConstantForce();
		cf.bz = -1;
		stt.addForce(cf);
		*/
                stt.setNumOfParticles(1);

    	stt.setBoundary(GeneralBoundaryType.Periodic);
                stt.setGridSolver(new SimpleSolver());

    		Particle par = new ParticleFull();
    		par.setX(1.0);
    		par.setVx(0);
    		par.setY(0.5);
    		par.setZ(0.5);
    		par.setRadius(radius);
    		par.setVy(0);
    		par.setVz(0);
    		par.setMass(1);
    		par.setCharge(-charge);
    		par.setColor(Color.red);
    		stt.addParticle(par);

    		stt.setPoissonSolver(new EmptyPoissonSolver());
    		stt.useGrid(true);
    		//stt.setPoissonSolver(new PoissonSolverFFTPeriodic());
    		//stt.setInterpolator(new NearestGridPoint());
    		stt.setInterpolator(new ChargeConservingCIC());
    	Simulation simulation = new Simulation(stt);
    	
    	simulation.grid.setEx(0, 0, 0, 1 );
    	simulation.grid.setEx(0, 0, 1, 1 );
    	simulation.grid.setEx(0, 1, 0, 1 );
    	simulation.grid.setEx(0, 1, 1, 1 );
    	simulation.grid.setEx(1, 0, 0, 2 );
    	simulation.grid.setEx(1, 0, 1, 2 );
    	simulation.grid.setEx(1, 1, 0, 2 );
    	simulation.grid.setEx(1, 1, 1, 2 );
    	
    	return simulation;
    }
    
    private static void DipoleCorrection(Simulation sim) {

    	double correctionX = 0;
    	double correctionY = 0;
    	double correctionZ = 0;
    	int volume = sim.grid.getNumCellsX()*sim.grid.getNumCellsY()*sim.grid.getNumCellsZ();
    	
    	for (int i = 0; i < sim.grid.getNumCellsX(); i++) {
    		for (int j = 0; j < sim.grid.getNumCellsY(); j++) {
    			for (int k = 0; k < sim.grid.getNumCellsZ(); k++) {
    				
    				correctionX += sim.grid.getRho(i, j, k) * i * sim.grid.getCellWidth();
    	    		correctionY += sim.grid.getRho(i, j, k) * j * sim.grid.getCellHeight();
    	    		correctionZ += sim.grid.getRho(i, j, k) * k * sim.grid.getCellDepth();
    	    		
    			}
    		}
    	}
    	
    	System.out.println(correctionX);
    	System.out.println(correctionY);
    	System.out.println(correctionZ);
    	
    	for (int i = 0; i < sim.grid.getNumCellsX(); i++) {
    		for (int j = 0; j < sim.grid.getNumCellsY(); j++) {
    			for (int k = 0; k < sim.grid.getNumCellsZ(); k++) {
    	    		
    	    		sim.grid.addEx( i, j, k, -correctionX/volume );
    	    		sim.grid.addEy( i, j, k, -correctionY/volume );
    	    		sim.grid.addEz( i, j, k, -correctionZ/volume );
    	    		
    			}
    		}
    	}
    	
    	
	}

    
}
