package fr.ign.cogit.calculation;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.sig3d.calculation.BooleanOperators;
import fr.ign.cogit.geoxygene.sig3d.calculation.Util;
import fr.ign.cogit.geoxygene.sig3d.geometry.Sphere;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_SolidBoundary;

public class TestBooleanOperators {

	// ---------------------------------- ATTRIBUTES ----------------------------------

	//private static double epsilon = Math.pow(10, -10);

	// ---------------------------------- PREPROCESS ----------------------------------

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	// ------------------------------------ TESTS -------------------------------------

/*
	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes intersection
	// --------------------------------------------------------------------------------
	public void testCompute1() {

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = createCube(0, 0, 0, 10);
		GM_Solid cube2 = createCube(5, 5, 5, 10);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		GM_Solid s = BooleanOperators.compute(object1, object2, BooleanOperators.INTERSECTION);

		// Processing expected result
		GM_Solid sExp= createTriangulatedCube(5, 5, 5, 5);

		// Comparison
		assertTrue("Output solid from intersection method is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes intersection (same positions)
	// --------------------------------------------------------------------------------
	public void testCompute2() {

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = createCube(5, 5, 5, 10);
		GM_Solid cube2 = createCube(5, 5, 5, 10);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		GM_Solid s = BooleanOperators.compute(object1, object2, BooleanOperators.INTERSECTION);

		// Processing expected result
		GM_Solid sExp= createTriangulatedCube(5, 5, 5, 10);

		// Comparison
		assertTrue("Output solid from intersection method is incorrect", s.equals(sExp));

	}

	@Test
	// --------------------------------------------------------------------------------
	// Test for solid cubes intersection (empty intersection)
	// --------------------------------------------------------------------------------
	public void testCompute3() {

		// Creating features
		DefaultFeature object1 = new DefaultFeature();
		DefaultFeature object2 = new DefaultFeature();

		// Creating geometries
		GM_Solid cube1 = createCube(5, 5, 5, 1);
		GM_Solid cube2 = createCube(7, 7, 7, 1);

		// Adding geometries
		object1.setGeom(cube1);
		object2.setGeom(cube2); 

		// Processing intersection
		GM_Solid s = BooleanOperators.compute(object1, object2, BooleanOperators.INTERSECTION);

		// Comparison
		assertNull("Output solid from intersection method is incorrect", s);
		
		System.out.println(Util.volumeTriangulatedSolid(new Sphere(new DirectPosition(0,0,0), 10, 100)));
		
		//ShapefileWriter.write(featureCollection, shapefileName);
			
		
		
	}

*/
	// --------------------------------------------------------------------------------
	// Method for creating cube : origine x,y,z, size c
	// --------------------------------------------------------------------------------
	public GM_Solid createCube(double x, double y, double z, double c) {

		// Creating boundaries
		ArrayList<GM_Polygon> BOUNDARY = new ArrayList<GM_Polygon>();

		// Filling boundary

		// FACE 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(x,   y,   z));
		dpl1.add(new DirectPosition(x+c, y,   z));
		dpl1.add(new DirectPosition(x+c, y+c, z));
		dpl1.add(new DirectPosition(x,   y+c, z));
		dpl1.add(new DirectPosition(x,   y,   z));
		GM_LineString ls1 = new GM_LineString(dpl1);
		GM_Polygon polygon1 = new GM_Polygon(ls1);
		BOUNDARY.add(polygon1);

		// FACE 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(x,   y, z));
		dpl2.add(new DirectPosition(x+c, y, z));
		dpl2.add(new DirectPosition(x+c, y, z+c));
		dpl2.add(new DirectPosition(x,   y, z+c));
		dpl2.add(new DirectPosition(x,   y, z));
		GM_LineString ls2 = new GM_LineString(dpl2);
		GM_Polygon polygon2 = new GM_Polygon(ls2);
		BOUNDARY.add(polygon2);

		// FACE 3
		DirectPositionList dpl3 = new DirectPositionList();
		dpl3.add(new DirectPosition(x, y,   z));
		dpl3.add(new DirectPosition(x, y+c, z));
		dpl3.add(new DirectPosition(x, y+c, z+c));
		dpl3.add(new DirectPosition(x, y,   z+c));
		dpl3.add(new DirectPosition(x, y,   z));
		GM_LineString ls3 = new GM_LineString(dpl3);
		GM_Polygon polygon3 = new GM_Polygon(ls3);
		BOUNDARY.add(polygon3);

		// FACE 4
		DirectPositionList dpl4 = new DirectPositionList();
		dpl4.add(new DirectPosition(x,   y,   z+c));
		dpl4.add(new DirectPosition(x+c, y,   z+c));
		dpl4.add(new DirectPosition(x+c, y+c, z+c));
		dpl4.add(new DirectPosition(x,   y+c, z+c));
		dpl4.add(new DirectPosition(x,   y,   z+c));
		GM_LineString ls4 = new GM_LineString(dpl4);
		GM_Polygon polygon4 = new GM_Polygon(ls4);
		BOUNDARY.add(polygon4);

		// FACE 5
		DirectPositionList dpl5 = new DirectPositionList();
		dpl5.add(new DirectPosition(x,   y+c, z));
		dpl5.add(new DirectPosition(x+c, y+c, z));
		dpl5.add(new DirectPosition(x+c, y+c, z+c));
		dpl5.add(new DirectPosition(x,   y+c, z+c));
		dpl5.add(new DirectPosition(x,   y+c, z));
		GM_LineString ls5 = new GM_LineString(dpl5);
		GM_Polygon polygon5 = new GM_Polygon(ls5);
		BOUNDARY.add(polygon5);

		// FACE 6
		DirectPositionList dpl6 = new DirectPositionList();
		dpl6.add(new DirectPosition(x+c, y,   z));
		dpl6.add(new DirectPosition(x+c, y+c, z));
		dpl6.add(new DirectPosition(x+c, y+c, z+c));
		dpl6.add(new DirectPosition(x+c, y,   z+c));
		dpl6.add(new DirectPosition(x+c, y,   z));
		GM_LineString ls6 = new GM_LineString(dpl6);
		GM_Polygon polygon6 = new GM_Polygon(ls6);
		BOUNDARY.add(polygon6);

		// Creating solid
		return new GM_Solid(new GM_SolidBoundary(BOUNDARY));

	}

