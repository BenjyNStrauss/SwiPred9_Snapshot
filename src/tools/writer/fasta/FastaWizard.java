package tools.writer.fasta;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import tools.DataSource;

/**
 * This class uses javax.swing instead of JavaFX for a reason!
 * That reason being so that this wizard can launch outside of GUI mode!
 * 
 * TODO: Does not show up for some reason!
 * @author Benjamin Strauss
 *
 */

class FastaWizard extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JPanel rootPanel = new JPanel();
	
	private final JPanel fastaSrcPanel = new JPanel();
	private final JPanel saveNamePanel = new JPanel();
	private final JPanel headerPanel = new JPanel();
	private final JPanel sequencePanel = new JPanel();
	private final JPanel confirmPanel = new JPanel();
	
	private final JComboBox<DataSource> fastaSrc = new JComboBox<DataSource>();
	private final JTextField saveName = new JTextField();
	private final JTextArea header = new JTextArea();
	private final JTextArea sequence = new JTextArea();
	private final JButton confirm = new JButton("OK");
	
	protected FastaWizard() { this(100, 100); }
	
	public FastaWizard(int width, int height) {
		super("New FASTA Wizard");
		setSize(width, height);
		setLayout(new FlowLayout());
		
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		add(rootPanel);
		
		saveNamePanel.add(new JLabel("Save as: "));
		saveNamePanel.add(saveName);
		fastaSrcPanel.add(new JLabel("Source: "));
		for(DataSource ds: DataSource.values()) {
			if(ds != DataSource.UNSPECIFIED) { fastaSrc.addItem(ds); }
		}
		
		fastaSrcPanel.add(fastaSrc);
		headerPanel.add(new JLabel("Fasta header: "));
		headerPanel.add(header);
		sequencePanel.add(new JLabel("Sequence: "));
		sequencePanel.add(sequence);
		confirmPanel.add(confirm);
		
		rootPanel.add(saveNamePanel);
		rootPanel.add(fastaSrcPanel);
		rootPanel.add(headerPanel);
		rootPanel.add(sequencePanel);
		rootPanel.add(confirmPanel);
		
		confirm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				notifyAll();
			}
		});
		
		setVisible(true);
	}
	
	public String getHeader() { 
		return header.getText();
	}
	
	public String getSaveName() { 
		return saveName.getText();
	}
	
	public String getSequence() { 
		return sequence.getSelectedText();
	}
	
	public DataSource getSrc() { 
		return (DataSource) fastaSrc.getSelectedItem();
	}
}
