import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class DrawPanel extends JPanel implements Runnable{
	
	private static final long serialVersionUID = 6748004371807231054L;
	
	private Bridge bridge;
	
	
	public DrawPanel() {
		setBackground(Color.LIGHT_GRAY);
	}
	
	public void setBridge(Bridge bridge) {
		this.bridge = bridge;

	}
	
	public Bridge getBridge() {
		return bridge;
		
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(bridge != null) {
			bridge.draw(g);
		}
	}

	@Override
	public void run() {
		while(true) {
			repaint();
			try {
				Thread.sleep(16);
			} catch (InterruptedException e) {
				System.err.println("Sleep error");
			}
		}
	}


}
