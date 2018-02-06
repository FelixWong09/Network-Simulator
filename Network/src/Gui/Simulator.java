package Gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.management.loading.PrivateClassLoader;
import javax.security.auth.x500.X500Principal;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.omg.CORBA.PRIVATE_MEMBER;

import protocols.Ethernetframe;


public class Simulator extends JPanel implements ActionListener{
	BufferedImage bgImage;
	static BufferedImage analogSignal; 
	static BufferedImage analogOne; 
	static BufferedImage analogZero; 
	static JFrame frame = new JFrame();
	final JButton pause = new JButton("Pause");
	final JButton resume = new JButton("Resume");
	final JButton detail = new JButton("Details");
	final JButton exit = new JButton("Exit");
	final JButton start = new JButton("Start");
	int prefwidth = 1293;
	int prefheig = 648;
	private int delay = 35;
	protected Timer timer;
	boolean ackDraw = false;
	boolean dupBubble = false;
	public String message = "Msg";
	String destinationMAC = "a";
	String sourceMAC = "b";
	Ethernetframe ef =  new Ethernetframe(destinationMAC.getBytes(), sourceMAC.getBytes(), 0x0800, message);
	String framePacket = ef.toString();
	String bits = stringToBinary(framePacket);
	
	private int nexthopR = 1;
	private int nexthopB = 1;

	private static int[] routerX = {239,358,940,1053,469,825,358,943};
	private static int[] routerY = {367,195,195,367,392,364,560,560};
	private static int[] labelX = {245,252,424,456,586,588,595,822,827,1008,1028};
	private static int[] labelY = {241,440,242,444,141,319,507,246,455,226,462};
	private int[] delayRed;
	private int[] delayBlue;
	private int[] pathRIndex;
	private int[] pathBIndex;
	private ArrayList<Point> pathRed;
	private ArrayList<Point> pathBlue;
	
	Ball blueBall = new Ball(35, 10, 10, Color.blue);
	Ball redBall = new Ball(35, 40, 10, Color.red);
	
	Ball ackball = new Ball(0, 0, 5, Color.red);
	Ball dupball = new Ball(0, 0, 10, Color.red);

