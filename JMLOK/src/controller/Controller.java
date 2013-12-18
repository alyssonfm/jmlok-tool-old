package controller;

import categorize.Categorize;

public class Controller {
	
	public static void fillGui(int compiler, String source, String lib, String time){
		// chama categorize e preenche a gui com o resultado.
		Categorize.categorize(source, lib, time, compiler);
	}

}
