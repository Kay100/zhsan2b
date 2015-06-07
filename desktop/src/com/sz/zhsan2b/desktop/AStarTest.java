package com.sz.zhsan2b.desktop;


import java.io.IOException;

 



import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.graphstream.algorithm.AStar;
import org.graphstream.algorithm.AStar.DistanceCosts;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import org.graphstream.graph.implementations.AbstractEdge;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.graph.implementations.SingleNode;
import org.graphstream.stream.file.FileSourceDGS;
import org.graphstream.ui.view.Viewer;



import com.rits.cloning.Cloner;
 
 public class AStarTest {
 	//     B-(1)-C
 	//    /       \
 	//  (1)       (10)
 	//  /           \
 	// A             F
 	//  \           /
 	//  (1)       (1)
 	//    \       /
 	//     D-(1)-E
 	static String my_graph = 
 		"DGS004\n" 
 		+ "my 0 0\n" 
 		+ "an A xy: 0,1\n" 
 		+ "an B xy: 1,2\n"
 		+ "an C xy: 2,2\n"
 		+ "an D xy: 1,0\n"
 		+ "an E xy: 2,0\n"
 		+ "an F xy: 3,1\n"
 		+ "ae AB A B weight:1 \n"
 		+ "ae AD A D weight:1 \n"
 		+ "ae BC B C weight:1 \n"
 		+ "ae CF C F weight:10 \n"
 		+ "ae DE D E weight:1 \n"
 		+ "ae EF E F weight:1 \n"
 		;
    protected static String styleSheet =
            "node {" +
            "   fill-color: black;" +
            "}" +
            "node.marked {" +
            "   fill-color: red;" +
            "}";
 	public static final int MAX_ROWS = 5;
 	public static final int MAX_COLS = 5;
    
 	public static void main(String[] args) throws IOException {
 		Graph graph = new DefaultGraph("A Test");
 		//graph.addAttributeSink(new MyAttributeSink(graph));
 		
 		//StringReader reader = new StringReader(my_graph);
 
 		FileSourceDGS source = new FileSourceDGS();
 		source.addSink(graph);
// 		source.readAll(reader);
 		// add node to graph
 		List<String> notes = new ArrayList<String>();
 		for(int y=0;y<MAX_ROWS;y++){
 			for(int x=0;x<MAX_COLS;x++){
 				int tempId = y*MAX_ROWS+x;
 				graph.addNode(String.valueOf(tempId));
 				graph.getNode(String.valueOf(tempId)).addAttribute("xy", x,y);
 				graph.getNode(String.valueOf(tempId)).addAttribute("ui.label", tempId);
 				notes.add(String.valueOf(tempId));
 				
 			}
 		}
 		// add edge to graph
 		
 		for(String nodeId : notes){
 			int x = Integer.parseInt(graph.getNode(nodeId).getArray("xy")[0].toString());
 			int y = Integer.parseInt(graph.getNode(nodeId).getArray("xy")[1].toString());
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
 		// add edge weight
 		for(Iterator<AbstractEdge> iter=graph.getEdgeIterator();iter.hasNext();){
 			         Edge edge = (Edge)iter.next();
					edge.addAttribute("weight", 1);
					
 			         
 		}
 		
 		//graph.getNode("11").setAttribute("weight", 10);
        graph.getEdge("1117").setAttribute("weight", 9);
        graph.getEdge("1418").setAttribute("weight", 11);
        graph.getEdge("1419").setAttribute("weight", 12);
        Cloner cloner=new Cloner();
        cloner.setDumpClonedClasses(false);
        Graph graphCloned=cloner.deepClone(graph);
        
 		AStar astar = new AStar(graphCloned);
 		//astar.setCosts(new DistanceCosts());
 		graph.removeNode("12");
 		graph.removeNode("13");
 		graph.removeNode("10");
 		
 		astar.compute("24", "1");
 		
        //System.out.println(graph);
// 	    Iterator<Node> iter = astar.getShortestPath().getNodePath().iterator();
//		System.out.println(iter.next());
//		System.out.println(iter.next());
		Path path = astar.getShortestPath();
 		System.out.println(path);
 		
 		graph.setAttribute("ui.stylesheet", styleSheet);
 		List<Node> nodeList = path.getNodePath();
 		for(Node node:nodeList){
 			
 			node.addAttribute("ui.class", "marked");
 		}

 		
 
 		
 		Viewer viewer = graph.display();
 		viewer.disableAutoLayout();

 	}
	
 }

