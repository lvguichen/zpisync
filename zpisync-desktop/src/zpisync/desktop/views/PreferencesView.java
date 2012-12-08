package zpisync.desktop.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import zpisync.desktop.App;
import zpisync.desktop.Resources;
import zpisync.desktop.controllers.AppController;

@SuppressWarnings("serial")
public class PreferencesView extends JFrame {

	private static final Logger log = Logger.getLogger(PreferencesView.class.getName());

	private AppController app;
	private JTable tblDevices;
	private JTable tblFiles;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App.setupLookAndFeel();
					PreferencesView window = new PreferencesView();
					window.setDefaultCloseOperation(EXIT_ON_CLOSE);
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PreferencesView() {
		this(AppController.NULL);
	}

	public PreferencesView(AppController app) {
		this.app = app;
		initialize();
	}

	// @formatter:off
	
	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		setIconImage(Resources.getAppIcon());
		setLocationRelativeTo(null);
		setTitle("ZpiSync Preferences");
		setSize(415, 463);

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);

		JButton btnOk = new JButton("OK");
		btnOk.setPreferredSize(new Dimension(75, 24));
		btnOk.setSize(new Dimension(200, 200));
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.setPreferredSize(new Dimension(75, 24));
		panel.add(btnCancel);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBorder(new EmptyBorder(4, 4, 0, 4));
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBackground(SystemColor.window);
		tabbedPane.addTab("Settings", null, panel_3, null);
		
		JLabel lblSecurityPin = new JLabel("Security PIN:");
		
		JButton btnGenerateNewPin = new JButton("Generate new PIN");
		
		JLabel lblPin = new JLabel("81X301Z");
		lblPin.setHorizontalAlignment(SwingConstants.CENTER);
		lblPin.setBorder(new LineBorder(SystemColor.controlHighlight));
		lblPin.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblPin.setOpaque(true);
		lblPin.setBackground(new Color(255, 250, 205));
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addComponent(lblSecurityPin)
							.addGap(10)
							.addComponent(lblPin, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
							.addGap(10))
						.addGroup(Alignment.TRAILING, gl_panel_3.createSequentialGroup()
							.addComponent(btnGenerateNewPin)
							.addContainerGap())))
		);
		gl_panel_3.setVerticalGroup(
			gl_panel_3.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addContainerGap()
							.addComponent(lblPin))
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGap(24)
							.addComponent(lblSecurityPin)))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnGenerateNewPin)
					.addContainerGap(278, Short.MAX_VALUE))
		);
		panel_3.setLayout(gl_panel_3);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.window);
		tabbedPane.addTab("Devices", null, panel_1, null);
		
		JLabel lblListOfKnown = new JLabel("List of known devices:");
		
		JButton btnDeviceInfo = new JButton("Information");
		
		JButton btnForget = new JButton("Forget");
		
		JScrollPane scrollPane = new JScrollPane();
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING)
							.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
							.addGroup(gl_panel_1.createSequentialGroup()
								.addComponent(btnDeviceInfo)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnForget)))
						.addComponent(lblListOfKnown))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblListOfKnown)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnDeviceInfo)
						.addComponent(btnForget))
					.addContainerGap())
		);
		gl_panel_1.linkSize(SwingConstants.VERTICAL, new Component[] {btnDeviceInfo, btnForget});
		gl_panel_1.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnDeviceInfo, btnForget});
		
		tblDevices = new JTable();
		scrollPane.setViewportView(tblDevices);
		tblDevices.setModel(new DefaultTableModel(
			new Object[][] {
				{"0", "localhost", "now"},
				{null, null, null},
			},
			new String[] {
				"UUID", "Name", "Last sync"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, String.class, String.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		panel_1.setLayout(gl_panel_1);

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(SystemColor.window);
		tabbedPane.addTab("Files", null, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane_1 = new JScrollPane();
		panel_2.add(scrollPane_1);
		
		tblFiles = new JTable();
		tblFiles.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null},
			},
			new String[] {
				"", "Path", "Last Modified"
			}
		) {
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		tblFiles.getColumnModel().getColumn(0).setResizable(false);
		tblFiles.getColumnModel().getColumn(0).setPreferredWidth(20);
		tblFiles.getColumnModel().getColumn(0).setMinWidth(20);
		tblFiles.getColumnModel().getColumn(0).setMaxWidth(20);
		scrollPane_1.setViewportView(tblFiles);
	}
	
	// @formatter:on
}
