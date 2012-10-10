package visualization;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import pdf.Block;
import pdf.PDF;
import pdf.Page;

import com.itextpdf.text.Rectangle;

public class Visualizer {
	
	protected void FitText(Graphics g, Block block, Rectangle pageSize, double resizeCoef) {
	
		String[] lines = block.text.split("\n");
		int boxHeight = (int)(block.rectangle.getHeight() * resizeCoef / lines.length);
		
		double textHeight = boxHeight;
		
		Font f = new Font("Arial", block.isBold() ? Font.BOLD : Font.PLAIN, boxHeight);
        g.setColor(Color.black);
		g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        double shrink = ((double)textHeight / (double)fm.getHeight());
        double newSize = (double)textHeight * shrink;
        double newAsc  = (double)fm.getAscent() * shrink;
        int yOffset = (int)newAsc - fm.getLeading();
        f = f.deriveFont((float)newSize);
        g.setFont(f);
		
		for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
	        g.drawString(lines[lineIndex], (int)(resizeCoef * block.rectangle.x), (int)(resizeCoef * block.rectangle.y) + yOffset + lineIndex * boxHeight);
		}
		
		g.setColor(Color.red);
		g.drawRect((int)(resizeCoef * block.rectangle.x), (int)(resizeCoef * block.rectangle.y), (int)(resizeCoef * block.rectangle.width), (int)(resizeCoef * block.rectangle.height));
	}
	
	protected BufferedImage visualizePage(Page page, double resizeCoef) {

		Rectangle size = page.getSize();
		
		BufferedImage image = new BufferedImage((int)(resizeCoef * size.getWidth()), (int)(resizeCoef * size.getHeight()), BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		
		g.setColor(Color.black);
		
		for (Block b: page.blocks) {
			FitText(g, b, page.getSize(), resizeCoef);
		}
		
		return image;
	}
	
	public void visualize(PDF pdf, String imageFilesDir, double resizeCoef) throws Exception {
		
		int index = 0;
		
		for (Page p: pdf.pages) {
			
			BufferedImage image = visualizePage(p, resizeCoef);
			
			ImageIO.write(image, "JPEG", new File(imageFilesDir + "\\" + ++index + ".jpg"));
		}
	}
}
