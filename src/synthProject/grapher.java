package synthProject;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;


public class grapher {
	public static void main(String[]args) throws FileNotFoundException{
		Graph mg = new Graph();
		
		JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
		j.setSize(mg.getSize());
		j.add(mg);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
class Graph extends JPanel implements MouseListener{
	Graphics2D gBuf;
	private BufferedImage buf;
	String[] init;
	
	ArrayList<Double> vals;
	
	public Graph() throws FileNotFoundException{
		setSize(1100, 900);
		setVisible(true);
		buf = new BufferedImage(1650,900, BufferedImage.TYPE_INT_RGB);
		gBuf  = buf.createGraphics();
		vals = new ArrayList<Double>();
		init(vals);
	}
	
	public void paintComponent(Graphics g){
		gBuf.setColor(Color.WHITE);
		gBuf.fillRect(0, 0, 1100, 900);
		gBuf.setColor(Color.BLACK);
		gBuf.drawLine(100, 600, 1000, 600);
		
		int ind=0;
		for(double d: vals){
			int x = (int)(200 + (ind++));
			int y = (int)(600 - (d*100));
			gBuf.fillRect(x,y,2,2);
		}
		
		g.drawImage(buf, 0, 0, this);
	}
	
	public void init(ArrayList<Double> vals) throws FileNotFoundException{
		Scanner scan = new Scanner(new File("data1"));
		while(scan.hasNext()){
			String s = scan.next();
			vals.add(Double.parseDouble(s));
		}
	}	
	
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}