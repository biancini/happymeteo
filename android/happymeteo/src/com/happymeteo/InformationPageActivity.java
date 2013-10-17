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
				"Lo diciamo perch�, oltre al divertimento di usare la app, stai contribuendo a qualcosa di utile per tutti. " +
				"Lo scopo di appymeteo �, infatti, quello di raccogliere una mole di dati " +
				"che coprano l\'intero territorio italiano per produrre delle previsioni meteo di felicit� su tutto " +
				"il Paese. Le previsioni riguarderanno sia tu che leggi, sia l\'area geografica che ti sta intorno. " +
				"Il benessere soggettivo � ormai una dimensione universalmente considerata tra le determinanti della " +
				"qualit� della vita, come � certificato anche dall�Istat, che lo considera una delle 12 componenti del BES (Benessere Equo e Sostenibile)" +
				
				"Noi ci proponiamo l�obiettivo di mappare tutta Italia attraverso una metodologia che sia coinvolgente e partecipativa. " +
				"Le previsioni meteo della felicit�, infatti, sono costruite sulla base di quello che tu ci dirai, e di quello che ci " +
				"diranno gli altri utenti. Quanto siano accurate, dipende dunque anche e soprattutto da te! " +
				"Vuoi vedere quanto � felice la tua citt�? Vuoi vedere se c'� una zona di bassa pressione della felicit� da cui stare al largo? " +
				"Nuvole di cattivo umore si addensano nel tuo futuro? Ti interessa tenere un diario della tua felicit�? " +
				"Vuoi confrontare la tua felicit� con quella dei tuoi amici? Questa � l'app giusta per te! " +
				
				"<br/><br/><b>Come funziona</b><br/>" +
				"Una volta iscritta/o, riceverai 4 impulsi in momenti casuali, durante la giornata. Con il minimo sforzo, " +
				"e in modo non invasivo, ti verr� chiesto di rispondere a due domande  molto semplici. " +
				"S�, ma in parole potabili?<br/><br/>" +
				"&#8226; Registrati / crea un account oppure registrati attraverso il tuo account Facebook<br/>" +
				"&#8226; Divertiti, la sera, a guardare cosa ti succede intorno <br/>" +
				"&#8226; Controlla, al mattino, se ti aspetta una giornata felice<br/>" +
				"&#8226; Confronta la tua felicit� con i tuoi amici invitandoli nella sezione appygame<br/>" +
				"<br/>" +
				"Perch� mai dovrei fare questa scocciatura?<br/><br/>" +
				"&#8226; Te lo ripetiamo: si tratta di una ricerca scientifica e i dati serviranno a comprendere meglio cosa ti/ci rende pi� o meno felici!" +
				"&#8226; Innanzitutto, perch� � divertente<br/>" +
				"&#8226; E poi perch� ogni risposta ti fa guadagnare punti, cos� come invitare i tuoi amici a scaricare l'app e giocare. E una classifica aggiornata ti dir� dove ti posizioni. <br/><br/>" +
				"Buon divertimento! E appymeteo! </body></html>";
		
		TextView information_page = (TextView) findViewById(R.id.information_page);
		information_page.setText(Html.fromHtml(help));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}