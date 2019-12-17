import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.concurrent.ThreadLocalRandom;

enum BusDirection {
	EAST("East", 1),
	WEST("West", -1);
	
	private String name;
	private int direction;
	
	BusDirection(String name, int direction){
		this.name = name;
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}
	
	@Override
	public String toString(){
		return name;
	}

}

enum BusState {
	BOARDING("³aduje pasa¿erów"),
	ON_ROAD_TO_BRIDGE("Jedzie do mostu"),
	//GET_ON_BRIDGE("Czeka na wjazd na most"),
	ON_BRIDGE("Jedzie przez most"),
	ON_ROAD_TO_PARKING("Jedzie na parking"),
	UNBOARDING("Wy³adowuje pasa¿erów");
	
	private String state;
	
	BusState(String state){
		this.state = state;
	}
	
	@Override
	public String toString() {
		return state;
	}
	
}

public class Bus implements Runnable{
	
	private static int BUS_NUMBER = 1;
	
	private static int MIN_BOARDING_TIME = 500;
	private static int MAX_BOARDING_TIME = 5000;
	private static int ON_ROAD_TO_BRIDGE_TIME = 500;
	private static int ON_BRIDGE_TIME = 3000;
	private static int ON_ROAD_TO_PARKING_TIME = 500;
	private static int MIN_UNBOARDING_TIME = 500;
	private static int MAX_UNBOARDING_TIME = 2000;

	private int x;
	private int y;
	private int width;
	private int height;

	private int speed;
	private BusState busState;
	private BusDirection busDirection;
	private Color color;
	
	private Bridge bridge;
	private LogPanel logPanel;
	private WorldMap worldMap;
	
	private int busID;
	
	private boolean runThread;
	
	public Bus(Bridge bridge, LogPanel logPanel, WorldMap worldMap) {
		this.bridge = bridge;
		this.logPanel = logPanel;
		this.worldMap = worldMap;
		this.busID = BUS_NUMBER++;
		runThread = true;
		
		width = 40;
		height = 20;
		speed = 0;
		
		busState = BusState.BOARDING;
		
		int rnd = ThreadLocalRandom.current().nextInt(2);
		if(rnd == 0) {
			busDirection = BusDirection.EAST;
			x = 4;
			color = Color.GREEN;
		}else {
			busDirection = BusDirection.WEST;
			x = worldMap.getWidth() - width - 4;
			color = Color.PINK;
		}
		
		y = height * (ThreadLocalRandom.current().nextInt(4, this.worldMap.getHeight() - height - 4) % height);
	}

	public Bridge getBridge() {
		return bridge;
	}

	public void setBridge(Bridge bridge) {
		this.bridge = bridge;
	}

	public int getBusID() {
		return busID;
	}

	public void setBusID(int busID) {
		this.busID = busID;
	}

	public boolean isRunThread() {
		return runThread;
	}

	public void setRunThread(boolean runThread) {
		this.runThread = runThread;
	}

	boolean isOnBridge = false;
	
	@Override
	public void run() {
		while(runThread) {
			switch (busState) {
			case BOARDING:
				boarding();
				break;
			case ON_ROAD_TO_BRIDGE:
				onRoadToBridge();
				break;
			case ON_BRIDGE:
				crossTheBridge();
				break;
			case ON_ROAD_TO_PARKING:
				onRoadToParking();
				break;
			case UNBOARDING:
				break;
			default:
				break;
			}

		
			x += speed;
			//System.out.println(x);
			
			if( x+width < 0 || x > worldMap.getWidth()) {
				runThread = false;
			}
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}
		
	}
	
	public void sendLog(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("[ID" + busID + "] ");
		sb.append(message);
		logPanel.addLog(sb.toString());
	}
	
