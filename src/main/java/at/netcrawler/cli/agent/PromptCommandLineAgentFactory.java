package at.netcrawler.cli.agent;

import java.io.IOException;

import at.andiwand.library.cli.CommandLineInterface;


public abstract class PromptCommandLineAgentFactory<A extends PromptCommandLineAgent, S extends PromptCommandLineAgentSettings> extends
		GenericCommandLineAgentFactory<A, S> {
	
	protected abstract A createAgentGenericImpl(CommandLineInterface cli,
			S settings) throws IOException;
	
}