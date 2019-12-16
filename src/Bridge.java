import java.awt.Graphics;
import java.util.ArrayList;

enum BridgeThroughput {
	ONE_BUS_ONE_WAY("Tylko jeden bus na mo�cie"),
	MANY_BUSES_ONE_WAY("Wiele bus�w na mo�cie, przejazd tylko w jedn� stron�"),
	MANY_BUSES_BOTH_WAYS("Wiele bus�w na mo�cie, przejazd w obie strony"),
	UNLIMITED("Nieograniczona ilo�� bus�w na mo�cie, przejazd w obie strony");
	
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
