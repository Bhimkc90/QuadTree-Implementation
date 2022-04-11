import java.io.*;
import java.util.*;

//******************* QtNode Class **********************
class QtNode{
	int color;
	int upperR;
	int upperC;
	int size;

	QtNode NWkid = null;
	QtNode NEkid = null;
	QtNode SWkid = null;
	QtNode SEkid = null;

	QtNode(int size, int upperR, int upperC, int color, QtNode NWkid, QtNode NEkid, QtNode SWkid, QtNode SEkid){
		this.upperR = upperR;
		this.upperC = upperC;
		this.size = size;
		this.NWkid = NWkid;
		this.NEkid = NEkid;
		this.SWkid = SWkid;
		this.SEkid = SEkid;
	}

//******************* Printing QNodes **********************

	public void printQtNode(QtNode node, FileWriter  outFile) throws IOException {
		if (node.NWkid == null && node.NEkid == null && node.SWkid == null && node.SEkid == null) {
			outFile.write("color:" + node.color + ", upperC:" + node.upperC + ", upperR: " + node.upperR + ", NWkid color:" + null + ", NEkid color:" + null + ", SWkid color:" + null + ", SEkid color:" + null + "\n");
		}else{
			outFile.write("color:" + node.color + ", upperC:" + node.upperC + ", upperR: " + node.upperR + ", NWkid color:" + node.NWkid.color + ", NEkid color:" + node.NEkid.color + ", SWkid color:" + node.SEkid.color + ", SEkid color:" + node.SWkid.color + "\n");
		}
	}
}


//******************* QuadTree Class **********************
class QuadTree{
	int numRows;
	int numCols;
	int minVal;
	int maxVal;
	int power2;
	int imgAry[][];
	QtNode QtRoot;

	public QuadTree(int numRows, int numCols, int minVal, int maxVal){
		this.numRows = numRows;
		this.numCols = numCols;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
//******************* Compute Power of 2 **********************
	public int computePower2(){
		int size = Math.max(numRows, numCols);
		int power2 = 2;
		while(size > power2){
			power2 *= 2;
		}
		return power2;
	}
	
//******************* Method to load Image **********************

	public void loadImage(FileReader inFile, int[][] imgAry){
		BufferedReader sc = new BufferedReader(inFile);
		Scanner scan = new Scanner(sc);
		int r = 0, c = 0, data;
		while(r < numRows){
			c = 0;
			while(c < numCols){
				if(scan.hasNextInt()){
					data = scan.nextInt();  
					imgAry[r][c] = data;
				}
				c++;
			}
			r++;
		}
		scan.close();
	}

//******************* Method to build the Quad Tree  **********************

	public QtNode buildQuadTree(int imgAry[][], int upR, int upC, int size, FileWriter outFile2) throws IOException{
		QtNode newQtNode = new QtNode(-1, upR, upC, size, null, null, null, null);
		newQtNode.printQtNode(newQtNode, outFile2);
		if(size == 1){
			newQtNode.color = imgAry[upR][upC];
		}else{
			int halfSize = size / 2;

			newQtNode.NWkid = buildQuadTree(imgAry, upR, upC, halfSize, outFile2);
			newQtNode.NEkid = buildQuadTree(imgAry, upR, upC + halfSize, halfSize, outFile2);
			newQtNode.SWkid = buildQuadTree(imgAry, upR + halfSize, upC, halfSize, outFile2);
			newQtNode.SEkid = buildQuadTree(imgAry, upR + halfSize, upC + halfSize, halfSize, outFile2);
			int sumColor = sumKidsColor(newQtNode);
			if(sumColor == 0){
				newQtNode.color = 0;
				setLeaf(newQtNode);
			}else{
				if(sumColor == 4){
					newQtNode.color = 1;
					setLeaf(newQtNode);
				}else{
					newQtNode.color = 5;
				}
			}
		}
		return newQtNode;
	}
	
//******************* Kids color method **********************

	public int sumKidsColor(QtNode newQtNode){
		return newQtNode.NWkid.color + newQtNode.NEkid.color + newQtNode.SWkid.color + newQtNode.SEkid.color;
	}
	
//******************* Method to set leaf node **********************
	public void setLeaf(QtNode newQtNode){
		newQtNode.NWkid = null;
		newQtNode.NEkid = null;
		newQtNode.SWkid = null;
		newQtNode.SEkid = null;
	}
	

//******************* Method to check if a node is leaf node **********************
	
	public boolean isLeaf(QtNode node){
		return (node.NWkid == null && node.NEkid == null && node.SWkid == null && node.SEkid == null);
	}

	//******************* PreOrder Method **********************

	public void preOrder(QtNode Qt, FileWriter outFile) throws IOException {
		if(isLeaf(Qt)){
			Qt.printQtNode(Qt, outFile);
			return;
		}
		else{
			Qt.printQtNode(Qt, outFile);
			preOrder(Qt.NWkid, outFile);
			preOrder(Qt.NEkid, outFile);
			preOrder(Qt.SWkid, outFile);
			preOrder(Qt.SEkid, outFile);


		}
	}

	//******************* postOrder Method **********************

	public void postOrder(QtNode Qt, FileWriter outFile) throws IOException {
		if(isLeaf(Qt)){
			Qt.printQtNode(Qt, outFile);
			return;
		}
		else{
			preOrder(Qt.NWkid, outFile);
			preOrder(Qt.NEkid, outFile);
			preOrder(Qt.SWkid, outFile);
			preOrder(Qt.SEkid, outFile);
			Qt.printQtNode(Qt, outFile);
		}
	}
}

//******************* Main Class **********************

public class Main {

	public static void main(String[] args) throws IOException {
		if(args.length != 3){
			System.out.println("Input insufficient!: need one imgFile and two outFile");
			return;
		}
		FileReader inFile = new FileReader(args[0]);
		FileWriter outFile1 = new FileWriter(args[1]);
		FileWriter outFile2 = new FileWriter(args[2]);
		
		Scanner sc = new Scanner(inFile);
		int numRows = sc.nextInt();
		int numCols = sc.nextInt();
		int minVal = sc.nextInt();
		int maxVal = sc.nextInt();

		QuadTree qt = new QuadTree(numRows, numCols, minVal, maxVal);

		qt.power2 = qt.computePower2();
		qt.imgAry = new int[qt.power2][qt.power2];
		qt.loadImage(inFile, qt.imgAry);

		sc.close();

		int r = 0, c = 0;
		outFile2.write("Printing current imgAry: \n");
		for(int i = 0; i < qt.power2; i++){
			for(int j = 0; j < qt.power2; j++){
				outFile2.write(qt.imgAry[i][j]+" ");
			}
			outFile2.write("\n");
		}

		outFile2.write("\n");
		qt.QtRoot = qt.buildQuadTree(qt.imgAry, 0, 0, qt.power2, outFile2);
		qt.preOrder(qt.QtRoot, outFile1);
		qt.postOrder(qt.QtRoot, outFile1);

		inFile.close();
		outFile1.close();
		outFile2.close();

	}

}