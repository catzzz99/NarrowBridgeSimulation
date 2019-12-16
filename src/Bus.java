import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;


public class Bus implements Runnable{
	
	private static int BUS_NUMBER = 0;

	private int x;
	private int y;
	private int width;
	private int height;
	
	private int speed = 3;
	
	private Bridge bridge;
	private Dimension panelSize;
	
	private int busID;
	
	public Bus(Bridge bridge, Dimension panelSize) {
		this.bridge = bridge;
		this.panelSize = panelSize;
		this.bridge.addBus(this);
		this.busID = ++BUS_NUMBER;
		
		width = 60;
		height = 30;
		x = -width;
		y = 0;
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


	@Override
	public void run() {
		boolean running = true;
		while(running) {
			//boarding
			//go to bridge
			//ride the bridge
			//go to parking
			//unload
		
			x += speed;
			if(x >= panelSize.getWidth()) {
				x = -width;
				y += 2 * height;
			}
			
			if(y >= panelSize.getWidth()) {
				running = false;
			}
			
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
			}
		}
		
	}
	
	
	public void draw(Graphics g) {	
		g.setColor(Color.ORANGE);
		g.fillRect(x, y, width, height);
		
		g.setColor(Color.DARK_GRAY);
		FontMetrics fm = g.getFontMetrics();
		int tx = x + (width - fm.stringWidth(Integer.toString(busID)))/2;
		int ty = y + (height - fm.getHeight())/2 + fm.getAscent();
		g.drawString(Integer.toString(busID), tx, ty);
	}
}
