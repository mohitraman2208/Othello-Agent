package com.ai.game.reversi;

public final class  BoardPosition
{
	private int row;
	
	private char col;
	
	private int value;

	public String toString()
	{
		return String.valueOf(col) + String.valueOf(row);
	}
	/**
	 * @return the row
	 */
	public int getRow() {
		return row;
	}

	public BoardPosition(int row, char col, int value) {
		super();
		this.row = row;
		this.col = col;
		this.value = value;
	}
	public BoardPosition() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param row the row to set
	 */
	public void setRow(int row) {
		this.row = row;
	}

	/**
	 * @return the col
	 */
	public char getCol() {
		return col;
	}

	/**
	 * @param col the col to set
	 */
	public void setCol(char col) {
		this.col = col;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(int value) {
		this.value = value;
	}
}
