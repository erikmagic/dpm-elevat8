/*
 * @author Sean Lawlor
 * @date November 3, 2011
 * @class ECSE 211 - Design Principle and Methods
 */
import gui.MainWindow;
import universal.Universal;

public class F15FinalProjectSandbox {
	// simply start the main window
	public static void main(String[] args) {
		// change arguments as requested, if help put ANYWHERE don't start program
		if (processArgs(args)) {
			System.out.println("type help as an argument if you want more information about command line arguments to this program");
			MainWindow mw = new MainWindow();
			while (mw != null)
				try { Thread.sleep(10); } catch (InterruptedException e) {}
		}

	}
	
	public static boolean processArgs(String[] args) {
		try {
			for (String s : args)
				if (s.startsWith(Universal.CONNECTION_STATE_PRE))
					Universal.CONNECTION_OPTION = Universal.ConnectionState.lookupState(s.substring(Universal.CONNECTION_STATE_PRE.length()));
				else if (s.startsWith(Universal.TRANSMIT_RULE_PRE))
					Universal.TRANSMIT_RULE = Universal.TransmitRule.lookup(s.substring(Universal.TRANSMIT_RULE_PRE.length()));
				else if (s.equals("help") || s.equals("-help") || s.equals("--help") || s.equals("\\?") || s.equals("?")) {
					printHelpMenu();
					return false;
				}
			System.out.println("Connection Type: " + Universal.CONNECTION_OPTION.help);
			System.out.println("Transmit Rule: " + Universal.TRANSMIT_RULE.help);
			return true;
		} catch (ArrayIndexOutOfBoundsException e) {
			return true;
		}
	}

	public static void printHelpMenu() {
		System.out.println("Valid program arguments are: ");
		System.out.println("help || -help || --help || \\? || ?");
		System.out.println(Universal.CONNECTION_STATE_PRE + "<connection_option>");
		System.out.println("\t" + Universal.ConnectionState.stringifyArguments());
		System.out.println(Universal.TRANSMIT_RULE_PRE + "<start mode>");
		System.out.println("\t" + Universal.TransmitRule.stringifyArguments());
	}
}
