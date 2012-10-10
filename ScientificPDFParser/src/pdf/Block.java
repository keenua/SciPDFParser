package pdf;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.DocumentFont;

public class Block {
	public Rectangle2D.Float rectangle;
	public String text;
	public DocumentFont font;
	public float whiteSpaceWidth;
	public float lineHeight;
	public float fontSize;
	
	protected boolean bold;
	protected boolean centered;
	
	public boolean isBold() {
		return bold || font.getPostscriptFontName().toLowerCase().contains("-bold");
	}
	
	public boolean isUpperCase() throws Exception {
		for (char c: text.toCharArray()) 
			if (Character.isAlphabetic(c) && !Character.isUpperCase(c)) {
				return false;
			}
				
	    return true;
	}
	
	public boolean isCentered() {
		return centered;
	}
	
	public void setCentered(Rectangle pageSize) throws Exception {
		centered = Math.abs((float)rectangle.getCenterX() - pageSize.getWidth() / 2f) < pageSize.getWidth() * 0.01f; 
	}
	
	@Override
	public String toString() {
	
		try {
			StringBuilder builder = new StringBuilder();
			builder.append("(" + rectangle.x + ", " + rectangle.y + ") width " + rectangle.width + " height " + rectangle.height + "\n");
			builder.append("Font: " + fontSize);
			builder.append(isBold() ? " bold" : "");
			builder.append(isUpperCase() ? " UPPER" : "");
			builder.append("\n");
			if (isCentered()) builder.append("CENTERED\n");
			builder.append("\n" + text + "\n");
	
			return builder.toString();
		} catch (Exception e) { 
			return "ERROR";
		}
	}
	
	Rectangle2D.Float union(Rectangle2D.Float r1, Rectangle2D.Float r2) {
		Rectangle2D.Float res = new Rectangle2D.Float();
		
		res.x = Math.min(r1.x, r2.x);
		res.y = Math.min(r1.y, r2.y);
		res.width = (r1.x > r2.x) ? r1.x - r2.x + r1.width : r2.x - r1.x + r2.width;
		res.height = (r1.y > r2.y) ? r1.y - r2.y + r1.height : r2.y - r1.y + r2.height;
		
		return res;
	}
	
	public void mergeWith(Block block) {
		rectangle = union(rectangle, block.rectangle); 
		whiteSpaceWidth = Math.max(whiteSpaceWidth, block.whiteSpaceWidth);
		fontSize = Math.max(fontSize, block.fontSize);
		text += block.text;
		bold |= block.bold;
		centered &= block.centered;
	}
	
	public void saveLineHeight() {
		lineHeight = rectangle.height;
	}
	
	public Block(Rectangle2D.Float rectangle, String text, DocumentFont font, float whiteSpaceWidth, float textSize, boolean bold) {
		this.rectangle = rectangle;
		this.text = text;
		this.font = font;
		this.whiteSpaceWidth = whiteSpaceWidth;
		this.fontSize = text.trim().isEmpty() ? 0 : textSize;
		this.bold = bold;
	}
}
