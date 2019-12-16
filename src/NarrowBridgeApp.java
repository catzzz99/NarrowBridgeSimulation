import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class NarrowBridgeApp extends JFrame implements ActionListener{

	private static final long serialVersionUID = 773440677198457816L;
	
	private static final String APP_TITLE = "Narrow Bridge Simulation";
	private static final String AUTHOR_INFO = "Autor: Ja";
	private static final String APP_INFO = "Symulacja";

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
	private JLabel trafficIntensityLabel = new JLabel("Wspó³czynnik natê¿enia ruchu:", JLabel.RIGHT);
	private JLabel bridgeThroughputLabel = new JLabel("Tryb pracy mostu:", JLabel.RIGHT);
	private JLabel maxBusesOnBridgeLabel = new JLabel("Liczba busów do przepuszczenia:", JLabel.RIGHT);
	
	
	private JTextField busesInQueueTextField = new JTextField("Kolejka jest pusta");
	private JTextField busesOnBridgeTextField = new JTextField("Most jest pusty");
	private JSlider trafficIntensitySlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 10);
	private JComboBox<BridgeThroughput> bridgeThroughputComboBox = new JComboBox<>(BridgeThroughput.values());
	private JSpinner maxBusesOnBridgeSpinner = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));
	
	
	private JTextArea logTextArea = new JTextArea();
	private JScrollPane logScrollPane = new JScrollPane(logTextArea);
	private DrawPanel drawPanel = new DrawPanel();
	
	private SimulationManager simulationManager;
	
	public NarrowBridgeApp() {
		super(APP_TITLE);
		setSize(1280, 720);
		setResizable(true);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		UIManager.put("OptionPane.messageFont", new Font("Monospaced", Font.BOLD, FONT_SIZE));
		
		addActionListeners();
		setInitialControlsProporties();
		createWindowLayout();
		createMenuBar();
		
		setVisible(true);
		
		startSimulation();
	}

	private void addActionListeners() {
		authorInfoMenuItem.addActionListener(this);
		appInfoMenuItem.addActionListener(this);

	}
	
	private void setInitialControlsProporties() {
		busesInQueueTextField.setEditable(false);
	
		busesOnBridgeTextField.setEditable(false);
		
		trafficIntensitySlider.setPaintLabels(true);
		trafficIntensitySlider.setPaintTicks(true);
		trafficIntensitySlider.setPaintTrack(true);
		trafficIntensitySlider.setMajorTickSpacing(25);
		trafficIntensitySlider.setMinorTickSpacing(5);
		
		maxBusesOnBridgeLabel.setEnabled(false);
		maxBusesOnBridgeSpinner.setEnabled(false);
	}

	private void createWindowLayout() {
		
		logScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		EmptyBorder border = (EmptyBorder) BorderFactory.createEmptyBorder(BORDER_THICKNESS, BORDER_THICKNESS ,BORDER_THICKNESS ,BORDER_THICKNESS);
		//LineBorder border = (LineBorder) BorderFactory.createLineBorder(Color.DARK_GRAY, BORDER_THICKNESS);
		
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
		centerPanel.add(logScrollPane);
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
		simulationManager = new SimulationManager(logTextArea, drawPanel);
		new Thread(simulationManager, "SIMULATION MANAGER").start();
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
		
		
	}
	
	private void showAuthorInfo() {
		JOptionPane.showMessageDialog(this, AUTHOR_INFO, "Informacje o autorze", JOptionPane.INFORMATION_MESSAGE);
	}

	private void showAppInfo() {
		JOptionPane.showMessageDialog(this, APP_INFO, "Informacje o programie", JOptionPane.INFORMATION_MESSAGE);
	}



}
