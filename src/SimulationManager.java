
import javax.swing.JTextArea;

public class SimulationManager implements Runnable {

	private static final int BUS_SPAWN_TIME = 500; //0.5 sec
	private static final int BUS_SPAWN_MAX_DELAY = 4500; //4.5 sec
	
	
	private JTextArea logTextArea;
	private DrawPanel drawPanel;
	
	private Bridge bridge;
	
	private float spawnDelayFactor;
	
	
	public SimulationManager(JTextArea logTextArea, DrawPanel drawPanel, int spawnDelayFactor) {
		this.logTextArea = logTextArea;
		this.drawPanel = drawPanel;
		setSpawnDelayFactor(spawnDelayFactor);
		this.bridge = new Bridge();
		this.drawPanel.setBridge(this.bridge);
	}

	public synchronized float getSpawnDelayFactor() {
		return spawnDelayFactor;
	}

	public synchronized void setSpawnDelayFactor(int spawnDelayFactor) {
		if(spawnDelayFactor == 0) {
			this.spawnDelayFactor = 1;
		}else {
			this.spawnDelayFactor = (float) 1/spawnDelayFactor;
		}
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
				Thread.sleep((int) (BUS_SPAWN_TIME + BUS_SPAWN_MAX_DELAY * spawnDelayFactor));
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}
		
	}

}
