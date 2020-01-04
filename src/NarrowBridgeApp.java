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
 * AUTOR: 	Micha� Tkacz 248869
 * 		 	Pi�tek TN 11:15
 * 
 * DATA:    6 stycznia 2020r
 * 
 */

public class NarrowBridgeApp extends JFrame implements ActionListener, ChangeListener{

	private static final long serialVersionUID = 773440677198457816L;
	
	private static final String APP_TITLE = "Narrow Bridge Simulation";
	
	private static final String AUTHOR_INFO = "Autor: Micha� Tkacz 248869\n"
											+ "Pi�tek TN 11:15 \n"
											+ "6 stycznia 2020";
	
	private static final String APP_INFO = "Program ilustruje spos�b symulacji wsp�bie�nych w�tk�w symuluj�cych pojazdy, \n" 
										 + "kt�re jad�c z przeciwnych kierunk�w musz� przejecha� przez \"w�ski most\". \n\n" 
										 + "Most posiada cztery r�ne tryby pracy: \n"
										 + "- \"Przejazd pojedy�czo\" - przez most w danej w chwili mo�e przeje�dza� tylko jeden bus. \n"
										 + "- \"Przejazd ograniczony, jednokierunkowy\" - przez most danej w chwili mo�e przeje�dza� ograniczona liczba \n"
										 + "    bus�w, w jednym, zgodnym kierunku. Limitem bus�w mo�na na bie��co sterowa�. Dopuszczony kierunek przejazu \n"
										 + "    automatycznie akutalzuje si� co dziesi�� sekund w spos�b losowy.\n"
										 + "- \"Przejazd ograniczony, dwukierunkowy\" - przez most w danej chwili mo�e przeje�d�a� ograniczona \n" 
										 + "    liczba bus�w, przy czym kierunek przejazdu ka�dego z bus�w jest dowolny. Limitem bus�w mo�na na bie��co sterowa�. \n"
										 + "- \"Przejazd nieograniczony\" - przez most w danej chwili mo�e przeje�d�a� nieograniczona liczba bus�w. \n";
	
	private static final int BORDER_THICKNESS = 4;
	private static final int FONT_SIZE = 12;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> {
			new NarrowBridgeApp();
		});
	}
	
	private JMenuBar menuBar = new JMenuBar();
	
	private JMenu helpMenu = new JMenu("Pomoc");
	private JMenuItem authorInfoMenuItem = new JMenuItem("O autorze");
	private JMenuItem appInfoMenuItem = new JMenuItem("O programie");	
	
	private JLabel busesInQueueLabel = new JLabel("Busy oczekuj�ce w kolejce przed mostem:", JLabel.LEFT);
	private JLabel busesOnBridgeLabel = new JLabel("Busy obecnie przeje�d�aj�ce przez most:", JLabel.LEFT);
	private JLabel trafficIntensityLabel = new JLabel("Cz�stotliwo�� pojawiania si� nowych bus�w:", JLabel.RIGHT);
	private JLabel bridgeThroughputModeLabel = new JLabel("Tryb pracy mostu:", JLabel.RIGHT);
	private JLabel maxBusesOnBridgeLabel = new JLabel("Maksymalna liczba bus�w na mo�cie:", JLabel.RIGHT);	
	
	private JTextField busesInQueueTextField = new JTextField("Kolejka jest pusta");
	private JTextField busesOnBridgeTextField = new JTextField("Most jest pusty");
	
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
		JOptionPane.showMessageDialog(this, AUTHOR_INFO, "Informacje o autorze", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAppInfo() {
		JOptionPane.showMessageDialog(this, APP_INFO, "Informacje o programie", JOptionPane.INFORMATION_MESSAGE);
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
