package com.ai.game.reversi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ai.homework2.Player;
import com.ai.log.MoveLogger;

public class ReversiGame 
{
	public static final int SIZE = 8;

	public static final long ONE_LONG = 1L;

	public static final long ZERO_LONG = 0L;

	/*
		Mask identifying edges of Board
	 */
	public static final long LEFT_MASK = 0x7F7F7F7F7F7F7F7FL;
	public static final long RIGHT_MASK =0xFEFEFEFEFEFEFEFEL;
	public static final long UP_MASK =   0x00FFFFFFFFFFFFFFL;
	public static final long DOWN_MASK = 0xFFFFFFFFFFFFFF00L;
	
	/**
	 * Mask For Corner
	 */
	public static final long CORNER_MASK = 0x8100000000000081L;
	/*
		Search Directions to search legal Moves and detect Flip/Captures of opponent
	 */
	public static final int UP_DOWN_DIRECTION = SIZE;
	public static final int LEFT_RIGHT_DIRECTION = 1;
	public static final int DIAGONAL_LEFT_DIR= SIZE+1;
	public static final int DIAGONAL_RIGHT_DIR = SIZE - 1;

	public static MoveFinder[] finders = { new MoveFinder(UP_DOWN_DIRECTION,UP_MASK,true),
		new MoveFinder(UP_DOWN_DIRECTION,DOWN_MASK,false),
		new MoveFinder(LEFT_RIGHT_DIRECTION, LEFT_MASK, true),
		new MoveFinder(LEFT_RIGHT_DIRECTION, RIGHT_MASK, false),
		new MoveFinder(DIAGONAL_LEFT_DIR, LEFT_MASK, true),
		new MoveFinder(DIAGONAL_LEFT_DIR, RIGHT_MASK, false),
		new MoveFinder(DIAGONAL_RIGHT_DIR, RIGHT_MASK, true),
		new MoveFinder(DIAGONAL_RIGHT_DIR, LEFT_MASK, false)
	};

	static final int[][] POSITN_WEIGHTS = {
		{ 99,-8,8,6,6,8,-8,99},
		{-8,-24,-4,-3,-3,-4,-24,-8},
		{8,-4,7,4,4,7,-4,8},
		{6,-3,4,0,0,4,-3,6},
		{6,-3,4,0,0,4,-3,6},
		{8,-4,7,4,4,7,-4,8},
		{-8,-24,-4,-3,-3,-4,-24,-8},
		{ 99,-8,8,6,6,8,-8,99}
	};

	public ReversiGame() {
		super();
	}

	private Map<Long,BoardPosition> valueToPositionMap = new HashMap<Long, BoardPosition>();

	/**
	 * @return the valueToPositionMap
	 */
	public Map<Long,BoardPosition> getValueToPositionMap() {
		return valueToPositionMap;
	}

	/**
	 * @param valueToPositionMap the valueToPositionMap to set
	 */
	public void setValueToPositionMap(Map<Long,BoardPosition> valueToPositionMap) {
		this.valueToPositionMap = valueToPositionMap;
	}


	public ReversiBitBoard initGame(StringBuffer inputState)
	{
		char[] myTempArray = new char[SIZE*SIZE];
		long whiteBoard =  ZERO_LONG;
		long blackBoard = ZERO_LONG;

		inputState.getChars(0, inputState.length(), myTempArray, 0);

		char c;
		int position;
		long positionValue;
		for(int i = 0 ; i < SIZE ; i++)
		{
			c = 'a';
			for(int j=0;j < SIZE;j++)
			{
				//Initializing Game Board of Agent and Opponent
				position = (i*SIZE + j);
				positionValue = ONE_LONG<<((SIZE-i-1)*SIZE+(SIZE-j-1));
				if(myTempArray[position] == Player.PLAYER_X)
				{
					whiteBoard |= positionValue;
				}
				else if(myTempArray[position] == Player.PLAYER_O)
				{
					blackBoard |= positionValue;
				}

				//Initializing  valueToPositionMap
				BoardPosition boardPosition = new BoardPosition(i+1, c++, POSITN_WEIGHTS[i][j]);
				this.valueToPositionMap.put( positionValue, boardPosition);
			}
		}
		ReversiBitBoard initState = new ReversiBitBoard(whiteBoard,blackBoard);
		MoveLogger.logState(initState, SIZE, "After Initializing State");
		return initState;
	}

