package synthProject;

import javax.sound.sampled.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.*;


//test routing fix calcConv, envelope testing
//work release

public class Prototype1{
	public static void main(String[] args) throws FileNotFoundException{
		MyGUI mg = new MyGUI();
				
		JFrame j = new JFrame();  //JFrame is the window; window is a depricated class
		j.setLayout(new FlowLayout());
		j.setSize(mg.getPreferredSize());
		j.setContentPane(mg);
		j.setVisible(true);
		j.validate();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MyGUI extends JPanel implements MouseListener, KeyListener {
	private static final long serialVersionUID = 1L;
	Graphics2D gBuff; //do you even lift;
	private BufferedImage buf;
	RoutePanel routePanel;
	OscillatorPanel oscPanel;
	EnvelopePanel envPanel;
	FilterPanel filtPanel;
	int count=0;
	java.util.Timer clock;
	ArrayList<Integer> times, notes, lengths;
	
	public MyGUI() throws FileNotFoundException{
		setPreferredSize(new Dimension(1400, 860));
		setVisible(true);
		buf = new BufferedImage(2000,1000, BufferedImage.TYPE_INT_RGB);
		gBuff  = buf.createGraphics();
		clock = new java.util.Timer();
		
		
		routePanel = new RoutePanel(this);
		add(routePanel);
		add(envPanel);
		add(filtPanel);
		add(oscPanel);
		validate();
		repaint();
		
		addMouseListener(this);
		setFocusable(true);
		addKeyListener(this);
		
		startPlaying();
		clock.schedule(new TimerTask(){
			public void run(){
				count++;
				check();
			}
		},0, ((long)100));
	}
	public void startPlaying() throws FileNotFoundException{
		Scanner scan = new Scanner(new File("melody.txt"));
		notes = new ArrayList<Integer>();
		times = new ArrayList<Integer>();
		lengths = new ArrayList<Integer>();
		while(scan.hasNextLine()){
			String[] args = scan.nextLine().split(" ");
			times.add(Integer.parseInt(args[0]));
			notes.add(Integer.parseInt(args[1]));
			lengths.add(Integer.parseInt(args[2]));
		}
	}
	public void check(){
		if(!times.isEmpty() && count==times.get(0)){
			times.remove(0);
			routePanel.routing.addNote(notes.remove(0), lengths.remove(0));
		}
	}
	public void addPanels(Osc o, Filt f, Envelope e){	
		if(envPanel==null){
			envPanel = new EnvelopePanel(e);
		}
		if(oscPanel==null){
			oscPanel = new OscillatorPanel(o,envPanel);
		}
		if(filtPanel==null){
			filtPanel = new FilterPanel(f,envPanel);
		}
		validate();
	}
	
	public void paintComponent(Graphics g){
		gBuff.setColor(Color.BLACK);
		gBuff.fillRect(0, 0, 2000, 1000);
		g.drawImage(buf,0,0,this);
	}
	public void mousePressed(MouseEvent e){
		count=0;
		try {
			startPlaying();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
	public void mouseReleased(MouseEvent e){
		//routePanel.routing.stop();
	}
	public void mouseClicked(MouseEvent e){
	} 
	public void mouseEntered(MouseEvent e){}
	public void mouseExited(MouseEvent e){}
	public void mouseMoved(MouseEvent e){}
	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_Q)
			routePanel.routing.addNote(1,1000);
		if(e.getKeyCode()==KeyEvent.VK_A)
			routePanel.routing.addNote(2,1000);
		if(e.getKeyCode()==KeyEvent.VK_S)
			routePanel.routing.addNote(3,1000);
		if(e.getKeyCode()==KeyEvent.VK_E)
			routePanel.routing.addNote(4,1000);
		if(e.getKeyCode()==KeyEvent.VK_D)
			routePanel.routing.addNote(5,1000);
		if(e.getKeyCode()==KeyEvent.VK_R)
			routePanel.routing.addNote(6,1000);
		if(e.getKeyCode()==KeyEvent.VK_F)
			routePanel.routing.addNote(7,1000);
		if(e.getKeyCode()==KeyEvent.VK_G)
			routePanel.routing.addNote(8,1000);
		if(e.getKeyCode()==KeyEvent.VK_Y)
			routePanel.routing.addNote(9,1000);
		if(e.getKeyCode()==KeyEvent.VK_H)
			routePanel.routing.addNote(10,1000);
		if(e.getKeyCode()==KeyEvent.VK_U)
			routePanel.routing.addNote(11,1000);
		if(e.getKeyCode()==KeyEvent.VK_J)
			routePanel.routing.addNote(12,1000);
		if(e.getKeyCode()==KeyEvent.VK_I)
			routePanel.routing.addNote(13,1000);
		if(e.getKeyCode()==KeyEvent.VK_K)
			routePanel.routing.addNote(14,1000);
		if(e.getKeyCode()==KeyEvent.VK_L)
			routePanel.routing.addNote(15,1000);
		if(e.getKeyCode()==KeyEvent.VK_P)
			routePanel.routing.addNote(16,1000);
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
	}
	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}

class Routing{
	Osc[] oscs;	 Filt[] filts; 	Converter[] convs; FX[] fx;  Output out;
	
	public Routing(){
		oscs = new Osc[4];
		filts = new Filt[8];
		convs = new Converter[4];
		fx = new FX[4];
		out = new Output(0);	//output id is 0
		out.playAudio();
	}
	public Osc addOsc(int id){
		for(int i=0; i<4; i++){
			if(oscs[i]==null){
				return oscs[i]=new Osc(id); //return oscs
			}
		}
		return null;
	}
	public Filt addFilt(int id){
		for(int i=0; i<8; i++){
			if(filts[i]==null) return filts[i]=new Filt(id);
		}
		return null;
	}
	public FX addFX(int id){
		for(int i=0; i<4; i++){
			if(fx[i]==null) return fx[i]=new FX(id);
		}
		return null;
	}
	public boolean addConv(Converter c){
		for(int i=0; i<4; i++){
			if(convs[i]==null){
				convs[i]=c;
				return true;
			}
		}
		return false;
	}
	public void remove(int id){
		for(int i=0; i<4; i++)
			if(oscs[i]!=null && oscs[i].id()==id) oscs[i]=null;
		for(int i=0; i<4; i++)
			if(fx[i]!=null && fx[i].id()==id) fx[i]=null;
		for(int i=0; i<4; i++)
			if(convs[i]!=null && convs[i].id()==id) convs[i]=null;
		for(int i=0; i<8; i++)
			if(filts[i]!=null && filts[i].id()==id) filts[i]=null;
		updateConverters();
	}
	public void addNote(int n, int l){
		for(Converter c: convs)
			if(c!=null){
				c.addNote(n,l);
			}
	}
	public void stop(){
		for(Converter c: convs)
			if(c!=null) c.stop();
	}
	public RouteNode find(int id){
		if (id==0) return out; 
		for(int i=0; i<4; i++)
			if(oscs[i]!=null && oscs[i].id()==id) return oscs[i];
		for(int i=0; i<4; i++)
			if(fx[i]!=null && fx[i].id()==id) return fx[i];
		for(int i=0; i<8; i++)
			if(filts[i]!=null && filts[i].id()==id) return filts[i];
		return null;
	}
	public Route route(int id1, int id2, int newID){
		RouteNode source = find(id1), target = find(id2);
		Converter conv = null;
		if(source==null || target==null) return null;
		if(source.sendsSoft() != target.sendsSoft()){
			conv = new Converter(newID);
			addConv(conv);
		}
		return new Route(source,target, conv);
	}
	public void deroute(Route r){
		Converter c = r.getConv();
		if(c==null){
			r.getPrev().removeTarget(r.getNext());
			r.getNext().removeSource(r.getPrev());
		}
		else{
			r.getPrev().removeTarget(c);
			r.getNext().removeSource(c);
			remove(c.id());
		}
		updateConverters();
	}
	public void updateConverters(){
		for(Converter co: convs)
			if(co!=null) co.startCalcPath();
	}
	public String toString(){
		String s = "";
		for(int i=0; i<4; i++)
			if(oscs[i]!=null) s += oscs[i].toString() + "\n";
		s+="\n";
		for(int i=0; i<8; i++)
			if(filts[i]!=null) s += filts[i].toString() + "\n";
		s+="\n";
		for(int i=0; i<4; i++)
			if(convs[i]!=null) s += convs[i].toString() + "\n";
		s+="\n";
		for(int i=0; i<4; i++)
			if(fx[i]!=null) s += fx[i].toString() + "\n";
		return s;
	}
}

class Osc extends RouteNode{
	Envelope[] envs;
	public double[] wave;
	public int[] values;
	public double reduction;
	public ArrayList<Double> waveform;
	String wavetype;
	double tVol, tPan, tDet;
	
	public Osc(int sentID){
		super(sentID);
		envs = new Envelope[3]; //vol, pan, detune
		envs[0] = new Envelope();
		envs[1] = new Envelope();
		envs[2] = new Envelope();
		values = new int[]{ 3, //note from A3
					100, //volume
					50, //detune
					50, //pan
					44}; //tone
		wave = new double[100];
		wavetype="SINE";
		waveform = new ArrayList<Double>();
		makeWave();
	}
	public void setWavetype(String w){
		wavetype = w;
	}
	public void makeWave(){
		for(int i=0; i<wave.length; i++)
			wave[i]=0;
		switch(wavetype){
		case("SAW"):{
			for (int i=0; i<values[4]; i++){
				wave[i]= 1.0/(i+1);
			}
			break;
		}
		case("SQUARE"):{
			for (int i=0; i<values[4]; i+=2){
				wave[i]= 1.0/(i+1);
			}
			break;
		}
		default:{
			wave[0]=1;
		}
		}
		getWaveform();
	}
	public void getWaveform(){
		waveform.clear();
		int periodSamples = 200;
		
		double max= Double.MIN_VALUE;
		for(int i=0; i<periodSamples+1; i++){
			double value=0;
			double x = i / (double) periodSamples;
			for(int k=0; k<wave.length; k++){
				value += Math.sin(Math.PI * 2.0 * x * (k+1)) * wave[k];
			}
			if(value>max) max=value;
			waveform.add(value);	
		}
		reduction = 1/max;
		for(int i=0; i<waveform.size(); i++){
			waveform.set(i, waveform.get(i)*reduction);
		}
	}	
	public void addEnvelope(int ind){
		envs[ind] = new Envelope();
	}
	public Envelope getEnvelope(int ind){
		return envs[ind];
	}
	public void addVoice(int i){ //will be 1 or -1
		values[0]+=i;
		if(values[0]>4 || values[0]<0) values[0]-=i;
	}
	public double getFrequency(){
		double fund = Math.pow(1.059460646483, values[0]) *220;
		double next = Math.pow(1.059460646483, values[0]+1) *220;
		double prev = Math.pow(1.059460646483, values[0]-1) *220;
		if(values[2]>50) return fund + (((values[2]-50)/50.0) * (next-fund));
		else return fund + (((50-values[2])/50.0) * (prev-fund));
	}
	public boolean isOsc(){
		return true;
	}
	public boolean sendsSoft(){
		return true;
	}
	public int type(){
		return 1;
	}
	public boolean getVolEnvLevel(MIDIEvent m, int i){
		double level = envs[0].getLevel(m.counter, m.sustainCounter.get(i).get(0), m.keyPressed);
		if(level>1){
			level-=1.5;
			m.incrementSustain(i,0);
		}
		if(level<0){
			tVol=0;
			return false;
		}
		tVol = (values[1]/100.0)*level;
		return true;
	}
	public boolean getPanEnvLevel(MIDIEvent m, int i){
		double level = envs[1].getLevel(m.counter, m.sustainCounter.get(i).get(1), m.keyPressed);
		if(level>1){
			level-=1.5;
			m.incrementSustain(i,1);
		}
		if(level<0){
			tPan=0;
			return false;
		}
		tPan = (values[3]/100.0)*level;
		return true;
	}
	public boolean getDetEnvLevel(MIDIEvent m, int i){
		double level = envs[2].getLevel(m.counter, m.sustainCounter.get(i).get(2), m.keyPressed);
		if(level>1){
			level-=1.5;
			m.incrementSustain(i,2);
		}
		if(level<0){
			tDet=0;
			return false;
		}
		tDet = (values[2]/100.0)*level;
		return true;
	}
}

class Filt extends RouteNode{
	Envelope cutEnv;
	int[] values;
	boolean lowCut;
	double slope, pos1, pos2;
	
	public Filt(int sentID){
		super(sentID);
		lowCut = true;
		cutEnv = new Envelope();
		values = new int[]{20000, //cutFreq
					50}; //Q
	}
	public Envelope getCutEnv(){
		return cutEnv;
	}
	public void setCut(int i){
		values[0] = i;
	}
	public void setQ(int i){
		values[1] = i;
	}
	public boolean sendsSoft(){
		return true;
	}
	public int type(){
		return 2;
	}
	public boolean calcCut(MIDIEvent m, int i, int j){
		double level = cutEnv.getLevel(m.counter, m.sustainCounter.get(i).get(j+3), m.keyPressed);
		if(level>1){
			level-=1.5;
			m.incrementSustain(i,j+3);
		}
		level = Math.pow(1000, level) / 1000;
		int cut = (int) (values[0] * level);
		slope = Math.tan(Math.PI*values[1]/200.0);
		if(cut<0){ 
			pos1 = Math.log(cut/20.0) / Math.log(1000);
			pos2 = pos1 + (1/slope);
			return false; 
		}
		if(!lowCut) slope*=-1;
		pos1 = Math.log(cut/20.0) / Math.log(1000);
		pos2 = pos1 + (1/slope);
		return true;
	}
	public double getValue(double freq){
		double scaledFreq = Math.log(freq/20.0) / Math.log(1000);
		if(lowCut){
			if(scaledFreq<pos1) return 1;
			if(scaledFreq<pos2)
					return 1 - ((scaledFreq-pos1) / (pos2-pos1));
		}
		else{
			if(scaledFreq>pos1) return 1;
			if(scaledFreq>pos2)
					return (scaledFreq-pos2) / (pos1-pos2);
		}
		return 0;
		
	}
}

class Converter extends RouteNode{
	ArrayList<ArrayList<RouteNode>> paths;
	ArrayList<Osc> oscList;
	ArrayList<MIDIEvent> notes;
	ArrayList<MIDIEvent> toAdd;
	
	public Converter(int sentID){
		super(sentID);
		notes = new ArrayList<MIDIEvent>();
		toAdd = new ArrayList<MIDIEvent>();
		oscList = new ArrayList<Osc>();
		paths = new ArrayList<ArrayList<RouteNode>>();
		paths.add(new ArrayList<RouteNode>());
	}
	public void startCalcPath(){
		paths.clear();
		oscList.clear();
		paths.add(new ArrayList<RouteNode>());
		paths = calcPaths(this, paths, 0);
		setStuff();
	}
	public void setStuff(){
		paths.remove(paths.size()-1);
		for(int i=0; i<paths.size(); i++){//ArrayList<RouteNode> arr:paths){
			ArrayList<RouteNode> arr = paths.get(i);
			if(arr.get(arr.size()-1).type()!=1){
				paths.remove(i);
				i--;
			}
			else{
				arr.remove(0);
				oscList.add((Osc) arr.remove(arr.size()-1));
			}
		}
	}
	public ArrayList<ArrayList<RouteNode>> calcPaths(RouteNode rn, ArrayList<ArrayList<RouteNode>> paths1, int depth){
		for(RouteNode r: rn.getSources()){
			copyChain(paths1, depth+1);
			paths1.get(paths.size()-1).add(r);
			paths1 = calcPaths(r, paths1, depth+1);
		}
		if(rn.getSources().isEmpty()){
			paths1.add(new ArrayList<RouteNode>());
		}
		return paths1;
	}
	public void copyChain(ArrayList<ArrayList<RouteNode>> paths1, int depth){
		if(paths1.get(0).size()==0) paths1.get(0).add(this);
		else if(paths1.get(paths.size()-1).size()==0){
			for(int i=0; i<depth; i++){
				paths1.get(paths1.size()-1).add( 	paths1.get(paths1.size()-2).get(i)  );
			}
		}
	}
	public void stop(){
		for(MIDIEvent m : notes)
			m.keyPressed=false;
	}
	public void addNote(int n, int l){
		toAdd.add(new MIDIEvent(n,l,oscList, paths));
	}
	public double getSample(){
		double value=0;
		boolean isComplete=false;
		for(MIDIEvent m: notes){
			for(int i=0; i<paths.size(); i++){
				Osc o = oscList.get(i);
				if(!o.getVolEnvLevel(m, i)) isComplete=true;
				if(!o.getPanEnvLevel(m, i)) isComplete=true;
				if(!o.getDetEnvLevel(m, i)) isComplete=true;
				for(int j=0; j<paths.get(i).size(); j++){
					Filt f = (Filt) paths.get(i).get(j);
					if(!(f.calcCut(m, i, j)))
						isComplete=true;
				}
				if(!isComplete){
					double freq = m.freqs.get(i);
					int maxHarmonic = m.maxHarmonics.get(i);
					
					for(int j=1; j<maxHarmonic; j++){
						if(o.wave[j-1]!=0){
							double envReduction = o.tVol;
							for(int k=0; k<paths.get(i).size(); k++){
								Filt f = (Filt) paths.get(i).get(k);
								envReduction *= f.getValue(j*freq);
							}
							value += Math.sin(Math.PI * 2.0 * m.getWaveFraction(i) * j) 
									* envReduction * o.wave[j-1];
						}
					}
					value *= o.reduction / notes.size() / paths.size();
				}
			}
			m.increment();
		}
		if(isComplete) notes.remove(0);
		while(!toAdd.isEmpty()){
			notes.add(toAdd.remove(0));
		}
		if(value>1) value=1;
		if(value<-1) value=-1;
//		System.out.println(value);
		return value;
	}
	public int type(){
		return 3;
	}
}

class MIDIEvent{
	int counter=0;
	int length;
	ArrayList<Double> freqs;
	ArrayList<Long> periodSamples;
	ArrayList<Long> sampleNumbers;
	boolean keyPressed;
	ArrayList<Integer> maxHarmonics;
	ArrayList<ArrayList<Integer>> sustainCounter;
	
	public MIDIEvent(int n, int l, ArrayList<Osc> oscList, ArrayList<ArrayList<RouteNode>> paths){
		keyPressed=true;
		length=l;
		freqs = new ArrayList<Double>();
		periodSamples = new ArrayList<Long>();
		sampleNumbers = new ArrayList<Long>();
		maxHarmonics = new ArrayList<Integer>();
		sustainCounter = new ArrayList<ArrayList<Integer>>();
		for(Osc o: oscList){
			o.values[0]=n;
			freqs.add(o.getFrequency());
			periodSamples.add((long)(22050 / o.getFrequency()));  //bitrate by freq = samples per wave
			sampleNumbers.add((long) 0);
			int num=1;
			while(num<o.values[4] && (num*o.getFrequency()) < 16000) num++;
			maxHarmonics.add(num);
		}
		for(int i=0; i<paths.size(); i++){
			sustainCounter.add(new ArrayList<Integer>());
			for(int j=0; j<paths.get(i).size()+3; j++) sustainCounter.get(i).add(0);
		}
	}
	public double getWaveFraction(int i){
		return sampleNumbers.get(i) / (double) periodSamples.get(i);
	}
	public void incrementSustain(int i, int j){
		sustainCounter.get(i).set(j, sustainCounter.get(i).get(j)+1);
	}
	public void increment(){
		counter++;
		if(counter==length) keyPressed=false;
		for(int i=0; i<sampleNumbers.size(); i++){
			long l = (sampleNumbers.get(i)+1) % periodSamples.get(i);
			sampleNumbers.set(i,l);
		}
	}
}

class FX extends RouteNode{
	
	public FX(int sentID){
		
	super(sentID);
	}
	public void accept(byte[] b){
	}
	public int type(){
		return 4;
	}
}

class Output extends RouteNode{
	SourceDataLine sourceDataLine;
	AudioFormat audForm;
	public byte[] buffer;
	int ind, numSources;
	boolean done;
	
	public static final int BUFFER_SIZE = 10000;
    public static final int SAMPLES_PER_BUFFER = BUFFER_SIZE / 2;
	
	public Output(int sentID){
		super(sentID);
		buffer = new byte[BUFFER_SIZE];
		try{
			audForm = getAudioFormat();
			
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audForm);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			
			sourceDataLine.open(audForm);
			sourceDataLine.start();
		} 
		catch (Exception e) {
			      System.out.println(e);
			      System.exit(0);
		}
	}
	public void playAudio(){
		Thread playThread = new Thread(new PlayThread());
		playThread.start();
	}
	public void countSources(){
		numSources=sources.size();
	}
	private static AudioFormat getAudioFormat(){
	    float sampleRate = 22050;
	    int sampleSizeInBits = 16;
	    int channels = 1;
	    boolean signed = true;
	    boolean bigEndian = true;
	    return new AudioFormat(
	                      sampleRate,
	                      sampleSizeInBits,
	                      channels,
	                      signed,
	                      bigEndian);
	 }
	public int getSamples(byte[] buffer){
		int index = 0;
		short ss=0;
		countSources();
	    for (int i = 0; i < SAMPLES_PER_BUFFER; i++) {
	    	for(int k=0; k<numSources; k++){
			      double ds = sources.get(k).getSample() * Short.MAX_VALUE / numSources; 
			      ss += (short) Math.round(ds);
	    	}
	    	buffer[index++] = (byte)(ss >> 8);
		    buffer[index++] = (byte)(ss & 0xFF);
		    ss=0;
	    }
	    return BUFFER_SIZE;
	 }
	class PlayThread extends Thread{
		public void run(){
			int nBytesRead=0; done=false;
		    try{
		      while((nBytesRead != -1) && (!done)){
		        nBytesRead = getSamples(buffer);
		    	if (nBytesRead>0) sourceDataLine.write(buffer, 0, nBytesRead);
		      }
		    }
		    catch (Exception e) {
		      System.out.println(e);
		      System.exit(0);
		    }
		}
	}
	public int type(){
		return 5;
	}
}

abstract class RouteNode{
	protected int id;
	protected ArrayList<RouteNode> targets;
	protected ArrayList<RouteNode> sources;
	
