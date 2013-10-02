package com.happymeteo;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class InformationPageActivity extends AppyMeteoLoggedActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_information_page);
		super.onCreate(savedInstanceState);
		
		String help = "<html><body>Lo scopo di appymeteo � quello di raccogliere una mole di dati " +
				"che coprano l\'intero territorio italiano per produrre delle previsioni meteo di felicit� su tutto " +
				"il Paese. Le previsioni riguarderanno sia tu che leggi, sia l\'area geografica che ti sta intorno. " +
				"Vuoi vedere quanto � felice il tuo quartiere? Vuoi vedere se c\'� una zona di bassa pressione della " +
				"felicit� in citt� da cui stare al largo? O se qualche nube di cattivo umore si addensa nel tuo futuro " +
				"prossimo? Vuoi tenere un diario della tua felicit�, consultabile nel tempo? Questa � l\'app giusta per te!" +
				"<br/><br/><b>Come funziona</b><br/>" +
				"Una volta iscritta/o, riceverai 4 impulsi in momenti casuali, durante la giornata. Con il minimo sforzo, " +
				"e in modo non invasivo, ti verr� chiesto di rispondere a due domande  molto semplici. " +
				"S�, ma in parole semplici?<br/><br/>" +
				"&#8226; Registrati / crea un account<br/>" +
				"&#8226; Aspetta gli impulsi del tuo smartphone e rispondi alle domande che ti verranno poste<br/>" +
				"&#8226; Ricevi di sera le previsioni meteo della tua felicit� e divertiti a guardare cosa ti succede intorno<br/>" +
				"&#8226; Vuoi vedere quanto sono felici i tuoi amici di Facebook? Invitali nella sezione 'Game'<br/>" +
				"<br/>" +
				"Perch� mai dovrei fare questa scocciatura?<br/><br/>" +
				"&#8226; Innanzitutto, perch� � divertente<br/>" +
				"&#8226; E poi perch� ogni risposta ti fa guadagnare punti, cos� come invitare i tuoi amici a scaricare l'app e giocare. E una classifica aggiornata ti dir� dove ti posizioni. <br/><br/>" +
				"Buon divertimento! E appymeteo! </body></html>";
		
		TextView information_page = (TextView) findViewById(R.id.information_page);
		information_page.setText(Html.fromHtml(help));
	}
}