	public int evaluateState(ReversiBitBoard state, char agentPlayer)
	{
		int val;
		if(agentPlayer == Player.PLAYER_X)
			//white - black
			val = evaluateBoard(state.getWhiteBoard()) - evaluateBoard(state.getBlackBoard());
		else
			//black - white
			val = evaluateBoard(state.getBlackBoard()) - evaluateBoard(state.getWhiteBoard());
		MoveLogger.logState(state, SIZE, "Evaluate move:" +state.getNodeName() + val);
		return val;
	}

	private int evaluateBoard(long board)
	{
		long bitPostn = ONE_LONG;
		int sum = 0;
		for( int i = 0 ; i < SIZE  ; i++)
		{

			for(int j = 0; j < SIZE ; j++)
			{
				if( ( bitPostn << ( (SIZE - i - 1)*SIZE + (SIZE -j - 1) ) & board ) != 0)
				{
					sum += POSITN_WEIGHTS[i][j];
				}
			}
		}
		return sum;
	}

	public List<ReversiBitBoard> getAllLegalStates(ReversiBitBoard state) 
	{
		List<ReversiBitBoard> allLegalStates = new ArrayList<ReversiBitBoard>();

		try 
		{
			List<Long> moves = findAllLegalMoves(state);
			if(moves != null && moves.size() > 0)
			{
				Collections.sort(moves, new Comparator<Long>() {

					public int compare(Long num1, Long num2) {
						BoardPosition b1 = valueToPositionMap.get(num1);
						BoardPosition b2 = valueToPositionMap.get(num2);
						if( b1 != null && b2 != null)
						{
							return (b1.getRow() == b2.getRow()) ? b1.getCol() - b2.getCol() : b1.getRow() - b2.getRow();
						}
						return 0;
					}});
				for (Long move : moves) 
				{
					try
					{
						MoveLogger.logBitBoard(move, SIZE, "Move-" + valueToPositionMap.get(move));
						
						ReversiBitBoard legalState = executeMove(move,state);
						legalState.setNodeName(valueToPositionMap.get(move).toString());
						
						MoveLogger.logState(legalState, SIZE, "State After Move");
						allLegalStates.add(legalState);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		return allLegalStates;
	}

	private ReversiBitBoard executeMove(Long move, ReversiBitBoard state) 
	{
		ReversiBitBoard nextState = null;
		try
		{
			long flipVertical = flipVertical(move,state);

			long flipHorizontal = flipHorizontal(move,state);

			long flipDiagLeft = flipDiagLeft(move,state);

			long flipDiagRight = flipDiagRight(move,state);

			long flippedPostns = (flipVertical | flipHorizontal | flipDiagRight | flipDiagLeft);

			MoveLogger.logBitBoard(flippedPostns, SIZE, "Flipped Positions:");

			long newAgentBoard =  flippedPostns | state.getAgentBoard();
			long newOpponentBoard = state.getOpponentBoard() & ~flippedPostns;

			nextState = state.getPlayer() == Player.PLAYER_X ? new ReversiBitBoard(newAgentBoard,newOpponentBoard):
				new ReversiBitBoard(newOpponentBoard,newAgentBoard);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return nextState;
	}

	private static long flipDiagRight(Long move, ReversiBitBoard state) {
		long oneMove = move;
		long positionTracker = move;
		long diagRightForward = ZERO_LONG;

		Long opponentBoard = state.getOpponentBoard();
		Long agentBoard = state.getAgentBoard();
		// Check Any Flips in Vertical UP Direction
		do
		{
			positionTracker = oneMove << DIAGONAL_RIGHT_DIR;
			oneMove = positionTracker & opponentBoard & RIGHT_MASK;
			diagRightForward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		diagRightForward = diagRightForward != ZERO_LONG && (((positionTracker ) & agentBoard) != ZERO_LONG) ? diagRightForward | move : ZERO_LONG;


		//Check Any Flips in Vertical Down Direction
		long diagRightBackward = ZERO_LONG;
		oneMove = move;
		do
		{
			positionTracker = oneMove >>> DIAGONAL_RIGHT_DIR;
			oneMove = positionTracker & opponentBoard & LEFT_MASK;
			diagRightBackward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		diagRightBackward = diagRightBackward != ZERO_LONG && (((positionTracker) & agentBoard) != ZERO_LONG) ? diagRightBackward | move : ZERO_LONG;

		return (diagRightBackward | diagRightForward);
	}

	private static long flipDiagLeft(Long move, ReversiBitBoard state) {

		long positionTracker = move;

		Long opponentBoard = state.getOpponentBoard();
		Long agentBoard = state.getAgentBoard();
		// Check Any Flips in Vertical UP Direction
		long diagLeftForward = ZERO_LONG;
		long oneMove = move;
		do
		{
			positionTracker = oneMove << DIAGONAL_LEFT_DIR;
			oneMove = positionTracker & opponentBoard & LEFT_MASK;
			diagLeftForward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		diagLeftForward = diagLeftForward != ZERO_LONG && (((positionTracker) & agentBoard) != ZERO_LONG) ? diagLeftForward | move : ZERO_LONG;


		//Check Any Flips in Vertical Down Direction
		oneMove = move;
		long diagLeftBackward = ZERO_LONG;
		do
		{
			positionTracker = oneMove >>> DIAGONAL_LEFT_DIR;
			oneMove = positionTracker & opponentBoard & RIGHT_MASK;
			diagLeftBackward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		diagLeftBackward = diagLeftBackward != ZERO_LONG && (((positionTracker ) & agentBoard) != ZERO_LONG) ? diagLeftBackward | move : ZERO_LONG;

		return (diagLeftBackward | diagLeftForward);
	}

	private static long flipHorizontal(Long move, ReversiBitBoard state) {

		long positionTracker = move;
		Long opponentBoard = state.getOpponentBoard();
		Long agentBoard = state.getAgentBoard();

		// Check Any Flips in Vertical UP Direction
		long horizontalForward = ZERO_LONG;
		long oneMove = move;
		do
		{
			positionTracker = oneMove << LEFT_RIGHT_DIRECTION;
			oneMove = positionTracker & opponentBoard & LEFT_MASK;
			horizontalForward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		horizontalForward = horizontalForward != ZERO_LONG && (((positionTracker) & agentBoard) != ZERO_LONG) ? horizontalForward | move : ZERO_LONG;

		//Check Any Flips in Vertical Down Direction
		long horizontalBackward = ZERO_LONG;
		oneMove = move;
		do
		{
			positionTracker = oneMove >>> LEFT_RIGHT_DIRECTION;
			oneMove = positionTracker & opponentBoard & RIGHT_MASK;
			horizontalBackward |= oneMove;
		}
		while(oneMove != 0);

		horizontalBackward = horizontalBackward != 0 && (((positionTracker) & agentBoard) != 0) ? horizontalBackward | move : ZERO_LONG;

		return (horizontalBackward | horizontalForward);
	}

	private static long flipVertical(Long move, ReversiBitBoard state) 
	{

		long positionTracker = move;
		Long opponentBoard = state.getOpponentBoard();
		Long agentBoard = state.getAgentBoard();

		// Check Any Flips in Vertical UP Direction
		long verticalForward = ZERO_LONG;
		long oneMove = move;
		do
		{
			positionTracker = oneMove << UP_DOWN_DIRECTION;
			oneMove = (oneMove<<UP_DOWN_DIRECTION) & opponentBoard;
			verticalForward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		verticalForward = verticalForward != ZERO_LONG && (((positionTracker) & agentBoard) != ZERO_LONG) ? verticalForward | move : ZERO_LONG;

		//Check Any Flips in Vertical Down Direction
		oneMove = move;
		long verticalBackward = ZERO_LONG;
		do
		{
			positionTracker = oneMove >>> UP_DOWN_DIRECTION;
			oneMove = positionTracker & opponentBoard;
			verticalBackward |= oneMove;
		}
		while(oneMove != ZERO_LONG);

		verticalBackward = verticalBackward != ZERO_LONG && ((positionTracker & agentBoard) != ZERO_LONG) ? verticalBackward | move : ZERO_LONG;

		return (verticalBackward|verticalForward);
	}

	public List<Long> findAllLegalMoves(ReversiBitBoard state)
	{
		long cloneAgentBoard = state.getAgentBoard();
		List<Long> legalMoves = new ArrayList<Long>();
		while(cloneAgentBoard != 0)
		{
			long agentPosition = Long.highestOneBit(cloneAgentBoard);
			getMovesFrmPostn(agentPosition,state,legalMoves);
			cloneAgentBoard = cloneAgentBoard & ~agentPosition; 
		}
		return legalMoves;
	}

	private List<Long> getMovesFrmPostn(long agentPosition, ReversiBitBoard state,List<Long> moves)
	{
		try 
		{
			for (MoveFinder finder : finders) 
			{

				Long move = finder.findMoves(agentPosition,state.getAgentBoard(),state.getOpponentBoard());
				if(move != ZERO_LONG && !moves.contains(move))
					moves.add(move);

			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return moves;
	}

	public String getStateMatrix(ReversiBitBoard nextState) 
	{
		StringBuffer stateMatrix = new StringBuffer();
		long bitPostn = ONE_LONG;
		for( int i = 0 ; i < SIZE ; i++)
		{

			for(int j = 0; j < SIZE ; j++)
			{
				int postn = (SIZE - i -1 )*SIZE + (SIZE -j-1);
				if( ((bitPostn <<postn) & nextState.getWhiteBoard()) != 0)
				{
					stateMatrix.append(Player.PLAYER_X);
				}
				else if(((bitPostn <<postn) & nextState.getBlackBoard()) != 0)
				{
					stateMatrix.append(Player.PLAYER_O);
				}
				else
				{
					stateMatrix.append("*");
				}
			}
			stateMatrix.append("\n");
		}
		return stateMatrix.toString();
	}

	public boolean endOfGameReached(ReversiBitBoard state) 
	{
		boolean endOfGameReached = false;
		// Board is FULL
		long allOnes = ~ZERO_LONG;
		if((state.getWhiteBoard() | state.getBlackBoard()) == allOnes)
			endOfGameReached = true;
		else if(state.getWhiteBoard() == ZERO_LONG || state.getBlackBoard()==ZERO_LONG) //No Black or White
			endOfGameReached = true;
		return endOfGameReached;
	}

	public int evaluateStateComp(ReversiBitBoard state, char agentPlayer)
	{
		int val;
		double frontierVal = 0,stabilityVal = 0,cornerVal = 0,evapVal = 0,mobilityVal = 0;
		long maxPlayerBoard,minPlayerBoard;
		
		if(agentPlayer == Player.PLAYER_X)
		{
			maxPlayerBoard = state.getWhiteBoard();
			minPlayerBoard = state.getBlackBoard();}
		else 
		{
			maxPlayerBoard = state.getBlackBoard();
			minPlayerBoard = state.getWhiteBoard();
		}
		
		//Evaporation Less peicess in board
		evapVal = getEvapVal(maxPlayerBoard,minPlayerBoard);

		long emptyPositions = ~(maxPlayerBoard | minPlayerBoard);

		long maxFrontiers = frontiers(maxPlayerBoard, emptyPositions);
		long minForntiers = frontiers(minPlayerBoard, emptyPositions);
		if(maxFrontiers != 0 || minForntiers != 0)
		{
			int maxPostns = Long.bitCount(maxFrontiers);
			int minPostns = Long.bitCount(maxFrontiers);
			frontierVal = (100d * (maxPostns - minPostns))/(maxPostns + minPostns);
		}
		
		// Calculate stability
		int stabilityMax = getStabilityVal(maxPlayerBoard,minPlayerBoard,maxFrontiers);
		int stabilityMin = getStabilityVal(minPlayerBoard,maxPlayerBoard,minForntiers);
		if(stabilityMax != 0 || stabilityMin !=0)
		{
			stabilityVal = (100d * (stabilityMax - stabilityMin))/(stabilityMax + stabilityMin);
		}
		
		// Calculate Corner
		cornerVal = getCornerVal(maxPlayerBoard,minPlayerBoard);
		
		//Mobility
		int mobilityMax = getMobility(maxPlayerBoard,minPlayerBoard);
		int mobilityMin = getMobility(maxPlayerBoard,minPlayerBoard);
		if(mobilityMax != 0 || mobilityMin !=0)
		{
			stabilityVal = (100d * (mobilityMax - mobilityMin))/(mobilityMax + mobilityMin);
		} 
		
		// Linear Function of features
		val = (int) (300*cornerVal + 78*frontierVal + 75*mobilityVal + 10*evapVal + 150*stabilityVal);
		return val;
	}

	private int getMobility(long agentBoard, long opponentBoard)
	{
		List<Long> legalMoves= new ArrayList<Long>(); 
		long cloneAgentBoard = agentBoard;
		while(cloneAgentBoard != 0)
		{
			long agentPosition = Long.highestOneBit(cloneAgentBoard);
			for (MoveFinder finder : finders) 
			{

				Long move = finder.findMoves(agentPosition,agentBoard,opponentBoard);
				if(move != ZERO_LONG && !legalMoves.contains(move))
					legalMoves.add(move);

			}
			cloneAgentBoard = cloneAgentBoard & ~agentPosition; 
		}

		return legalMoves.size();
	}

	protected double getEvapVal(long maxPlayerBoard, long minPlayerBoard)
	{
		
		int minNumOfPieces = Long.bitCount(minPlayerBoard);
		int maxNumOfPeices = Long.bitCount(maxPlayerBoard);
		
		if( minNumOfPieces + maxNumOfPeices < 35)
			return (100d * (minNumOfPieces - maxNumOfPeices))/(minNumOfPieces + maxNumOfPeices);
		
		return 0;
		
	}

	protected double getCornerVal(long maxPlayerBoard, long minPlayerBoard) 
	{
		double val = 0;
		int maxCorners = Long.bitCount(maxPlayerBoard & CORNER_MASK);
		int minCorners = Long.bitCount(minPlayerBoard & CORNER_MASK);
		if(maxCorners != 0 || minCorners != 0)
		{
			val = (100d * (maxCorners - minCorners))/(maxCorners + minCorners);
		}
			
		return val;
	}

	protected int getStabilityVal(long agentBoard, long opponentBoard, long frontiers)
	{
		long unstableMax = getAllUnStabilePositns(agentBoard,opponentBoard);
		int stableMax = Long.bitCount(agentBoard & ~(frontiers | unstableMax)); 
		return stableMax - Long.bitCount(unstableMax);
	}

	private long getAllUnStabilePositns(long agentBoard, long opponentBoard) 
	{
		long cloneOpponentBoard = opponentBoard;
		long unstablePostns = 0;
		while(cloneOpponentBoard != 0)
		{
			long opponentPosition = Long.highestOneBit(cloneOpponentBoard);
			for (MoveFinder finder : finders) 
			{

				long mathces = finder.findUnstablePostns(opponentPosition, opponentBoard,agentBoard);
				if(mathces != ZERO_LONG)
					unstablePostns |= mathces;
			}
			cloneOpponentBoard = cloneOpponentBoard & ~opponentBoard; 
		}
		return unstablePostns;
	}
	
	/*protected double getFrontierVal(long maxPlayerBoard, long minPlayerBoard) 
	{
		double value = 0;

		long emptyPositions = ~(maxPlayerBoard | minPlayerBoard);

		long maxFrontiers = frontiers(maxPlayerBoard, emptyPositions);
		long minForntiers = frontiers(minPlayerBoard, emptyPositions);
		
		if(maxFrontiers != 0 || minForntiers != 0)
			value = (100d * (maxFrontiers - minForntiers))/(maxFrontiers + minForntiers);

		return value;
	}
*/
	long frontiers(long board,long emptyPositions)
	{
		long frontiers = ZERO_LONG;
		long position;
		while( board != 0)
		{
			position = Long.highestOneBit(board);
			board = board & ~position;
			position = position & (LEFT_MASK & RIGHT_MASK & UP_MASK & DOWN_MASK);
			if (position != 0
					&& ((((position << UP_DOWN_DIRECTION) | (position >>> UP_DOWN_DIRECTION)
							& emptyPositions) != 0)
							|| (((position << LEFT_RIGHT_DIRECTION) | (position >>> LEFT_RIGHT_DIRECTION)
									& emptyPositions) != 0)
							|| (((position << DIAGONAL_LEFT_DIR) | (position >>> DIAGONAL_LEFT_DIR)
									& emptyPositions) != 0) || (((position << DIAGONAL_RIGHT_DIR) | (position >>> DIAGONAL_RIGHT_DIR)
							& emptyPositions) != 0)))

			{
				frontiers |= position;

			}
		}
		return frontiers;
	}
}
