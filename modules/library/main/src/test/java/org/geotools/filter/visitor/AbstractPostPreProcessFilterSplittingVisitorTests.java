/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 * 
 *    (C) 2005-2008, Open Source Geospatial Foundation (OSGeo)
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotools.filter.visitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.geotools.data.DataUtilities;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.feature.SchemaException;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.Id;
import org.opengis.filter.PropertyIsEqualTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.spatial.Equals;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.geometry.Boundary;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.Precision;
import org.opengis.geometry.TransfiniteSet;
import org.opengis.geometry.complex.Complex;
import org.opengis.geometry.coordinate.Position;
import org.opengis.geometry.primitive.Bearing;
import org.opengis.geometry.primitive.Point;
import org.geotools.filter.FilterCapabilities;
import org.geotools.filter.IllegalFilterException;
import org.geotools.filter.function.FilterFunction_geometryType;

import com.vividsolutions.jts.geom.Envelope;

public class AbstractPostPreProcessFilterSplittingVisitorTests extends TestCase {
	public class TestAccessor implements ClientTransactionAccessor {

		private Filter updateFilter;
		private String attribute;

		public Filter getDeleteFilter() {
			return null;
		}

		public Filter getUpdateFilter(String attributePath) {
			if( attributePath.equals(attribute) )
				return updateFilter;
			else
				return null;
		}

		public void setUpdate(String attribute, Filter updateFilter) {
			this.attribute=attribute;
			this.updateFilter=updateFilter;
		}

	}

	protected FilterFactory ff = CommonFactoryFinder.getFilterFactory(null);
	protected TestAccessor accessor;
	protected static final String typeName = "test";
	protected static final String geomAtt = "geom";
	protected static final String nameAtt = "name";
	protected static final String numAtt = "num";
    
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected PostPreProcessFilterSplittingVisitor newVisitor(FilterCapabilities supportedCaps) throws SchemaException {
		return new PostPreProcessFilterSplittingVisitor(supportedCaps,
                DataUtilities.createType(typeName,geomAtt+":Point,"+nameAtt+":String," + numAtt+":int"),
                accessor);
	} 
	
	protected PropertyIsEqualTo createPropertyIsEqualToFilter(String attr, String value) throws IllegalFilterException {
		return ff.equals(ff.property(attr), ff.literal(value));
	}

    protected Envelope createTestEnvelope() {
        return new Envelope(10,20,10,20);
    }
	
	/**
	 * Runs 3 tests.  1 with out filtercapabilities containing filter type.  1 with filter caps containing filter type
	 * 1 with an edit to the attribute being queried by filter.
	 * @param filter filter to process
	 * @param filterTypeMask the constant in {@link FilterCapabilities} that is equivalent to the FilterType used in filter
	 * @param attToEdit the attribute in filter that is queried.  If null then edit test is not ran.
	 */
	protected void runTest(Filter filter, FilterCapabilities supportedCaps, String attToEdit) throws SchemaException {
		// initialize fields that might be previously modified in current test
        PostPreProcessFilterSplittingVisitor visitor=newVisitor(new FilterCapabilities()); 
		if (accessor!=null )
		accessor.setUpdate("",null);

		// Testing when FilterCapabilites indicate that filter type is not supported
		filter.accept(visitor, null);

		assertEquals(filter, visitor.getFilterPost());
		assertEquals(Filter.INCLUDE, visitor.getFilterPre());
		
		// now filter type is supported
		visitor=newVisitor(supportedCaps);
		
        filter.accept(visitor, null);
		
		assertEquals(Filter.INCLUDE, visitor.getFilterPost());
		assertEquals(filter, visitor.getFilterPre());
		
		if (attToEdit != null && accessor!=null ) {
			// Test when the an update exists that affects the attribute of a
			// feature
            HashSet idSet = new HashSet();
            idSet.add(ff.featureId("fid"));
			Id updateFilter = ff.id(idSet);

			accessor.setUpdate(attToEdit, updateFilter);

			visitor = newVisitor(supportedCaps);

            filter.accept(visitor, null);

			assertEquals(filter, visitor.getFilterPost());
			assertEquals(ff.or(filter, updateFilter), visitor.getFilterPre());
		}
	}

	protected PropertyIsEqualTo createFunctionFilter() throws Exception {
        
		FilterFunction_geometryType geomTypeExpr = new FilterFunction_geometryType();
		geomTypeExpr.setParameters(Arrays.asList(new Expression[] { ff
				.property("geom") }));
	
		PropertyIsEqualTo filter = ff.equals(geomTypeExpr, ff.literal("Polygon"));
		return filter;
	}
	
    class MockGeometryImpl implements Geometry {
        public boolean contains(DirectPosition arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        public boolean contains(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        public TransfiniteSet difference(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        public boolean equals(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        public Boundary getBoundary() {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry getBuffer(double arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        public DirectPosition getCentroid() {
            // TODO Auto-generated method stub
            return null;
        }
        public Complex getClosure() {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry getConvexHull() {
            // TODO Auto-generated method stub
            return null;
        }
        public int getCoordinateDimension() {
            // TODO Auto-generated method stub
            return 0;
        }
        public CoordinateReferenceSystem getCoordinateReferenceSystem() {
            // TODO Auto-generated method stub
            return null;
        }
        public int getDimension(DirectPosition arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
        public double getDistance(Geometry arg0) {
            return distance(arg0);
        }
        public double distance(Geometry arg0) {
            // TODO Auto-generated method stub
            return 0;
        }
        public org.opengis.geometry.Envelope getEnvelope() {
            // TODO Auto-generated method stub
            return null;
        }
        public Set getMaximalComplex() {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry getMbRegion() {
            // TODO Auto-generated method stub
            return null;
        }
        public DirectPosition getRepresentativePoint() {
            // TODO Auto-generated method stub
            return null;
        }
        public TransfiniteSet intersection(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        public boolean intersects(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return false;
        }
        public boolean isCycle() {
            // TODO Auto-generated method stub
            return false;
        }
        public boolean isMutable() {
            // TODO Auto-generated method stub
            return false;
        }
        public boolean isSimple() {
            // TODO Auto-generated method stub
            return false;
        }
        public TransfiniteSet symmetricDifference(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry toImmutable() {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry transform(CoordinateReferenceSystem arg0) throws TransformException {
            // TODO Auto-generated method stub
            return null;
        }
        public Geometry transform(CoordinateReferenceSystem arg0, MathTransform arg1) throws TransformException {
            // TODO Auto-generated method stub
            return null;
        }
        public TransfiniteSet union(TransfiniteSet arg0) {
            // TODO Auto-generated method stub
            return null;
        }
        public MockGeometryImpl clone() throws CloneNotSupportedException {
            // TODO Auto-generated method stub
            return (MockGeometryImpl) super.clone();
        }
        public Precision getPrecision() {
            // TODO Auto-generated method stub
            return null;
        }
        public String toString() {
            return "MOCKGEOM";
        }
    }

}
