package Demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import client.myActionListener;

public class MessageDemo extends JFrame{
	public JTextArea ta;
	public JPanel southPanel;
	public JPanel sendPanel;
	public JScrollPane scroll;
	public JButton button_message;
	public JButton button_file;
	public JTextArea text;
	public MessageDemo(String user_sendfor){
		ta=new JTextArea();
		ta.setEditable(false);
		ta.setLineWrap(true);
		scroll=new JScrollPane(ta);
		southPanel=new JPanel();
		sendPanel=new JPanel();
		southPanel.setPreferredSize(new Dimension(0,55));
		southPanel.setLayout(new BorderLayout());
		sendPanel.setLayout(new BorderLayout());
		//listener=new myActionListener(output,user_sendfor,text);
		button_message=new JButton("发送");
		button_file=new JButton("选择文件");
		text=new JTextArea();
		text.setLineWrap(true);
		sendPanel.add(button_message, BorderLayout.NORTH);
		sendPanel.add(button_file, BorderLayout.SOUTH);
		southPanel.add(text, BorderLayout.CENTER);
		southPanel.add(sendPanel, BorderLayout.EAST);
		add(scroll,BorderLayout.CENTER);
		add(southPanel,BorderLayout.SOUTH);
		setTitle(user_sendfor);
		pack();
		setSize(500,500);
		//setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		//setDefaultCloseOperation(3);
	}
}