	public RouteNode(int sentID){
		id = sentID;
		targets = new ArrayList<RouteNode>();
		sources= new ArrayList<RouteNode>();
	}
	public boolean isOsc() {
		return false;
	}
	public int id(){
		return id;
	}
	public void addTarget(RouteNode t){
		if(targets.size()<10) targets.add(t);
	}
	public void removeTarget(RouteNode t){
		for(int i=0; i<targets.size(); i++)
			if(targets.get(i).equals(t)) targets.remove(i);
	}
	public void addSource(RouteNode t){
		if(sources.size()<10) sources.add(t);
	}
	public void removeSource(RouteNode t){
		for(int i=0; i<sources.size(); i++)
			if(sources.get(i).equals(t)) sources.remove(i);
	}
	public ArrayList<RouteNode> getSources(){
		return sources;
	}
	public boolean sendsSoft(){
		return false;
	}
	public void accept(byte[] b){}
	public int[] getValues(){return null;}
	public String toString(){
		String s = id+"";// + ":\t";
//		for(RouteNode r: targets)
//			if(r!=null) s += r.id() + " ";
		return s;
	}
	public double getSample(){
		return 0;
	}
	public abstract int type();
}

class Route{
	RouteNode prev, next;
	Converter conv;
	
	public Route(RouteNode p, RouteNode n, Converter c){
		prev=p; next=n; conv=c;
		if(c==null){
			p.addTarget(n);
			n.addSource(p);
		}
		else{
			p.addTarget(c);
			c.addSource(p);
			c.addTarget(n);
			n.addSource(c);
		}
	}
	public RouteNode getPrev(){
		return prev;
	}
	public RouteNode getNext(){
		return next;
	}
	public Converter getConv(){
		return conv;
	}
	public boolean has(int id){
		return prev.id()==id || next.id()==id;
	}
}


class Envelope{
	ArrayList<EPoint> points;
	boolean enabled, playing;
	int current=0, startInd;
	EPoint sustain, start, end, before, after;
	
