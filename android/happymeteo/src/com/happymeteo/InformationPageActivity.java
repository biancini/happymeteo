package com.happymeteo;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class InformationPageActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page);
		super.onCreate(savedInstanceState);
		
		String help = "<html><body>" +
				"Per prima cosa, vorremmo che tu sapessi che <u>stai partecipando a un progetto di ricerca scientifico</u>. " +
				"Lo diciamo perché, oltre al divertimento di usare la app, stai contribuendo a qualcosa di utile per tutti. " +
				"Lo scopo di appymeteo è, infatti, quello di raccogliere una mole di dati " +
				"che coprano l\'intero territorio italiano per produrre delle previsioni meteo di felicità su tutto " +
				"il Paese. Le previsioni riguarderanno sia tu che leggi, sia l\'area geografica che ti sta intorno. " +
				"Il benessere soggettivo è ormai una dimensione universalmente considerata tra le determinanti della " +
				"qualità della vita, come è certificato anche dall’Istat, che lo considera una delle 12 componenti del BES (Benessere Equo e Sostenibile)" +
				
				"Noi ci proponiamo l’obiettivo di mappare tutta Italia attraverso una metodologia che sia coinvolgente e partecipativa. " +
				"Le previsioni meteo della felicità, infatti, sono costruite sulla base di quello che tu ci dirai, e di quello che ci " +
				"diranno gli altri utenti. Quanto siano accurate, dipende dunque anche e soprattutto da te! " +
				"Vuoi vedere quanto è felice la tua città? Vuoi vedere se c'è una zona di bassa pressione della felicità da cui stare al largo? " +
				"Nuvole di cattivo umore si addensano nel tuo futuro? Ti interessa tenere un diario della tua felicità? " +
				"Vuoi confrontare la tua felicità con quella dei tuoi amici? Questa è l'app giusta per te! " +
				
				"<br/><br/><b>Come funziona</b><br/>" +
				"Una volta iscritta/o, riceverai 4 impulsi in momenti casuali, durante la giornata. Con il minimo sforzo, " +
				"e in modo non invasivo, ti verrà chiesto di rispondere a due domande  molto semplici. " +
				"Sì, ma in parole potabili?<br/><br/>" +
				"&#8226; Registrati / crea un account oppure registrati attraverso il tuo account Facebook<br/>" +
				"&#8226; Divertiti, la sera, a guardare cosa ti succede intorno <br/>" +
				"&#8226; Controlla, al mattino, se ti aspetta una giornata felice<br/>" +
				"&#8226; Confronta la tua felicità con i tuoi amici invitandoli nella sezione appygame<br/>" +
				"<br/>" +
				"Perchè mai dovrei fare questa scocciatura?<br/><br/>" +
				"&#8226; Te lo ripetiamo: si tratta di una ricerca scientifica e i dati serviranno a comprendere meglio cosa ti/ci rende più o meno felici!" +
				"&#8226; Innanzitutto, perché è divertente<br/>" +
				"&#8226; E poi perché ogni risposta ti fa guadagnare punti, così come invitare i tuoi amici a scaricare l'app e giocare. E una classifica aggiornata ti dirà dove ti posizioni. <br/><br/>" +
				"Buon divertimento! E appymeteo! </body></html>";
		
		TextView information_page = (TextView) findViewById(R.id.information_page);
		information_page.setText(Html.fromHtml(help));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}