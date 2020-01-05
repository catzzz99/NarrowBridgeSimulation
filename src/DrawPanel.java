import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/*
 * PROGRAM: "Narrow Bridge Simulation"
 *
 * PLIKI: 	NarrowBridgeApp.java
 * 			Bridge.java
 * 			Bus.java
 * 			DrawPanel.java
 * 			LogPanel.java
 * 			SimulationManager.java
 * 			WorldMap.java			
 * 
 * AUTOR: 	Micha³ Tkacz 248869
 * 		 	Pi¹tek TN 11:15
 * 
 * DATA:    6 stycznia 2020r
 * 
 */

public class DrawPanel extends JPanel implements Runnable {
	
	private static final long serialVersionUID = 6748004371807231054L;
	
	private SimulationManager simulationManager;
	
	public DrawPanel() {
		setBackground(Color.DARK_GRAY);
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
	}
	
	public SimulationManager getSimulationManager() {
		return simulationManager;
	}

	public void setSimulationManager(SimulationManager simulationManager) {
		this.simulationManager = simulationManager;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if(simulationManager != null) {
			simulationManager.draw(g);
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
