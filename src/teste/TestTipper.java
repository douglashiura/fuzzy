package teste;

import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class TestTipper {
	public static void main(String[] args) throws Exception {
		// Load from 'FCL' file
		String fileName = "lib/modelo.fcl";
		FIS fis = FIS.load(fileName, true);
		// Error while loading?
		// if (fis == null) {
		// System.err.println("Can't load file: '" + fileName + "'");
		// return;
		// }

		// Show
		// JFuzzyChart.get().chart(fis);

		// Set inputs
		for (int i = 0; i < 11; i++) {
			FunctionBlock fb = fis.getFunctionBlock(null);
			// Set inputs
			fb.setVariable("DR1", i);
			fb.setVariable("DR2", i);
			fb.setVariable("DR3", 10/(i+1));
			fb.setVariable("DR4", 10/(i+1));

			fb.setVariable("GR1", i);
			fb.setVariable("GR2", i);
			fb.setVariable("GR3", 10/(i+1));
			fb.setVariable("GR4", 10/(i+1));

			fb.setVariable("CR1", 10/(i+1));
			fb.setVariable("CR2", i);
			fb.setVariable("CR3", 10/(i+1));
			fb.setVariable("CR4", i);

			// Evaluate
			fb.evaluate();

			Variable tip = fb.getVariable("CRITICIDADE");
			JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
			System.out.println(tip.getDefuzzifier().defuzzify());
		}

		// JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);

		// Get output

		// Show output variable's chart
		// System.out.println(fis.getVariable("CRITICIDADE").getDefuzzifier());

		// Print ruleSet
	}
}