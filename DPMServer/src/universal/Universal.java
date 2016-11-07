package universal;

//TODO Figure out what's happening. Could most of this get removed?
public class Universal {
	
	public static TransmitRule TRANSMIT_RULE = TransmitRule.BOTH;
	
	public static ConnectionState CONNECTION_OPTION = ConnectionState.NXT_COMM_SUPER_NEW;
	
	public static final String CONNECTION_STATE_PRE = "connection=";
	public static final String TRANSMIT_RULE_PRE = "transmitto=";
	
	public static final String MESSAGE_TYPE_PRE = "messagetype=";
	public static MessageType MESSAGE_TYPE = MessageType.F11;
	
	public enum MessageType {
		W11("w11"),
		F11("f11");
		
		public String str;
		
		private MessageType(String s) {
			this.str = s;
		}
		
		public static String strignifyArguments() {
			String s = "";
			for (MessageType t : values())
				s += t.str + " ";
			return s;
		}
		
		public static MessageType lookup(String s) {
			for (MessageType type : values())
				if (type.str.equals(s))
					return type;
			return F11;
		}
	}
	
	public enum ConnectionState {
		OLD("old", "Use the old class with NXTComm"),
		NXT_COMM("nxtcomm", "Use NXTComm class"),
		NXT_COMM_NEW("nxtcommnew", "Use the new type with NXTComm class"),
		NXT_COMM_SUPER_NEW("nxtcommsupernew", "Use the NEW new NXTComm class"),
		NXT_CONNECTOR("nxtconnector", "Use NXTConnector class");
		public String str, help;
		private ConnectionState(String s, String h) {
			str = s;
			help = h;
		}
		
		public static String stringifyArguments() {
			String s = "";
			for (ConnectionState state : values())
				s += state.str + " ";
			return s;
		}
		
		public static ConnectionState lookupState(String s) {
			for (ConnectionState state : ConnectionState.values())
				if (state.str.equals(s))
					return state;
			return ConnectionState.NXT_COMM;
		}
	}
	
	public enum TransmitRule {
		OFFENCE_ONLY("red", "Transmit to red player only"),
		DEFENCE_ONLY("green", "Transmit to green player only"),
		BOTH("both", "Transmit to both red and green players");
		
		public String str, help;
		private TransmitRule(String s, String h) {
			str = s;
			help = h;
		}
		
		public static String stringifyArguments() {
			String s = "";
			for (TransmitRule mode : TransmitRule.values())
				s += mode.str + " ";
			return s;
		}
		
		public static TransmitRule lookup(String s) {
			for (TransmitRule mode : TransmitRule.values())
				if (mode.str.equals(s))
					return mode;
			return TransmitRule.BOTH;
		}
	}
	
}
