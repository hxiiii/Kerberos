package Demo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

public class ClientDemo extends JFrame {
	public JList list;
	public DefaultListModel listModel;
	public JScrollPane scrollPane;
	public ClientDemo(){
		listModel=new DefaultListModel();
		listModel.addElement("All Online Users");
		list=new JList(listModel);
		list.setSelectionBackground(Color.red);
		scrollPane=new JScrollPane(list);
		add(scrollPane,BorderLayout.CENTER);
		setTitle("Ö÷Ò³Ãæ");
		list.setBackground(Color.cyan);
		//list.setBorder(BorderFactory.createBevelBorder(1));
		pack();
		setSize(300,500);
		//setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
	}
	public static void main(String[] args) {
		new ClientDemo();
	}
}
