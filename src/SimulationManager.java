
import java.awt.Graphics;
import java.util.ArrayList;


public class SimulationManager implements Runnable {
	
	private static int BUS_ID = 1;

	private static final int BUS_SPAWN_TIME = 1000; //0.5 sec
	private static final int BUS_SPAWN_MAX_DELAY = 4000; //4.5 sec
	
	private LogPanel logPanel;
	private DrawPanel drawPanel;
	
	private ArrayList<Bus> buses;
	private Bridge bridge;
	private WorldMap worldMap;
	
	private float spawnDelayFactor;
	
	
	public SimulationManager(LogPanel logPanel, DrawPanel drawPanel, int spawnDelayFactor) {
		this.logPanel = logPanel;
		this.drawPanel = drawPanel;
		setSpawnDelayFactor(spawnDelayFactor);
		bridge = new Bridge();
		buses = new ArrayList<Bus>();
	}
	
	public Bridge getBridge() {
		return bridge;
	}

	public void setBridge(Bridge bridge) {
		this.bridge = bridge;
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
		worldMap = new WorldMap(drawPanel.getSize());
		drawPanel.setSimulationManager(this);
		
		//for(int i= 0; i<10; i++) {
		while(true) {
			Bus bus = new Bus(bridge, logPanel, worldMap);
			buses.add(bus);
			new Thread(bus, "BUS" + BUS_ID++).start();
			
			buses.removeIf((x) -> !x.isRunThread());
			System.out.println(buses.size());
			try {
				Thread.sleep((int) (BUS_SPAWN_TIME + BUS_SPAWN_MAX_DELAY * spawnDelayFactor));
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
