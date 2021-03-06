import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

enum BridgeThroughput {
	ONE_BUS_ONE_WAY("One bus, one way", 1),
	MANY_BUSES_ONE_WAY("Many buses, one way", 3),
	MANY_BUSES_BOTH_WAYS("Many buses, both ways", 3),
	UNLIMITED("Unlimited", Integer.MAX_VALUE);
	
	
	private String text;
	private int busLimit;
	
	private BridgeThroughput(String text, int busLimit) {
		this.text = text;
		this.busLimit = busLimit;
	}
	
	@Override
	public String toString() {
		return text;
	}

	public void setbusLimit(int busLimit) {
		this.busLimit = busLimit;
	}
	
	public int getBusLimit() {
		return busLimit;
	}
	
}

public class Bridge {
	
	private class DirectionSwitcher implements Runnable {
		
		private final static int SWITCH_TIME = 10000;

		private boolean useSwitcher;		
		private BusDirection previousDirection;
		
		public DirectionSwitcher() {
			useSwitcher = true;
			if(ThreadLocalRandom.current().nextBoolean())
				previousDirection = BusDirection.EAST;
			else
				previousDirection = BusDirection.WEST;
		}
		
		@Override
		public void run() {
			while(useSwitcher) {
				
				BusDirection newDirection;
				if(previousDirection == BusDirection.WEST){
					newDirection = BusDirection.EAST;
				} else {
					newDirection = BusDirection.WEST;
				}
				
				allowedDirections.clear();
				allowedDirections.add(newDirection);
				previousDirection = newDirection;
							
				try {
					Thread.sleep(SWITCH_TIME);
				} catch (InterruptedException e) {
					System.err.println("Sleep error");
				}
			}	
		}
		
		public void closeSwitcher() {
			this.useSwitcher = false;
		}
	}
	
	private ArrayList<Bus> busesWaiting;
	private ArrayList<Bus> busesCrossing;
	
	private BridgeThroughput bridgeThroughput;
	
	private HashSet<BusDirection> allowedDirections;
	private DirectionSwitcher directionSwitcher;

	public Bridge(BridgeThroughput bridgeThroughput) {
		allowedDirections = new HashSet<BusDirection>();
		
		busesWaiting = new ArrayList<Bus>();
		busesCrossing = new ArrayList<Bus>();
		
		setBridgeThroughput(bridgeThroughput);
	}

	public void setBridgeThroughput(BridgeThroughput bridgeThroughput) {
		this.bridgeThroughput = bridgeThroughput;
		setAllowedDirections();
	}
	
	private synchronized void setAllowedDirections() {
		
		
		switch (bridgeThroughput) {
		case ONE_BUS_ONE_WAY:
		case UNLIMITED:
		case MANY_BUSES_BOTH_WAYS:
			if(directionSwitcher != null) {
				directionSwitcher.closeSwitcher();
				directionSwitcher = null;
			}
			allowedDirections.clear();
			allowedDirections.add(BusDirection.EAST);
			allowedDirections.add(BusDirection.WEST);
			break;
		case MANY_BUSES_ONE_WAY:
			if(directionSwitcher == null) {
				directionSwitcher = new DirectionSwitcher();
				new Thread(directionSwitcher, "DIRECTON_SWITCHER").start();
			}
			break;
		default:
			break;
		}
		
		notifyBuses();
	}
	
	public synchronized void getOnTheBridge(Bus bus) {
		while(busesCrossing.size() >= bridgeThroughput.getBusLimit() ||
				!allowedDirections.contains(bus.getBusDirection())) {
			
			busesWaiting.add(bus);
			bus.setInactiveColor();
			bus.sendLog(BusState.GET_ON_BRIDGE.toString());
			
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
			
			busesWaiting.remove(bus);
		}
		
		bus.setActiveColor();
		busesCrossing.add(bus);
	}
	
	public synchronized void getOffTheBridge(Bus bus) {
		busesCrossing.remove(bus);
		bus.sendLog(BusState.GET_OFF_BRIDGE.toString());
		
		notifyBuses();
	}
	
	private synchronized void notifyBuses() {
		switch(bridgeThroughput) {
		case MANY_BUSES_ONE_WAY:
		case MANY_BUSES_BOTH_WAYS:
			for(int i=bridgeThroughput.getBusLimit()-busesCrossing.size(); i>0; i--) {
				notify();
			}		
			break;
		case ONE_BUS_ONE_WAY:
			notify();
			break;
		case UNLIMITED:
			notifyAll();
			break;
		default:
			break;	
		}

	}

	public synchronized String getWaitngBusesList() {
		StringBuilder sb = new StringBuilder();
		for (Bus bus : busesWaiting) {
			sb.append(bus.toString());
			sb.append("   ");
		}
		return sb.toString();
	}

	public synchronized String getCrossingBusesList() {
		StringBuilder sb = new StringBuilder();
		for (Bus bus : busesCrossing) {
			sb.append(bus.toString());
			sb.append("   ");
		}
		return sb.toString();
	}
	
}
