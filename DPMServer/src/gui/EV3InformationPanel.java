//  Written by Sean Lawlor, 2011
//  Modified by F.P. Ferrie, February 28, 2014
//
package gui;

import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

import transmission.*;
import universal.Universal;
import gui.defaults.*;

@SuppressWarnings("serial")
public class EV3InformationPanel extends DPMPanel implements ActionListener {
	private ServerEV3 server;
	private Button start, stop, clear;
	private TextField dTeamNumber, dStartCorner;
	private TextField oTeamNumber, oStartCorner;
	private TextField w1, d1, d2;
	private TextField llx, lly, urx, ury;
	private TextField bc;
	private SendSelectPanel sendSelectPanel;

	public EV3InformationPanel(MainWindow mw) {
		// initalize information panel
		super(mw);
		this.layoutPanel(mw);
		server = new ServerEV3(mw);
	}

	private void layoutPanel(MainWindow mw) {
		GridBagConstraints gridConstraints = new GridBagConstraints();
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));
		this.setLayout(new GridBagLayout());

		// send select check box
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.gridwidth = 2;
		gridConstraints.gridheight = 1;
		gridConstraints.ipady = 25;
		sendSelectPanel = new SendSelectPanel(mw);
		this.add(sendSelectPanel, gridConstraints);

		// DEFENCE (GREEN) PLAYER TEAM NUMBER
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 2;
		gridConstraints.gridwidth = 1;
		gridConstraints.ipady = 5;
		Label greenTeamNumberLabel = new Label("Defence Player Team Number: ", Label.RIGHT);
		this.add(greenTeamNumberLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 2;
		gridConstraints.gridwidth = 2;
		dTeamNumber = new TextField(11);
		//new DPMToolTip("Enter the defence team's number here", gTeamNumber);
		this.add(dTeamNumber, gridConstraints);
		
		// DEFENCE PLAYER CORNER
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 3;
		gridConstraints.gridwidth = 1;
		Label greenStartCornerLabel = new Label("Defence Player Start Corner: ", Label.RIGHT);
		this.add(greenStartCornerLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 3;
		gridConstraints.gridwidth = 2;
		dStartCorner = new TextField(11);
		//new DPMToolTip("Enter the EV3's starting corner (1-4)", gStartCorner);
		this.add(dStartCorner, gridConstraints);

		
		// OFFENCE (RED) PLAYER TEAM NUMBER
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 4;
		gridConstraints.gridwidth = 1;
		Label redTeamNumberLabel = new Label("Offence Player Team Number: ", Label.RIGHT);
		this.add(redTeamNumberLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 4;
		gridConstraints.gridwidth = 2;
		oTeamNumber = new TextField(11);
		//new DPMToolTip("Enter the EV3's Bluetooth name here", rTeamNumber);
		this.add(oTeamNumber, gridConstraints);
		
		
		// OFFENCE PLAYER CORNER
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 5;
		gridConstraints.gridwidth = 1;
		Label redStartCornerLabel = new Label("Offence Player Start Corner: ", Label.RIGHT);
		this.add(redStartCornerLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 5;
		gridConstraints.gridwidth = 2;
		oStartCorner = new TextField(11);
		//new DPMToolTip("Enter the EV3's starting corner (1-4)", rStartCorner);
		this.add(oStartCorner, gridConstraints);

		//GOAL WIDTH
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 6;
		gridConstraints.gridwidth = 1;
		Label goalWidthLabel = new Label("Goal width [1,5] element of Z: ", Label.RIGHT);
		this.add(goalWidthLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 6;
		gridConstraints.gridwidth = 1;
		w1 = new TextField(4);
		//new DPMToolTip("Enter X location of the Green Zone bottom-left corner here", greenZone1X);
		this.add(w1, gridConstraints);
		
		//D1
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 7;
		gridConstraints.gridwidth = 1;
		Label defenceLineLabel = new Label("Defence Line: ", Label.RIGHT);
		this.add(defenceLineLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 7;
		gridConstraints.gridwidth = 1;
		d1 = new TextField(4);
		//new DPMToolTip("Enter X location of the Green Zone top-right corner here", greenZone2X);
		this.add(d1, gridConstraints);

		//D2
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 8;
		gridConstraints.gridwidth = 1;
		Label offenceLineLabel = new Label("Offence Line: ", Label.RIGHT);
		this.add(offenceLineLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 8;
		gridConstraints.gridwidth = 1;
		d2 = new TextField(4);
		//new DPMToolTip("Enter X location of the Red Zone bottom-left corner here", redZone1X);
		this.add(d2, gridConstraints);
		
		// BALL LOWER LEFT CORNER (X,Y)
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 9;
		gridConstraints.gridwidth = 1;
		Label ballLowerLeftLabel = new Label("Ball area lower left corner (x, y): ", Label.RIGHT);
		this.add(ballLowerLeftLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 9;
		gridConstraints.gridwidth = 1;
		llx = new TextField(4);
		//new DPMToolTip("Enter X location of the Red Zone top-right corner here", redZone2X);
		this.add(llx, gridConstraints);
		
		gridConstraints.gridx = 2;
		gridConstraints.gridy = 9;
		gridConstraints.gridwidth = 1;
		lly = new TextField(4);
		//new DPMToolTip("Enter Y location of the Red Zone top-right corner here", redZone2Y);
		this.add(lly, gridConstraints);
		
		// BALL UPPER RIGHT (X,Y)
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 10;
		gridConstraints.gridwidth = 1;
		Label ballUpperRightLabel = new Label("Ball area upper right corner (x, y): ", Label.RIGHT);
		this.add(ballUpperRightLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 10;
		gridConstraints.gridwidth = 1;
		urx = new TextField(4);
		//new DPMToolTip("Enter X location of the Green Drop Zone bottom-left corner here", urx);
		this.add(urx, gridConstraints);
		
		gridConstraints.gridx = 2;
		gridConstraints.gridy = 10;
		gridConstraints.gridwidth = 1;
		ury = new TextField(4);
		//new DPMToolTip("Enter Y location of the Green Drop Zone bottom-left corner here", ury);
		this.add(ury, gridConstraints);

		//BC (Ball Coulour)
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 12;
		gridConstraints.gridwidth = 1;
		Label ballColourLabel = new Label("Ball Colour [0-2]: ", Label.RIGHT);
		this.add(ballColourLabel, gridConstraints);

		gridConstraints.gridx = 1;
		gridConstraints.gridy = 12;
		gridConstraints.gridwidth = 1;
		bc = new TextField(4);
		//new DPMToolTip("Enter value of Green Flag Type here:", greenFlag);
		this.add(bc, gridConstraints);

		// START, STOP and CLEAR BUTTONS		
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 14;
		gridConstraints.gridwidth = 1;
		this.start = new Button("Start");
		this.start.addActionListener(this);
		new DPMToolTip("Start the program", this.start);
		this.add(start, gridConstraints);

		gridConstraints.gridx = 1;
		this.stop = new Button("Stop");
		this.stop.addActionListener(this);
		new DPMToolTip("Stop the program", this.stop);
		this.add(stop, gridConstraints);

		gridConstraints.gridx = 2;
		this.clear = new Button("Clear");
		this.clear.addActionListener(this);
		new DPMToolTip("Clear all entered values", this.clear);
		this.add(clear, gridConstraints);

	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Button bt = (Button) e.getSource();
		try {
			if (bt.equals(this.start)) {
				Universal.TransmitRule sendTo = this.sendSelectPanel.getSendSelction();
				Universal.TRANSMIT_RULE = sendTo;

				int oStartCornerN = Integer.parseInt(this.oStartCorner.getText().trim());

				if (sendTo == Universal.TransmitRule.BOTH || sendTo == Universal.TransmitRule.OFFENCE_ONLY) {
					if (oStartCornerN > 4 || oStartCornerN < 1) {
						new DPMPopupNotification("Red player starting corner of " + oStartCornerN + " is out of the range 1-4", this.mw);
						return;
					}
				}

				int dStartCornerN = Integer.parseInt(this.dStartCorner.getText().trim());

				if (sendTo == Universal.TransmitRule.BOTH || sendTo == Universal.TransmitRule.DEFENCE_ONLY) {
					if (dStartCornerN > 4 || dStartCornerN < 1) {
						new DPMPopupNotification("Green player starting corner of " + dStartCornerN + " is out of the range 1-4", this.mw);
						return;
					}
				}

				if (sendTo == Universal.TransmitRule.BOTH) {
					if (oStartCornerN == dStartCornerN) {
						new DPMPopupNotification("Green and Red starting positions can't be the same", this.mw);
						return;
					}
				}
				
				//	Note to Maintainers											//
				//																//
				//	If you're using this as a template for other parameter sets	//
				//	you need to do 2 things:									//
				//	1.  Modify the layout of the screen to display and input	//
				//      new parameters (above).									//
				//	2.  Convert text strings to variables (below).				//
				//	3.  Modify the server transmit method in the server class	//
				//		but instantiated below.									//
				//																//
				//	F.P. Ferrie, February 28, 2014.								//
				
				//  Interpretation of test strings to ints  //

				int oTeamNumberN = Integer.parseInt(this.oTeamNumber.getText().trim());
				int dTeamNumberN = Integer.parseInt(this.dTeamNumber.getText().trim());
				int valuew1 = Integer.parseInt(this.w1.getText().trim()), 
						valued1 = Integer.parseInt(this.d1.getText().trim()),  
						valued2 = Integer.parseInt(this.d2.getText().trim()), 
						valuellx = Integer.parseInt(this.llx.getText().trim()), 
						valuelly = Integer.parseInt(this.lly.getText().trim()),
						valueurx = Integer.parseInt(this.urx.getText().trim()),
						valueury = Integer.parseInt(this.ury.getText().trim()),
						valuebc = Integer.parseInt(this.bc.getText().trim());
						
				//TODO Re-implement logical value checks
				/*if (gz2x <= gz1x || gz2y <= gz1y) {
					new DPMPopupNotification("Green Zone top-right corner coordinates should be " +
							"strictly larger than bottom-left corner coordinates", this.mw);
					return;
				}
				if (rz2x <= rz1x || rz2y <= rz1y) {
					new DPMPopupNotification("Red Zone top-right corner coordinates should be " +
							"strictly larger than bottom-left corner coordinates", this.mw);
					return;	
				}*/

				HashMap<String,Integer> StartData = new HashMap<String,Integer>();
				
				int[]teamNumbers = new int[] {oTeamNumberN, dTeamNumberN};
				char[] roles = new char[] { 'R', 'G' };
				//TODO Find nicer handling of start corners
				StartData.put("DSC", Integer.parseInt(dStartCorner.getText().trim()));
				StartData.put("OSC", Integer.parseInt(oStartCorner.getText().trim()));
				StartData.put("OTN", oTeamNumberN);
				StartData.put("DTN", dTeamNumberN);
				StartData.put("ur-x", valueurx);
				StartData.put("ur-y", valueury);
				StartData.put("w1", valuew1);
				StartData.put("d1", valued1);
				StartData.put("d2", valued2);
				StartData.put("ll-x", valuellx);
				StartData.put("ll-y", valuelly);
				StartData.put("BC", valuebc);

				//System.out.println(StartData.toString());
				
				// try wifi transmission
				int success = 0;
				success = server.transmit(teamNumbers, roles, StartData);
				if (success == 0) {
					this.mw.startTimer();
				} //else
					// "Transmission failed" popup is commented out - clunky and unnecessary feature
					//new DPMPopupNotification("Some wifi error occured trying to transmit data, please retry", this.mw);
			} else if (bt.equals(this.stop)) {
				// stop button pressed
				this.mw.pauseTimer();
			} else if (bt.equals(this.clear)) {
				// clear button pressed, clear fields, reset timer, and clear wifi panel
				this.clearFields();
				this.mw.clearTimer();
				this.mw.clearWifiPanel();
			} else {
				System.out.println("Non-handled event...");
			}
		} catch (NumberFormatException ex) {
			// string where number should be
			new DPMPopupNotification(
					"One of the numerical values was not a number", this.mw);
			return;
		}

	}
	//TODO decide whether this ought to be. Probably does.
	@Deprecated
	private void clearFields() {
		this.oTeamNumber.setText("");
		this.oStartCorner.setText("");
		this.w1.setText("");
		this.d1.setText("");
		this.d2.setText("");
		this.llx.setText("");
		this.lly.setText("");
		this.dTeamNumber.setText("");
		this.dStartCorner.setText("");
		this.urx.setText("");
		this.ury.setText("");
		this.bc.setText("");
	}
}
