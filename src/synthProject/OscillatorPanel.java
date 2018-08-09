package synthProject;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.BoundedRangeModel;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//add envelope enabler, filter graphing

class OscillatorPanel extends JPanel implements MouseListener, MouseMotionListener{
	Osc osc;
	EnvelopePanel envPanel;
	RotaryController vol, detune, pan, tone;
	ArrayList<Rectangle> controllers;
	Rectangle controllerPanel, sine, square, saw;
	int selected;
	Point initPoint;
	
	public OscillatorPanel(Osc o, EnvelopePanel e){
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		osc =o;
		envPanel=e;
		selected=-1;
		setPreferredSize(new Dimension(800,400));
		controllers = new ArrayList<Rectangle>();
		setControllers();
		sine = new Rectangle(60,80,100,25);
		square = new Rectangle(60,115,100,25);
		saw = new Rectangle(60,150,100,25);
		
		setLayout(null);		
		vol = new RotaryController(new DefaultBoundedRangeModel());
		detune = new RotaryController(new DefaultBoundedRangeModel());
		pan = new RotaryController(new DefaultBoundedRangeModel());
		tone = new RotaryController(new DefaultBoundedRangeModel());
		updateRotaries();
		add(vol);
		add(pan);
		add(detune);
		add(tone);		
		Dimension size = vol.getPreferredSize();
		vol.setBounds(450, 5, size.width, size.height);
		pan.setBounds(550, 5, size.width, size.height);
		detune.setBounds(450, 105, size.width, size.height);
		tone.setBounds(550, 105, size.width, size.height);
		vol.addMouseListener(this);
		detune.addMouseListener(this);
		pan.addMouseListener(this);
		tone.addMouseListener(this);
			
	}
	public void loadOscillator(Osc o){
		osc=o;
		updateRotaries();
		repaint();
	}
	public void updateRotaries(){
		vol.getModel().setValue(osc.values[1]);
		detune.getModel().setValue(osc.values[2]);
		pan.getModel().setValue(osc.values[3]);
		tone.getModel().setValue(osc.values[4]);
	}
	public void setControllers(){
		int xPos = 50; int yPos = 220; int width=13; int height=150;
		controllerPanel = new Rectangle(xPos, yPos, width*54+1, height);
		for(int i=0; i<8; i++){
			controllers.add(new Rectangle(xPos+(i*width), yPos, width, height));
		}
		for(int i=8; i<54; i++){
			controllers.add(new Rectangle(xPos+(i*width)+1, yPos, width, height/2));
		}
		for(int i=8; i<54; i++){
			controllers.add(new Rectangle(xPos+(i*width)+1, yPos+height/2, width, height/2));
		}
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		g.setColor(this.getBackground());
		g.fillRoundRect(0, 0, getPreferredSize().width, getPreferredSize().height, 10, 10);
		g.setColor(this.getForeground());
		
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Impact", Font.ITALIC, 36));
		g.drawString("OSC  PANEL", 20,50);
		g.setFont(this.getFont());
		
		g.setFont(new Font("Arial", Font.PLAIN, 16));
		g.drawString("Volume", 462, 98);
		g.drawString("Pan", 575, 98);
		g.drawString("Detune", 465, 198);
		g.drawString("Tone", 570, 198);
		
		g.drawRoundRect(200, 20, 200, 160, 10, 10);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(200, 100, 400, 100);
		g.setColor(Color.BLACK);
		double stretchFactor = 200.0/(osc.waveform.size()-1);
		int ind=0, prevx=0, prevy=0;
		for(double i: osc.waveform){
			int x = (int)(200 + (ind++)*stretchFactor);
			int y = (int)(100 - (i*75));
			if(ind>1){
				g.drawLine(x,y,prevx,prevy);
			}
			prevx=x; prevy=y;
		}
		
		int cnt=0;
		for(int i=0; i<controllers.size(); i++){
			Rectangle r = controllers.get(i);
			int x = (int)r.getX();
			g.drawRect(x, (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
			if(i==Math.pow(2,cnt)-1){
				g.setColor(Color.LIGHT_GRAY);
				g.fillRect(x+1, (int)r.getY()+1, (int)r.getWidth()-1, (int)r.getHeight()-1);
				cnt++;
			}
			double d = osc.wave[i];
			g.setColor(Color.GRAY);
			int height = (int)(r.getHeight()*d);
			g.fillRect(x, (int)(r.getY()+r.getHeight()-height), (int)r.getWidth()-1, height);
		}
		
		g.setColor(Color.BLACK);
		g.setFont(new Font("Arial", Font.BOLD, 14));
		g.drawRoundRect((int)sine.getX(), (int)sine.getY(),
				(int)sine.getWidth(), (int)sine.getHeight(),10,10);
		g.drawString("SINE", 95, 98);
		g.drawRoundRect((int)square.getX(), (int)square.getY(),
				(int)square.getWidth(), (int)square.getHeight(),10,10);
		g.drawString("SQUARE", 82, 133);
		g.drawRoundRect((int)saw.getX(), (int)saw.getY(),
				(int)saw.getWidth(), (int)saw.getHeight(),10,10);
		g.drawString("SAW", 95, 168);
	}
	public void mouseClicked(MouseEvent arg0) {
		repaint();
		validate();
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
	public void mousePressed(MouseEvent e) {
		if(controllerPanel.contains(e.getPoint())){
			initPoint = e.getPoint();
			int ind=0;
			while(selected==-1){
				if (controllers.get(ind).contains(e.getPoint()))
					selected = ind;
				else ind++;
			}
		}
		else{
			boolean done=true;
			if(saw.contains(e.getPoint()))
				osc.setWavetype("SAW");
			else if(square.contains(e.getPoint()))
				osc.setWavetype("SQUARE");
			else if(sine.contains(e.getPoint()))
				osc.setWavetype("SINE");
			else done=false;
			if(done) osc.makeWave();
		}
		if(e.getSource().equals(vol)) envPanel.loadEnvelope(osc.envs[0]);
		else if(e.getSource().equals(detune)) envPanel.loadEnvelope(osc.envs[2]);
		else if(e.getSource().equals(pan)) envPanel.loadEnvelope(osc.envs[1]);
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(selected!=-1) osc.getWaveform();
		selected=-1; initPoint=null;
		if(e.getSource().equals(vol)) osc.values[1] = vol.getModel().getValue();
		else if(e.getSource().equals(detune)) osc.values[2] = detune.getModel().getValue();
		else if(e.getSource().equals(pan)) osc.values[3] = pan.getModel().getValue();
		else if(e.getSource().equals(tone)) osc.values[4] = tone.getModel().getValue();
		repaint();
	}
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selected!=-1){
			double changeY = initPoint.getY() - e.getY();
			double val = changeY / (controllers.get(selected).getHeight());
			osc.wave[selected] += val;
			if(osc.wave[selected]<0) osc.wave[selected]=0;
			if(osc.wave[selected]>1) osc.wave[selected]=1;
			initPoint=e.getPoint();
			repaint();
		}
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}

class RoutePanel extends JPanel implements MouseListener, MouseMotionListener{
	MyGUI m;
	ArrayList<RouteNodeController> nodes;
	Routing routing;
	ArrayList<Route> routes;
	int IDgen; //id generator for constructing route nodes
	int selected;
	Point init, current, menuPt;
	Rectangle OscAdder, FiltAdder, FXAdder, unroute, delete;
	boolean creatingRoute, menu;
	
	public RoutePanel(MyGUI mg){
		m=mg;
		setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
		setPreferredSize(new Dimension(800,400));
		
		nodes = new ArrayList<RouteNodeController>();
		nodes.add(new RouteNodeController(4, new Point(600,200),0));
		routing = new Routing();
		routes = new ArrayList<Route>();
		IDgen=1;
		selected=-1;
		menuPt = new Point(0,0);
		
		addPanels();
		
		routes.add(routing.route(1,2, IDgen++));
		routes.add(routing.route(2,0, IDgen++));
		routing.out.countSources();
		routing.updateConverters();
		
		OscAdder = new Rectangle(625,20,50,50);
		FiltAdder = new Rectangle(700,20,50,50);
//		FXAdder = new Rectangle(700,20,50,50);
	}
	public void addPanels(){
		addNode(1, new Point(200,200));
		addNode(2, new Point(400,200));
		Osc o = (Osc)routing.find(IDgen-2);
		Filt f = (Filt)routing.find(IDgen-1);
		Envelope e = o.envs[0];
		m.addPanels(o, f, e);		
	}
	public void addNode(int i, Point p){
		switch(i){
		case 1:
			routing.addOsc(IDgen++);
			break;
		case 2:
			routing.addFilt(IDgen++);
			break;
		case 3:
			routing.addFX(IDgen++);
		}
		nodes.add(new RouteNodeController(i,p,IDgen-1));
	}
	public void removeNode(int id){
		for(int i=0; i<routes.size(); i++){
			Route r = routes.get(i);
			if(r.has(id)){
				routing.deroute(r);
				routes.remove(i);
				i--;
			}		
		}
		routing.remove(id);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		g.setColor(this.getBackground());
		g.fillRoundRect(0, 0, getPreferredSize().width, getPreferredSize().height, 10, 10);
		g.setColor(this.getForeground());
		
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Impact", Font.ITALIC, 36));
		g.drawString("ROUTING", 20,50);
		g.setFont(this.getFont());
		
		for(RouteNodeController r : nodes)
			r.paintComponent(g);
		
		for(Route r: routes){
			RouteNodeController prev = find(r.getPrev().id());
			RouteNodeController next = find(r.getNext().id());
			g.drawLine(prev.x+50, prev.y+25, next.x, next.y+25);
		}
		
		if(creatingRoute)
			g.drawLine(init.x, init.y, current.x, current.y);
		
		g.setFont(new Font("Arial", Font.PLAIN, 14));
		g.drawString("Add:", 575, 50);
		
		int x = OscAdder.x; int y = OscAdder.y;
		g.drawRoundRect(x, y,50,50,10,10);
		for(int i=1; i<50; i++)
			g.drawLine(x+i-1, (int)(y+25+Math.sin((i-1)*25)*20),
					x+i, (int)(y+25+Math.sin(i*25)*20));
		
		x = FiltAdder.x; y = FiltAdder.y;
		g.drawRoundRect(x, y,50,50,10,10);
		g.drawLine(x, y+25, x+25, y+25);
		g.drawLine(x+25, y+25, x+48, y+48);
		
		if(menu) paintMenu(g);
	}
	public void paintMenu(Graphics g){
		g.setFont(this.getFont());
		int x = menuPt.x;
		int y = menuPt.y;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x, y, 100, 35);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 100, 35);
		
		g.drawString("Deroute sources", x+3, y+15);
		unroute  = new Rectangle(x,y,100,17);
		g.drawLine(x, y+17, x+100, y+17);
		
		g.drawString("Delete", x+3, y+32);
		delete = new Rectangle(x,y+17,100,17);
	}
	public RouteNodeController find(int id){
		for(RouteNodeController r: nodes)
			if(r.id==id) return r;
		return null;
	}
	public boolean routable(int s, int t){
		RouteNode source = routing.find(s);
		RouteNode target = routing.find(t);
		if(source.type()==2 && target.type()==2) return true;
		return source.type()<target.type();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(selected!=-1 && nodes.get(selected).draggable(init)){
			menuPt.setLocation(e.getPoint());
			nodes.get(selected).moveBy(init, e.getPoint());
			init = e.getPoint();
		}
		current = e.getPoint();
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {
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
	public void mousePressed(MouseEvent e) {
		int ind=0;
		init = e.getPoint();
		boolean found=false;
		while(ind<nodes.size() && !found){
			if(nodes.get(ind).contains(e.getPoint())){
				selected=ind;
				found=true;
			}
			else ind++;
		}
		if(selected!=-1){
			if(nodes.get(selected).outContains(init)) creatingRoute=true;
			RouteNode rn = routing.find(nodes.get(selected).id);
			if(rn.type()==1)
				m.oscPanel.loadOscillator((Osc)rn);
			else if(rn.type()==2)
				m.filtPanel.loadFilt((Filt)rn);
			if(rn.id()!=0 && delete!=null && delete.contains(e.getPoint())){
				removeNode(nodes.get(selected).id);
				nodes.remove(selected);
			}
			else if(unroute!=null && unroute.contains(e.getPoint())){
				for(int i=0; i<routes.size(); i++){
					Route r = routes.get(i);
					if(r.next.equals(rn)){
						routing.deroute(r);
						routes.remove(i);
						i--;
					}		
				}
			}
		}
		else{
			if(OscAdder.contains(e.getPoint())){
				addNode(1, OscAdder.getLocation());
				selected=nodes.size()-1;
				m.oscPanel.loadOscillator((Osc)routing.find(nodes.get(selected).id));
			}
			else if(FiltAdder.contains(e.getPoint())){
				addNode(2, FiltAdder.getLocation());
				selected=nodes.size()-1;
				m.filtPanel.loadFilt((Filt)routing.find(nodes.get(selected).id));
			}
		}
		if(e.getButton()==MouseEvent.BUTTON1 && menu){
			menu=false;	
			unroute = delete = null;
		}
		if(e.getButton()==MouseEvent.BUTTON3){			
			if(found){
				menu=true;
				int x = e.getX()-700<0 ? 0 : 700-e.getX();
				int y = e.getY()-365<0 ? 0 : 365-e.getY();
				
				menuPt = new Point(e.getX()+x, e.getY()+y);
			}
		}
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		if(creatingRoute){
			for(RouteNodeController n: nodes)
				if(n.inContains(e.getPoint()) && routable(nodes.get(selected).id, n.id)){
					routes.add(routing.route(nodes.get(selected).id, n.id, IDgen++));
					routing.updateConverters();
				}
		}
		repaint();
		if(!menu) selected=-1;
		init=null;
		creatingRoute=false;
	}
}

class FilterPanel extends JPanel implements MouseListener, MouseMotionListener{
	Filt f;
	EnvelopePanel envPanel;
	RotaryController cut, q;
	Point end;
	
	public FilterPanel(Filt fil, EnvelopePanel e){
		setVisible(true);
		setOpaque(true);
		setPreferredSize(new Dimension(500,400));
		f=fil;
		envPanel=e;
		cut = new RotaryController(new DefaultBoundedRangeModel());
		q = new RotaryController(new DefaultBoundedRangeModel());
		updateRotaries();
		
		setLayout(null);
		add(cut);
		add(q);
		Dimension size = cut.getPreferredSize();
		cut.setBounds(100, 255 , size.width, size.height);
		q.setBounds(325, 255 , size.width, size.height);
		
		calcEndPoint();
		
		repaint();
		q.addMouseMotionListener(this);
		cut.addMouseMotionListener(this);
		cut.addMouseListener(this);
	}
	public void loadFilt(Filt fi){
		f=fi;
		updateRotaries();
		calcEndPoint();
		repaint();
	}
	public void updateRotaries(){
		cut.getModel().setValue((int) (100 * convertFreq(f.values[0])));
		q.getModel().setValue(f.values[1]);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		g.setColor(this.getBackground());
		g.fillRoundRect(0, 0, getPreferredSize().width, getPreferredSize().height, 10, 10);
		g.setColor(this.getForeground());
		
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Impact", Font.ITALIC, 36));
		g.drawString("FILTER", 20,50);
		
		g.setColor(Color.BLACK);
		g.drawRoundRect(20, 80, 460, 150,10,10);
		
		g.setFont(new Font("Arial", Font.PLAIN, 16));
		g.drawString("Cutoff", 117, 350);
		g.drawString("Slope", 343, 350);
		
		g.setColor(Color.LIGHT_GRAY);
		g.setFont(new Font("Times New Roman", Font.PLAIN, 12));
		int[] freqs = {80, 250, 1000, 5000, 10000};
		for(int i :freqs){
			int x = 20 + (int)(460*convertFreq(i));
			g.drawLine(x, 80, x, 230);
			g.drawString(i+"", x+5, 228);
		}

		g.setColor(Color.BLACK);
		int x = 20 + (int)(460*convertFreq(f.values[0]));
		g.drawLine(20, 155, x, 155);		
		g.drawLine(x, 155, end.x, end.y);
		
	}
	public void calcEndPoint(){
		int prevx, x;
		prevx = x = 20 + (int)(460*convertFreq(f.values[0]));
		int y = 155;
		double slope = Math.tan(Math.PI*f.values[1]/200.0);
		while(x<480 && y<230){
			x++;
			y = (int) (155 + slope*(x-prevx));
		}
		if(y>230) y=230;
		end = new Point(x,y);
	}
	public double convertFreq(int i){
		return Math.log(i/20.0) / Math.log(1000);
	}
	public int convertVal(double d){
		return (int) (20*Math.pow(1000, d));
	}

	public void mouseDragged(MouseEvent arg0) {
		f.values[0] = convertVal(cut.getModel().getValue()/100.0);
		f.values[1] = q.getModel().getValue();
		calcEndPoint();
		repaint();		
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
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
	public void mousePressed(MouseEvent e) {
		if(e.getSource().equals(cut)){
			envPanel.loadEnvelope(f.cutEnv);
		}
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
class EnvelopePanel extends JPanel implements MouseListener, MouseMotionListener{
	Envelope env;
	ArrayList<Rectangle> points;
	int selected;
	double horizScale;
	boolean moved;
	boolean menu; Point menuPt;
	Rectangle setSus, setNone, setDelete;
	
	public EnvelopePanel(Envelope en){
		setVisible(true);
		setPreferredSize(new Dimension(500,400));
		points = new ArrayList<Rectangle>();
		loadEnvelope(en);
		addMouseListener(this);
		addMouseMotionListener(this);
		repaint();
	}
	public void loadEnvelope(Envelope en){
		env=en;
		selected=-1;
		horizScale=40;
		points.clear();
		for(Envelope.EPoint e: env.points){
			int x = (int) (e.position/horizScale + 20);
			int y = 330-(int)(e.level*250);
			addPoint(x,y);
		}
		repaint();
	}
	public void addPoint(int x, int y){
		for(int i=0; i<points.size(); i++)
			if(points.get(i).getX()>x){
				points.add(i,new Rectangle(x-3,y-3,6,6));
				return;
			}
		points.add(new Rectangle(x-3,y-3,6,6));
	}
	public void removePoint(){
		if(env.removePoint(selected))
			points.remove(selected);
	}
	public void sort(int ind){
		Rectangle r = points.get(selected);
		points.remove(selected);
		addPoint((int)r.getX(), (int)r.getY());
		Envelope.EPoint e = env.points.get(selected);
		env.points.remove(selected);
		env.addPoint(e);
	}
	public void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D)gr;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getPreferredSize().width, getPreferredSize().height);
		g.setColor(this.getBackground());
		g.fillRoundRect(0, 0, getPreferredSize().width, getPreferredSize().height, 10, 10);
		g.setColor(this.getForeground());
		
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setFont(new Font("Impact", Font.ITALIC, 36));
		g.drawString("ENVELOPE", 20,50);
		g.setFont(this.getFont());
		
		g.setColor(Color.BLACK);
		g.drawRoundRect(20, 80, 460, 250,10,10);
		
		g.setColor(Color.BLACK);
		for(int i=0; i<points.size(); i++){
			Envelope.EPoint e = env.points.get(i);
			int x = (int) (e.position/horizScale + 20);
			int y = 330-(int)(e.level*250);
			int big=0;
			if(e.loop==1) big=2;
			g.fillOval(x-3-big, y-3-big, 6+2*big, 6+2*big);
			if(i>0){
				Envelope.EPoint e2 = env.points.get(i-1);
				int x2 = (int) (e2.position/horizScale + 20);
				int y2 = 330-(int)(e2.level*250);
				g.drawLine(x,y,x2,y2);
			}
		}
		if(menu) paintMenu(g);
	}
	public void paintMenu(Graphics g){
		int x = menuPt.x;
		int y = menuPt.y;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(x, y, 100, 50);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, 100, 50);
		
		g.drawString("Set Sustain", x+3, y+15);
		setSus  = new Rectangle(x,y,100,17);
		g.drawLine(x, y+17, x+100, y+17);
		
		g.drawString("Remove Sustain", x+3, y+32);
		setNone = new Rectangle(x,y+17,100,17);
		g.drawLine(x, y+34, x+100, y+34);
		
		g.drawString("Delete", x+3, y+48);
		setDelete  = new Rectangle(x,y+34,100,17);
	}
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	@Override
	public void mousePressed(MouseEvent e) {
		boolean found=false;
		for(int i=0; i<points.size(); i++)
			if (!found && points.get(i).contains(e.getPoint())){
				found=true; selected = i;
			}
		if(setDelete!=null && setDelete.contains(e.getPoint())){
			System.out.println(selected);
			removePoint();
			repaint();
		}
		else if(setSus!=null && setSus.contains(e.getPoint())){
			System.out.println("B");
		}
		else if(setNone!=null && setNone.contains(e.getPoint())){
			System.out.println("C");
		}
		if(e.getButton()==MouseEvent.BUTTON1){
			if(menu){
				menu=false;
				setSus = setNone = setDelete = null;
				repaint();
			}
			else if(!found && e.getX()>20 && e.getX()<480 && e.getY()>80 && e.getY()<330){
				addPoint(e.getX(), e.getY());
				int pos = (int) ((e.getX()-20) * horizScale);
				double level = (330-e.getY()) / 250.0;
				env.addPoint(env.new EPoint(level, pos,0));
			}
		}
		if(e.getButton()==MouseEvent.BUTTON3){			
			if(found){
				menu=true;
				int x = e.getX()-380<0 ? 0 : 380-e.getX();
				int y = e.getY()-280<0 ? 0 : 280-e.getY();
				
				menuPt = new Point(e.getX()+x, e.getY()+y);
			}
		}
		else{
			menu=false;
			setSus = setNone = setDelete = null;
		}		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		if(moved) sort(selected);
		moved=false;
		if(!menu)selected=-1;
		repaint();
	}
	public void mouseDragged(MouseEvent e) {
		if(selected!=-1){
			int x = e.getX(); int y = e.getY();
			if(x<20) x=20;	if(x>480) x=480; if(selected==0) x=20;
			if(y<80) y=80;	if(y>330) y=330;
			points.get(selected).setLocation(x,y);
			int pos = (int) ((x-20) * horizScale);
			double level = (330-y) / 250.0;
			env.points.get(selected).setPosition(pos);
			env.points.get(selected).setLevel(level);
			moved=true;
		}
		repaint();
	}
	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}

class RouteNodeController extends JPanel{
	int type; //1-osc, 2-filt, 3-fx, 4-out
	int x, y;
	Rectangle r;
	Rectangle in, out;
	int id;
	
