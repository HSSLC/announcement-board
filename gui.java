package net.incognitas.announcementBoard;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

class gui extends JFrame
{
	JButton saveAsBtn = new JButton("�t�s"), saveNewBtn = new JButton("�s�W"), delBtn = new JButton("�R��"), editBtn = new JButton("�s��"), cancelBtn = new JButton("����");
	JTextField titleText = new JTextField(), sourceText = new JTextField();
	JFormattedTextField activeText = new JFormattedTextField(), deadText = new JFormattedTextField();
	JTextArea content = new JTextArea();
	JTextComponent[] options = {titleText,sourceText,content};
	JFormattedTextField[] jftfOptions = {activeText,deadText};
	ATM atm;
	boolean isEditing = false;
	boolean isEventChangingSel = false;
	int lastSelRow = 0;
	JTable jt;
	boolean isAddNew = false;
	private boolean isClosing = false;
	private class ATM extends DefaultTableModel
	{
		public void addRow(announcement data)
		{
			Object[] dataObj = {data.id,data.title,data.source,data.content,data.activeTime,data.deadTime,data.createTime};
			super.addRow(dataObj);
		}
		public void replaceRow(int row,announcement data)
		{
			Object[] dataObj = {data.id,data.title,data.source,data.content,data.activeTime,data.deadTime,data.createTime};
			int i = 0;
			for(Object j : dataObj)
			{
				setValueAt(j,row,i);
				i++;
			}
		}
	}
	private void addNewAnnouncement()
	{
		//����
		if(!isDateOK(true)) return;
		isAddNew = false;
		announcement newAnno = new announcement();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX");
		Date ct = new Date(System.currentTimeMillis());
		newAnno.createTime = df.format(ct);
		newAnno.id = Long.toString(System.currentTimeMillis());
		setAnnouncementAttr(newAnno);
		main.announcements.put((String)newAnno.getJSONObject().get("id"),newAnno);
		main.save();
		isEventChangingSel = true;
		atm.addRow(newAnno);
		isEventChangingSel = false;
		cancelEdit();
		jt.addRowSelectionInterval(atm.getRowCount()-1,atm.getRowCount()-1);
	}
	private boolean isDateOK(boolean ask)
	{
		String at = activeText.getText(), dt = deadText.getText();
		// String datePattern = "[0-9]{4}-((0[1-9])|(1[0-2]))-((0[1-9])|([1-2][0-9])|(3[0-1]))T(([0-1][0-9])|(2[0-4])):([0-5][0-9]):([0-5][0-9]).([0-9]{3})\\+0800";
		for(JTextComponent jtc : jftfOptions)
		{
			String str = jtc.getText();
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX");
			Date d;
			try
			{
				d = df.parse(str);
			}
			catch(Exception e)
			{
				JOptionPane.showMessageDialog(this,"����榡���~","",JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			jtc.setText(df.format(d));
		}
		return true;
	}
	private void setAnnouncementAttr(announcement anno)
	{
		anno.title = titleText.getText();
		anno.source = sourceText.getText();
		anno.content = content.getText();
		anno.activeTime = activeText.getText();
		anno.deadTime = deadText.getText();
	}
	private void setOptionsEditable(boolean editable)
	{
		for(JTextComponent jtc : options)
		{
			jtc.setEditable(editable);
		}
		for(JFormattedTextField jftf : jftfOptions)
		{
			jftf.setEditable(editable);
		}
	}
	private void setOptionsValue()
	{
		if(jt.getSelectedRow() != -1)
		{
			announcement selectAnno = main.announcements.get(jt.getValueAt(jt.getSelectedRow(),0));
			titleText.setText(selectAnno.title);
			sourceText.setText(selectAnno.source);
			activeText.setText(selectAnno.activeTime);
			deadText.setText(selectAnno.deadTime);
			content.setText(selectAnno.content);
		}
		else
		{
			setOptionsToEmpty();
		}
	}
	private void setOptionsToEmpty()
	{
		for(JTextComponent jtc : options)
		{
			jtc.setText("");
		}
		for(JFormattedTextField jftf : jftfOptions)
		{
			jftf.setText("00000000000000000");
		}
	}
	private void cancelEdit()
	{
		saveNewBtn.setText("�s�W");
		if(jt.getSelectedRow() != -1)
		{
			delBtn.setEnabled(true);
			editBtn.setEnabled(true);
		}
		cancelBtn.setEnabled(false);
		setOptionsEditable(false);
		isEditing = false;
		isAddNew = false;
	}
	private boolean askCancelEdit()
	{
		return JOptionPane.showConfirmDialog(gui.this,"�T�w�n�����s��H","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION?true:false;
	}
	private void startEdit()
	{
		saveNewBtn.setText("�x�s");
		delBtn.setEnabled(false);
		editBtn.setEnabled(false);
		cancelBtn.setEnabled(true);
		setOptionsEditable(true);
		isEditing = true;
	}
	public gui()
	{
		super("���i��-���A����");
		atm = new ATM();
		jt = new JTable(atm)
		{
			@Override
			public boolean isCellEditable(int row,int col)
			{
				return false;
			}
			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				super.valueChanged(e);
				if(isEventChangingSel)
				{
					isEventChangingSel = false;
					return;
				}
				if(isEditing && lastSelRow != jt.getSelectedRow() || isAddNew)
				{
					if(!askCancelEdit())//�߰ݬO�_���s��
					{
						isEventChangingSel = true;
						if(!isAddNew)
						{
							addRowSelectionInterval(lastSelRow,lastSelRow);
						}
						else
						{
							removeRowSelectionInterval(jt.getSelectedRow(),jt.getSelectedRow());
						}
						return;
					}
					else
					{
						cancelEdit();
					}
				}
				if(getSelectedRow() != -1)
				{
					editBtn.setEnabled(true);
					delBtn.setEnabled(true);
					saveAsBtn.setEnabled(true);
					lastSelRow = getSelectedRow();
					setOptionsValue();
				}
				else
				{
					lastSelRow = -1;
					editBtn.setEnabled(false);
					delBtn.setEnabled(false);
					saveAsBtn.setEnabled(false);
					setOptionsToEmpty();
				}
			}
		};
		jt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		String[] colName = {"ID","Title","Source","Content","ActiveTime","DeadTime","CreateTime"};
		for(String col : colName)
		{
			atm.addColumn(col);
		}
		main.announcements.forEach((k,v) ->
		{
			atm.addRow(v);
		});
		
		//���s��l��
		setOptionsEditable(false);
		cancelBtn.setEnabled(false);
		editBtn.setEnabled(false);
		delBtn.setEnabled(false);
		saveAsBtn.setEnabled(false);
		
		//���s�ƥ�
		
		editBtn.addActionListener(e ->
		{
			startEdit();
		});
		
		cancelBtn.addActionListener(e ->
		{
			if(askCancelEdit())
			{
				cancelEdit();
				setOptionsValue();
			}
		});
		
		delBtn.addActionListener(e ->
		{
			if(JOptionPane.showConfirmDialog(gui.this,"�T�w�n�R���H","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION)
			{
				String id = (String)jt.getValueAt(jt.getSelectedRow(),0);
				main.announcements.remove(id);
				int sel = jt.getSelectedRow();
				atm.removeRow(jt.getSelectedRow());
				try
				{
					jt.addRowSelectionInterval(sel,sel);
				}
				catch(Exception e2)
				{
					try
					{
						jt.addRowSelectionInterval(sel-1,sel-1);
					}
					catch(Exception e3) {}
				}
				main.save();
			}
		});
		
		saveNewBtn.addActionListener(e ->
		{
			if(e.getActionCommand().equals("�s�W"))
			{
				isAddNew = true;
				if(jt.getSelectedRow() >= 0)
				{
					isEventChangingSel = true;
					jt.removeRowSelectionInterval(jt.getSelectedRow(),jt.getSelectedRow());
				}
				setOptionsToEmpty();
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX");
				Date ct = new Date(System.currentTimeMillis());
				String strDate = df.format(ct);
				for(JTextComponent jtc : jftfOptions)
				{
					jtc.setText(strDate);
				}
				startEdit();
			}
			else if(e.getActionCommand().equals("�x�s"))
			{
				if(isAddNew)
				{
					addNewAnnouncement();
				}
				else
				{
					if(!isDateOK(true)) return;
					announcement editingAnno = main.announcements.get(jt.getValueAt(jt.getSelectedRow(),0));
					//���editingAnno���e
					setAnnouncementAttr(editingAnno);
					main.save();
					atm.replaceRow(jt.getSelectedRow(),editingAnno);
					cancelEdit();
				}
			}
		});
		saveAsBtn.addActionListener(e ->
		{
			if(jt.getSelectedRow() != -1)
			{
				addNewAnnouncement();
			}
			else
			{
				JOptionPane.showMessageDialog(this,"��������ܤ@�ӥؼ�","",JOptionPane.ERROR_MESSAGE);
			}
		});
		//��r�ت�l��
		try
		{
			MaskFormatter timeMfAt = new MaskFormatter("####-##-##T##:##:##.###+0800");
			MaskFormatter timeMfDt = new MaskFormatter("####-##-##T##:##:##.###+0800");
			timeMfAt.setPlaceholderCharacter('_');
			timeMfDt.setPlaceholderCharacter('_');
			activeText.setFormatterFactory(new DefaultFormatterFactory(timeMfAt));
			deadText.setFormatterFactory(new DefaultFormatterFactory(timeMfDt));
			setOptionsToEmpty();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//�D���O
		Container c = getContentPane();
		c.setLayout(new BorderLayout(15, 15));
		JScrollPane jsp = new JScrollPane(jt);
		
		JPanel controls = new JPanel();
		controls.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5 , 2, 5);
		gbc.ipadx = gbc.ipady = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		/**�Ĥ@��**/
		gbc.gridx = 0;
		gbc.gridy = 0;
		controls.add(new JLabel("���D"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(titleText,gbc);
		/**�Ĥ@�浲��**/
		
		/**�ĤG��**/
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		controls.add(new JLabel("�ӷ�"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(sourceText,gbc);
		/**�ĤG�浲��**/
		
		/**�ĤT��**/
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		controls.add(new JLabel("�Ұʮɶ�"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(activeText,gbc);
		/**�ĤT�浲��**/
		
		/**�ĥ|��**/
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		controls.add(new JLabel("���`�ɶ�"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(deadText,gbc);
		/**�ĥ|�浲��**/
		
		/**�Ĥ���**/
		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		
		JPanel jp = new JPanel();
		jp.setLayout(new BoxLayout(jp,BoxLayout.X_AXIS));
		jp.add(Box.createHorizontalGlue());
		jp.add(saveAsBtn);
		jp.add(Box.createHorizontalStrut(10));
		jp.add(saveNewBtn);
		jp.add(Box.createHorizontalStrut(10));
		jp.add(delBtn);
		jp.add(Box.createHorizontalStrut(10));
		jp.add(editBtn);
		jp.add(Box.createHorizontalStrut(10));
		jp.add(cancelBtn);
		
		
		controls.add(jp,gbc);
		/**�Ĥ��浲��**/
		
		/**���e����**/
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5 , 2, 5);
		gbc.ipadx = gbc.ipady = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.NORTHWEST;
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		contentPanel.add(new JLabel("���e"),gbc);
		
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		contentPanel.add(new JScrollPane(content),gbc);
		/**���e��������**/
		
		JPanel north = new JPanel();
		north.setLayout(new BorderLayout());
		north.add(controls,BorderLayout.WEST);
		north.add(contentPanel,BorderLayout.CENTER);
		c.add(new JSplitPane(JSplitPane.VERTICAL_SPLIT,north,jsp),BorderLayout.CENTER);
		// c.add(jsp);
		
		
		setSize(600,400);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((scrSize.width - getWidth()) / 2, (scrSize.height - getHeight()) / 2);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				if(!isClosing)
				{
					if(JOptionPane.showConfirmDialog(gui.this,"�T�w�n�����{���H","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION)
					{
						isClosing = true;
						System.out.println("���n�����{��...");
						System.out.println("���b�x�s���");
						main.save();
						System.out.println("���b����Server");
						network.stop();
						System.out.println("���b�����{��...");
						System.exit(0);
					}
					else
					{
						return;
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null,"���b�������F��","",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		setVisible(true);
	}
}