import pdf.Block;
import pdf.PDF;

public class Main {

	static final String defaultDir = "D:\\Work\\Misc\\Vernad\\pdf-extractor\\";

	static void PrintEachBlock(int pdfFileIndex) throws Exception {

		PDF pdf = new PDF(defaultDir + pdfFileIndex + ".pdf");

		for (Block b : pdf.pages.get(0).blocks) {
			System.out.println("===================");
			System.out.println(b);
		}
	}

	static void OpenPDF(String path) throws Exception {
		PDF pdf = new PDF(path);

		System.out.println(pdf.title.toUpperCase());
	}

	static void OpenAllPdfs(int count) throws Exception {
		for (int i = 1; i <= count; i++) {
			System.out.println("[" + i + "]");
			OpenPDF(defaultDir + i + ".pdf");
			System.out.println();
		}
	}

	public static void main(String args[]) throws Exception {
		// OpenAllPdfs(7);
		PrintEachBlock(1);
	}
}