	public Simulator(int []labelRed, int []labelBlue, int []pathRIndex, int []pathBIndex, int []delayRed, int []delayBlue){
		timer = new Timer(delay, this);
		this.setLayout(null);
		
		this.delayBlue = delayBlue;
		this.delayRed = delayRed;
		for(int i = 0; i < 11; i ++) {
			JLabel label = new JLabel();
			String str = "<html><font color='red'>" +
					String.valueOf(labelRed[i]) + 
					"</font>, " +
					"<font color='blue'>" +
					String.valueOf(labelBlue[i]) + 
					"</font></html>";
			label.setText(str);
			label.setBounds(labelX[i]+5, labelY[i]+5, 50, 35);
			this.add(label);
		}
		
		this.pathRed = new ArrayList<Point>();
		this.pathRed.add(new Point(38, 88));
		this.pathRed.add(new Point(38, 362));
		for(int i = 0; i < pathRIndex.length; i ++) {
			this.pathRed.add(new Point(routerX[pathRIndex[i]], routerY[pathRIndex[i]]));
		}
		this.pathRed.add(new Point(1270, 366));
		this.pathRed.add(new Point(1270, 118));
		
		this.pathBlue = new ArrayList<Point>();
		this.pathBlue.add(new Point(38, 88));
		this.pathBlue.add(new Point(38, 362));
		for(int i = 0; i < pathBIndex.length; i ++) {
			this.pathBlue.add(new Point(routerX[pathBIndex[i]], routerY[pathBIndex[i]]));
		}
		this.pathBlue.add(new Point(1270, 366));
		this.pathBlue.add(new Point(1270, 118));
		for (int i = 0; i < pathBlue.size(); i++) {
			System.out.println("x= "+pathBlue.get(i).getX() + "; Y= " + pathBlue.get(i).getY()); 
		}
		
		ackball.setX(pathRed.get(3).getX());
		ackball.setY(pathRed.get(3).getY());
		
		start.setBounds(420, 5, 72, 33);
		pause.setBounds(500, 5, 72, 33);
		resume.setBounds(580, 5, 72, 33);
		detail.setBounds(660, 5, 72, 33);
		exit.setBounds(740, 5, 72, 33);
		this.add(start);
		this.add(pause);
		this.add(resume);
		this.add(detail);
		this.add(exit);
		start.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				timer.start();
				System.out.println("start");
			}
		});
		pause.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.out.println("pause");
				timer.stop();
			}
		});
		resume.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub

				System.out.println("resume");
				timer.start();
			}
		});
		detail.addActionListener(new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
				// To free up processor time
					Thread.sleep(200);
				} catch (InterruptedException e1) {
					System.out.println("Woke up prematurely");
				}
				double x1 = blueBall.getX();
				double y1 = blueBall.getY();
				double x2 = redBall.getX();
				double y2 = redBall.getY();
				int type1 = typeDetermined(x1,y1) ;
				int type2 = typeDetermined(x1,y2);
				
			
				DetailFrame df1 = new DetailFrame(type1,message,1);
				DetailFrame df2 = new DetailFrame(type2,message,2);
			}
		});
		exit.addActionListener(new ActionListener(){
	
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		try {
			bgImage = ImageIO.read(getClass().getResource("/img/bgV3.png"));
			analogOne = ImageIO.read(getClass().getResource("/img/analogOne.png"));
			analogZero = ImageIO.read(getClass().getResource("/img/analogZero.png"));
		} catch (IOException ex) {
			System.out.println(ex.toString());
		}
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent me) { 
	            System.out.println(me.getPoint());
	        }
		});
	}
	
	public void actionPerformed(ActionEvent e)
	   // will run when the timer fires
	   {
		if (blueBall.checkPos(pathBlue, nexthopB)) {
			if (nexthopB <= delayBlue.length -1) {
				nexthopB += 1;
			}
			System.out.println("blue: nexthop"+ nexthopB);
			System.out.println("x=" + blueBall.getX() + "; y= " + blueBall.getY());

		}
		blueBall.setNexthop(pathBlue, nexthopB, delayBlue[nexthopB-1]);
		
		if (redBall.checkPos(pathRed, nexthopR)) {
			if (nexthopR <= delayRed.length -1) {
				nexthopR += 1;
				if (nexthopR == 3) {
					dupBubble = true;
					System.out.println("dup");
					dupball.setX(redBall.getX());
					dupball.setY(redBall.getY());
					//dupball.update();
					
				} else if (nexthopR == 4) {
					ackDraw = true;
					System.out.println("ack");
				}
			}
			
		}
		redBall.setNexthop(pathRed, nexthopR, delayRed[nexthopR-1]);
		
		if (ackDraw) {
			ackball.setNexthopAck(pathRed, 3, delayRed[3]);
			ackball.update();
		}
		
		blueBall.update();
		redBall.update();
		repaint();
	   }
	
	public int typeDetermined(double x,double y) {
		int type = 1;
		if (y >= 125 && y < 190 && (x <=50 || x >= 1075) )  {//Application layer
			type = 1;
		} else if (x<1110 && x>165) {//sine wave
			type = 6;
		} else if (y >= 190 && y < 215 ) {//Transport layer
			type = 2;
		} else if (y >= 215 && y < 240) {//Network layer
			type = 3;
		} else if (y >= 240 && y < 265) {//Data link layer
			type = 4;
		} else if (y >= 265 && y < 290) {//Physical layer
			type = 5;
		}  
//		else {
//			type = 6;
//		}
		return type;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(prefwidth, prefheig);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(bgImage, 0, 40, getWidth(), getHeight()-40, this);
		Graphics2D g2d = (Graphics2D) g;
		if (dupBubble) {
			
			dupball.draw(g2d);
			if (ackball.checkACK(pathRed, 2)) {
				dupBubble = false;
				ackDraw = false;
			}
		}
		if (ackDraw) {
			
			ackball.draw(g2d);
		}
		redBall.draw(g2d);
		blueBall.draw(g2d);
		
	}
	private class DetailFrame extends javax.swing.JFrame {
		
		public DetailFrame(int type, String msg, int frameNo) {
			super();
			Random rand = new Random();
			String bubble = "";
			
			if (frameNo == 1) {
				bubble = "Blue";
			} else {
				bubble = "Red";
			}
			if (type == 1) {
				this.setTitle("Application Layer | " + bubble);
				this.getContentPane().add(new JLabel(msg,SwingConstants.CENTER), BorderLayout.CENTER);
				this.setPreferredSize(new Dimension(350, 100));
			} else if (type == 2) {
				msg = "32, 64 |" + msg;
				this.setTitle("Transport Layer | " + bubble);
				this.getContentPane().add(new JLabel(msg,SwingConstants.CENTER), BorderLayout.CENTER);
				this.setPreferredSize(new Dimension(350, 100));
			} else if (type == 3) {
				msg = "1, 2 | 32, 64 |" + msg;
				this.setTitle("Network Layer | " + bubble);
				this.getContentPane().add(new JLabel(msg,SwingConstants.CENTER), BorderLayout.CENTER);
				this.setPreferredSize(new Dimension(350, 100));
			} else if (type == 4) {
				msg = framePacket;
				this.setTitle("Datalink Layer | " + bubble);
				this.getContentPane().add(new JLabel(msg,SwingConstants.CENTER), BorderLayout.CENTER);
				this.setPreferredSize(new Dimension(800, 100));
			} else if (type == 5) {
				msg = bits;
				this.setTitle("Physical Layer | " + bubble);
				//JScrollPane scroller = new JScrollPane(new JLabel(msg));
			    //scroller.setAutoscrolls(true);
				JTextArea textArea = new JTextArea();
				textArea.setText(msg);
			    this.getContentPane().add(textArea, BorderLayout.CENTER);
			    this.setPreferredSize(new Dimension(800, 100));
			} else if (type == 6) {
				BufferedImage image = getanalogSignal(bits);
				JScrollPane scroller = new JScrollPane(new JLabel(new ImageIcon(image)));
			    scroller.setAutoscrolls(true);
			    scroller.setPreferredSize(new Dimension(800, 200));
			    this.getContentPane().add(scroller, BorderLayout.CENTER);
			    this.setTitle("Analog Signal | " + bubble);
			} else {
				msg = "error!";
			}
			this.setLocationRelativeTo(null);
			this.setLocation(rand.nextInt(500)+50, rand.nextInt(500)+50);
			this.pack();
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}
	
	public static BufferedImage joinImage(BufferedImage image1, BufferedImage image2) {
		int offset = 1;
	    int width = image1.getWidth() + image2.getWidth() + offset;
	    int height = Math.max(image1.getHeight(), image2.getHeight());
	    BufferedImage newImage = new BufferedImage(width, height,
	        BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = newImage.createGraphics();
	    Color oldColor = g2.getColor();
	    g2.setPaint(Color.BLACK);
	    g2.fillRect(0, 0, width, height);
	    g2.setColor(oldColor);
	    g2.drawImage(image1, null, 0, 0);
	    g2.drawImage(image2, null, image1.getWidth() + offset, 0);
	    g2.dispose();
	    return newImage;
	}
	public static BufferedImage getanalogSignal(String bits){
	    if (bits.charAt(0) == '1') {
			analogSignal = analogOne;
		} else {
			analogSignal = analogZero;
		}
		for(int i =0;i<bits.length();i++){
			if(bits.charAt(i) == '1'){
				analogSignal = joinImage(analogSignal, analogOne);
			}
			if(bits.charAt(i) == '0'){
				analogSignal = joinImage(analogSignal, analogZero);
			}
		}
		return analogSignal;
	}
	public static String stringToBinary(String message){
		String binarymsg = "";
		for(int i =0;i<message.length();i++){
			char ch = message.charAt(i);
			int chvalue = (int) ch;
			String Binary = Integer.toBinaryString(chvalue);
			binarymsg = binarymsg + Binary;
		}
		return binarymsg;
	}
//	public static  void main(String[] args) {
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				Simulator simulator = new Simulator(labelRed, labelBlue, pathRed, pathBlue, delayRed, delayBlue);
//				frame.add(simulator);
//
//				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//				frame.pack();
//				frame.setLocationRelativeTo(null);
//				frame.setTitle("Network Simulator");
//				frame.setVisible(true);
//			}
//			
//		});
//	}
	
}


