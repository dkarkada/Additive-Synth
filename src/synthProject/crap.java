package synthProject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.sound.sampled.AudioInputStream;



public class crap {
	
	/*	public ArrayList<ArrayList<RouteNode>> calcPaths(RouteNode rn, int depth, ArrayList<ArrayList<RouteNode>> pathr){
	if(depth==-1) pathr.add(new ArrayList<RouteNode>());
	int ind=pathr.size()-1;
	if(rn!=null){
		depth++;
		for(RouteNode r: rn.getSources()){
			ind=pathr.size()-1;
			System.out.println(pathr.size());
			pathr.get(ind).add(r);
			pathr = calcPaths(r,depth, pathr);
			depth--;
		}
		if(rn.getSources().isEmpty()){
			System.out.println(ind+" "+depth + " "+pathr);
			pathr.add(new ArrayList<RouteNode>());
			for(int i=0; i<depth-1; i++){
				pathr.get(ind+1).add( 	pathr.get(ind).get(i)  );
			}
		}
	}
	if(depth==-1) paths=pathr;
	return pathr;
}
*/	
/*
 * 
 * 
 
 
class Converter extends RouteNode{
	ArrayList<ArrayList<RouteNode>> paths;
	ArrayList<MIDIEvent> notes;
	ArrayList<MIDIEvent> toAdd;
	
	public Converter(int sentID){
		super(sentID);
		notes = new ArrayList<MIDIEvent>();
		toAdd = new ArrayList<MIDIEvent>();
		paths = new ArrayList<ArrayList<RouteNode>>();
		paths.add(new ArrayList<RouteNode>());
	}
	public void startCalcPath(){
		paths = new ArrayList<ArrayList<RouteNode>>();
		paths.add(new ArrayList<RouteNode>());
		paths = calcPaths(this, paths, 0);
		System.out.println("FINAL " + paths);
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
			System.out.println(depth);
			for(int i=0; i<depth; i++){
				paths1.get(paths1.size()-1).add( 	paths1.get(paths1.size()-2).get(i)  );
			}

			System.out.println(paths1);
		}
	}
	public void stop(){
		for(MIDIEvent m : notes)
			m.keyPressed=false;
	}
	public void addNote(int f){
		toAdd.add(new MIDIEvent(f));
	}
	public double getSample(){
		ArrayList<RouteNode> seq = paths.get(0);
		Osc osc=null;
		for(RouteNode r: seq)
			if (r.isOsc()) osc=(Osc)r;

		double value=0;
		for(MIDIEvent m:notes){
			double cutFreq =osc.getEnvelope(0).getLevel(m.counter,0,m.keyPressed) * (20000-osc.values[0]) + osc.values[0];
			for(int i=0; i<osc.wave.length; i++){
				if(osc.wave[i]!=0){
					if(osc.getEnvelope(0)!=null && osc.getEnvelope(0).isEnabled()){
						if(cutFreq > i*osc.values[0])
							value += Math.sin(Math.PI * 2.0 * m.getWaveFraction() * (i+1)) * osc.wave[i];
					}
					else value += Math.sin(Math.PI * 2.0 * m.getWaveFraction() * (i+1)) * osc.wave[i];
				}
			}
			double env = osc.getEnvelope(1).getLevel(m.counter,0,m.keyPressed);
			if(env==-1){ notes.remove(0); return 0; }
			value *= osc.reduction * env;
			m.increment();
			value/= notes.size();
		}
		for(int i=0; i<toAdd.size(); i++){
			notes.add(toAdd.remove(i));
		}
		return value;
		
	}
	public int type(){
		return 3;
	}
}
 
 
class Envelope{
	ArrayList<EPoint> points;
	boolean enabled, keyOn, playing;
	int current=0, startInd;
	long sustainLength=0;
	EPoint sustain, start, end, before, after;
	
	public Envelope(){
		points =  new ArrayList<EPoint>();
		before=new EPoint(1,0,0);//(0,0,0);
		points.add(before);
		after=new EPoint(0,15000,1);//(1, 10000, 0);
		points.add(after);
//		sustain = new EPoint(.75, 15000, 1);
//		points.add(sustain);
//		points.add(new EPoint(0, 20000, 0));
		enabled = true;
	}
	public boolean isEnabled(){
		return enabled;
	}
	public boolean isPlaying(){
		return playing;
	}
	public void toggleKey(){
		keyOn=false;
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
	public double getLevel(long sample, long sustain, boolean playing){
		if(sample==0){ playing=true; keyOn=true;}
		if(playing==false){return -1; }
		if(before.loop==1 && keyOn){
			sustainLength++;
			System.out.println(sustainLength);
			return before.level;
		}
		if(!keyOn){  sample -= sustainLength;}
		while (sample>after.position && (before.loop!=1 || !keyOn)){
			if(after.loop==3 && keyOn) current = startInd;
			else current++;
			if(current>points.size()-2){  current=0; playing=false; return -1;}
			before=points.get(current);
			after=points.get(current+1);
		}
		double factor = ((double)(sample-before.position))/(after.position-before.position);
		return before.level + factor*(after.level - before.level);
	}
InputStream byteArrIn = new ByteArrayInputStream(b.get(0));
			AudioInputStream audioInputStream = new AudioInputStream(byteArrIn, audForm, 44100/audForm.getFrameSize());
		    try{
		    	for(int i=0; i<100; i++){
				}
	    	 int cnt;
		      while((cnt = audioInputStream.read(b.get(0), 0, b.get(0).length-1)) != -1){
		        if(cnt > 0)
		          sourceDataLine.write(b.get(0), 0, cnt);	  

//		  		System.out.println(b[0][cnt] + " " + cnt);
		      }
		      sourceDataLine.drain();
//		      sourceDataLine.close();
		      byteArrIn.close();
		      audioInputStream.close();
		    }
		    catch (Exception e) {
		      System.out.println(e);
		      System.exit(0);
		    }
		    temp = b.get(0).clone();
		    b.clear();
		    System.out.println("cleared " + b.size());
		    repeat();
		    
		    
public void repeat(){
		System.out.println("repeating");
		for (RouteNode r: sources) ((Converter) r).send();
	}



*/
}
