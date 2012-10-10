package pdf;

import java.util.List;

import com.itextpdf.text.Rectangle;

public class Page {
	public List<Block> blocks;
	
	protected Rectangle size;
	
	public Rectangle getSize() {
		return size;
	}
	
	public Page(List<Block> blocks, Rectangle size) {
		this.blocks = blocks;
		this.size = size;
	}
}
