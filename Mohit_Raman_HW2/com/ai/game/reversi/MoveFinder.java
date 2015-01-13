package com.ai.game.reversi;

public class MoveFinder {
	
	private int direction;
	
	private Long mask;
	
	public MoveFinder(int direction, Long mask, boolean moveForward) {
		super();
		this.direction = direction;
		this.mask = mask;
		this.moveForward = moveForward;
	}

	private boolean moveForward;

	protected Long findMoves(long agentPosition, long agentBoard, long opponentBoard)
	 {
		
		long oneMove = agentPosition;
		long positionTracker = agentPosition;
		long matchedPositions = ReversiGame.ZERO_LONG;
		
		while((positionTracker & mask) != ReversiGame.ZERO_LONG && oneMove != ReversiGame.ZERO_LONG)
		{
			if(moveForward)
				positionTracker = oneMove<<direction;
			else
				positionTracker = oneMove>>>direction;
			
			oneMove = positionTracker & opponentBoard;
			matchedPositions |= oneMove;
		}
		
		positionTracker = (matchedPositions != ReversiGame.ZERO_LONG && 
				((positionTracker & ~(agentBoard | opponentBoard))
					!= ReversiGame.ZERO_LONG)) ? positionTracker : ReversiGame.ZERO_LONG;
		
		return positionTracker;
	}
	protected Long findUnstablePostns(long opponentPostn, long opponentBoard, long agentBoard)
	 {
		
		long oneMove = opponentPostn;
		long positionTracker = opponentPostn;
		long matchedPositions = ReversiGame.ZERO_LONG;
		
		while((positionTracker & mask) != ReversiGame.ZERO_LONG && oneMove != ReversiGame.ZERO_LONG)
		{
			if(moveForward)
				positionTracker = oneMove<<direction;
			else
				positionTracker = oneMove>>>direction;
				
			oneMove = positionTracker & agentBoard;
			matchedPositions |= oneMove;
		}
		
		positionTracker = (matchedPositions != ReversiGame.ZERO_LONG && 
				((positionTracker & ~(agentBoard | opponentBoard))
					!= ReversiGame.ZERO_LONG)) ? positionTracker : ReversiGame.ZERO_LONG;
		if(positionTracker != 0)
			return matchedPositions;
		return positionTracker;
	}
	
	

}