	// --------------------------------------------------------------------------------
	// Method for creating cube : origine x,y,z, size c
	// --------------------------------------------------------------------------------
	public GM_Solid createTriangulatedCube(double x, double y, double z, double c) {

		// Creating boundaries
		ArrayList<GM_Polygon> BOUNDARY = new ArrayList<GM_Polygon>();

		// Filling boundary

		// FACE 1
		DirectPositionList dpl1 = new DirectPositionList();
		dpl1.add(new DirectPosition(x,   y,   z));
		dpl1.add(new DirectPosition(x+c, y,   z));
		dpl1.add(new DirectPosition(x+c, y+c, z));
		dpl1.add(new DirectPosition(x,   y,   z));
		GM_LineString ls1 = new GM_LineString(dpl1);
		GM_Polygon polygon1 = new GM_Polygon(ls1);
		BOUNDARY.add(polygon1);

		// FACE 1b
		DirectPositionList dpl1b = new DirectPositionList();
		dpl1b.add(new DirectPosition(x,   y,   z));
		dpl1b.add(new DirectPosition(x,   y+c,   z));
		dpl1b.add(new DirectPosition(x+c, y+c, z));
		dpl1b.add(new DirectPosition(x,   y,   z));
		GM_LineString ls1b = new GM_LineString(dpl1b);
		GM_Polygon polygon1b = new GM_Polygon(ls1b);
		BOUNDARY.add(polygon1b);

		// FACE 2
		DirectPositionList dpl2 = new DirectPositionList();
		dpl2.add(new DirectPosition(x,   y, z));
		dpl2.add(new DirectPosition(x+c, y, z));
		dpl2.add(new DirectPosition(x+c, y, z+c));
		dpl2.add(new DirectPosition(x,   y, z));
		GM_LineString ls2 = new GM_LineString(dpl2);
		GM_Polygon polygon2 = new GM_Polygon(ls2);
		BOUNDARY.add(polygon2);

		// FACE 2b
		DirectPositionList dpl2b = new DirectPositionList();
		dpl2b.add(new DirectPosition(x,   y, z));
		dpl2b.add(new DirectPosition(x  , y, z+c));
		dpl2b.add(new DirectPosition(x+c, y, z+c));
		dpl2b.add(new DirectPosition(x,   y, z));
		GM_LineString ls2b = new GM_LineString(dpl2b);
		GM_Polygon polygon2b = new GM_Polygon(ls2b);
		BOUNDARY.add(polygon2b);

		// FACE 3
		DirectPositionList dpl3 = new DirectPositionList();
		dpl3.add(new DirectPosition(x, y,   z));
		dpl3.add(new DirectPosition(x, y+c, z));
		dpl3.add(new DirectPosition(x, y+c, z+c));
		dpl3.add(new DirectPosition(x, y,   z));
		GM_LineString ls3 = new GM_LineString(dpl3);
		GM_Polygon polygon3 = new GM_Polygon(ls3);
		BOUNDARY.add(polygon3);


		// FACE 3b
		DirectPositionList dpl3b = new DirectPositionList();
		dpl3b.add(new DirectPosition(x, y,   z));
		dpl3b.add(new DirectPosition(x, y+c, z+c));
		dpl3b.add(new DirectPosition(x, y  , z+c));
		dpl3b.add(new DirectPosition(x, y,   z));
		GM_LineString ls3b = new GM_LineString(dpl3b);
		GM_Polygon polygon3b = new GM_Polygon(ls3b);
		BOUNDARY.add(polygon3b);

		// FACE 4
		DirectPositionList dpl4 = new DirectPositionList();
		dpl4.add(new DirectPosition(x,   y,   z+c));
		dpl4.add(new DirectPosition(x+c, y,   z+c));
		dpl4.add(new DirectPosition(x+c, y+c, z+c));
		dpl4.add(new DirectPosition(x,   y,   z+c));
		GM_LineString ls4 = new GM_LineString(dpl4);
		GM_Polygon polygon4 = new GM_Polygon(ls4);
		BOUNDARY.add(polygon4);

		// FACE 4b
		DirectPositionList dpl4b = new DirectPositionList();
		dpl4b.add(new DirectPosition(x,   y,   z+c));
		dpl4b.add(new DirectPosition(x,   y+c,   z+c));
		dpl4b.add(new DirectPosition(x+c, y+c, z+c));
		dpl4b.add(new DirectPosition(x,   y,   z+c));
		GM_LineString ls4b = new GM_LineString(dpl4b);
		GM_Polygon polygon4b = new GM_Polygon(ls4b);
		BOUNDARY.add(polygon4b);

		// FACE 5
		DirectPositionList dpl5 = new DirectPositionList();
		dpl5.add(new DirectPosition(x,   y+c, z));
		dpl5.add(new DirectPosition(x+c, y+c, z));
		dpl5.add(new DirectPosition(x+c, y+c, z+c));
		dpl5.add(new DirectPosition(x,   y+c, z));
		GM_LineString ls5 = new GM_LineString(dpl5);
		GM_Polygon polygon5 = new GM_Polygon(ls5);
		BOUNDARY.add(polygon5);

		// FACE 5b
		DirectPositionList dpl5b = new DirectPositionList();
		dpl5b.add(new DirectPosition(x,   y+c, z));
		dpl5b.add(new DirectPosition(x,   y+c, z+c));
		dpl5b.add(new DirectPosition(x+c, y+c, z+c));
		dpl5b.add(new DirectPosition(x,   y+c, z));
		GM_LineString ls5b = new GM_LineString(dpl5b);
		GM_Polygon polygon5b = new GM_Polygon(ls5b);
		BOUNDARY.add(polygon5b);

		// FACE 6
		DirectPositionList dpl6 = new DirectPositionList();
		dpl6.add(new DirectPosition(x+c, y,   z));
		dpl6.add(new DirectPosition(x+c, y+c, z));
		dpl6.add(new DirectPosition(x+c, y+c, z+c));
		dpl6.add(new DirectPosition(x+c, y,   z));
		GM_LineString ls6 = new GM_LineString(dpl6);
		GM_Polygon polygon6 = new GM_Polygon(ls6);
		BOUNDARY.add(polygon6);

		// FACE 6b
		DirectPositionList dpl6b = new DirectPositionList();
		dpl6b.add(new DirectPosition(x+c, y,   z));
		dpl6b.add(new DirectPosition(x+c, y,   z+c));
		dpl6b.add(new DirectPosition(x+c, y+c, z+c));
		dpl6b.add(new DirectPosition(x+c, y,   z));
		GM_LineString ls6b = new GM_LineString(dpl6b);
		GM_Polygon polygon6b = new GM_Polygon(ls6b);
		BOUNDARY.add(polygon6b);


		// Creating solid
		return new GM_Solid(new GM_SolidBoundary(BOUNDARY));

	}

}








