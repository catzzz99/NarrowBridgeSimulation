import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class LogPanel extends JScrollPane {
	private static final long serialVersionUID = -1665608884566053419L;

	private static JTextArea logTextArea = new JTextArea();
	
	public LogPanel() {
		super(logTextArea);
		setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		setEnabled(false);
		setAutoscrolls(true);
	}
	
	public synchronized void addLog(String log) {
		logTextArea.setText(log + "\n" + logTextArea.getText());
	}
	
}
