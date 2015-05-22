package com.sz.zhsan2b.core;

import java.util.List;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;

import com.badlogic.gdx.utils.Array;
import com.rits.cloning.Cloner;


public class Map {
	private Graph graph;
	private final Cloner cloner = new Cloner();
	

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public Array<Position> calculatePositionPath(Position from,
			Position to,Array<Position> occupiedPositions) {
        //temp handle the question by cloning , but this is costly , someday ,should modify to use rebuild nodes which is just removed.
		
		Graph tempGraph = cloner.deepClone(graph);
 		
 		for(Position p:occupiedPositions){
 			if(!p.equal(from)&&!p.equal(to)){
 				String idS=String.valueOf(p.getId());
 				if(tempGraph.getNode(idS) !=null){
 					tempGraph.removeNode(idS);
 				}
 				
 			}
 			
 		}
 		AStar astar = new AStar(tempGraph);
 		astar.compute(String.valueOf(from.getId()), String.valueOf(to.getId()));
 		Path path = astar.getShortestPath();
 		Array<Position> pathPositions =null;
 		
 		if(path!=null){
 			List<Node> nodeList = path.getNodePath();
 			pathPositions= new Array<Position>(nodeList.size()+3);
 			for(Node n:nodeList){
 				pathPositions.add(new Position(n.getId()));
 			}
 		}
		return pathPositions;
		
	}

	public int calculateNextEdgeWeight(Position from, Position to) {
	    Position tmp = null;
		if(from.compareTo(to)>0){
	    	tmp =from;
	    	from = to ;
	    	to = tmp;
	    }
		Integer i =(Integer)(graph.getEdge(String.valueOf(from.getId())+String.valueOf(to.getId())).getAttribute("weight"));
		return i.intValue();
	}

	
	

}