	public Envelope(){
		points =  new ArrayList<EPoint>();
		points.add(new EPoint(1,0,0));
		sustain = new EPoint(.5, 2000, 1);
		points.add(sustain);
		points.add(new EPoint(0,10000,0));
		enabled = true;
	}
	public boolean isEnabled(){
		return enabled;
	}
	public boolean isPlaying(){
		return playing;
	}
	public void addPoint(EPoint e){
		boolean done=false;
		for(int i=0; i<points.size(); i++)
			if(points.get(i).position>e.position){
				points.add(i, new EPoint(e.level, e.position, e.loop));
				i+=points.size();
				done=true;
			}
		if(!done) points.add(new EPoint(e.level, e.position, e.loop));
		before = points.get(0);
		after = points.get(1);
	}
	public boolean setPointLoop(int ind, int loop){
		if (ind>=points.size()) return false;
		EPoint e = points.get(ind);
		if(checkLooping(e.loop, loop, e.position)){
			switch (loop){
			case 1: sustain=e; break;
			case 2: start=e; startInd=ind; break;
			case 3: end=e; break;
			}
			e.setLoop(loop);
			return true;
		}
		return false;
	}
	public boolean checkLooping(int preLoop, int postLoop, int pos){
		if(preLoop==postLoop) return true;
		if(preLoop==0){
			if(postLoop==1){
				if(sustain!=null) sustain.setLoop(0);
				if(start!=null){  start.setLoop(0); start=null;}
				if(end!=null){  end.setLoop(0); end=null;}
			}
			if(postLoop==2){
				if(sustain!=null){ sustain.setLoop(0); sustain=null;}
				if(start!=null)  start.setLoop(0);
				if(end!=null && end.position<pos){  end.setLoop(0); end=null;}
			}
			if(postLoop==3){
				if(sustain!=null){ sustain.setLoop(0); sustain=null;}
				if(start!=null && start.position>pos){ start.setLoop(0); start=null;}
				if(end!=null)  end.setLoop(0);
			}
		}
		if(preLoop==1){
			if(postLoop==0) return false;
			if(postLoop==3) return false;
			sustain=null;
		}
		if (preLoop==2){
			if (postLoop==0) return false;
			if (postLoop==1){ end.setLoop(0); end=null;}
			if (postLoop==3) return false;
		}
		if (preLoop==3){
			if (postLoop==0) return false;
			if (postLoop==1){ start.setLoop(0); start=null;}
			if (postLoop==2) return false;
		}
		return true;
	}
	public boolean removePoint(int ind){
		if(ind<points.size()){
			EPoint e = points.get(ind);
			if (checkLooping(e.loop, 0, e.position)){
				points.remove(ind);
				return true;
			}
		}
		return false;
	}
	public double getLevel(int sample, int sustainLength, boolean keyPressed){
		sample -= sustainLength;
		if(keyPressed) playing=true;
		else sample+=1;
		findLocation(sample, keyPressed);
		if(playing==false)	return -1;
		if(before.loop==1 && keyPressed){
			return 1.5 + before.level;
		}
		double factor = ((double)(sample-before.position))/(after.position-before.position);
		return before.level + factor*(after.level - before.level);
	}
	public void findLocation(int sample, boolean keyPressed){
		int i=0;
		while(i<points.size() && points.get(i).position <= sample) i++;
		i--;
		if(i==points.size()-1){
			playing=false;
		}
		else{
			before = points.get(i);
			after=points.get(i+1);
		}
	}
	
	class EPoint{
		public double level;
		public int position, loop;
		
		public EPoint(double l, int p, int lp){
			level=l; position=p;
			loop=lp; //0=none  1=sustain  2=loopstart  3=loopend
		}
		public void setLevel(double l){
			level=l;
		}
		public void setPosition(int p){
			position=p;
		}
		public void setLoop(int lp){
			loop=lp;
		}
	}
}
