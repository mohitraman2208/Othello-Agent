package com.ai.agent;

import java.util.List;

import com.ai.game.reversi.BoardPosition;
import com.ai.game.reversi.ReversiBitBoard;
import com.ai.game.reversi.ReversiGame;
import com.ai.homework2.Player;
import com.ai.log.MoveLogger;


public class AdversarialAgent 
{
	public static final String ROOT_STRING= "root";
	public static final String POSITIVE_INFINITY_STRING = "Infinity";
	public static final String NEGATIVE_INFINITY_STRING = "-Infinity";
	public static final String PASS_STRING = "pass";
	private int size;  // SIZE of board 8X8
	
	private AgentMode agentMode;
	
	private ReversiGame advGame;

	private int cutOffDepth;
	
	private char agentPlayer;
	
	private char opponentPlayer;
	
	private StringBuffer inputState;
	
	private ReversiBitBoard currentGameState;
	
	public StringBuffer log = new StringBuffer();
	
	public long cpuTime;
	
	public AdversarialAgent() {
		super();
		advGame = new ReversiGame();
		size = ReversiGame.SIZE;
		inputState = new StringBuffer();
	}

	
	public char getAgentPlayer()
	{
		return agentPlayer;
	}
	
	public char getOpponentPlayer()
	{
		return opponentPlayer;
	}
	
	public void setPlayer(char player)
	{
		this.agentPlayer = player;
		this.opponentPlayer = this.agentPlayer == Player.PLAYER_X ? Player.PLAYER_O : Player.PLAYER_X;
	}
	
	/**
	 * @return the agentMode
	 */
	public AgentMode getAgentMode() {
		return agentMode;
	}

	/**
	 * @param agentMode the agentMode to set
	 */
	public void setAgentMode(int agentMode) 
	{
		
		if(agentMode == AgentMode.CompMode.getValue())
		{
			this.agentMode = AgentMode.CompMode;
		}
		else if(agentMode == AgentMode.Greedy.getValue())
		{
			this.agentMode = AgentMode.Greedy;
		}
		else if(agentMode == AgentMode.MinMax.getValue())
		{
			this.agentMode = AgentMode.MinMax;
		}
		else if(agentMode == AgentMode.AlphaBeta.getValue())
		{
			this.agentMode = AgentMode.AlphaBeta;
		}
		else if(agentMode == AgentMode.LogMode.getValue())
		{
			this.agentMode = AgentMode.LogMode;
		}
		else
		{
			System.out.print("Error in Input!! Please Verify!");
		}
	}

	/**
	 * @return the advGame
	 */
	public ReversiGame getAdvGame() {
		return advGame;
	}

	/**
	 * @param advGame the advGame to set
	 */
	public void setAdvGame(ReversiGame advGame) {
		this.advGame = advGame;
	}

	public int getSize() {
		return size;
	}
	
	/**
	 * @return the inputState
	 */
	public StringBuffer getInputState() {
		return inputState;
	}

	/**
	 * @param inputState the inputState to set
	 */
	public void setInputState(StringBuffer inputState) {
		this.inputState = inputState;
	}
	
	public String playNextMove() 
	{
		ReversiBitBoard nextState = null;
		String retval = "";
		try 
		{
			this.currentGameState = advGame.initGame(inputState);
			this.currentGameState.setNodeName(ROOT_STRING);
			
			switch(this.agentMode)
			{
			case CompMode:
				return compAlphaBetaMinMax();
			case Greedy:
				this.cutOffDepth = 1;
				nextState = MinMax();
				log.setLength(0);
				break;
			case MinMax:
				log.append("Node,Depth,Value\n");
				nextState = MinMax();
				log.setLength(log.length() - 1);
				break;
			case AlphaBeta:
				log.append("Node,Depth,Value,Alpha,Beta\n");
				nextState = aplhaBetaMinMax();
				log.setLength(log.length() - 1);
				break;
			case LogMode:
				MoveLogger.printLog = true;
				MoveLogger.logState(currentGameState, 8, "Init State");
				nextState = MinMax();
				break;
			default:
				break;
			}
		} 
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if( nextState.getBlackBoard() != 0 || nextState.getWhiteBoard() !=0 )
				retval = advGame.getStateMatrix(nextState);
		else
			retval = advGame.getStateMatrix(currentGameState);
		return retval;
	}
	
	//Min-Max
	protected ReversiBitBoard MinMax()
	{
		int depth = 0;
		ReversiBitBoard nextState = new ReversiBitBoard();
		MaxValues(currentGameState,depth,nextState,0);
		return nextState;
	}
	
