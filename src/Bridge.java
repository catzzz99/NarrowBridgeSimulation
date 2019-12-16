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
	
	private ArrayList<Bus> allBuses = new ArrayList<Bus>();
	
	public void addBus(Bus bus) {
		allBuses.add(bus);
	}
	
	public void removeBus(Bus bus) {
		allBuses.remove(bus);
	}

	public ArrayList<Bus> getAllBuses() {
		return allBuses;
	}

	public void draw(Graphics g) {
		// parking, droga, kolejka, most
		synchronized (allBuses) {
			for(Bus bus : allBuses) {
				bus.draw(g);
			}
		}	
	}
	
}
