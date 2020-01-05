import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/*
 * PROGRAM: "Narrow Bridge Simulation"
 *
 * PLIKI: 	NarrowBridgeApp.java
 * 			Bridge.java
 * 			Bus.java
 * 			DrawPanel.java
 * 			LogPanel.java
 * 			SimulationManager.java
 * 			WorldMap.java			
 * 
 * AUTOR: 	Micha³ Tkacz 248869
 * 		 	Pi¹tek TN 11:15
 * 
 * DATA:    6 stycznia 2020r
 * 
 */

public class LogPanel extends JScrollPane {
	private static final long serialVersionUID = -1665608884566053419L;

	private static JTextArea logTextArea = new JTextArea();
	
	public LogPanel() {
		super(logTextArea);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setEnabled(false);
		setAutoscrolls(true);
	}
	
	public synchronized void addLog(String log) {
		logTextArea.setText(log + "\n" + logTextArea.getText());
	}
	
}
