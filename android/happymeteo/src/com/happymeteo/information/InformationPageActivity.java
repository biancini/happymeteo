package com.happymeteo.information;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.happymeteo.AppyMeteoLoggedActivity;
import com.happymeteo.R;

public class InformationPageActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page);
		super.onCreate(savedInstanceState);
		
		String help = "<html><body>" +
				"Per prima cosa, vorremmo che tu sapessi che <u>stai partecipando a un progetto di ricerca scientifico</u>. " +
				"Lo diciamo perch\u00E9, oltre al divertimento di usare la app, stai contribuendo a qualcosa di utile per tutti. " +
				"Lo scopo di appymeteo \u00E9, infatti, quello di raccogliere una mole di dati " +
				"che coprano l\'intero territorio italiano per produrre delle previsioni meteo di felicit\u00E0 su tutto " +
				"il Paese. Le previsioni riguarderanno sia tu che leggi, sia l\'area geografica che ti sta intorno. " +
				"Il benessere soggettivo \u00E9 ormai una dimensione universalmente considerata tra le determinanti della " +
				"qualit\u00E0 della vita, come \u00E9 certificato anche dall'Istat, che lo considera una delle 12 componenti del BES (Benessere Equo e Sostenibile)" +
				
				"Noi ci proponiamo l'obiettivo di mappare tutta Italia attraverso una metodologia che sia coinvolgente e partecipativa. " +
				"Le previsioni meteo della felicit\u00E0, infatti, sono costruite sulla base di quello che tu ci dirai, e di quello che ci " +
				"diranno gli altri utenti. Quanto siano accurate, dipende dunque anche e soprattutto da te! " +
				"Vuoi vedere quanto \u00E9 felice la tua citt\u00E0? Vuoi vedere se c'\u00E9 una zona di bassa pressione della felicit\u00E0 da cui stare al largo? " +
				"Nuvole di cattivo umore si addensano nel tuo futuro? Ti interessa tenere un diario della tua felicit\u00E0? " +
				"Vuoi confrontare la tua felicit\u00E0 con quella dei tuoi amici? Questa \u00E9 l'app giusta per te! " +
				
				"<br/><br/><b>Come funziona</b><br/>" +
				"Una volta iscritta/o, riceverai 4 impulsi in momenti casuali, durante la giornata. Con il minimo sforzo, " +
				"e in modo non invasivo, ti verr\u00E0 chiesto di rispondere a due domande  molto semplici. " +
				"S\u00EC, ma in parole potabili?<br/><br/>" +
				"&#8226; Registrati / crea un account oppure registrati attraverso il tuo account Facebook<br/>" +
				"&#8226; Divertiti, la sera, a guardare cosa ti succede intorno <br/>" +
				"&#8226; Controlla, al mattino, se ti aspetta una giornata felice<br/>" +
				"&#8226; Confronta la tua felicit\u00E0 con i tuoi amici invitandoli nella sezione appygame<br/>" +
				"<br/>" +
				"Perch\u00E9 mai dovrei fare questa scocciatura?<br/><br/>" +
				"&#8226; Te lo ripetiamo: si tratta di una ricerca scientifica e i dati serviranno a comprendere meglio cosa ti/ci rende pi\u00F9 o meno felici!<br/>" +
				"&#8226; Innanzitutto, perch\u00E9 \u00E9 divertente<br/>" +
				"&#8226; E poi perch\u00E9 ogni risposta ti fa guadagnare punti, cos\u00EC come invitare i tuoi amici a scaricare l'app e giocare. E una classifica aggiornata ti dir\u00E0 dove ti posizioni. <br/><br/>" +
				"Buon divertimento! E appymeteo! </body></html>";
		
		TextView information_page = (TextView) findViewById(R.id.information_page);
		information_page.setText(Html.fromHtml(help));
	}

	@Override
	public void onPostExecute(int id, String result, Exception exception) {
		// Do Nothing
	}
}