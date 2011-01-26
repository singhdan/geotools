/*
 *    GeoTools - The Open Source Java GIS Toolkit
 *    http://geotools.org
 *
 *    (C) 2002-2008, Open Source Geospatial Foundation (OSGeo)
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
package org.geotools.graph.build.line;

import org.geotools.graph.build.basic.BasicDirectedGraphBuilder;

/**
 * An implementation of GraphGenerator used to generate a graph representing a
 * directed line network. Graphs are generated by supplying the generator with 
 * objects of type LineSegment via the add(Object) method. <BR>
 * <BR>
 * For each line segment added, an edge in the graph is created. The builder
 * records the end coordinates of each line added, and maintains a map of 
 * coordinates to nodes, creating nodes when neccessary.<BR>
 * <BR>
 * Edges created by the generator are of type BasicDirectedEdge and contain an 
 * object of type LineSegment.<BR>
 * Nodes created by the generator are of type BasicDirectedXYNode and contain 
 * an object of type Coordinate. 
 *
 * @see org.geotools.graph.structure.basic.BasicDirectedXYNode
 * @see org.geotools.graph.structure.basic.BasicDirectedEdge
 * @see com.vividsolutions.jts.geom.LineSegment
 * @see com.vividsolutions.jts.geom.Coordinate
 * 
 * @author Justin Deoliveira, Refractions Research Inc, jdeolive@refractions.net
 *
 * @source $URL$
 */
public class BasicDirectedLineGraphGenerator extends BasicLineGraphGenerator {
  
  /**
   * Constructs a new BasicDirectedLineGraphGenerator.
   */
  public BasicDirectedLineGraphGenerator() {
    super();
    setGraphBuilder(new BasicDirectedGraphBuilder());  
  } 
}
