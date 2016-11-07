//  Written by Sean Lawlor, 2011
//  Modified by F.P. Ferrie, February 28, 2014
//

package gui;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import universal.Universal;

import gui.defaults.DPMPanel;

@SuppressWarnings("serial")
public class SendSelectPanel extends DPMPanel implements ActionListener{
	private CheckboxGroup sendSelect;
	private Checkbox sendBoth, sendO, sendD;

	public SendSelectPanel(MainWindow mw) {
		super(mw);
		this.layoutPanel();
	}


	private void layoutPanel() {
		GridBagConstraints gridConstraints = new GridBagConstraints();
		this.setFont(new Font("Helvetica", Font.PLAIN, 14));
		this.setLayout(new GridBagLayout());
		
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 0;
		gridConstraints.gridwidth = 1;
		gridConstraints.ipady = 1;
		Label sendTo = new Label("Connect to:", Label.CENTER);
		this.add(sendTo, gridConstraints);
		
		//add checkbox group
		sendSelect = new CheckboxGroup();
		gridConstraints.gridx = 0;
		gridConstraints.gridy = 1;
		gridConstraints.gridwidth = 1;
		Checkbox sendBoth = new Checkbox("Defence and Offence", sendSelect, true);
		this.add(sendBoth, gridConstraints);
		gridConstraints.gridx = 1;
		sendD = new Checkbox("Defence only", sendSelect, false);
		this.add(sendD, gridConstraints);
		gridConstraints.gridx = 2;
		sendO = new Checkbox("Offence only", sendSelect, false);
		this.add(sendO, gridConstraints);
	}
	
	public Universal.TransmitRule getSendSelction(){
		Checkbox selection = this.sendSelect.getSelectedCheckbox();
		if(selection.equals(this.sendBoth))return Universal.TransmitRule.BOTH;
		else if(selection.equals(this.sendO))return Universal.TransmitRule.OFFENCE_ONLY;
		else if(selection.equals(this.sendD))return Universal.TransmitRule.DEFENCE_ONLY;
		else return Universal.TransmitRule.BOTH;
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}
