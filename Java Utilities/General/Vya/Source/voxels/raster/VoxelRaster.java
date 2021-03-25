package voxels.raster;

import java.util.ArrayList;

import voxels.voxel.Voxel;

public class VoxelRaster {

	private ArrayList<ArrayList<Integer>> raster;
	
	public VoxelRaster() {
		raster = new ArrayList<ArrayList<Integer>>();
	}
	
	public ArrayList<ArrayList<Integer>> getRaster() {
		return raster;
	}
	
	public void writeVoxelsToRaster(ArrayList<Voxel> voxels, int width, int height) {
		
		ArrayList<ArrayList<Integer>> newRaster = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Float>> depthMap = new ArrayList<ArrayList<Float>>();
		
		initialize(width, height, newRaster, depthMap);
		
		for(int i = 0; i < voxels.size(); i++)
			writeVoxelToRaster(newRaster, depthMap, voxels.get(i));
		
		swapRaster(newRaster);
	}
	
	private void initialize(
			int width,
			int height,
			ArrayList<ArrayList<Integer>> newRaster,
			ArrayList<ArrayList<Float>> depthMap) {
		
		initializeNewRaster(width, height, newRaster);
		initializeDepthMap(width, height, depthMap);
	}
	
	private void initializeNewRaster(
			int width,
			int height,
			ArrayList<ArrayList<Integer>> newRaster) {
		
		for(int i = 0; i < height; i++) {
			
			ArrayList<Integer> row = new ArrayList<Integer>(width);
			
			for(int j = 0; j < width; j++)
				row.add(0);
			
			newRaster.add(row);
		}
	}
	
	private void initializeDepthMap(
			int width,
			int height,
			ArrayList<ArrayList<Float>> depthMap) {
		
		for(int i = 0; i < height; i++) {
			
			ArrayList<Float> row = new ArrayList<Float>(width);
			
			for(int j = 0; j < width; j++)
				row.add((float) 1);
			
			depthMap.add(row);
		}
	}
	
	private void writeVoxelToRaster(
			ArrayList<ArrayList<Integer>> newRaster,
			ArrayList<ArrayList<Float>> depthMap,
			Voxel voxel) {
		
		float depth = voxel.getZ();
		
		if(depth < 0)
			return;
		
		int width = newRaster.get(0).size();
		int height = newRaster.size();
		
		int xCenter = width / 2;
		int yCenter = height / 2;
		
		float aspect = (width + height) / 2;
		
		float xPosition = voxel.getX() * (1 - depth);
		float yPosition = voxel.getY() * (1 - depth);
		
		float size = voxel.getSize() * (1 - depth);
		
		int voxelX = (int) (xPosition * aspect) + xCenter;
		int voxelY = (int) (yPosition * aspect) + yCenter;
		
		int voxelRadius = (int) Math.ceil((size * aspect) / 2);
		
		int xAlpha = voxelX - voxelRadius;
		int xBeta = voxelX + voxelRadius;
		
		int yAlpha = voxelY - voxelRadius;
		int yBeta = voxelY + voxelRadius;
		
		if(xAlpha > newRaster.get(0).size() - 1 ||
				xBeta < 0 ||
				yAlpha > newRaster.size() - 1 ||
				yBeta < 0) {
			
			return;
		}
		
		if(xAlpha < 0)
			xAlpha = 0;
		
		if(xBeta > newRaster.get(0).size() - 1)
			xBeta = newRaster.get(0).size() - 1;
		
		if(yAlpha < 0)
			yAlpha = 0;
		
		if(yBeta > newRaster.size() - 1)
			yBeta = newRaster.size() - 1;
		
		writeVoxelToRasterPixels(newRaster, depthMap, voxel, xAlpha, xBeta, yAlpha, yBeta);
	}
	
	private void writeVoxelToRasterPixels(
			ArrayList<ArrayList<Integer>> newRaster,
			ArrayList<ArrayList<Float>> depthMap,
			Voxel voxel,
			int xAlpha,
			int xBeta,
			int yAlpha,
			int yBeta) {
		
		float depth = voxel.getZ();
		
		int color = getColor(voxel);
		
		for(int y = yAlpha; y <= yBeta; y++) {
			
			for(int x = xAlpha; x <= xBeta; x++) {
				
				int alphaColor = 0;
				int betaColor = 0;

				if(depth <= depthMap.get(y).get(x)) {
					alphaColor = color;
					betaColor = newRaster.get(y).get(x);
				}

				else {
					alphaColor = newRaster.get(y).get(x);
					betaColor = color;
				}
				
				int alphaAlpha = (alphaColor & 0xFF000000) >>> 24;
				int alphaRed = (alphaColor & 0x00FF0000) >>> 16;
				int alphaGreen = (alphaColor & 0x0000FF00) >>> 8;
				int alphaBlue = alphaColor & 0x000000FF;
				
				int betaAlpha = (betaColor & 0xFF000000) >>> 24;
				int betaRed = (betaColor & 0x00FF0000) >>> 16;
				int betaGreen = (betaColor & 0x0000FF00) >>> 8;
				int betaBlue = betaColor & 0x000000FF;
				
				float alphaGap = (alphaAlpha / 255);
				
				int newAlpha = alphaAlpha + (int) (betaAlpha * alphaGap);
				int newRed = alphaRed + (int) (betaRed * alphaGap);
				int newGreen = alphaGreen + (int) (betaGreen * alphaGap);
				int newBlue = alphaBlue + (int) (betaBlue * alphaGap);
				
				if(newAlpha > 255)
					newAlpha = 255;
				
				if(newRed > 255)
					newRed = 255;
				
				if(newGreen > 255)
					newGreen = 255;
				
				if(newBlue > 255)
					newBlue = 255;
				
				newAlpha = newAlpha << 24;
				newRed = newRed << 16;
				newGreen = newGreen << 8;
				
				newRaster.get(y).set(
						x,
						newAlpha |
						newRed |
						newGreen |
						newBlue);
				
				depthMap.get(y).set(x, depth);
			}
		}
	}
	
	private int getColor(Voxel voxel) {

		int alpha = (int) (voxel.getAlpha() * 255) << 24;
		int red = (int) (voxel.getRed() * 255) << 16;
		int green = (int) (voxel.getGreen() * 255) << 8;
		int blue = (int) (voxel.getBlue() * 255);
		
		return alpha | red | green | blue;
	}
	
	private void swapRaster(ArrayList<ArrayList<Integer>> newRaster) {
		raster = newRaster;
	}
}