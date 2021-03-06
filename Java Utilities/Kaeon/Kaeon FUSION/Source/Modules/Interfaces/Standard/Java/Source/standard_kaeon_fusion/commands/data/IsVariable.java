package standard_kaeon_fusion.commands.data;

import java.util.ArrayList;

import fusion.FUSIONUnit;
import one.Element;
import philosophers_stone.PhilosophersStoneUtilities;
import standard_kaeon_fusion.utilities.state.State;

public class IsVariable extends FUSIONUnit {

	public State state;
	
	public IsVariable() {
		tags.add("Standard");
	}
	
	public boolean verify(Element element) {
		return element.content.equalsIgnoreCase("Is Variable");
	}
	
	public Object process(Element element, ArrayList<Object> processed) {
		
		if(state == null)
			state = (State) PhilosophersStoneUtilities.get(this, "State").get(0);
		
		if(processed.size() > 1) {
			
			if(Boolean.parseBoolean("" + processed.get(1)) == false)
				return state.hasAliasAndType("" + processed.get(0), "ALIAS", true, false);
			
			else
				return state.hasAliasAndType("" + processed.get(0), "ALIAS", false, true);
		}
		
		return state.hasAliasAndType("" + processed.get(0), "ALIAS");
	}
}