	public RouteNodeController(int t, Point p, int ident){
		type =t;
		setPreferredSize(new Dimension(50,50));
		x = p.x;
		y = p.y;
		r = new Rectangle(x,y,50,50);
		in = new Rectangle(x,y+15,10,20);
		out = new Rectangle(x+40,y+15,10,20);
		id=ident;
	}
	public void paintComponent(Graphics gr){
		Graphics2D g = (Graphics2D)gr;
		g.setRenderingHint(
		        RenderingHints.KEY_TEXT_ANTIALIASING,
		        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		
		g.setColor(Color.LIGHT_GRAY.brighter());
		g.fillRoundRect(x,y,50,50,10,10);
		g.setColor(Color.BLACK);
		g.drawRoundRect(x,y,50,50,10,10);
		g.setFont(this.getFont());
		g.drawString(""+id, x+21, y-2);
		switch(type){
		case 1:
			for(int i=1; i<50; i++)
				g.drawLine(x+i-1, (int)(y+25+Math.sin((i-1)*25)*20),
						x+i, (int)(y+25+Math.sin(i*25)*20));
			break;
		case 2:
			g.drawLine(x, y+25, x+25, y+25);
			g.drawLine(x+25, y+25, x+48, y+48);
			break;
		case 3:
			break;
		case 4:
			g.setFont(new Font("Arial", Font.BOLD, 16));
			g.drawString("OUT",x+7, y+33);
		}
	}
	public void moveBy(Point orig, Point p){
		x+= p.x - orig.x;
		y+= p.y - orig.y;
		if(x<0) x=0;		if(x>749) x=749;
		if(y<0) y=0;		if(y>349) y=349; 
		r.setLocation(x, y);
		in.setLocation(x, y+15);
		out.setLocation(x+40, y+15);
	}
	public boolean contains(Point p){
		return r.contains(p);// && !in.contains(p) && !out.contains(p);
	}
	public boolean inContains(Point p){
		return in.contains(p);
	}
	public boolean outContains(Point p){
		return out.contains(p) && type!=4;
	}
	public boolean draggable(Point p){
		return r.contains(p) && !in.contains(p) && !out.contains(p);
	}
}

class RotaryController extends JPanel{
    private static final long serialVersionUID = 6681532871556659546L;
    private static final double SENSITIVITY = 0.01;
    private BoundedRangeModel model;

