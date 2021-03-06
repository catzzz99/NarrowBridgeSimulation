import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.concurrent.ThreadLocalRandom;

enum BusDirection {
	EAST("E", 1),
	WEST("W", -1);
	
	private String name;
	private int direction;
	
	BusDirection(String name, int direction){
		this.name = name;
		this.direction = direction;
	}
	
	@Override
	public String toString(){
		return name;
	}

	public int getDirection() {
		return direction;
	}
}

enum BusState {
	INITIALIZATION("[Bus has spawned on start parking] "),
	EMBARKATION("Is embarking "),
	ON_ROAD_TO_BRIDGE("Is driving towards bridge "),
	GET_ON_BRIDGE("Is waiting in queue "),
	CROSS_THE_BRIDGE("Is crossing the bridge "),
	GET_OFF_BRIDGE("Is leaving the bridge "),
	ON_ROAD_TO_PARKING("Is driving towards end parking"),
	DISEMBARKATION("Is diembarking "),
	TO_REMOVE("[Bus has despawned] ");
	
	private String stateMessage;
	
	BusState(String stateMessage){
		this.stateMessage = stateMessage;
	}
	
	@Override
	public String toString() {
		return stateMessage;
	}
	
}

public class Bus implements Runnable {
	
	private static int BUS_NUMBER = 1;
	
	private static int MIN_EMBARKATION_TIME = 500;
	private static int MAX_EMBARKATION_TIME = 5000;
	
	private static int ON_ROAD_TO_BRIDGE_TIME = 2000;
	
	private static int ON_BRIDGE_TIME = 3000;
	
	private static int ON_ROAD_TO_PARKING_TIME = 2000;
	
	private static int MIN_DISEMBARKATION_TIME = 500;
	private static int MAX_DISEMBARKATION_TIME = 1500;

	private int x;
	private int y;
	private int width;
	private int height;

	private int speed;
	private BusDirection busDirection;
	private Color activeColor;
	private Color inactiveColor;
	private Color currentColor;
	
	private Bridge bridge;
	private LogPanel logPanel;
	private WorldMap worldMap;
	
	private int busID;
	
	private BusState currentState;
	private BusState nextState;
	
	
	public Bus(Bridge bridge, LogPanel logPanel, WorldMap worldMap) {
		this.bridge = bridge;
		this.logPanel = logPanel;
		this.worldMap = worldMap;
		this.busID = BUS_NUMBER++;

		width = 40;
		height = 20;
		speed = 0;
		
		currentState = BusState.INITIALIZATION;
		nextState = BusState.EMBARKATION;
		
		if(ThreadLocalRandom.current().nextBoolean()) {
			busDirection = BusDirection.EAST;
			x = 4;
			activeColor = new Color(51, 204, 51);
			inactiveColor = new Color(0, 153, 51);
		}else {
			busDirection = BusDirection.WEST;
			x = worldMap.getWidth() - width - 4;
			activeColor =  new Color(0, 204, 255);
			inactiveColor =  new Color(0, 153, 204);
		}
		
		currentColor = activeColor;
		
		int worldHeight = worldMap.getHeight(); 
		int randomPixel  = ThreadLocalRandom.current().nextInt(0, worldHeight + 1);
		int spawnLineY =  randomPixel % ((int) worldHeight/height);
		y = height * spawnLineY;
	}
	
	@Override
	public String toString() {
		return "BUS" + Integer.toString(busID);
	}

	public int getBusID() {
		return busID;
	}

	public BusDirection getBusDirection() {
		return busDirection;
	}
	
	@Override
	public void run() {
		while(currentState != BusState.TO_REMOVE) {
			switch (currentState) {
			case INITIALIZATION:
				initialization();
				break;
			case EMBARKATION:
				embarkation();
				break;
			case ON_ROAD_TO_BRIDGE:
				onRoadToBridge();
				break;
			case GET_ON_BRIDGE:
				getOnBridge();
				break;
			case CROSS_THE_BRIDGE:
				crossTheBridge();
				break;
			case GET_OFF_BRIDGE:
				getOffBridge();
				break;
			case ON_ROAD_TO_PARKING:
				onRoadToParking();
				break;
			case DISEMBARKATION:
				disembarkation();
				break;
			case TO_REMOVE:
				toRemove();
				break;
			default:
				break;
			}
		
			updatePostition();
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}		
	}
	
