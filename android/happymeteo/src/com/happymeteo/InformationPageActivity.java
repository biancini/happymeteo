package com.happymeteo;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class InformationPageActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page);
		super.onCreate(savedInstanceState);
		
		String help = "<html><body>Lo scopo di appymeteo è quello di raccogliere una mole di dati " +
				"che coprano l\'intero territorio italiano per produrre delle previsioni meteo di felicità su tutto " +
				"il Paese. Le previsioni riguarderanno sia tu che leggi, sia l\'area geografica che ti sta intorno. " +
				"Vuoi vedere quanto è felice il tuo quartiere? Vuoi vedere se c\'è una zona di bassa pressione della " +
				"felicità in città da cui stare al largo? O se qualche nube di cattivo umore si addensa nel tuo futuro " +
				"prossimo? Vuoi tenere un diario della tua felicità, consultabile nel tempo? Questa è l\'app giusta per te!" +
				"<br/><br/><b>Come funziona</b><br/>" +
				"Una volta iscritta/o, riceverai 4 impulsi in momenti casuali, durante la giornata. Con il minimo sforzo, " +
				"e in modo non invasivo, ti verrà chiesto di rispondere a due domande  molto semplici. " +
				"Sì, ma in parole semplici?<br/><br/>" +
				"&#8226; Registrati / crea un account<br/>" +
				"&#8226; Aspetta gli impulsi del tuo smartphone e rispondi alle domande che ti verranno poste<br/>" +
				"&#8226; Ricevi di sera le previsioni meteo della tua felicità e divertiti a guardare cosa ti succede intorno<br/>" +
				"&#8226; Vuoi vedere quanto sono felici i tuoi amici di Facebook? Invitali nella sezione 'Game'<br/>" +
				"<br/>" +
				"Perchè mai dovrei fare questa scocciatura?<br/><br/>" +
				"&#8226; Innanzitutto, perché è divertente<br/>" +
				"&#8226; E poi perché ogni risposta ti fa guadagnare punti, così come invitare i tuoi amici a scaricare l'app e giocare. E una classifica aggiornata ti dirà dove ti posizioni. <br/><br/>" +
				"Buon divertimento! E appymeteo! </body></html>";
		
		TextView information_page = (TextView) findViewById(R.id.information_page);
		information_page.setText(Html.fromHtml(help));
	}
}