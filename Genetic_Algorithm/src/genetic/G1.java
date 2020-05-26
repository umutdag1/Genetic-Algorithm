package genetic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class G1 {
	private static int[] X;
	private static int result = 0;
	private static double[] f_obj;
	private static double[] fitness;
	private static double fitnesstotal = 0;
	private static double[] probability;
	private static double[] cumulative;
	private static double[] R;
	private static int[] C;
	private static boolean control = true;
	private static G1 g1;
	private static population popu;
	private static List<chromosome> listch,newlistch,parents;
	private static int chromosomesizeIn ;
	public static void main(String[] args) {
		System.out.println("This program finds variables of unknown in a function.");
		System.out.println("Please enter the number of your function's inputs : ");
		Scanner scan = new Scanner(System.in);
		chromosomesizeIn = scan.nextInt();
		System.out.println("Enter your function's input's coefficients : ");
		int position = 0;
		X = new int[chromosomesizeIn];
		while(position < chromosomesizeIn) {
			System.out.println("X"+position+":");
			X[position] = scan.nextInt();
			position++;
		}
		System.out.println("Your Function : ");
		for(int i = X.length-1; i >= 0; i--) {
			if(i != 0) {
				System.out.print(X[i]+"X"+i+"+");
			}
			else {
				System.out.print(X[i]+" = 0");
			}
		}
		chromosomesizeIn-=1;
		g1 = new G1();
		popu = g1.new population(6,chromosomesizeIn,X[0]);
		listch = popu.returnListCh();
		while(control) {
			F_ObjResult();
			fitness();
			probability();
			cumulative();
			random_number();
			compare_random_cumulative();
			select_parents();
			crossover_break_points();
			if(parents.size() != 0) {
				crossover();
				mutation();
				final_control();
			}

		}
		
		System.out.println("\nResult : " + Arrays.toString(listch.get(result).array));
		
	}
	
	public static void F_ObjResult(){
		f_obj = new double[popu.chromosomesize];
		for(int i = 0; i < f_obj.length; i++) {
			for(int j = 0; j < chromosomesizeIn; j++) {
				f_obj[i]  += X[j+1]*listch.get(i).array[j];
			}
			f_obj[i] = Math.abs(f_obj[i] - X[0]);
		}
	}
	
	public static void fitness() {
		fitness = new double[popu.chromosomesize];
		for(int i = 0; i < fitness.length; i++) {
			fitness[i] = 1 / (1 + f_obj[i]); 
		}
		for(int i = 0; i < fitness.length; i++) {
			fitnesstotal += fitness[i];
		}
	}
	
	public static void probability() {
		probability = new double[popu.chromosomesize];
		for(int i = 0 ; i < probability.length; i++) {
			probability[i] = fitness[i] / fitnesstotal;
		}
	}
	
	public static void cumulative() {
		cumulative = new double[popu.chromosomesize];
		for(int i = 0; i < cumulative.length; i++) {
			if(i == 0) {
				cumulative[0] = probability[0];
				continue;
			}
			for(int j = 0; j < i + 1; j++) {
				cumulative[i] += probability[j];
			}
		}
		cumulative[cumulative.length - 1] = Math.round(cumulative[cumulative.length - 1]);
	}
	
	public static void random_number() {
		R = new double[popu.chromosomesize];
		for(int i = 0; i < R.length; i++) {
			double random = Math.random();
			R[i] = random;
		}
	}
	
	public static void compare_random_cumulative() {
		newlistch = new ArrayList<>();
		for(int i = 0; i < R.length; i++) {
			for(int j = 0 ; j < cumulative.length - 1; j++) {
				if(R[i] > 0 && R[i] < cumulative[1]) {
					newlistch.add(listch.get(1));
					break;
				} else if(R[i] > cumulative[j] && R[i] < cumulative[j+1]) {
					newlistch.add(listch.get(j+1));
				    break;
				}
			}
		}
	}
	
	public static void select_parents() {
		int k = 0;
		R = new double[popu.chromosomesize];
	    parents = new ArrayList<>();
		double crossover_rate = 0.30;
		while(k < popu.chromosomesize) {
			R[k] = Math.random();
			if(R[k] < crossover_rate) {
				parents.add(listch.get(k));
			}
			k++;
		}
	}
	
	public static void crossover_break_points() {
		C = new int[parents.size()];
		for(int i = 0; i < C.length; i++) {
			C[i] = (int) Math.round(Math.random()*(C.length-1));

		}
	}
	
	public static void crossover() {
		int[] keepfirstch = new int[popu.chromosomesizeIn];
		for(int i = 0; i < popu.chromosomesizeIn(); i++) {
			keepfirstch[i] = parents.get(0).array[i];
		}
		
		for(int i = 0; i < parents.size() - 1; i+=1) {
			for(int j = C[i] + 1; j < popu.chromosomesizeIn(); j++) {
				parents.get(i).array[j] = parents.get(i+1).array[j];
			}
		}
		
		for(int i = 0; i < popu.chromosomesizeIn(); i++) {
			parents.get(parents.size() - 1).array[i] = keepfirstch[i];
		}
	}
	
	public static void mutation() {
		int total_gen = popu.chromosomesize() * popu.chromosomesizeIn();
		double mutation_rate = 0.05;
		int mutation_score = (int) Math.round(total_gen * mutation_rate);
		
		int[] totalarray = new int[popu.chromosomesize()];
		for(int i = 0; i < popu.chromosomesize(); i++) {
			for(int j = 0 ; j < chromosomesizeIn; j++) {
				totalarray[i] += X[j+1]*listch.get(i).array[j];
			}
			totalarray[i] = totalarray[i]-X[0];
		}
		int max = 0;
		int keep = 0;
		for(int i = 0; i < popu.chromosomesize(); i++) {
			if(totalarray[i] > max) {
				max = totalarray[i];
				keep = i;
			}
		}
		int gen = 0;
		while(mutation_score > gen) {
			int randomgenselect = (int)Math.round(Math.random() * (popu.chromosomesizeIn - 1));
			int randomnumberselect = (int)Math.round(Math.random() * X[0]);
			listch.get(keep).array[randomgenselect] = randomnumberselect;
			gen++;
		}
	}
	
	public static void final_control() {
		for(int i = 0; i < popu.chromosomesize(); i++) {
			int res = 0;
			for(int j = 0; j < chromosomesizeIn; j++) {
				res += X[j+1]*listch.get(i).array[j];
			}
			if(res - X[0] == 0) {
			    result = i;
			    control = false;
			    break;
			} 
		}
	}
	
	public void printCh(List<chromosome> list) {
		for(int i = 0; i < list.size(); i++) {
			System.out.println(Arrays.toString(list.get(i).array));
		}
	}
	
	class chromosome{
		private int[] array;
		public chromosome(int size,int constant) {
			array = new int[size];
			for(int i = 0; i < size; i++ ) {
				int random = (int) Math.round(Math.random() * constant);
				array[i] = random;
			}
		}
	}

	class population{
		private int chromosomesize;
		private int chromosomesizeIn;
		private int constant;
		public population(int chromosomesize, int chromosomesizeIn,int constant) {
			this.chromosomesize = chromosomesize;
			this.chromosomesizeIn = chromosomesizeIn;
			this.constant = constant;
		}
		
		public ArrayList<chromosome> returnListCh(){
			ArrayList<chromosome> listch = new ArrayList<>();
			for(int i = 0; i < chromosomesize; i++) {
				listch.add(new chromosome(chromosomesizeIn,constant));
			}
			return listch;
		}
		
		public int chromosomesizeIn() {
			return chromosomesizeIn;
		}
		
		public int chromosomesize() {
			return chromosomesize;
		}
	}

}