    private double minAngle = 1.4 * Math.PI;
    private double maxAngle = -0.4 * Math.PI;
    private double unitIncrement = 0.005;
    private int lastY;
    private int startX;
    private Color knobColor = Color.LIGHT_GRAY;
    private Color lineColor = Color.BLUE;
    private double baseValue;

    public enum Style {
        LINE, LINEDOT, ARROW, ARC
    };
    private Style style = Style.LINEDOT;
    public RotaryController(BoundedRangeModel model) {
    	setPreferredSize(new Dimension(75,75));
        this.model = model;
        addMouseListener(new MouseHandler());
        addMouseMotionListener(new MouseMotionHandler());
        model.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                repaint();
            }
        });
    }
    public BoundedRangeModel getModel() {
        return model;
    }
    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            lastY = e.getY();
            startX = e.getX();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (isEnabled()) {
                setKnobByXY(e.getX(), e.getY());
            }
        }
    }
    private class MouseMotionHandler extends MouseMotionAdapter {
        @Override
        public void mouseDragged(MouseEvent e) {
            if (isEnabled()) {
                setKnobByXY(e.getX(), e.getY());
            }
        }
    }
    private int getModelRange() {
        return (((model.getMaximum() - model.getExtent()) - model.getMinimum()));
    }
    /**
     * A fractional value is useful for drawing.
     * 
     * @return model value as a normalized fraction between 0.0 and 1.0
     */
    public double getFractionFromModel() {
        double value = model.getValue();
        return convertValueToFraction(value);
    }
    private double convertValueToFraction(double value) {
        return (value - model.getMinimum()) / getModelRange();
    }
    private void setKnobByXY(int x, int y) {
        // Scale increment by X position.
        int xdiff = startX - x; // More to left causes bigger increments.
        double power = xdiff * SENSITIVITY;
        double perPixel = unitIncrement * Math.pow(2.0, power);

        int ydiff = lastY - y;
        double fractionalDelta = ydiff * perPixel;
        // Only update the model if we actually change values.
        // This is needed in case the range is small.
        int valueDelta = (int) Math.round(fractionalDelta * getModelRange());
        if (valueDelta != 0) {
            model.setValue(model.getValue() + valueDelta);
            lastY = y;
        }
    }
    private double fractionToAngle(double fraction) {
        return (fraction * (maxAngle - minAngle)) + minAngle;
    }
    private void drawLineIndicator(Graphics g, int x, int y, int radius, double angle,
            boolean drawDot) {
        double arrowSize = radius * 0.95;
        int arrowX = (int) (arrowSize * Math.cos(angle));
        int arrowY = (int) (arrowSize * Math.sin(angle));
        g.setColor(lineColor);
        //g.drawLine(x, y, x + arrowX, y - arrowY);
        if (drawDot) {
            // draw little dot at end
            double dotScale = 0.1;
            int dotRadius = (int) (dotScale * arrowSize);
            if (dotRadius > 1) {
                int dotX = x + (int) ((0.99 - dotScale) * arrowX) - dotRadius;
                int dotY = y - (int) ((0.99 - dotScale) * arrowY) - dotRadius;
                g.fillOval(dotX, dotY, dotRadius * 2, dotRadius * 2);
            }
        }
    }
    private void drawArrowIndicator(Graphics g, int x0, int y0, int radius, double angle) {
        int arrowSize = (int) (radius * 0.95);
        int arrowWidth = (int) (radius * 0.2);
        int xp[] = {
                0, arrowWidth, 0, -arrowWidth
        };
        int yp[] = {
                arrowSize, -arrowSize / 2, 0, -arrowSize / 2
        };
        double sa = Math.sin(angle);
        double ca = Math.cos(angle);
        for (int i = 0; i < xp.length; i++) {
            int x = xp[i];
            int y = yp[i];
            xp[i] = x0 - (int) ((x * ca) - (y * sa));
            yp[i] = y0 - (int) ((x * sa) + (y * ca));
        }
        g.fillPolygon(xp, yp, xp.length);
    }
    private void drawArcIndicator(Graphics g, int x, int y, int radius, double angle) {
        final double DEGREES_PER_RADIAN = 180.0 / Math.PI;
        final int minAngleDegrees = (int) (minAngle * DEGREES_PER_RADIAN);
        final int maxAngleDegrees = (int) (maxAngle * DEGREES_PER_RADIAN);

        int zeroAngleDegrees = (int) (fractionToAngle(baseValue) * DEGREES_PER_RADIAN);

        double arrowSize = radius * 0.95;
        int arcX = x - radius;
        int arcY = y - radius;
        int arcAngle = (int) (angle * DEGREES_PER_RADIAN);
        int arrowX = (int) (arrowSize * Math.cos(angle));
        int arrowY = (int) (arrowSize * Math.sin(angle));

        g.setColor(knobColor.darker().darker());
        g.fillArc(arcX, arcY, 2 * radius, 2 * radius, minAngleDegrees, maxAngleDegrees
                - minAngleDegrees);
        g.setColor(Color.ORANGE);
        g.fillArc(arcX, arcY, 2 * radius, 2 * radius, zeroAngleDegrees, arcAngle - zeroAngleDegrees);

        // fill in middle
        int arcWidth = radius / 4;
        int diameter = ((radius - arcWidth) * 2);
        g.setColor(knobColor);
        g.fillOval(arcWidth + x - radius, arcWidth + y - radius, diameter, diameter);

        g.setColor(lineColor);
        g.drawLine(x, y, x + arrowX, y - arrowY);

    }
    /**
     * Override this method if you want to draw your own line or dot on the knob.
     */
    public void drawIndicator(Graphics g, int x, int y, int radius, double angle) {
        g.setColor(isEnabled() ? lineColor : lineColor.darker());
        switch (style) {
            case LINE:
                drawLineIndicator(g, x, y, radius, angle, false);
                break;
            case LINEDOT:
                drawLineIndicator(g, x, y, radius, angle, true);
                break;
            case ARROW:
                drawArrowIndicator(g, x, y, radius, angle);
                break;
            case ARC:
                drawArcIndicator(g, x, y, radius, angle);
                break;
        }
    }

    /**
     * Override this method if you want to draw your own knob.
     * 
     * @param g graphics context
     * @param x position of center of knob
     * @param y position of center of knob
     * @param radius of knob in pixels
     * @param angle in radians. Zero is straight up.
     */
    public void drawKnob(Graphics g, int x, int y, int radius, double angle) {
        Graphics2D g2 = (Graphics2D) g;
//        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int diameter = radius * 2;
        // Draw shaded side.
        g.setColor(knobColor.darker());
        g.fillOval(x - radius + 2, y - radius + 2, diameter, diameter);
        g.setColor(knobColor);
        g.fillOval(x - radius, y - radius, diameter, diameter);

        // Draw line or other indicator of knob position.
        drawIndicator(g, x, y, radius, angle);
    }
    // Draw the round knob based on the current size and model value.
    // This used to have a bug where the scope would draw in this components background.
    // Then I changed it from overriding paint() to overriding paintComponent() and it worked.
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getPreferredSize().width;
        int height = getPreferredSize().height;
        int x = width / 2;
        int y = height / 2;

        // Calculate radius from size of component.
        int diameter = (width < height) ? width : height;
        diameter -= 4;
        int radius = diameter / 2;

        double angle = fractionToAngle(getFractionFromModel());
        drawKnob(g, x, y, radius, angle);
    }
    public Color getKnobColor() {
        return knobColor;
    }
    /**
     * @param knobColor color of body of knob
     */
    public void setKnobColor(Color knobColor) {
        this.knobColor = knobColor;
    }
    public Color getLineColor() {
        return lineColor;
    }
    /**
     * @param lineColor color of indicator on knob like a line or arrow
     */
    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }
    public void setStyle(Style style) {
        this.style = style;
    }
    public Style getStyle() {
        return style;
    }
    public double getBaseValue() {
        return baseValue;
    }
    /*
     * Specify where the orange arc originates. For example a pan knob with a centered arc would
     * have a baseValue of 0.5.
     * @param baseValue a fraction between 0.0 and 1.0.
     */
    public void setBaseValue(double baseValue) {
        if (baseValue < 0.0) {
            baseValue = 0.0;
        } else if (baseValue > 1.0) {
            baseValue = 1.0;
        }
        this.baseValue = baseValue;
    }
}
