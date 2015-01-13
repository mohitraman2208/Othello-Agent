package com.ai.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.ai.agent.AdversarialAgent;
import com.ai.agent.AgentMode;

public class IOHandler {

	public boolean readInputAndInitAgent(AdversarialAgent agent, String inputFileName) 
	{
		boolean retval = false;

		File inputFile = new File(inputFileName);

		if(!inputFile.exists())
		{
			System.out.print("******** Error Opening File!***********\n*************File Does not exist!************\n");
		}
		else
		{
			BufferedReader readObj = null; 
			try 
			{
				readObj = new BufferedReader(new FileReader(inputFile));;
				String line = new String("");
				int counter = 0;
				while(++counter < 4 && (line = readObj.readLine())!=null)
				{
					switch (counter)
					{
					case 1:
						agent.setAgentMode(Integer.parseInt(line.trim()));
						break;
					case 2:
						agent.setPlayer(line.trim().charAt(0));
						break;
					case 3:
						if(agent.getAgentMode() == AgentMode.CompMode)
						{
							agent.cpuTime = Long.parseLong(line.trim());
						}
						else
						{
							agent.setCutOffDepth(Integer.parseInt(line.trim()));
						}
						break;
					default:
						break;
					}
				}

				readStateAndInitAgent(readObj,agent);
				readObj.close();

				retval = true;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return retval;
	}

	private void readStateAndInitAgent(BufferedReader readObj,AdversarialAgent agent)
	{
		String line;
		try
		{
			while((line = readObj.readLine()) != null)
			{
				agent.getInputState().append(line);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}


	public boolean writeToFile(String nextState, String log,
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

			BufferedWriter writer = new BufferedWriter(new FileWriter(myFile));
			writer.write(nextState);
			writer.write(log);
			
			writer.close();
			
			retval = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return retval;
	}

	public boolean writeToFileCompMode(String nextState, String outputfilename)
	{
		
		boolean retval = false;
		try
		{
			File myFile = new File(outputfilename);
			if(!myFile.exists())
			{
				myFile.createNewFile();
			}

			BufferedWriter writer = new BufferedWriter(new FileWriter(myFile));
			writer.write(nextState);
			
			writer.close();
			
			retval = true;
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		return retval;
	}

}