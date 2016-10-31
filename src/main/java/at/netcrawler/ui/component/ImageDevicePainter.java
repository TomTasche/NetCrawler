package at.netcrawler.ui.component;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

import at.andiwand.library.component.GraphViewerVertex;


public class ImageDevicePainter implements DevicePainter {
	
	private final Image image;
	
	public ImageDevicePainter(Image image) {
		this.image = image;
	}
	
	public Dimension getSize() {
		return new Dimension(image.getWidth(null), image.getHeight(null));
	}
	
	public void paint(Graphics g, GraphViewerVertex vertex) {
		Point p = vertex.getPosition();
		g.drawImage(image, p.x, p.y, null);
	}
	
}