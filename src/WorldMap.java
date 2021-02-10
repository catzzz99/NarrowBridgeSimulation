import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

enum WorldZoneType{
	WEST_PARKING("WEST PARKING", Color.DARK_GRAY, 1),
	WEST_ROAD("WEST ROAD", Color.LIGHT_GRAY, 3),
	WEST_GATE("WEST GATE", Color.DARK_GRAY, 1),
	BRIDGE("BRIDGE", new Color(102, 102, 153), 3),
	EAST_GATE("EAST GATE", Color.DARK_GRAY, 1),
	EAST_ROAD("EAST ROAD", Color.LIGHT_GRAY, 3),
	EAST_PARKING("EAST PARKING", Color.DARK_GRAY, 1);
	
	private String name;
	private Color color;
	private int widthRatio;
	
	WorldZoneType(String name, Color color, int widthRatio) {
		this.name = name;
		this.color = color;
		this.widthRatio = widthRatio;
	}

	public String getName() {
		return name;
	}
	
	public Color getColor() {
		return color;
	}
			
	public int getWidthRatio() {
		return widthRatio;
	}

	@Override
	public String toString() {
		return name;
	}
	
}

public class WorldMap {
		
	private class WorldZone extends Rectangle {
		
		private static final long serialVersionUID = -6198608761521202696L;

		private WorldZoneType worldZoneType;
		
		public WorldZone(int x, int y, int width, int height, WorldZoneType worldZoneType) {
			super(x, y, width, height);
			this.worldZoneType = worldZoneType;
		}

		public WorldZoneType getWorldZoneType() {
			return worldZoneType;
		}

		public void draw(Graphics g) {
			g.setColor(worldZoneType.getColor());
			g.fillRect(x, y, width, height);
					  
			AffineTransform affineTransform = new AffineTransform();
			affineTransform.rotate(-Math.PI/2);
		
			Font font = new Font("Arial", Font.BOLD, 24);  
			Font rotatedFont = font.deriveFont(affineTransform);

			Graphics2D g2 = (Graphics2D) g;
			g2.setFont(rotatedFont);
			g2.setColor(Color.WHITE);
			
			FontMetrics fm = g2.getFontMetrics(font);
			int tx = x + width/2 + fm.getHeight()/4;
			int ty = y + height/2 + fm.stringWidth(worldZoneType.getName())/4 + fm.getAscent()/8;

			g2.drawString(worldZoneType.getName(), tx, ty);
		}
	}
	
	private ArrayList<WorldZone> worldZones;

	private Dimension worldSize;
	
	private int mapZoneWidth;
	private int mapZoneHeight;
	
	public WorldMap(Dimension worldSize) {
		this.worldSize = worldSize;
		calculateMapZoneSize();
		createWorldMap();
	}
	
	
	private void calculateMapZoneSize() {
		int numberOfTiles = 0;
		for(WorldZoneType type : WorldZoneType.values()) {
			numberOfTiles += type.getWidthRatio();
		}
		mapZoneWidth = (int) (this.worldSize.getWidth()/numberOfTiles);
		mapZoneHeight = (int) this.worldSize.getHeight();
	}
	
	private void createWorldMap(){
		worldZones = new ArrayList<WorldZone>();
		
		int y = 0;
		int height = mapZoneHeight;
		
		int x = 0;
		int width = 0;
		
		for(WorldZoneType type : WorldZoneType.values()) {
			x += width;
		    width = (int) mapZoneWidth * type.getWidthRatio() ;
		    worldZones.add(new WorldZone(x, y, width, height, type));
		}	
	}
	
	public WorldZone getWorldZone(WorldZoneType type) {
		for(WorldZone worldZone : worldZones) {
			if(worldZone.getWorldZoneType() == type) {
				return worldZone;
			}
		}
		return null;
	}
	
	public Dimension getSize() {
		return worldSize;
	}
	
	public int getWidth() {
		return (int) worldSize.getWidth();
	}
	
	public int getHeight() {
		return (int) worldSize.getHeight();
	}
	
	public Dimension getWorldZoneSize(WorldZoneType type) {
		WorldZone worldZone = getWorldZone(type);
		if(worldZone == null)
			return null;
		else		
			return worldZone.getSize();
	}
	
	public int getWorldZoneWidth(WorldZoneType type) {
		Dimension size = getWorldZoneSize(type);
		if(size == null)
			return 0;
		else
			return (int) size.getWidth();
	}
	
	public int getWorldZoneHeight(WorldZoneType type) {
		Dimension size = getWorldZoneSize(type);
		if(size == null)
			return 0;
		else
			return (int) size.getHeight();
	}
	
	public int getWorldZoneX(WorldZoneType type) {
		WorldZone worldZone = getWorldZone(type);
		if(worldZone == null)
			return -1;
		else 
			return (int) worldZone.getX();
	}
	
	public int getWorldZoneY(WorldZoneType type) {
		WorldZone worldZone = getWorldZone(type);
		if(worldZone == null)
			return -1;
		else 
			return (int) worldZone.getY();
	}
	
	public void draw(Graphics g) {
		for(WorldZone worldZone : worldZones) {
			worldZone.draw(g);
		}
	}
	
}