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
	JButton saveAsBtn = new JButton("另存"), saveNewBtn = new JButton("新增"), delBtn = new JButton("刪除"), editBtn = new JButton("編輯"), cancelBtn = new JButton("取消");
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
		//驗證
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
				JOptionPane.showMessageDialog(this,"日期格式錯誤","",JOptionPane.INFORMATION_MESSAGE);
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
		saveNewBtn.setText("新增");
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
		return JOptionPane.showConfirmDialog(gui.this,"確定要取消編輯？","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION?true:false;
	}
	private void startEdit()
	{
		saveNewBtn.setText("儲存");
		delBtn.setEnabled(false);
		editBtn.setEnabled(false);
		cancelBtn.setEnabled(true);
		setOptionsEditable(true);
		isEditing = true;
	}
	public gui()
	{
		super("公告版-伺服器端");
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
					if(!askCancelEdit())//詢問是否放棄編輯
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
		
		//按鈕初始化
		setOptionsEditable(false);
		cancelBtn.setEnabled(false);
		editBtn.setEnabled(false);
		delBtn.setEnabled(false);
		saveAsBtn.setEnabled(false);
		
		//按鈕事件
		
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
			if(JOptionPane.showConfirmDialog(gui.this,"確定要刪除？","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION)
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
			if(e.getActionCommand().equals("新增"))
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
			else if(e.getActionCommand().equals("儲存"))
			{
				if(isAddNew)
				{
					addNewAnnouncement();
				}
				else
				{
					if(!isDateOK(true)) return;
					announcement editingAnno = main.announcements.get(jt.getValueAt(jt.getSelectedRow(),0));
					//更改editingAnno內容
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
				JOptionPane.showMessageDialog(this,"必須先選擇一個目標","",JOptionPane.ERROR_MESSAGE);
			}
		});
		//文字框初始化
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
		
		//主面板
		Container c = getContentPane();
		c.setLayout(new BorderLayout(15, 15));
		JScrollPane jsp = new JScrollPane(jt);
		
		JPanel controls = new JPanel();
		controls.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2, 5 , 2, 5);
		gbc.ipadx = gbc.ipady = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		/**第一行**/
		gbc.gridx = 0;
		gbc.gridy = 0;
		controls.add(new JLabel("標題"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(titleText,gbc);
		/**第一行結束**/
		
		/**第二行**/
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		controls.add(new JLabel("來源"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(sourceText,gbc);
		/**第二行結束**/
		
		/**第三行**/
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		controls.add(new JLabel("啟動時間"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(activeText,gbc);
		/**第三行結束**/
		
		/**第四行**/
		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		controls.add(new JLabel("死亡時間"),gbc);
		
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		controls.add(deadText,gbc);
		/**第四行結束**/
		
		/**第五行**/
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
		/**第五行結束**/
		
		/**內容版面**/
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
		contentPanel.add(new JLabel("內容"),gbc);
		
		gbc.gridy = 1;
		gbc.weighty = 1.0;
		gbc.fill = GridBagConstraints.BOTH;
		contentPanel.add(new JScrollPane(content),gbc);
		/**內容版面結束**/
		
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
					if(JOptionPane.showConfirmDialog(gui.this,"確定要結束程式？","",JOptionPane.OK_CANCEL_OPTION) == JOptionPane.YES_OPTION)
					{
						isClosing = true;
						System.out.println("正要結束程式...");
						System.out.println("正在儲存資料");
						main.save();
						System.out.println("正在停止Server");
						network.stop();
						System.out.println("正在結束程式...");
						System.exit(0);
					}
					else
					{
						return;
					}
				}
				else
				{
					JOptionPane.showMessageDialog(null,"正在關閉中了啦","",JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		setVisible(true);
	}
}