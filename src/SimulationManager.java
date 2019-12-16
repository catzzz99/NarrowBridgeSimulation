import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JTextArea;

public class SimulationManager implements Runnable {

	private JTextArea logTextArea;
	private DrawPanel drawPanel;
	
	private Bridge bridge;
	
	
	public SimulationManager(JTextArea logTextArea, DrawPanel drawPanel) {
		this.logTextArea = logTextArea;
		this.drawPanel = drawPanel;
		this.bridge = new Bridge();
		this.drawPanel.setBridge(this.bridge);
	}

	@Override
	public void run() {
	
		new Thread(drawPanel, "DRAW PANEL").start();
		
		int busID = 1;
		while(true) {
			if(busID % 25 == 0) {
				logTextArea.setText(logTextArea.getText() + busID + "\n");
			}else {
				logTextArea.setText(logTextArea.getText() + " " + busID + ", ");
			}
			busID++;
			
			Bus bus = new Bus(bridge, drawPanel.getSize());
			new Thread(bus, "BUS" + busID).start();
			
			try {
				Thread.sleep(400 + ThreadLocalRandom.current().nextInt(1600));
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}
		
	}

}