	private void updatePostition() {
		x += speed;
	}

	public void sendLog(String message) {
		StringBuilder sb = new StringBuilder();
		sb.append("<< [Bus");
		sb.append(String.format("%04d", busID));
		sb.append(" -> ");
		sb.append(busDirection);
		sb.append("] ");
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
	
	private void calculateSpeed(int time) {
		if(time == 0) {
			speed = 0;
			return;
		}
		
		int distance = 0;
		if(busDirection == BusDirection.WEST) {
			distance = worldMap.getWorldZoneHeight(WorldZoneType.EAST_ROAD);
		}else {
			distance = worldMap.getWorldZoneHeight(WorldZoneType.WEST_ROAD);
		}
		speed = 10 * busDirection.getDirection() * distance/time;
	}
	
	private void initialization() {
		nextState = BusState.EMBARKATION;	
		currentState = nextState;
	}

	private void embarkation() {
		if(nextState == BusState.EMBARKATION) {
			sendLog(currentState.toString());
			takeNap(MIN_EMBARKATION_TIME, MAX_EMBARKATION_TIME);
			
			nextState = BusState.ON_ROAD_TO_BRIDGE;
			currentState = nextState;
		}
	}

	private void onRoadToBridge() {
		if(nextState == BusState.ON_ROAD_TO_BRIDGE) {
			sendLog(currentState.toString());
			calculateSpeed(ON_ROAD_TO_BRIDGE_TIME);
			nextState = BusState.GET_ON_BRIDGE;
		}else {
			if(busDirection == BusDirection.EAST) {
				if(x > worldMap.getWorldZoneX(WorldZoneType.WEST_GATE))
					currentState = nextState;
			}else if(busDirection == BusDirection.WEST) {
				if(x + width < worldMap.getWorldZoneX(WorldZoneType.EAST_ROAD))
					currentState = nextState;
			}
		}
	}
	
	private void getOnBridge() {
		bridge.getOnTheBridge(this);
		nextState = BusState.CROSS_THE_BRIDGE;
		currentState = nextState;
	}
	
	private void crossTheBridge() {
		if(nextState == BusState.CROSS_THE_BRIDGE) {
			sendLog(currentState.toString());
			calculateSpeed(ON_BRIDGE_TIME);
			nextState = BusState.GET_OFF_BRIDGE;
		}else {
			if(busDirection == BusDirection.EAST) {
				if (x > worldMap.getWorldZoneX(WorldZoneType.EAST_GATE))
					currentState = nextState;	
			}else if(busDirection == BusDirection.WEST) {
				if (x + width < worldMap.getWorldZoneX(WorldZoneType.BRIDGE)) 
					currentState = nextState;
			}
		}
	}
	
	private void getOffBridge() {
		bridge.getOffTheBridge(this);
		nextState = BusState.ON_ROAD_TO_PARKING;
		currentState = nextState;
	}
	
	private void onRoadToParking() {
		if(nextState == BusState.ON_ROAD_TO_PARKING) {
			sendLog(currentState.toString());
			calculateSpeed(ON_ROAD_TO_PARKING_TIME);
			nextState = BusState.DISEMBARKATION;
		}else {
			if(busDirection == BusDirection.EAST) {
				if (x > worldMap.getWorldZoneX(WorldZoneType.EAST_PARKING))
					currentState = nextState;
			}else if(busDirection == BusDirection.WEST) {
				if (x + width < worldMap.getWorldZoneX(WorldZoneType.WEST_ROAD))
					currentState = nextState;
			}
		}	
	}
	
	private void disembarkation() {
		if(nextState == BusState.DISEMBARKATION) {
			sendLog(currentState.toString());
			calculateSpeed(0);
			takeNap(MIN_DISEMBARKATION_TIME, MAX_DISEMBARKATION_TIME);
			nextState = BusState.TO_REMOVE;
			currentState = nextState;
		}
	}

	public void draw(Graphics g) {	
		g.setColor(currentColor);
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

	private void toRemove() {
		nextState = BusState.TO_REMOVE;
		currentState = nextState;
	}
	
	public boolean isToRemove() {
		return currentState == BusState.TO_REMOVE;
	}

	public void setActiveColor() {
		currentColor = activeColor;
	}
	
	public void setInactiveColor() {
		currentColor = inactiveColor;
	}	
}
