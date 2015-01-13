package com.ai.game.reversi;

import com.ai.homework2.Player;

/*
 * State Representation for Reversi Game
 * Player positions are stored as binary longs for fast access and efficient storage
 * Player 'X': whiteBoard
 * Player 'Y': blackBoard  
 * Eg- Othello start state: 
 * whiteBoard :
 * blackBoard :  
 */
public class ReversiBitBoard
{
	
	private long whiteBoard;
	
	private long blackBoard;

	private String nodeName;
	
	private int nodeValue;
	
	private char player;
	
	public ReversiBitBoard(long whiteBoard, long blackBoard) 
	{
		
		this.setWhiteBoard(whiteBoard);
		this.setBlackBoard(blackBoard);
	}

	public ReversiBitBoard() {
		super();
	}

	public long getAgentBoard() {
		if(player==Player.PLAYER_X)
			return whiteBoard;
		else
			return blackBoard;
	}


	public long getOpponentBoard() {
		if(player != Player.PLAYER_X)
			return whiteBoard;
		else
			return blackBoard;
	}
	
	public void setAgentBoard(long agentBoard) {
		if(player==Player.PLAYER_X)
			whiteBoard = agentBoard;
		else
			blackBoard = agentBoard;
	}
	
	public void setOpponentBoard(long opponentBoard) {
		if(player !=Player.PLAYER_X)
			whiteBoard = opponentBoard;
		else
			blackBoard = opponentBoard;
	}
    /*
	 * @return the nodeName
	 */
	public String getNodeName() {
		return nodeName;
	}

	/**
	 * @param nodeName the nodeName to set
	 */
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	/**
	 * @return the nodeValue
	 */
	public int getNodeValue() {
		return nodeValue;
	}

	/**
	 * @param nodeValue the nodeValue to set
	 */
	public void setNodeValue(int nodeValue) {
		this.nodeValue = nodeValue;
	}

	/**
	 * @return the player
	 */
	public char getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(char player) {
		this.player = player;
	}

	/**
	 * @return the whiteBoard
	 */
	public long getWhiteBoard() {
		return whiteBoard;
	}

	/**
	 * @param whiteBoard the whiteBoard to set
	 */
	public void setWhiteBoard(long whiteBoard) {
		this.whiteBoard = whiteBoard;
	}

	/**
	 * @return the blackBoard
	 */
	public long getBlackBoard() {
		return blackBoard;
	}

	/**
	 * @param blackBoard the blackBoard to set
	 */
	public void setBlackBoard(long blackBoard) {
		this.blackBoard = blackBoard;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj!=null && obj.getClass() == getClass() && this.whiteBoard == ((ReversiBitBoard)obj).whiteBoard
				&& this.blackBoard == ((ReversiBitBoard)obj).blackBoard;
	}
	
	public void copy(ReversiBitBoard obj)
	{
		this.whiteBoard = obj.whiteBoard;
		
		this.blackBoard = obj.blackBoard;

		this.nodeName = obj.nodeName;
		
		this.nodeValue = obj.nodeValue;
		
		this.player = obj.player;

	}
}
