package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.Map.Entry;

import com.model.*;
import com.model.Class;
import javafx.util.Pair;

public class Diagram extends JPanel implements MouseListener, MouseMotionListener,
        MouseWheelListener, KeyListener{
    private Collection<ContainerStructure> structures = new ArrayList<>();
    private List<Relationship> relationships = new ArrayList<>();
    private HashMap<ContainerStructure, MyPanel> components = new LinkedHashMap<>();
    private MyPanel[] bounds = null;
    private JScrollPane scrollPane = null;
    private JPanel pressedComponent=null;
    private Point pressedLocation = null;
    private Point componentLocation = null;
    public boolean fixedStructure = true;
    public double scale = 1.0;
    private int Width;
    private int Height;
    public String keyWord = "$%&^*";

    public JavaProject p = (JavaProject) Project.getInstance();

    public Diagram(){
        super();
        structures = p.getStructures();

        setLayout(null);
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        addKeyListener(this);

        scrollPane = new JScrollPane(this);
        int scrollUnit = 15;
        scrollPane.getVerticalScrollBar().setUnitIncrement(scrollUnit);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(scrollUnit);
        scrollPane.setWheelScrollingEnabled(false);

        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setPreferredSize(new Dimension());
    }

    public JScrollPane getInstance() {
        return scrollPane;
    }

    public int lvl(ContainerStructure s){
        int lvl = 1;
        if (s instanceof Class){
            Class c = (Class) s;
            if (c.getSuperClass()!= null) lvl = Math.max(lvl, lvl(c.getSuperClass())+1);
            if (c.getInterfaces().size()>0){
                Iterator<Interface> it = c.getInterfaces().iterator();
                while (it.hasNext()){
                    lvl = Math.max(lvl, lvl(it.next())+1);
                }
            }
            return lvl;
        }
        Interface i = (Interface) s;
        if (i.getSuperInterfaces().size()>0){
            Iterator<Interface> it = i.getSuperInterfaces().iterator();
            while (it.hasNext()){
                lvl = Math.max(lvl, lvl(it.next())+1);
            }
        }
        return lvl;
    }

    public void updateRelationships(){
        int n = structures.size();
        synchronized (relationships) {
            relationships.clear();
        }

        Color ex = Color.BLUE;
        Color im = Color.GREEN;

        synchronized (structures) {
            synchronized (relationships) {
                Iterator<ContainerStructure> itr = structures.iterator();
                while (itr.hasNext()) {
                    ContainerStructure s = itr.next();
                    if (s instanceof Class){
                        Class c = (Class) s;
                        if (c.getSuperClass() != null)
                            relationships.add(new Relationship(getBoundsOf(c), getBoundsOf(c.getSuperClass()),ex));

                        ContainerStructure[] interfaces = c.getInterfaces().toArray(new ContainerStructure[0]);
                        for (int j=0; j<interfaces.length; j++)
                            relationships.add(new Relationship(getBoundsOf(c), getBoundsOf(interfaces[j]),im));
                        continue;
                    }

                    Interface i = (Interface) s;
                    ContainerStructure[] interfaces = i.getSubInterfaces().toArray(new ContainerStructure[0]);
                    for (int j=0; j<interfaces.length; j++)
                        relationships.add(new Relationship(getBoundsOf(i), getBoundsOf(interfaces[j]),im));
                }
            }
        }
    }

    private void updateStructures(){
        synchronized (components) {
            components.clear();
        }

        synchronized (structures){
            structures = p.getStructures();
            Iterator<ContainerStructure> it = structures.iterator();
            while (it.hasNext()){
                ContainerStructure s = it.next();
                components.put(s, new MyPanel(s));
            }

        }

        fixedStructure = false;
    }

    private MyPanel[] getAllBounds(){

        return components.values().toArray(new MyPanel[0]);
    }

    private MyPanel getBoundsOf(ContainerStructure s){
        if (components != null && s != null) return components.get(s);
        else return null;
    }

    private void setAllBounds(){
        if (components == null) return;

        List<Pair<Integer,ContainerStructure>> level = new LinkedList<>();
        synchronized (structures){
            Iterator<ContainerStructure> itr = structures.iterator();
            while (itr.hasNext()) {
                ContainerStructure s = itr.next();
                level.add(new Pair<>(lvl(s),s));
            }
        }

        level.sort(new Comparator<Pair<Integer, ContainerStructure>>() {
            @Override
            public int compare(Pair<Integer, ContainerStructure> o1, Pair<Integer, ContainerStructure> o2) {
                return o1.getKey()-o2.getKey();
            }
        });

        int[] maxHeight = new int[10];
        for (int i=0; i<level.size(); i++){
            int lvl = level.get(i).getKey();
            ContainerStructure s = level.get(i).getValue();
            maxHeight[lvl] = Math.max(maxHeight[lvl], getBoundsOf(s).getHeight());
        }
        for (int i=1; i<10; i++) maxHeight[i] += maxHeight[i-1];

        int i=0;
        int s=0;
        int x = 0, y = 0;
        int count[] = new int[10];
        int witdhL[] = new int [10];
        Iterator<Entry<ContainerStructure,MyPanel>> entries = components.entrySet().iterator();
        while (entries.hasNext()){
            Entry<ContainerStructure, MyPanel> entry = entries.next();
            MyPanel en_rect = entry.getValue();

            int lvl = lvl(entry.getKey());
            x= 50+ count[lvl]*70 + witdhL[lvl] ;
            y= 50+ maxHeight[lvl-1] + (lvl-1)*70;
            witdhL[lvl] += en_rect.getWidth();
            if (en_rect.getX() == 0 && en_rect.getY() == 0) en_rect.setLocation(x,y);
            en_rect.addMouseMotionListener(this);
            en_rect.addMouseListener(this);
            count[lvl]++;
        }
    }

    public void DrawAllBounds(){
        if (components == null) return;

        Iterator<Entry<ContainerStructure,MyPanel>> entries = components.entrySet().iterator();
        while (entries.hasNext()){
            Entry<ContainerStructure, MyPanel> entry = entries.next();
            MyPanel en_rect = entry.getValue();

            this.add(en_rect);
        }
    }

    public void clear(){
        this.removeAll();
        this.revalidate();
        synchronized (structures) {
            structures = new ArrayList<>();
        }
        synchronized (relationships) {
            relationships.clear();
        }
        synchronized (components) {
            components.clear();
        }
        p.removeAll();
        repaint();
    }

    public void DrawAllRelationphip(Graphics g){
        //g.setColor(Color.RED);
        for (int i=0; i<relationships.size(); i++) relationships.get(i).draw(g);
    }

    public void setKeyWordForAll(){
        bounds =getAllBounds();
        for (MyPanel panel : bounds) panel.setKeyWord(keyWord);
    }

    public void find(String keyWord){
        if (keyWord=="") this.keyWord = "*&%$";
        else this.keyWord = keyWord;
        setKeyWordForAll();
        repaint();
    }

    public void repaint(Graphics g){
        this.removeAll();
        this.revalidate();
        super.repaint();

        if (fixedStructure) {
            updateStructures();
            updateRelationships();
            setAllBounds();
        }
        bounds = getAllBounds();
        Width = 0;
        Height = 0;
        for (MyPanel panel: bounds){
            Width = Math.max(Width, panel.getWidth()+50+panel.getX());
            Height = Math.max(Height, panel.getHeight()+50+panel.getY());
        }
        setPreferredSize(new Dimension((int) (Width*scale), (int) (Height*scale)));
        Graphics2D g2 = (Graphics2D) g;
        g2.scale(scale,scale);
        DrawAllBounds();
        DrawAllRelationphip(g);
        //System.out.println(structures.size());
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        repaint(g);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2 
        		&& e.getButton() == MouseEvent.BUTTON1) {
    		pressedComponent = getMyPanelAt(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    	if (e.getButton() == MouseEvent.BUTTON1) {
            pressedComponent = getMyPanelAt(e);
            if (pressedComponent != null) {
                componentLocation = pressedComponent.getLocation();
                pressedLocation = e.getLocationOnScreen();
                requestFocus(); // prepare to use arrow keys later
            }
        }
    }
    
    private MyPanel getMyPanelAt(MouseEvent e) {
    	// the scaled distances relative to top left of this Diagram
    	if (e.getSource() != this) {
    		e = SwingUtilities.convertMouseEvent((Component)e.getSource(), e, this);
    	}
    	int scaledX = e.getX();
    	int scaledY = e.getY();
    	// the unscaled distances
    	int realX = (int)(scaledX / scale);
		int realY = (int)(scaledY / scale);

		for (Component c : this.getComponents()) {
			if (c.getBounds().contains(realX, realY) && c instanceof MyPanel) {	
	        	return (MyPanel)c;
			}
		}
        return null;
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        pressedComponent = null;
        componentLocation = null;
        pressedLocation = null;
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
    	if (pressedComponent != null) {
            int x = e.getLocationOnScreen().x - pressedLocation.x;
            int y = e.getLocationOnScreen().y - pressedLocation.y;
            x /= scale;
            y /= scale;
            pressedComponent.setLocation(componentLocation.x + x , componentLocation.y + y);
            repaint(getGraphics());
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource()==this) {
            double delta = 0.05f * e.getPreciseWheelRotation();
            scale -= delta;
            if (scale>2) scale = 2;
            if (scale<0.60) scale = 0.60;
            revalidate();
            repaint();
        }
    }
}