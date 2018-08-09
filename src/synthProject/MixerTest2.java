package synthProject;
import javax.sound.sampled.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.*;

public class MixerTest2 {
	public static void main (String [] args){
		GUI gui = new GUI();
				
		JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
		j.setSize(gui.getSize());
		j.add(gui);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class GUI extends JPanel implements MouseListener{
	Graphics2D gBuf;
	private BufferedImage buf;
	Output2 out;
	Rectangle r1;
	
	public GUI(){
		setSize(1100, 900);
		setVisible(true);
		buf = new BufferedImage(1650,900, BufferedImage.TYPE_INT_RGB);
		gBuf  = buf.createGraphics();
		out = new Output2();
		
		addMouseListener(this);
	}
	public void paintComponent(Graphics g)
	{
		gBuf.setColor(Color.WHITE);
		gBuf.fillRect(0, 0, 1100, 900);
		gBuf.setColor(Color.BLACK);
		gBuf.drawLine(100, 600, 1000, 600);
		gBuf.drawLine(550, 100, 550, 600);
		gBuf.fillRect(10,10,20,20);
		r1 = new Rectangle(10,10,20,20);
		
		if(out.b!=null){
			for(int i=0; i<7000; i++){
				int x = (int)(200 + (i));
				int y = (int)(600 - (out.b[i]));
	//			System.out.println(i + " " + b[i]);	
				gBuf.fillRect(x,y,3,3);
			}
		}
		
		g.drawImage(buf, 0, 0, this);
	}
	public void mouseClicked(MouseEvent e) {
		if(r1.contains(e.getX(),e.getY())) out.playAudio();
		repaint();
	}
	public void mouseEntered(MouseEvent e) {
	}
	public void mouseExited(MouseEvent e) {
	}
	public void mousePressed(MouseEvent e) {
	}
	public void mouseReleased(MouseEvent e) {
	}
}
class Output2{
	AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	byte[]b;
	public void playAudio(){
		b  = new byte[44100];
		for(int i=0; i<44100; i++)
			b[i] = (byte) (127  * Math.sin(i * Math.PI/180));
		try{
			InputStream byteArrIn = new ByteArrayInputStream(b);
			AudioFormat audForm = getAudioFormat();
			audioInputStream = new AudioInputStream(byteArrIn, audForm, 44100/audForm.getFrameSize());
			
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audForm);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			
			sourceDataLine.open(audForm);
			sourceDataLine.start();
			
			Thread playThread = new Thread(new PlayThread());
			playThread.start();
		} 
		catch (Exception e) {
			      System.out.println(e);
			      System.exit(0);
		}
	}
	private static AudioFormat getAudioFormat(){
	    float sampleRate = 44100;
	    int sampleSizeInBits = 16;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = false;
	    return new AudioFormat(
	                      sampleRate,
	                      sampleSizeInBits,
	                      channels,
	                      signed,
	                      bigEndian);
	 }
	class PlayThread extends Thread{
		byte tempBuffer[] = new byte[10000];

		public void run(){
		    try{
		      int cnt;
		      while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)		//copies up to 10,000 vals into buffer at a time
		        if(cnt > 0)
		          sourceDataLine.write(tempBuffer, 0, cnt); 									//writes all of buffer into mixer
		      sourceDataLine.drain();
		      sourceDataLine.close();
		    }
		    catch (Exception e) {
		      System.out.println(e);
		      System.exit(0);
		    }
		}
	}
}


/*
public void accept(array, id){
	set id spot to array
	if all arrays present, run()
	}
public void run(){
	try/catch bs
	  int cnt;
	  
	  envelope markers=0
	  while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)		//copies up to 10,000 vals into buffer at a time
	  	using envs, apply effects
	  	reset markers if necessary
	    if(cnt > 0)
	      send(array, thisID) to all targets
	      
	  sourceDataLine.drain();
	  sourceDataLine.close();

*/