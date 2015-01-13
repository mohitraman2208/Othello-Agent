package com.ai.homework2;

import com.ai.agent.AdversarialAgent;
import com.ai.agent.AgentMode;
import com.ai.io.IOHandler;

public class Solution 
{
public static final String inputFileName = "input.txt";
	
	public static final String outputFileName = "output.txt";
	
	public static void main(String[] args)
	{
		try 
		{
			boolean retval = false;
			
			AdversarialAgent agent= new AdversarialAgent();
			IOHandler ioHandle = new IOHandler();
			
			retval = ioHandle.readInputAndInitAgent(agent,inputFileName);
			
			if(retval)
			{
				String nextState = agent.playNextMove();
				if(agent.getAgentMode() == AgentMode.CompMode)
				{
					retval = ioHandle.writeToFileCompMode(nextState, outputFileName);
				}
				else
				{
					retval = ioHandle.writeToFile(nextState,agent.log.toString(),outputFileName);
				}
				
				if(!retval)
				{
					System.out.println("Log: Error Writing O/P file. Please check output.txt");
				}
			}
			else
			{
				System.out.println("Log: Error Reading I/P file. Please check input.txt");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
