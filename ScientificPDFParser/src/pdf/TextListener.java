package pdf;

import java.util.ArrayList;
import java.util.List;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import com.itextpdf.text.pdf.parser.Vector;

public class TextListener implements RenderListener {

	List<Block> blocks;
	
	@Override
	public void beginTextBlock() {
		// TODO Auto-generated method stub
	}

	@Override
	public void endTextBlock() {
		// TODO Auto-generated method stub
	}

	@Override
	public void renderImage(ImageRenderInfo arg0) {
		// TODO Auto-generated method stub
	}
	
	boolean isSameLine(Block block1, Block block2) {
		if (Math.abs(block1.rectangle.y - block2.rectangle.y) < Math.max(block1.rectangle.height, block2.rectangle.height)) return true;
		
		return false;
	}
	
	boolean isSameParagraph(Block block1, Block block2) throws Exception {

		if (block2.text.trim().isEmpty() || block1.isBold() != block2.isBold()) return false;
		
		float maxWhiteSpaceWidth = Math.max(block1.whiteSpaceWidth, block2.whiteSpaceWidth);
	
		if (Math.abs(block1.rectangle.x - block2.rectangle.x) > maxWhiteSpaceWidth
				&& Math.abs(block1.rectangle.x + block1.rectangle.width - block2.rectangle.x - block2.rectangle.width) > maxWhiteSpaceWidth
				&& Math.abs(block1.rectangle.x + block1.rectangle.width / 2 - block2.rectangle.x - block2.rectangle.width / 2) > maxWhiteSpaceWidth) return false;
		
		float diff;
		
		if (block1.rectangle.y > block2.rectangle.y) {
			diff = (block1.rectangle.y - block2.rectangle.height - block2.rectangle.y);
		} else {
			diff = (block2.rectangle.y - block1.rectangle.height - block1.rectangle.y);
		}
		
		return diff <= 0.2 * Math.min(block1.lineHeight, block2.lineHeight);
	}
	
	void mergeBlocks(Rectangle pageSize) throws Exception {

		for (int i = 0; i < blocks.size() - 1; i++) 
			for (int j = i + 1; j < blocks.size(); j++) {
				
				Block block1 = blocks.get(i);
				Block block2 = blocks.get(j);
				
				if (isSameLine(block1, block2)) {
					block1.mergeWith(block2);
					blocks.remove(j);
					block2 = null;
					j--;
				}
			}
		
		for (Block b: blocks) {
			b.saveLineHeight();
			b.setCentered(pageSize);
		}
		
		for (int i = 0; i < blocks.size() - 1; i++) 
			for (int j = i + 1; j < blocks.size(); j++) {
				
				Block block1 = blocks.get(i);
				Block block2 = blocks.get(j);
				
				if (isSameParagraph(block1, block2)) {
					block1.text += "\n";
					block1.mergeWith(block2);
					blocks.remove(j);
					block2 = null;
					j--;
				}
			}
	}
	
	@Override
	public void renderText(TextRenderInfo renderInfo) {
		
		// TODO Auto-generated method stub
		try {
			Rectangle2D.Float ascent = renderInfo.getAscentLine().getBoundingRectange();
			Rectangle2D.Float descent = renderInfo.getDescentLine().getBoundingRectange();
			
			Rectangle2D.Float rect = new Rectangle2D.Float(ascent.x, ascent.y, ascent.width, ascent.y - descent.y);
			
			Vector curBaseline = renderInfo.getBaseline().getStartPoint();
			Vector topRight = renderInfo.getAscentLine().getEndPoint();

			Rectangle textRect = new Rectangle(curBaseline.get(0), curBaseline.get(1), topRight.get(0), topRight.get(1));
			float curFontSize = textRect.getHeight();
			
			boolean bold = renderInfo.getTextRenderMode() == 2;
			
			Block block = new Block(rect, renderInfo.getText(), renderInfo.getFont(), renderInfo.getSingleSpaceWidth(), curFontSize, bold); 
			blocks.add(block);
		}
		catch (Exception e){

		}
	}
	
	public List<Block> getBlocks(Rectangle pageSize) throws Exception {
		mergeBlocks(pageSize);
		
		List<Block> result = new ArrayList<Block>();
		
		for (Block b: blocks) {
			
			if (b.text.trim().isEmpty()) continue;
			
			b.rectangle.y = pageSize.getHeight() - b.rectangle.y - b.rectangle.height;
			
			result.add(b);
		}
		
		return result;
	}
	
	public TextListener() {
		blocks = new ArrayList<Block>();
	}
}