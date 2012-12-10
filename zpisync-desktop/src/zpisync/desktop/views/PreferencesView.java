package zpisync.desktop.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import zpisync.desktop.App;
import zpisync.desktop.IView;
import zpisync.desktop.Resources;
import zpisync.desktop.controllers.AppController;
import zpisync.desktop.models.DeviceInfoModel;
import zpisync.desktop.models.PreferencesModel;
import zpisync.shared.FileInfo;
import zpisync.shared.Util;

@SuppressWarnings("serial")
public class PreferencesView extends JFrame implements IView<PreferencesModel> {

	private static final Logger log = Logger.getLogger(PreferencesView.class.getName());

	private AppController app;
	private JTable tblDevices;
	private JTable tblFiles;
	private JLabel lblPin;
	private JTextField tfDataDir;

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

	protected void onBtnOkClicked(ActionEvent e) {
		app.saveState();
		setVisible(false);
	}

	protected void onBtnCancelClicked(ActionEvent e) {
		setVisible(false);
	}

	protected void onBtnBrowseDataDirClicked(ActionEvent e) {
		JFileChooser fc = new JFileChooser(tfDataDir.getText());
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setMultiSelectionEnabled(false);
		fc.showOpenDialog(this);
		if (fc.getSelectedFile() != null)
			tfDataDir.setText(fc.getSelectedFile().getAbsolutePath());
	}

	protected void onBtnGenerateNewPinClicked(ActionEvent e) {
		lblPin.setText(PreferencesModel.generatePin());
	}

	protected void onBtnAssociateClicked(ActionEvent e) {
		int row = tblDevices.getSelectedRow();
		if (row >= 0) {
			String udn = (String) tblDevices.getValueAt(row, 0);
			app.associate(udn);
		}
	}

	protected void onBtnDevicesRefreshClicked(ActionEvent e) {
		app.rescanDevices();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	// @formatter:off
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
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnOkClicked(e);
			}
		});
		btnOk.setPreferredSize(new Dimension(75, 24));
		btnOk.setSize(new Dimension(200, 200));
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnCancelClicked(e);
			}
		});
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
		btnGenerateNewPin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnGenerateNewPinClicked(e);
			}
		});
		
		lblPin = new JLabel("81X301Z");
		lblPin.setHorizontalAlignment(SwingConstants.CENTER);
		lblPin.setBorder(new LineBorder(SystemColor.controlHighlight));
		lblPin.setFont(new Font("Tahoma", Font.PLAIN, 32));
		lblPin.setOpaque(true);
		lblPin.setBackground(new Color(255, 250, 205));
		
		JLabel lblDataDir = new JLabel("Data Directory:");
		
		tfDataDir = new JTextField();
		tfDataDir.setEditable(false);
		tfDataDir.setColumns(10);
		
		JButton btnBrowseDataDir = new JButton("...");
		btnBrowseDataDir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnBrowseDataDirClicked(e);
			}
		});
		GroupLayout gl_panel_3 = new GroupLayout(panel_3);
		gl_panel_3.setHorizontalGroup(
			gl_panel_3.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_3.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_3.createSequentialGroup()
							.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addComponent(lblDataDir)
								.addComponent(lblSecurityPin))
							.addGroup(gl_panel_3.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_3.createSequentialGroup()
									.addGap(3)
									.addComponent(lblPin, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
									.addGap(10))
								.addGroup(gl_panel_3.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(tfDataDir, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnBrowseDataDir)
									.addContainerGap())))
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
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_3.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblDataDir)
						.addComponent(tfDataDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowseDataDir))
					.addContainerGap(249, Short.MAX_VALUE))
		);
		gl_panel_3.linkSize(SwingConstants.HORIZONTAL, new Component[] {lblSecurityPin, lblDataDir});
		panel_3.setLayout(gl_panel_3);

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(SystemColor.window);
		tabbedPane.addTab("Devices", null, panel_1, null);
		
		JLabel lblListOfKnown = new JLabel("List of known devices:");
		
		JButton btnDevicesRefresh = new JButton("Refresh");
		btnDevicesRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnDevicesRefreshClicked(e);
			}
		});
		
		JButton btnForget = new JButton("Forget");
		
		JScrollPane scrollPane = new JScrollPane();
		
		JButton btnAssociate = new JButton("Associate");
		btnAssociate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onBtnAssociateClicked(e);
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
						.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
							.addComponent(btnAssociate)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnDevicesRefresh)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnForget))
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
						.addComponent(btnDevicesRefresh)
						.addComponent(btnForget)
						.addComponent(btnAssociate))
					.addContainerGap())
		);
		gl_panel_1.linkSize(SwingConstants.VERTICAL, new Component[] {btnDevicesRefresh, btnForget});
		gl_panel_1.linkSize(SwingConstants.HORIZONTAL, new Component[] {btnDevicesRefresh, btnForget, btnAssociate});
		
		tblDevices = new JTable();
		scrollPane.setViewportView(tblDevices);
		tblDevices.setModel(new DefaultTableModel(
			new Object[][] {
				{"0", "localhost", "now"},
				{null, null, null},
			},
			new String[] {
				"UDN", "Name", "Last sync"
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
		
		JScrollPane scrollPane_1 = new JScrollPane();
		
		tblFiles = new JTable();
		tblFiles.setFillsViewportHeight(true);
		tblFiles.setModel(new DefaultTableModel(
			new Object[][] {
				{null, null, null, null},
			},
			new String[] {
				"", "Path", "Size", "Last Modified"
			}
		) {
			Class[] columnTypes = new Class[] {
				Object.class, Object.class, String.class, Object.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false, false
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
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 366, Short.MAX_VALUE)
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
	}
	// @formatter:on

	@Override
	public void modelToView(PreferencesModel model) {
		lblPin.setText(model.getPin());
		tfDataDir.setText(model.getDataDir().getAbsolutePath());

		DefaultTableModel tblDevicesModel = (DefaultTableModel) tblDevices.getModel();
		tblDevicesModel.setRowCount(0);
		for (DeviceInfoModel devInfo : model.getKnownDevices()) {
			Object[] rowData = new Object[] { devInfo.getUdn(), devInfo.getDisplayName(), devInfo.getLastSyncTime() };
			tblDevicesModel.addRow(rowData);
		}
	}

	/**
	 * Cheat a little and update file list outside modelToView for improved
	 * performance.
	 * 
	 * @param files
	 */
	public void fireFilesChanged(List<FileInfo> files) {
		DefaultTableModel tblFilesModel = (DefaultTableModel) tblFiles.getModel();
		tblFilesModel.setRowCount(0);
		for (FileInfo fileInfo : files) {
			String symbol = fileInfo.isDirectory() ? "D" : "";
			String size = Util.humanReadableByteCount(fileInfo.getSize(), false);
			Object[] rowData = new Object[] { symbol, fileInfo.getPath(), size, fileInfo.getModificationTime() };
			tblFilesModel.addRow(rowData);
		}
	}

	@Override
	public void viewToModel(PreferencesModel model) {
		model.setPin(lblPin.getText());
		model.setDataDir(new File(tfDataDir.getText()));

		model.getKnownDevices().clear();
		DefaultTableModel tblDevicesModel = (DefaultTableModel) tblDevices.getModel();
		for (Object rowObject : tblDevicesModel.getDataVector()) {
			Vector<?> rowData = (Vector<?>) rowObject;
			DeviceInfoModel devInfo = new DeviceInfoModel();
			devInfo.setUdn((String) rowData.get(0));
			devInfo.setDisplayName((String) rowData.get(1));
			devInfo.setLastModified((Date) rowData.get(2));
		}
	}
}
