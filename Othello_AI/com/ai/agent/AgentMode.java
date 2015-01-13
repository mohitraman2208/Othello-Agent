package com.ai.agent;

public enum AgentMode 
{
		Greedy(1),MinMax(2),AlphaBeta(3),CompMode(4),LogMode(5);

		private int value;

		AgentMode(int value)
		{
			this.setValue(value);
		}

		/**
		 * @return the value
		 */
		public int getValue()
		{
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(int value)
		{
			this.value = value;
		}
}
