import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NarrowBridgeApp extends JFrame implements ActionListener, ChangeListener{

	private static final long serialVersionUID = 773440677198457816L;
	
	private static final String APP_TITLE = "Narrow Bridge Simulation";
	
	private static final String AUTHOR_INFO = "Author: Michal Tkacz \n"
											+ "Date: January 2020";
	
	private static final String APP_INFO = "Program ilustrates bridge crossing problem\n"
										 + "Bridge may work in 4 different ways: \n"
										 + "- \"One bus, one way\" - only one bus crossing bridge at the time.\n"
										 + "- \"Many buses, one way\" - many buses crossing bridge at the time, but only in one direction. \n"
										 + "    Bus limit might be changed in any time. \n"
										 + "    Directions changes automatically every 10 seconds.\n"
										 + "- \"Many buses, both ways\" - same as for  \"Many buses, one way\", but buses might drive in both directions.  \n"
										 + "- \"Unlimited\" - unilitmited crossing in both directions\n";
	
	private static final int BORDER_THICKNESS = 4;
	private static final int FONT_SIZE = 12;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> {
			new NarrowBridgeApp();
		});
	}
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu helpMenu = new JMenu("Help");
	private JMenuItem authorInfoMenuItem = new JMenuItem("Author");
	private JMenuItem appInfoMenuItem = new JMenuItem("About");	
	
	private JLabel busesInQueueLabel = new JLabel("Buses in queue:", JLabel.LEFT);
	private JLabel busesOnBridgeLabel = new JLabel("Buses crossing the bridge:", JLabel.LEFT);
	private JLabel trafficIntensityLabel = new JLabel("Frequency of bus spawning:", JLabel.RIGHT);
	private JLabel bridgeThroughputModeLabel = new JLabel("Bridge mode:", JLabel.RIGHT);
	private JLabel maxBusesOnBridgeLabel = new JLabel("Limit of number of buses crossing the bridge:", JLabel.RIGHT);	
	
	private JTextField busesInQueueTextField = new JTextField("Queue is empty");
	private JTextField busesOnBridgeTextField = new JTextField("Bride is empty");
	
	private JSlider trafficIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1000, 6000, 4000);
	
	private JComboBox<BridgeThroughput> bridgeThroughputModeComboBox = new JComboBox<>(BridgeThroughput.values());
	
	private JSpinner maxBusesOnBridgeSpinner = new JSpinner(new SpinnerNumberModel(((BridgeThroughput)bridgeThroughputModeComboBox.getSelectedItem()).getBusLimit(), 1, 10, 1));
	
	private LogPanel logPanel = new LogPanel();
	private DrawPanel drawPanel = new DrawPanel();
	private SimulationManager simulationManager = new SimulationManager(logPanel, drawPanel, trafficIntensitySlider.getValue(), busesInQueueTextField, busesOnBridgeTextField);
	

	public NarrowBridgeApp() {
		super(APP_TITLE);
		setSize(1280, 720);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		UIManager.put("OptionPane.messageFont", new Font("Monospaced", Font.BOLD, FONT_SIZE));
		
		addListeners();
		setInitialControlsProporties();
		createWindowLayout();
		createMenuBar();
		
		setVisible(true);
		
		startSimulation();
	}

	private void addListeners() {
		authorInfoMenuItem.addActionListener(this);
		appInfoMenuItem.addActionListener(this);
		
		bridgeThroughputModeComboBox.addActionListener(this);
		
		trafficIntensitySlider.addChangeListener(this);
		maxBusesOnBridgeSpinner.addChangeListener(this);
	}
	
	private void setInitialControlsProporties() {
		busesInQueueTextField.setEditable(false);
		busesOnBridgeTextField.setEditable(false);
		
		trafficIntensitySlider.setPaintLabels(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		int minValue = trafficIntensitySlider.getMinimum();
		int maxValue = trafficIntensitySlider.getMaximum();
		int midValue = (int) (maxValue + minValue)/2;
		labelTable.put(new Integer(minValue), new JLabel("MAX"));
		labelTable.put(new Integer(midValue), new JLabel("MID"));
		labelTable.put(new Integer(maxValue), new JLabel("MIN"));
		trafficIntensitySlider.setLabelTable(labelTable);
		trafficIntensitySlider.setPaintTicks(true);
		trafficIntensitySlider.setPaintTrack(true);
		trafficIntensitySlider.setMajorTickSpacing(500);
		trafficIntensitySlider.setMinorTickSpacing(100);
		
		updateMaxBusesOnBridgeSpinner();
	}

	private void createWindowLayout() {		
		Border emptyBorder = BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS ,BORDER_THICKNESS ,BORDER_THICKNESS);
		Border etchedBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(BORDER_THICKNESS, BORDER_THICKNESS));
		mainPanel.setBorder(emptyBorder);
		
		JPanel northPanel = new JPanel(new GridLayout(1, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		
		JPanel northWestPanel = new JPanel();
		northWestPanel.setLayout(new GridLayout(4, 1, BORDER_THICKNESS, BORDER_THICKNESS));
		northWestPanel.setBorder(emptyBorder);
		northWestPanel.add(busesInQueueLabel);
		northWestPanel.add(busesInQueueTextField);
		northWestPanel.add(busesOnBridgeLabel);
		northWestPanel.add(busesOnBridgeTextField);
		northPanel.add(northWestPanel);

		JPanel northEastPanel = new JPanel();
		northEastPanel.setLayout(new GridLayout(3, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		northEastPanel.setBorder(etchedBorder);
		northEastPanel.add(trafficIntensityLabel);
		northEastPanel.add(trafficIntensitySlider);
		northEastPanel.add(bridgeThroughputModeLabel);
		northEastPanel.add(bridgeThroughputModeComboBox);
		northEastPanel.add(maxBusesOnBridgeLabel);
		northEastPanel.add(maxBusesOnBridgeSpinner);
		northPanel.add(northEastPanel);
		
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		centerPanel.add(logPanel);
		centerPanel.add(drawPanel);	
		
		mainPanel.add(northPanel, BorderLayout.NORTH);
		mainPanel.add(centerPanel, BorderLayout.CENTER);
		setContentPane(mainPanel);
	}

	private void createMenuBar() {
		helpMenu.add(authorInfoMenuItem);
		helpMenu.add(appInfoMenuItem);
		
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);
	}

	private void startSimulation() {
		new Thread(simulationManager, "SIMULATION_MANAGER").start();
		new Thread(drawPanel, "DRAW_PANEL").start();
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		Object eSource = event.getSource();
		
		if(eSource == authorInfoMenuItem) {
			showAuthorInfo();
		}
		
		if(eSource == appInfoMenuItem){
			showAppInfo();
		}
		
		if(eSource == bridgeThroughputModeComboBox) {
			updateMaxBusesOnBridgeSpinner();
			updateSimulationBridgeThroughputMode();
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent event) {
		Object eSource = event.getSource();
		
		if(eSource == trafficIntensitySlider) {
			setSimulationTrafficIntensity();
		}
		
		if(eSource == maxBusesOnBridgeSpinner) {
			updateBridgeThroughput();
			updateSimulationBridgeThroughputMode();
		}	
	}
	
	private void showAuthorInfo() {
		JOptionPane.showMessageDialog(this, AUTHOR_INFO, "About author", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAppInfo() {
		JOptionPane.showMessageDialog(this, APP_INFO, "About program", JOptionPane.INFORMATION_MESSAGE);
	}

	private void updateSimulationBridgeThroughputMode() {
		BridgeThroughput bridgeThroughput = (BridgeThroughput) bridgeThroughputModeComboBox.getSelectedItem(); 
		simulationManager.setBridgeThroughput(bridgeThroughput);
	}

	private void updateBridgeThroughput() {
		int busLimit = (int) maxBusesOnBridgeSpinner.getValue();
		((BridgeThroughput) bridgeThroughputModeComboBox.getSelectedItem()).setbusLimit(busLimit);		
	}

	private void setSimulationTrafficIntensity(){
		int trafficIntensity = trafficIntensitySlider.getValue();
		simulationManager.setMaxBusSpawnRate(trafficIntensity);
	}

	private void updateMaxBusesOnBridgeSpinner() {
		BridgeThroughput bt = (BridgeThroughput) bridgeThroughputModeComboBox.getSelectedItem(); 
		boolean enabled = (bt == BridgeThroughput.MANY_BUSES_BOTH_WAYS || bt == BridgeThroughput.MANY_BUSES_ONE_WAY);
		maxBusesOnBridgeSpinner.setEnabled(enabled);
		maxBusesOnBridgeLabel.setEnabled(enabled);
		
		maxBusesOnBridgeSpinner.setValue(bt.getBusLimit());
	}
}
