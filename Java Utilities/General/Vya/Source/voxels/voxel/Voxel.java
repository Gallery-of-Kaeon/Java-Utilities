package voxels.voxel;

public class Voxel {
	
	private float x;
	private float y;
	private float z;

	private float alpha;
	private float red;
	private float green;
	private float blue;
	
	private float size;
	
	public Voxel() {
		
		x = 0;
		y = 0;
		z = 0;
		
		alpha = 1;
		red = 1;
		green = 1;
		blue = 1;
		
		size = 1;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}
	
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	public void setRed(float red) {
		this.red = red;
	}
	
	public void setGreen(float green) {
		this.green = green;
	}
	
	public void setBlue(float blue) {
		this.blue = blue;
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public float getAlpha() {
		return alpha;
	}
	
	public float getRed() {
		return red;
	}
	
	public float getGreen() {
		return green;
	}
	
	public float getBlue() {
		return blue;
	}
	
	public float getSize() {
		return size;
	}
}