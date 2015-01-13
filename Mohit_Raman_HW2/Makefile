agent: Solution.java IOHandler.java AdversarialAgent.java AgentMode.java BoardPosition.java MoveFinder.java ReversiBitBoard.java ReversiGame.java Player.java MoveLogger.java

Solution.java: IOHandler.java AdversarialAgent.java AgentMode.java
			javac -classpath . com/ai/homework2/Solution.java

IOHandler.java: AdversarialAgent.java AgentMode.java
			javac -classpath . com/ai/io/IOHandler.java

AdversarialAgent.java: ReversiBitBoard.java ReversiGame.java Player.java MoveLogger.java
			javac -classpath . com/ai/agent/AdversarialAgent.java
AgentMode.java: 
			javac -classpath . com/ai/agent/AgentMode.java
BoardPosition.java:
			javac -classpath . com/ai/game/reversi/BoardPosition.java
MoveFinder.java:
			javac -classpath . com/ai/game/reversi/MoveFinder.java
ReversiBitBoard.java: Player.java
			javac -classpath . com/ai/game/reversi/ReversiBitBoard.java
ReversiGame.java: Player.java MoveLogger.java MoveFinder.java ReversiBitBoard.java
			javac -classpath . com/ai/game/reversi/ReversiGame.java
Player.java:
			javac -classpath . com/ai/homework2/Player.java
MoveLogger.java: Player.java ReversiBitBoard.java
			javac -classpath . com/ai/log/MoveLogger.java

run: Solution.class

Solution.class: 
			java -classpath . com.ai.homework2.Solution