
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JTextField;


public class SimulationManager implements Runnable {
	
	private static int BUS_ID = 1;
	
	private LogPanel logPanel;
	private DrawPanel drawPanel;
	
	private ArrayList<Bus> buses;
	private Bridge bridge;
	private WorldMap worldMap;
	
	private JTextField busesWaitingTextField;
	private JTextField busesCrossingTextField;
	
	private int busSpawnMaxDelay;
	
	public SimulationManager(LogPanel logPanel, DrawPanel drawPanel, int busSpawnMaxDelay, JTextField busesInQueueTextField, JTextField busesOnBridgeTextField) {
		this.logPanel = logPanel;
		this.drawPanel = drawPanel;
		this.busSpawnMaxDelay = busSpawnMaxDelay;
		busesWaitingTextField = busesInQueueTextField;
		busesCrossingTextField = busesOnBridgeTextField;
		bridge = new Bridge(BridgeThroughput.ONE_BUS_ONE_WAY);
		buses = new ArrayList<Bus>();
	}

	public float getBusSpawnMaxDelay (){
		return busSpawnMaxDelay;
	}

	public void setMaxBusSpawnRate(int busSpawnMaxDelay) {
		this.busSpawnMaxDelay = busSpawnMaxDelay;
	}
	
	public void setBridgeThroughput(BridgeThroughput bridgeThroughput) {
		bridge.setBridgeThroughput(bridgeThroughput);
	}

	@Override
	public void run() {
		worldMap = new WorldMap(drawPanel.getSize());
		drawPanel.setSimulationManager(this);
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					Bus bus = new Bus(bridge, logPanel, worldMap);
					synchronized (buses) {
						buses.add(bus);
					}
					new Thread(bus, "BUS" + BUS_ID++).start();
					
					try {
						int timeToNextSpawn = ThreadLocalRandom.current().nextInt((int)busSpawnMaxDelay/2, busSpawnMaxDelay);
						Thread.sleep(timeToNextSpawn);
					} catch (InterruptedException e) {
						System.err.println("Sleep error");
					}
				}				
			}
		}, "BUS_SPAWNER").start();
		
		while(true) {
			synchronized (buses) {
				buses.removeIf((x) -> x.isToRemove());
			}
			
			busesWaitingTextField.setText(bridge.getWaitngBusesList());
			busesCrossingTextField.setText(bridge.getCrossingBusesList());
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}
	}
	
	public void draw(Graphics g) {
		
		if(worldMap != null) {
			worldMap.draw(g);
		}
		
		synchronized (buses) {
			for(Bus bus : buses) {
				bus.draw(g);
			}
		}
	}
}
