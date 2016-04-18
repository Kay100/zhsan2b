package com.sz.zhsan2b.core.util;

import java.util.ArrayList;
import java.util.List;
import org.graphstream.algorithm.AStar;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.Path;
import com.rits.cloning.Cloner;
import com.sz.zhsan2b.core.entity.Position;
import com.sz.zhsan2b.core.util.GameMap.MoveRangeAlgo.PositionEntry;


public class GameMap {
	public class MoveRangeAlgo {

		public class PositionEntry {
			public Position position;
			public int moveLeft;
			public PositionEntry(Position position, int moveLeft) {
				this.position = position;
				this.moveLeft = moveLeft;
			}
			

		}
		private List<PositionEntry> moveRangeList = new ArrayList<PositionEntry>();
		private List<PositionEntry> tempRangeList = new ArrayList<PositionEntry>();
		private int cursor = 0;
		
		public List<PositionEntry> calculateMoveRangeList(Position orgin,int moveLong,List<Position> unMovablePositions){

			moveRangeList.add(new PositionEntry(orgin, moveLong));
			while(canFetchOne(cursor)){
				PositionEntry curPE =  moveRangeList.get(cursor);
				Position curOrigin =curPE.position;
				List<Position> tempPositionList = BattleUtils.getEightDirectionPosition(curOrigin);
				List<Position> removeUnMovableAndRepeatThenLeftPositions = calculateOneLoopPathPositions(tempPositionList,unMovablePositions,getPositionListByPositionEntryList(moveRangeList));
				a:
				for(Position loopPathP:removeUnMovableAndRepeatThenLeftPositions){
					List<Position> tempRPList = getPositionListByPositionEntryList(tempRangeList);
					int cost =calculateNextNodeWeight(loopPathP);
					if(cost>curPE.moveLeft){
						continue;
					}
					PositionEntry pE = new PositionEntry(loopPathP, curPE.moveLeft-cost);
					for(Position tempP:tempRPList){
						if(tempP.compareTo(loopPathP)==0){
							
							replaceOrNotTempRangeListBy(pE);
							continue a;
						}
					}
					tempRangeList.add(pE);
					
				}
				if(!canFetchOne(cursor+1)){
					moveRangeList.addAll(tempRangeList);
					tempRangeList.clear();
				}
				cursor++;
			}
			return moveRangeList;
			

		}

		private void replaceOrNotTempRangeListBy(PositionEntry pE) {
			for(int i=0;i<tempRangeList.size();i++){
				PositionEntry curPE = tempRangeList.get(i);
				if(curPE.position.compareTo(pE.position)==0){
					if(pE.moveLeft>curPE.moveLeft){
						tempRangeList.set(i, pE);
					}
				}
			}
			
		}

		private List<Position> calculateOneLoopPathPositions(
				List<Position> tempPositionList,
				List<Position> unMovablePositions,
				List<Position> moveRangePositionList) {
			for(int i=tempPositionList.size()-1;i>=0;i--){
				if(BattleUtils.contains(unMovablePositions, tempPositionList.get(i))){
					tempPositionList.remove(i);
					continue;
				}
				if(BattleUtils.contains(moveRangePositionList, tempPositionList.get(i))){
					tempPositionList.remove(i);
					continue;
				}
				
			}
			return tempPositionList;
		}

		private boolean canFetchOne(int cursor) {
			
			return cursor>=moveRangeList.size()?false:true;
		}

		
	}	
	private Graph graph;
	private final Cloner cloner = new Cloner();
	

	public Graph getGraph() {
		return graph;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	public List<Position> calculatePositionPath(Position from,
			Position to,List<Position> occupiedPositions) {
        //temp handle the question by cloning , but this is costly , someday ,should modify to use rebuild nodes which is just removed.
		
		Graph tempGraph = cloner.deepClone(graph);
 		
 		for(Position p:occupiedPositions){
 			if(!p.equals(from)&&!p.equals(to)){
 				String idS=String.valueOf(p.getId());
 				if(tempGraph.getNode(idS) !=null){
 					tempGraph.removeNode(idS);
 				}
 				
 			}
 			
 		}
 		AStar astar = new AStar(tempGraph);
 		astar.compute(String.valueOf(from.getId()), String.valueOf(to.getId()));
 		Path path = astar.getShortestPath();
 		List<Position> pathPositions =null;
 		
 		if(path!=null){
 			List<Node> nodeList = path.getNodePath();
 			pathPositions= new ArrayList<Position>(nodeList.size()+3);
 			for(Node n:nodeList){
 				pathPositions.add(new Position(n.getId()));
 			}
 		}
		return pathPositions;
		
	}

	public int calculateNextNodeWeight(Position to) {

		return graph.getNode(String.valueOf(to.getId())).getAttribute("weight");
	}

	public List<Position> calculateMoveRangeList(Position orgin,int moveLong,List<Position> unMovablePositions){
		List<PositionEntry> moveRangeList = new MoveRangeAlgo().calculateMoveRangeList(orgin, moveLong, unMovablePositions);
		return getPositionListByPositionEntryList(moveRangeList);
	}
	private List<Position> getPositionListByPositionEntryList(List<PositionEntry> entryList){
		List<Position> returnArray = new ArrayList<Position>();
		for(PositionEntry pE:entryList){
			returnArray.add(pE.position);
		}
		return returnArray;
		
	}	
	

}
