package Gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;

public class Ball {
	private double x;
	private double y;
	private int radius;
	private Color color;
	private double dx = 0;
	private double dy = 0;
	
	public Ball(int x, int y, int radius, Color color) {
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.color = color;
	}
	
	public void update() {
		if(!(x >= 1267 && y < 120)) {
			x = x+dx;
			y = y+dy;
		}
	}
	
	public void draw(Graphics2D g2d) {
		g2d.setColor(this.color);
		g2d.fillOval((int)(x - radius), (int)(y - radius), radius*2, radius*2);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	public void setNexthop(ArrayList<Point> path, int nexthop, int delay) {
	
		Point p0 = path.get(nexthop - 1);
		Point p1 = path.get(nexthop);
		dx =  (p1.getX()-p0.getX())/200*3.5/delay;
		dy =  (p1.getY()-p0.getY())/200*3.5/delay;
		//System.out.println(delay);
	}
	
	public void setNexthopAck(ArrayList<Point> path, int nexthop, int delay) {
		Point p0 = path.get(nexthop - 1);
		Point p1 = path.get(nexthop);
		dx =  -(p1.getX()-p0.getX())/200*3.5/delay;
		dy =  -(p1.getY()-p0.getY())/200*3.5/delay;

	}
	public boolean checkACK(ArrayList<Point> path, int nexthop) {
		// 5 is the minimal distance the balls moves forward. Make sure to find the next hop before it arrives.
		if (Math.abs(path.get(nexthop).getX()+1 - (int)this.x) < 5 && Math.abs(path.get(nexthop).getY()+1 - (int)this.y) < 5) {
			return true;
		} else {
			//System.out.println("check: false");
			return false;
		}
	}
	
	public boolean checkPos(ArrayList<Point> path, int nexthop) {
		if (Math.abs(path.get(nexthop).getX()+1 - (int)this.x) < 6 && Math.abs(path.get(nexthop).getY()+1 - (int)this.y) < 6) {
			return true;
		} else {
			//System.out.println("check: false");
			return false;
		}
	}
	
}
