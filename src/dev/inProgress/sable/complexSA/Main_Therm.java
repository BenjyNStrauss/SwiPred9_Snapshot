package dev.inProgress.sable.complexSA;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import assist.exceptions.FileNotFoundRuntimeException;
import dev.inProgress.sable.AbstractMain;
import dev.inProgress.sable.NetworkBase;

/**
 * 
 * @translator Benjamin Strauss
 * 
 */

public class Main_Therm extends AbstractMain {
	
	public Main_Therm(NetworkBase network) {
		super(network);
	}

	protected int SaveResults(String fileName) {
		int i,j;
		float[] out = new float[20];
		File fileWrite;

		fileWrite=fopen(fileName,"w");
	  
		if(!fileWrite.exists()) {
			return 1;
		}

		for(i=0;i<vec_number;i++) {
			printf("\n# %d\n",i);
			for(j=0;j<features;j++) {
				printf(" %f",data[i][j]);
			}
			network.net(data[i],out,0);
	      
			for(j=0;j<20;j++) {
				fprintf(fileWrite,"%f ",out[j]);
			}
			fprintf(fileWrite,"\n");
		}

		fclose(fileWrite);

		return 0;
	}

	protected int ReadFile(String fileName) {
		File fileRead;
		int i,j;
	  
		fileRead=fopen(fileName,"r");

		if(!fileRead.exists()) {
			return 1;
		}

		data = new float[vec_number][features];
		
		Scanner fileScan = null;
		try {
			fileScan = new Scanner(fileRead);
		} catch (FileNotFoundException FNFE) {
			throw new FileNotFoundRuntimeException(FNFE);
		}
		for(i=0;i<vec_number;i++) {
			for(j=0;j<features;j++) {
				data[i][j] = fileScan.nextFloat();
			}
		}

		fclose(fileRead);
		fileScan.close();
		return 0;
	}

	/*##############################################################
	###Parameters: number of features, number of vectors, file name
	##############################################################*/
	public String main(String[] args) {
		StringBuilder output = new StringBuilder();
		int res;

		if(args.length<4) {
			output.append("Number of parameters have to be 4\n");
			output.append("number of features\nnumber of vectors\ndata file name\noutput file nmae\n");
			exit(-1);
		}
		
		features=atoi(args[1]);
		vec_number=atoi(args[2]);

		printf("\n features=%d vec_num=%d",features,vec_number);

		res = ReadFile(args[3]);
		if(res != 0) {
			output.append("Reading file "+args[3]+" ERROR\n");
			exit(-1);
		}
		res = SaveResults(args[4]);
		if(res != 0) {
			output.append("Result file "+args[4]+" ERROR\n");
			exit(-1);
		}
		return output.toString();
	}
}
