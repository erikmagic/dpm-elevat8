package guigeneration;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.xpath.*;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gui.MainWindow;
import gui.defaults.*;
import transmission.ServerEV3;

public class EV3GeneratedPanel extends DPMPanel implements ActionListener {

	private ServerEV3 server;
	private Document contents;
	public DPMEntryItemList textBoxes, teamBoxes;
	private Button start, stop, clear, fill;
	
	private FileDialog fileSelect;
	
	public EV3GeneratedPanel(MainWindow mw, Document fields){
		super(mw);
		contents = fields;
		Node inputs = contents.getElementsByTagName("inputs").item(0);
		textBoxes = new DPMEntryItemList();
		teamBoxes = new DPMEntryItemList();
		layoutPanel(mw, inputs);
		server = new ServerEV3(mw);
	}
	
	private void layoutPanel(MainWindow mw, Node inputs) {
		GridBagConstraints gridConstraints = new GridBagConstraints();
		setFont(new Font("Helvetica", Font.PLAIN, 14));
		setLayout(new GridBagLayout());
		
		Node entry, tempNode;
		NodeList entries = inputs.getChildNodes();
		int entriesLength = entries.getLength();
		String labelString, key, xKey, yKey;
		Label textLabel;
		DPMEntryItem dpmEntry;
		TextField textInput, xInput, yInput;
		//TextField textBox;
		
		for (int i = 0; i < entriesLength ; i++){
			gridConstraints.gridx = 0;
			gridConstraints.gridy = i;
			entry = entries.item(i);
			switch (entry.getNodeName()){
				case "#text":
					break;
				case "team":
					tempNode = entry.getFirstChild();
					tempNode = XMLReader.getNextNonTextNode(tempNode, true);
					labelString = tempNode.getFirstChild().getNodeValue();
					textLabel = new Label(labelString, Label.RIGHT);
					gridConstraints.gridwidth = 1;
					this.add(textLabel, gridConstraints);
					textInput = new TextField(11);
					gridConstraints.gridx = 1;
					gridConstraints.gridwidth = 2;
					this.add(textInput, gridConstraints);
					key = entry.getAttributes().getNamedItem("key").getNodeValue();
					dpmEntry = new DPMEntryItem(key, textInput);
					textBoxes.add(dpmEntry);
					teamBoxes.add(dpmEntry);
					break;
				case "text":
					tempNode = entry.getFirstChild();
					tempNode = XMLReader.getNextNonTextNode(tempNode, true);
					labelString = tempNode.getFirstChild().getNodeValue();
					textLabel = new Label(labelString, Label.RIGHT);
					gridConstraints.gridwidth = 1;
					this.add(textLabel, gridConstraints);
					textInput = new TextField(11);
					gridConstraints.gridx = 1;
					gridConstraints.gridwidth = 2;
					this.add(textInput, gridConstraints);
					key = entry.getAttributes().getNamedItem("key").getNodeValue();
					dpmEntry = new DPMEntryItem(key, textInput);
					textBoxes.add(dpmEntry);
					break;
				case "coordinate":
					tempNode = entry.getFirstChild();
					tempNode = XMLReader.getNextNonTextNode(tempNode, true);
					labelString = tempNode.getFirstChild().getNodeValue();
					textLabel = new Label(labelString, Label.RIGHT);
					gridConstraints.gridwidth = 1;
					this.add(textLabel, gridConstraints);
					xInput = new TextField(4);
					gridConstraints.gridx = 1;
					this.add(xInput, gridConstraints);
					xKey = entry.getAttributes().getNamedItem("keyx").getNodeValue();
					textBoxes.add(new DPMEntryItem(xKey, xInput));
					yInput = new TextField(4);
					gridConstraints.gridx = 2;
					this.add(yInput, gridConstraints);
					yKey = entry.getAttributes().getNamedItem("keyy").getNodeValue();
					textBoxes.add(new DPMEntryItem(yKey, yInput));
					break;
				default:
					break;
			}
		}
		
		// START, STOP and CLEAR BUTTONS
		gridConstraints.gridx = 0;
		gridConstraints.gridy = entriesLength;
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
		
		gridConstraints.gridx = 1;
		gridConstraints.gridy = entriesLength + 1;
		gridConstraints.gridwidth = 1;
		fill = new Button("Fill");
		fill.addActionListener(this);
		add(fill, gridConstraints);
	}
	
	

	@Override
	public void actionPerformed(ActionEvent e) {
		Button bt = (Button) e.getSource();
		HashMap<String,Integer> startData = new HashMap<String,Integer>();
		
		Integer entryValue;
		int[]teamNumbers;
		int numTeams = 0;
		ArrayList<Integer> targetTeams = new ArrayList<Integer>();
		targetTeams.ensureCapacity(teamBoxes.size());
		
		try {
			if (bt == start){
				for (DPMEntryItem field : textBoxes) {
					try {
						entryValue = Integer.valueOf(field.entry.getText().trim());
					} catch (NumberFormatException nan) {
						entryValue = 0;
					}
					startData.put(field.key, entryValue);
				}
				
				System.out.println(startData.toString());
				
				for (DPMEntryItem team : teamBoxes) {
					try {
						entryValue = Integer.valueOf(team.entry.getText().trim());
					} catch (NumberFormatException nan) {
						entryValue = 0;
					}
					if (entryValue != 0) {
						numTeams++;
						targetTeams.add(entryValue);
					}
				}
				teamNumbers = new int[numTeams];
				for (Integer target : targetTeams){
					numTeams--;
					teamNumbers[numTeams] = target.intValue();
				}
				if (teamNumbers.length != 0){
					server.transmit(teamNumbers, startData);
					this.mw.startTimer();
				}
			} else if (bt == clear) {
				clearFields();
				mw.clearTimer();
				mw.clearWifiPanel();
			} else if (bt == stop) {
				// stop button pressed
				mw.pauseTimer();
			} else if (bt == fill) {
				fillFields();
			}
		} finally {
			
		}
	}

	private void clearFields() {
		for (DPMEntryItem field : textBoxes) {
			field.entry.setText("");
		}
	}
	
	//TODO be improved when searching DPMEntryItemList is improved.
	private void fillFields() {
		fileSelect = new FileDialog(mw, "File to fill from");
		fileSelect.setVisible(true);
		String file, directory, path;
		file = fileSelect.getFile();
		directory = fileSelect.getDirectory();
		if((file != null) && (directory!= null)){
			path = directory + file;
		} else {
			return;
		}
		System.out.println(path);
		Document content = XMLReader.getContentsDoc(path);
		if (content != null) {
			clearFields();
			HashMap<String,String> values = new HashMap<String,String>();
			Node value = content.getFirstChild().getFirstChild();
			String entry;
			value = XMLReader.getNextNonTextNode(value, false);
			while (value != null){
				values.put(value.getAttributes().getNamedItem("key").getNodeValue(), value.getFirstChild().getNodeValue());
				value = value.getNextSibling();
				value = XMLReader.getNextNonTextNode(value, false);
			}
			for (DPMEntryItem field : textBoxes){
				entry = values.get(field.key);
				if (entry != null){
					field.entry.setText(entry);
				}
			}
		}
	}

}
