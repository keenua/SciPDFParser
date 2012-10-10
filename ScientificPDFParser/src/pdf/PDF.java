package pdf;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;

public class PDF {
	
	public List<Page> pages;
	public String title;
	
	protected void GuessTitle() throws Exception {
		
		title = "UNKNOWN";
		
		List<Block> firstPageBlocks = pages.get(0).blocks;

		List<Block> centered = new ArrayList<Block>();
		
		for (Block b: firstPageBlocks) if (b.isCentered()) centered.add(b);
		
		if (centered.isEmpty()) centered = firstPageBlocks;
				
		float maxFontSize = 0f;
		List<Block> maxFontBlocks = new ArrayList<Block>();
		
		for (Block b: centered) 
			if (b.fontSize > maxFontSize) {
				maxFontSize = b.fontSize; 
				maxFontBlocks.clear();
				maxFontBlocks.add(b);
			}
			else if (b.fontSize == maxFontSize) maxFontBlocks.add(b);
				
		for (Block b: maxFontBlocks) if (b.isUpperCase()) {
			title = b.text;
			return;
		}
				
		if (!centered.isEmpty()) title = maxFontBlocks.get(0).text;
	}
	
	public PDF(String path) throws Exception {
		
		pages = new ArrayList<Page>();
		
		PdfReader reader = new PdfReader(new FileInputStream(new File(path)));
		
		PdfReaderContentParser parser = new PdfReaderContentParser(reader);
		
		for (int i = 1; i <= reader.getNumberOfPages(); i++) {
		
			Rectangle size = reader.getPageSize(i);
			
			List<Block> blocks = parser.processContent(i, new TextListener()).getBlocks(size);
			
			pages.add(new Page(blocks, size));
		}
		
		GuessTitle();
	}
}
