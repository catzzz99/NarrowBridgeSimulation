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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NarrowBridgeApp extends JFrame implements ActionListener, ChangeListener{

	private static final long serialVersionUID = 773440677198457816L;
	
	private static final String APP_TITLE = "Narrow Bridge Simulation";
	private static final String AUTHOR_INFO = "Autor: Micha³ Tkacz 248869\n"
											+ "Pi¹tek TN 11:15";
	private static final String APP_INFO = "...";
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
	
	private JLabel busesInQueueLabel = new JLabel("Busy oczekuj¹ce przed mostem:", JLabel.LEFT);
	private JLabel busesOnBridgeLabel = new JLabel("Busy na moœcie:", JLabel.LEFT);
	private JLabel trafficIntensityLabel = new JLabel("Natê¿enie ruchu:", JLabel.RIGHT);
	private JLabel bridgeThroughputLabel = new JLabel("Tryb pracy mostu:", JLabel.RIGHT);
	private JLabel maxBusesOnBridgeLabel = new JLabel("Ograniczenie liczby busów na moœcie:", JLabel.RIGHT);	
	
	private JTextField busesInQueueTextField = new JTextField("Kolejka jest pusta");
	private JTextField busesOnBridgeTextField = new JTextField("Most jest pusty");
	private JSlider trafficIntensitySlider = new JSlider(JSlider.HORIZONTAL, 1000, 6000, 4000);
	private JComboBox<BridgeThroughput> bridgeThroughputComboBox = new JComboBox<>(BridgeThroughput.values());
	private JSpinner maxBusesOnBridgeSpinner = new JSpinner(new SpinnerNumberModel(((BridgeThroughput)bridgeThroughputComboBox.getSelectedItem()).getBusLimit(), 1, 10, 1));
	
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
		bridgeThroughputComboBox.addActionListener(this);
		
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
		EmptyBorder border = (EmptyBorder) BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS ,BORDER_THICKNESS ,BORDER_THICKNESS);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout(BORDER_THICKNESS, BORDER_THICKNESS));
		mainPanel.setBorder(border);
		
		
		JPanel northPanel = new JPanel(new GridLayout(1, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		
		JPanel northEastPanel = new JPanel();
		northEastPanel.setLayout(new GridLayout(4, 1, BORDER_THICKNESS, BORDER_THICKNESS));
		northEastPanel.setBorder(border);
		northEastPanel.add(busesInQueueLabel);
		northEastPanel.add(busesInQueueTextField);
		northEastPanel.add(busesOnBridgeLabel);
		northEastPanel.add(busesOnBridgeTextField);
		northPanel.add(northEastPanel);
		
		JPanel northWestPanel = new JPanel();
		northWestPanel.setLayout(new GridLayout(3, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		northWestPanel.setBorder(border);
		northWestPanel.add(trafficIntensityLabel);
		northWestPanel.add(trafficIntensitySlider);
		northWestPanel.add(bridgeThroughputLabel);
		northWestPanel.add(bridgeThroughputComboBox);
		northWestPanel.add(maxBusesOnBridgeLabel);
		northWestPanel.add(maxBusesOnBridgeSpinner);
		northPanel.add(northWestPanel);
		
		
		JPanel centerPanel = new JPanel(new GridLayout(1, 2, BORDER_THICKNESS, BORDER_THICKNESS));
		centerPanel.setBorder(border);
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
		new Thread(simulationManager, "SIMULATION MANAGER").start();
		new Thread(drawPanel, "DRAW PANEL").start();
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
		
		if(eSource == bridgeThroughputComboBox) {
			updateMaxBusesOnBridgeSpinner();
			updateSimulationSettings();
		}
		
	}
	
	@Override
	public void stateChanged(ChangeEvent event) {
		Object eSource = event.getSource();
		
		if(eSource == trafficIntensitySlider) {
			setSimulationTrafficFactor();
		}
		
		if(eSource == maxBusesOnBridgeSpinner) {
			updateBridgeThroughput();
			updateSimulationSettings();
		}
		
	}
	
	private void showAuthorInfo() {
		JOptionPane.showMessageDialog(this, AUTHOR_INFO, "Informacje o autorze", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAppInfo() {
		JOptionPane.showMessageDialog(this, APP_INFO, "Informacje o programie", JOptionPane.INFORMATION_MESSAGE);
	}

	private void updateSimulationSettings() {
		BridgeThroughput bridgeThroughput = (BridgeThroughput) bridgeThroughputComboBox.getSelectedItem(); 
		simulationManager.setBridgeThroughput(bridgeThroughput);
	}

	private void updateBridgeThroughput() {
		int busLimit = (int) maxBusesOnBridgeSpinner.getValue();
		((BridgeThroughput) bridgeThroughputComboBox.getSelectedItem()).setbusLimit(busLimit);		
	}

	private void setSimulationTrafficFactor(){
		int trafficIntensity = trafficIntensitySlider.getValue();
		simulationManager.setBusSpawnMaxDelay(trafficIntensity);
	}

	private void updateMaxBusesOnBridgeSpinner() {
		BridgeThroughput bt = (BridgeThroughput) bridgeThroughputComboBox.getSelectedItem(); 
		boolean enabled = (bt == BridgeThroughput.MANY_BUSES_BOTH_WAYS || bt == BridgeThroughput.MANY_BUSES_ONE_WAY);
		maxBusesOnBridgeSpinner.setEnabled(enabled);
		maxBusesOnBridgeLabel.setEnabled(enabled);
		
		maxBusesOnBridgeSpinner.setValue(bt.getBusLimit());
	}
}
