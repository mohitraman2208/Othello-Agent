package com.ai.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import com.ai.game.reversi.ReversiBitBoard;
import com.ai.homework2.Player;

public class MoveLogger
{
	public static boolean printLog = false;
	
	public static BufferedWriter writer = null;
	public static boolean writeToFile(String nextState, String log,
			String outputfilename) 
	{
		boolean retval = false;
		try
		{
			File myFile = new File(outputfilename);
			if(!myFile.exists())
			{
				myFile.createNewFile();
			}

			if(writer == null)
				writer = new BufferedWriter(new FileWriter(myFile));
			
			writer.write(nextState);
			
			//writer.close();
			
			retval = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return retval;
	}
	
	public static void logBitBoard(Long board, int size, String desc)
	{	
		if(printLog)
		{
			StringBuffer boardMatrix= new StringBuffer();
			boardMatrix.append(desc).append(":").append("\n");
			long bitPostn = 1L;
			for( int i = 1 ; i <= size ; i++)
			{

				for(int j = 1; j <= size ; j++)
				{
					if( ( bitPostn << ( (size - i)*size + (size -j) ) & board ) != 0)
					{
						boardMatrix.append("X");
					}
					else
					{
						boardMatrix.append("-");
					}
				}
				boardMatrix.append("\n");
			}
			//System.out.println(boardMatrix.toString());
			writeToFile(boardMatrix.toString(),"","log.txt");
		}
	}
	
	public static void logState(ReversiBitBoard state, int size,String desc)
	{
		if(printLog)
		{
			StringBuffer stateMatrix = new StringBuffer();
			stateMatrix.append(desc + ":");
			
			long bitPostn = 1L;
			for( int i = 0 ; i < size ; i++)
			{
				
				for(int j = 0; j < size ; j++)
				{
					int postn = (size - i -1 )*size + (size -j-1);
					if( ((bitPostn <<postn) & state.getWhiteBoard()) != 0)
					{
						stateMatrix.append(Player.PLAYER_X);
					}
					else if(((bitPostn <<postn) & state.getBlackBoard()) != 0)
					{
						stateMatrix.append(Player.PLAYER_O);
					}
					else
					{
						stateMatrix.append("-");
					}
				}
				stateMatrix.append("\n");
			}
			
			//System.out.println(stateMatrix.toString());
			writeToFile(stateMatrix.toString(),"","log.txt");
		}
		
	}
}