	private void takeNap(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			System.err.println("Sleep error");
		}
	}
	
	private void takeNap(int minTime, int maxTime) {
		int napTime = ThreadLocalRandom.current().nextInt(minTime, maxTime);
		takeNap(napTime);
	}
	
	private void setNextState(BusState nextState, int time){
		busState = nextState;
		calculateSpeed(time);
	}
	
	private void calculateSpeed(int time) {
		int distance = 0;
		if(busDirection == BusDirection.WEST) {
			distance = worldMap.getWorldZoneHeight(WorldZoneType.EAST_ROAD);
		}else {
			distance = worldMap.getWorldZoneHeight(WorldZoneType.WEST_ROAD);
		}
		speed = 10 * busDirection.getDirection() * distance/time;
	}
	
	private void boarding() {
		sendLog("Stoi na parkingu");
		takeNap(MIN_BOARDING_TIME, MAX_BOARDING_TIME);
		setNextState(BusState.ON_ROAD_TO_BRIDGE, ON_ROAD_TO_BRIDGE_TIME);
	}

	private void onRoadToBridge() {
		sendLog("Jedzie do mostu w kierunku " + busDirection.toString());
		setNextState(BusState.ON_BRIDGE, ON_BRIDGE_TIME);
	}
	
	private void crossTheBridge() {
		//TODO divide into two separate methods
		if(busDirection == BusDirection.EAST) {
			if (x > worldMap.getWorldZoneX(WorldZoneType.EAST_GATE)){
				bridge.getOffTheBridge(this);
				setNextState(BusState.ON_ROAD_TO_PARKING, ON_ROAD_TO_PARKING_TIME);
			}else if(x > worldMap.getWorldZoneX(WorldZoneType.WEST_GATE)) {
				if(isOnBridge) return;
				isOnBridge = true;
				bridge.getOnTheBridge(this);
			} 
		}else if(busDirection == BusDirection.WEST) {
			if (x + width < worldMap.getWorldZoneX(WorldZoneType.BRIDGE)) {
				bridge.getOffTheBridge(this);
				setNextState(BusState.ON_ROAD_TO_PARKING, ON_ROAD_TO_PARKING_TIME);
			} else if(x + width < worldMap.getWorldZoneX(WorldZoneType.EAST_ROAD)) {
				if(isOnBridge) return;
				isOnBridge = true;
				bridge.getOnTheBridge(this);
			}
		}
		
	}
	
	private void onRoadToParking() {
		sendLog("Jedzie do mostu w kierunku " + busDirection.toString());
		if(busDirection == BusDirection.EAST) {
			if (x > worldMap.getWorldZoneX(WorldZoneType.EAST_GATE)){
				bridge.getOffTheBridge(this);
				setNextState(BusState.ON_ROAD_TO_PARKING, ON_ROAD_TO_PARKING_TIME);
			}
		}else if(busDirection == BusDirection.WEST) {
			if (x + width < worldMap.getWorldZoneX(WorldZoneType.BRIDGE)) {
				bridge.getOffTheBridge(this);
				setNextState(BusState.ON_ROAD_TO_PARKING, ON_ROAD_TO_PARKING_TIME);
			} else if(x + width < worldMap.getWorldZoneX(WorldZoneType.EAST_ROAD)) {
				if(isOnBridge) return;
				isOnBridge = true;
				bridge.getOnTheBridge(this);
			}
		}
		setNextState(BusState.ON_ROAD_TO_PARKING, ON_ROAD_TO_PARKING_TIME);
	}

	public void draw(Graphics g) {	
		g.setColor(color);
		g.fillRect(x, y, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
		
		g.setFont(new Font("Arial", Font.BOLD, 16));
		g.setColor(Color.DARK_GRAY);	
		FontMetrics fm = g.getFontMetrics();
		int tx = x + (width - fm.stringWidth(Integer.toString(busID)))/2;
		int ty = y + (height - fm.getHeight())/2 + fm.getAscent();
		g.drawString(Integer.toString(busID), tx, ty);

	}
}
