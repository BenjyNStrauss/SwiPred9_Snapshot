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

public class Main_App extends AbstractMain {
	
	public Main_App(NetworkBase module) {
		super(module);
	}

	protected int SaveResults(String fileName) {
		int i;
		float[] out = new float[9];
		File fileWrite;

		fileWrite = fopen(fileName,"w");
	  
		if(!fileWrite.exists()) {
			return 1;
		}

		for(i=0; i < vec_number; i++) {
			fprintf(fileWrite,"# %d\n",i);

			network.net(data[i], out, 0);

			fprintf(fileWrite,"%f ",out[0]);
			fprintf(fileWrite,"\n");
		}

		fclose(fileWrite);
		return 0;
	}
	
	@SuppressWarnings("resource")
	protected int ReadFile(String fileName) {
		Scanner input = null;
		File fileRead;
		String buffor;
		int i,j;
	  
		fileRead = fopen(fileName, "r");
		
		if(!fileRead.exists()) { 
			return 1;
		}
	  
		try {
			input = new Scanner(fileRead);
		} catch (FileNotFoundException e) {
			throw new FileNotFoundRuntimeException(e);
		}
		
		data = new float[vec_number][];
		for(i=0;i<vec_number;i++) {
			data[i] = new float[features];
		}
	   
		//fscanf(fileRead,"%s", &buffor[0]);
		buffor = input.next();
		while(strcmp(buffor, "output")) {
			//fscanf(fileRead,"%s", &buffor[0]);
			buffor = input.next();
		}

		//fscanf(fileRead,"%s",&buffor[0]);
		//fscanf(fileRead,"%s",&buffor[0]);
		//fscanf(fileRead,"%s",&buffor[0]);
		buffor = input.next();
		buffor = input.next();
		buffor = input.next();
		
		for(i=0;i<vec_number;i++) {
			for(j=0;j<features;j++) {
				//fscanf(fileRead,"%f", &data[i][j]);
				data[i][j] = input.nextFloat();
			}
		}
		
		input.close();
		fclose(fileRead);

	  	return 0;
	}

	/*##############################################################
	###Parameters: number of features, number of vectors, file name
	##############################################################*/
	@SuppressWarnings("unused")
	public String main(String[] args) {
		StringBuilder output = new StringBuilder();
		int res, i;

		if(args.length < 4) {
			output.append("Number of parameters have to be 4\n");
			output.append("number of features\nnumber of vectors\ndata file name\noutput file nmae\n");
			exit(-1);
		}

		features = atoi(args[1]);
		vec_number = atoi(args[2]);

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
