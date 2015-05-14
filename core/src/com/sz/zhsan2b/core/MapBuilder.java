package com.sz.zhsan2b.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;

import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.DefaultGraph;



public class MapBuilder {
 	public static final int MAX_ROWS = Constants.BATTLE_FIELD_YCOUNT;
 	public static final int MAX_COLS = Constants.BATTLE_FIELD_XCOUNT;
	
	public static Map buildMap(long militaryKindId){
		Map map = new Map();
		
 		Graph graph = new DefaultGraph("GraphOfMilitaryKind"+String.valueOf(militaryKindId));

 		// add node to graph
 		List<String> notes = new ArrayList<String>();
 		for(int y=0;y<MAX_ROWS;y++){
 			for(int x=0;x<MAX_COLS;x++){
 				int tempId = y*MAX_ROWS+x;
 				graph.addNode(String.valueOf(tempId));
 				graph.getNode(String.valueOf(tempId)).addAttribute("xy", x,y);
 				notes.add(String.valueOf(tempId));
 				
 			}
 		}
 		// add edge to graph
 		
 		for(String nodeId : notes){
 			//int x = Integer.parseInt(graph.getNode(nodeId).getArray("xy")[0].toString());
 			int x = ((Integer)graph.getNode(nodeId).getArray("xy")[0]).intValue();
 			int y = ((Integer)graph.getNode(nodeId).getArray("xy")[1]).intValue();
 			if(x+1<MAX_COLS){
 				int anotherNodeId = y*MAX_ROWS+x+1;
 				graph.addEdge(nodeId+String.valueOf(anotherNodeId), nodeId, String.valueOf(anotherNodeId));
 				if(y+1<MAX_COLS){
 					anotherNodeId = (y+1)*MAX_ROWS+x+1;
 					graph.addEdge(nodeId+String.valueOf(anotherNodeId), nodeId, String.valueOf(anotherNodeId));	
 				}
 			}
 			if(y+1<MAX_ROWS){
 				int anotherNodeId = (y+1)*MAX_ROWS+x;
 				graph.addEdge(nodeId+String.valueOf(anotherNodeId), nodeId, String.valueOf(anotherNodeId));	
 				if(x>0){
 					anotherNodeId =(y+1)*MAX_ROWS+x-1;
 					graph.addEdge(nodeId+String.valueOf(anotherNodeId), nodeId, String.valueOf(anotherNodeId));	
 				}
 			}
 			
 			
 		}
 		// add edge weight to graph
 		for(Iterator<AbstractEdge> iter=graph.getEdgeIterator();iter.hasNext();){
 			         Edge edge = (Edge)iter.next();
					edge.addAttribute("weight", 5);
					
 			         
 		}
 		
 		map.setGraph(graph);

		return map;
	}
	

}
