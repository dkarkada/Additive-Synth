package synthProject;
import javax.sound.sampled.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.swing.*;

@SuppressWarnings("serial")
public class MixerTest1 extends JPanel{
	static AudioFormat audioFormat;
	TargetDataLine targetDataLine;
	AudioInputStream audioInputStream;
	SourceDataLine sourceDataLine;
	Graphics2D gBuf;
	private BufferedImage buf;
	byte[]b;
	
	
	public static void main (String [] args){
		MixerTest1 mt = new MixerTest1();
		mt.playAudio();
		
		JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
		j.setSize(mt.getSize());
		j.add(mt);
		j.setVisible(true);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	public MixerTest1(){
		setSize(1100, 900);
		setVisible(true);
		buf = new BufferedImage(1650,900, BufferedImage.TYPE_INT_RGB);
		gBuf  = buf.createGraphics();
	}
	
	public void playAudio(){
		b  = new byte[44100];
		for(int i=0; i<44100; i++)
			b[i] = (byte) (127  * Math.sin(i * Math.PI/180 * 16));
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
	    int sampleSizeInBits = 8;//16
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
		      while((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1)
		        if(cnt > 0)
		          sourceDataLine.write(tempBuffer, 0, cnt);
		      sourceDataLine.drain();
		      sourceDataLine.close();
		    }
		    catch (Exception e) {
		      System.out.println(e);
		      System.exit(0);
		    }
		}
	}
	public void paintComponent(Graphics g)
	{
		gBuf.setColor(Color.WHITE);
		gBuf.fillRect(0, 0, 1100, 900);
		gBuf.setColor(Color.BLACK);
		gBuf.drawLine(100, 600, 1000, 600);
		gBuf.drawLine(550, 100, 550, 600);
	
		
		

		for(int i=0; i<7000; i++){
			int x = (int)(200 + (i));
			int y = (int)(600 - (b[i]));
//			System.out.println(i + " " + b[i]);
			gBuf.fillRect(x,y,3,3);
		}
		
		g.drawImage(buf, 0, 0, this);						//draw Buffered image onto MyPanelb
		
		
	}
}

