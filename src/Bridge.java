import java.awt.Graphics;
import java.util.ArrayList;

enum BridgeThroughput {
	ONE_BUS_ONE_WAY("Tylko jeden bus na moœcie"),
	MANY_BUSES_ONE_WAY("Wiele busów na moœcie, przejazd tylko w jedn¹ stronê"),
	MANY_BUSES_BOTH_WAYS("Wiele busów na moœcie, przejazd w obie strony"),
	UNLIMITED("Nieograniczona iloœæ busów na moœcie, przejazd w obie strony");
	
	String text;
	private BridgeThroughput(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return text;
	}
	
}


public class Bridge {
	
	ArrayList<Bus> busesWaiting;
	ArrayList<Bus> busesCrossing;

	public Bridge() {
		busesWaiting = new ArrayList<Bus>();
		busesCrossing = new ArrayList<Bus>();
	}

	public synchronized void getOnTheBridge(Bus bus) {
		while(!busesCrossing.isEmpty()) {
			busesWaiting.add(bus);
			bus.sendLog("Czeka przed mostem");
			try {
				wait();
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
			
			busesWaiting.remove(bus);
		}
		
		bus.sendLog("Jedzie przez most");
		busesCrossing.add(bus);
	}
	
	public synchronized void getOffTheBridge(Bus bus) {
		busesCrossing.remove(bus);
		bus.sendLog("Zje¿dza z mostu");
		notify();
	}
	
}
