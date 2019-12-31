import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

enum BridgeThroughput {
	ONE_BUS_ONE_WAY("Tylko jeden bus na moœcie", 1),
	MANY_BUSES_ONE_WAY("Ograniczony przejazd tylko w jedn¹ stronê", 3),
	MANY_BUSES_BOTH_WAYS("Ograniczony przejazd w obie strony", 3),
	UNLIMITED("Nieograniczony przejazd w obie strony", Integer.MAX_VALUE);
	
	
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
	
	private ArrayList<Bus> busesWaiting;
	private ArrayList<Bus> busesCrossing;
	
	private BridgeThroughput bridgeThroughput;
	
	private HashSet<BusDirection> allowedDirections;

	public Bridge(BridgeThroughput bridgeThroughput) {
		setBridgeThroughput(bridgeThroughput);
		
		busesWaiting = new ArrayList<Bus>();
		busesCrossing = new ArrayList<Bus>();
	}

	public void setBridgeThroughput(BridgeThroughput bridgeThroughput) {
		this.bridgeThroughput = bridgeThroughput;
		setAllowedDirections();
	}
	
	private void setAllowedDirections() {
		allowedDirections = new HashSet<BusDirection>();
		switch (bridgeThroughput) {
		case ONE_BUS_ONE_WAY:
		case MANY_BUSES_BOTH_WAYS:
		case UNLIMITED:
			allowedDirections.add(BusDirection.EAST);
			allowedDirections.add(BusDirection.WEST);
			break;
		case MANY_BUSES_ONE_WAY:
			if(ThreadLocalRandom.current().nextBoolean())
				allowedDirections.add(BusDirection.EAST);
			else
				allowedDirections.add(BusDirection.WEST);
			break;
		default:
			break;
		}
	}
	
	public synchronized void getOnTheBridge(Bus bus) {
		while(busesCrossing.size() >= bridgeThroughput.getBusLimit() ||
				!allowedDirections.contains(bus.getBusDirection())) {
			
			busesWaiting.add(bus);
			bus.sendLog(BusState.GET_ON_BRIDGE.toString());
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
			
			busesWaiting.remove(bus);
		}
		busesCrossing.add(bus);
	}
	
	public synchronized void getOffTheBridge(Bus bus) {
		busesCrossing.remove(bus);
		bus.sendLog(BusState.GET_OFF_BRIDGE.toString());
		
		if(bridgeThroughput == BridgeThroughput.UNLIMITED) {
			notifyAll();
		} else {
			for(int i=bridgeThroughput.getBusLimit()-busesCrossing.size(); i>0; i--)
				notify();
		}
			
	}

	public String getWaitngBusesList() {
		StringBuilder sb = new StringBuilder();
		for (Bus bus : busesWaiting) {
			sb.append(bus.toString());
			sb.append("   ");
		}
		return sb.toString();
	}

	public String getCrossingBusesList() {
		StringBuilder sb = new StringBuilder();
		for (Bus bus : busesCrossing) {
			sb.append(bus.toString());
			sb.append("   ");
		}
		return sb.toString();
	}
	
}