	protected int MaxValues(ReversiBitBoard state, int depth,ReversiBitBoard nextState,int pass)
	{
		int maxVal;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			maxVal = advGame.evaluateState(state,agentPlayer);
			logStateWithValue(state.getNodeName(),maxVal,depth);
			return maxVal;
		}
		MoveLogger.logState(state, 8, "Max State");
		maxVal = Integer.MIN_VALUE;
		logStateWithValue(state.getNodeName(), maxVal, depth);
		state.setPlayer(agentPlayer);
		int value;
		
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		if(nextLegalStates == null || nextLegalStates.size() == 0)  //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			value =  MinValues(nextLegalState,depth+1,null,pass);
			if(value > maxVal)
			{
				maxVal = value;
				if(nextState != null)
					nextState.copy(nextLegalState);
			}
			logStateWithValue(state.getNodeName(), maxVal, depth);
		}
		return maxVal;
	}


	private void logStateWithValue(String nodeName, int value, int depth)
	{
		String valueStr = value != Integer.MAX_VALUE ?(value != Integer.MIN_VALUE ? String.valueOf(value) : NEGATIVE_INFINITY_STRING) : POSITIVE_INFINITY_STRING;
		log.append(nodeName).append(",").append(depth).append(",").append(valueStr).append("\n");
	}


	protected int MinValues(ReversiBitBoard state, int depth, ReversiBitBoard nextState,int pass) 
	{
		int minValue;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			minValue = advGame.evaluateState(state,agentPlayer);
			logStateWithValue(state.getNodeName(),minValue,depth);
			//TODO Do something with nextState
			state.setNodeValue(minValue);
			return minValue;
		}
		
		MoveLogger.logState(state, 8, "Min State");
		int value;
		minValue = Integer.MAX_VALUE;
		logStateWithValue(state.getNodeName(), minValue, depth);
		state.setPlayer(opponentPlayer);
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		if(nextLegalStates == null || nextLegalStates.size() == 0) //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			//TODO Take care of null value in nextState attribute
			value =  MaxValues(nextLegalState,depth+1,null,pass);
			if(value < minValue)
			{
				minValue = value;
				if(nextState != null)
					nextState.copy(nextLegalState);
			}
				
			logStateWithValue(state.getNodeName(), minValue, depth);
		}
		
		return minValue;
	}
	
	
	private boolean endOfGameReached(ReversiBitBoard state) 
	{
		return advGame.endOfGameReached(state);
	}


	// Alpha beta
	
	
	protected ReversiBitBoard aplhaBetaMinMax()
	{
		int depth = 0;
		ReversiBitBoard nextState = new ReversiBitBoard();
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		MaxAlphaBeta(currentGameState,depth,nextState,alpha,beta,0);
		return nextState;
	}


	protected int MaxAlphaBeta(ReversiBitBoard state, int depth,ReversiBitBoard nextState, int alpha, int beta,int pass) 
	{
		
		int value;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			value = advGame.evaluateState(state,agentPlayer);
			logStateWithAlphaBetaValue(state.getNodeName(),value,depth,alpha,beta);
			return value;
		}
		MoveLogger.logState(state, 8, "Max State");
		
		int maxValue = Integer.MIN_VALUE;
		logStateWithAlphaBetaValue(state.getNodeName(), maxValue, depth,alpha,beta);
		state.setPlayer(agentPlayer);
		
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		if(nextLegalStates == null || nextLegalStates.size() == 0)  //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			value =  MinAlphaBeta(nextLegalState,depth+1,null,alpha,beta,pass);
			
			if(value > maxValue)
			{
				maxValue = value;
				if(nextState != null)
					nextState.copy(nextLegalState);
			}
			
			if(maxValue >= beta)
			{
				logStateWithAlphaBetaValue(state.getNodeName(), maxValue, depth,alpha,beta);
				return maxValue;
			}
			
			alpha = Math.max(alpha, maxValue);
			logStateWithAlphaBetaValue(state.getNodeName(), maxValue, depth,alpha,beta);
			
		}
		return maxValue;
	}


	protected int MinAlphaBeta(ReversiBitBoard state, int depth, ReversiBitBoard nextState, int alpha, int beta,int pass) 
	{
		int value;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			value = advGame.evaluateState(state,agentPlayer);
			logStateWithAlphaBetaValue(state.getNodeName(), value, depth, alpha, beta);
			return value;
		}
		
		MoveLogger.logState(state, 8, "Min State");
		
		int minValue = Integer.MAX_VALUE;
		logStateWithAlphaBetaValue(state.getNodeName(), minValue, depth, alpha, beta);
		
		state.setPlayer(opponentPlayer);
		
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		
		if(nextLegalStates == null || nextLegalStates.size() == 0)   //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			value =  MaxAlphaBeta(nextLegalState, depth+1, null, alpha, beta,pass);
			if(value < minValue)
			{
				minValue = value;
				if(nextState != null)
					nextState.copy(nextLegalState);
			}
			
			if(minValue <= alpha )
			{
				logStateWithAlphaBetaValue(state.getNodeName(), minValue, depth, alpha, beta);
				return minValue;
			}
			beta = Math.min(minValue, beta);
			logStateWithAlphaBetaValue(state.getNodeName(), minValue, depth, alpha, beta);
			
		}
		
		return minValue;
	}


	protected void logStateWithAlphaBetaValue(String nodeName, int value, int depth, int alpha, int beta) 
	{
		String valueStr = value != Integer.MAX_VALUE ?(value != Integer.MIN_VALUE ? String.valueOf(value) : NEGATIVE_INFINITY_STRING) : POSITIVE_INFINITY_STRING;
		String alphaStr = alpha != Integer.MAX_VALUE ?(alpha != Integer.MIN_VALUE ? String.valueOf(alpha) : NEGATIVE_INFINITY_STRING) : POSITIVE_INFINITY_STRING;
		String betaStr = beta != Integer.MAX_VALUE ?(beta != Integer.MIN_VALUE ? String.valueOf(beta) : NEGATIVE_INFINITY_STRING) : POSITIVE_INFINITY_STRING;
		
		log.append(nodeName).append(",").append(depth).append(",")
		.append(valueStr).append(",").append(alphaStr).append(",").append(betaStr).append("\n");
	}


	protected boolean cutOffReached(int depth) 
	{
		return depth >= this.cutOffDepth;
	}


	/**
	 * @return the currentGameState
	 */
	public ReversiBitBoard getCurrentGameState() {
		return currentGameState;
	}


	/**
	 * @param currentGameState the currentGameState to set
	 */
	public void setCurrentGameState(ReversiBitBoard currentGameState) {
		this.currentGameState = currentGameState;
	}


	/**
	 * @return the cutOffDepth
	 */
	public int getCutOffDepth() {
		return cutOffDepth;
	}


	/**
	 * @param cutOffDepth the cutOffDepth to set
	 */
	public void setCutOffDepth(int cutOffDepth) {
		this.cutOffDepth = cutOffDepth;
	}
	
	protected String compAlphaBetaMinMax() 
	{
		int depth = 0;
		this.cutOffDepth = 4;
		BoardPosition nextMove = new BoardPosition();
		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;
		compMaxAlphaBeta(currentGameState,depth,nextMove,alpha,beta,0);
		return nextMove.toString();
	}


	protected int compMaxAlphaBeta(ReversiBitBoard state, int depth,
			BoardPosition nextState, int alpha, int beta, int pass) {
		int value;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			value = advGame.evaluateStateComp(state,agentPlayer);
			return value;
		}
		
		int maxValue = Integer.MIN_VALUE;
		state.setPlayer(agentPlayer);
		
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		if(nextLegalStates == null || nextLegalStates.size() == 0)  //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			value =  compMinAlphaBeta(nextLegalState,depth+1,null,alpha,beta,pass);
			
			if(value > maxValue)
			{
				maxValue = value;
				if(nextState != null)
				{
					nextState.setCol(nextLegalState.getNodeName().charAt(0));
					nextState.setRow(Character.getNumericValue((nextLegalState.getNodeName().charAt(1))));
				}
			}
			
			if(maxValue >= beta)
			{
				return maxValue;
			}
			alpha = Math.max(alpha, maxValue);
		}
		return maxValue;
	}
	
	protected int compMinAlphaBeta(ReversiBitBoard state, int depth, BoardPosition nextState, int alpha, int beta,int pass) 
	{
		int value;
		if(cutOffReached(depth) || endOfGameReached(state) || pass > 1)
		{
			value = advGame.evaluateState(state,agentPlayer);
			return value;
		}
		
		
		int minValue = Integer.MAX_VALUE;
		
		state.setPlayer(opponentPlayer);
		
		List<ReversiBitBoard> nextLegalStates = advGame.getAllLegalStates(state);
		
		if(nextLegalStates == null || nextLegalStates.size() == 0)   //nextLegalStates should never be null
		{
			//Pass move
			ReversiBitBoard passState = new ReversiBitBoard();
			passState.copy(state);
			passState.setNodeName(PASS_STRING);
			nextLegalStates.add(passState);
			pass++;
		}
		
		for (ReversiBitBoard nextLegalState : nextLegalStates)
		{
			value =  compMaxAlphaBeta(nextLegalState, depth+1, null, alpha, beta,pass);
			if(value < minValue)
			{
				minValue = value;
				if(nextState != null)
				{
					nextState.setCol(nextLegalState.getNodeName().charAt(0));
					nextState.setRow(Character.getNumericValue((nextLegalState.getNodeName().charAt(1))));
				}
			}
			
			if(minValue <= alpha )
			{
				return minValue;
			}
			beta = Math.min(minValue, beta);
		}
		
		return minValue;
	}
}
