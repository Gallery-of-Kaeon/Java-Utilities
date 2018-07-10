package speech;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Port;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;

public class Speech {
	
	public static ArrayList<Voice> voices;
	
	public static void speak(String text) {
		
		init();
		
		try {
			voices.get(0).tts.speak(text, 2.0f, false, true);
		}
		
		catch(Exception exception) {
			
		}
	}
	
	public static void speak(String text, String voice) {
		
		init();
		
		try {
			getVoice(voice).tts.speak(text, 2.0f, false, true);
		}
		
		catch(Exception exception) {
			
		}
	}
	
	public static void init() {
		
		if(voices == null) {
			
			voices = new ArrayList<Voice>();
			
			voices.add(getVoice("cmu-slt-hsmm"));
		}
	}
	
	public static Voice getVoice(String voice) {
		
		for(int i = 0; i < voices.size(); i++) {
			
			if(voices.get(i).voice.equals(voice))
				return voices.get(i);
		}
		
		try {
			
			Voice newVoice = new Voice();
			
			newVoice.voice = voice;
			
			TextToSpeech tts = new TextToSpeech();
			tts.setVoice(voice);
			
			newVoice.tts = tts;
			
			voices.add(newVoice);
			
			return newVoice;
		}
		
		catch(Exception exception) {
			
		}
		
		return null;
	}
	
	public boolean running = false;
	public boolean ready = false;
	
	private LiveSpeechRecognizer recognizer;
	
	private String speechRecognitionResult;
	
	private boolean ignoreSpeechRecognitionResults = false;
	private boolean resourcesThreadRunning;
	
	private ExecutorService eventsExecutorService = Executors.newFixedThreadPool(2);
	
	public Speech() {
		this(null);
	}
	
	public Speech(String grammar) {
		
		new Thread() {
			
			public void run() {
				
				Configuration configuration = new Configuration();
				
				configuration.setAcousticModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us");
				configuration.setDictionaryPath("resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict");
				
				if(grammar == null)
					configuration.setLanguageModelPath("resource:/edu/cmu/sphinx/models/en-us/en-us.lm.bin");
				
				else {
					configuration.setGrammarPath(grammar);
					configuration.setGrammarName("grammar");
					configuration.setUseGrammar(true);
				}
				
				try {
					recognizer = new LiveSpeechRecognizer(configuration);
				}
				
				catch (IOException ex) {
					
				}
				
				running = true;
				
				startResourcesThread();
				startSpeechRecognition();
			}
		}.start();
	}
	
	public synchronized void startSpeechRecognition() {
			
		eventsExecutorService.submit(() -> {
			
			ignoreSpeechRecognitionResults = false;
			
			recognizer.startRecognition(true);
			
			try {
				
				while (running) {
					
					ready = true;
					
					SpeechResult speechResult = recognizer.getResult();
					
					if(!ignoreSpeechRecognitionResults) {
						
						if (speechResult == null)
							;
						
						else {
							
							speechRecognitionResult = speechResult.getHypothesis();
							
							onHear(speechRecognitionResult);
						}
					}
					
					else
						;
				}
			}
			
			catch (Exception ex) {
				running = false;
			}
		});
	}
	
	public synchronized void stopIgnoreSpeechRecognitionResults() {
		ignoreSpeechRecognitionResults = false;
	}
	
	public synchronized void ignoreSpeechRecognitionResults() {
		ignoreSpeechRecognitionResults = true;
	}
	
	public void startResourcesThread() {
		
		if (resourcesThreadRunning)
			;
		
		else
			
			eventsExecutorService.submit(() -> {
				
				try {
					
					resourcesThreadRunning = true;
					
					while(running) {
						
						if (!AudioSystem.isLineSupported(Port.Info.MICROPHONE))
							;
						
						Thread.sleep(350);
					}
					
				} catch (InterruptedException ex) {
					resourcesThreadRunning = false;
				}
			});
	}
	
	public boolean getIgnoreSpeechRecognitionResults() {
		return ignoreSpeechRecognitionResults;
	}
	
	public boolean getSpeechRecognizerThreadRunning() {
		return running;
	}
	
	public void onHear(String speech) {
		
	}
}