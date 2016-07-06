package myi.Stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.Mixer.Info;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.ferry.IBuffer;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IAudioSamples.Format;
import com.xuggle.xuggler.IPacket;

public class EncodeAudio implements Runnable{
	
	static TargetDataLine inputLine = null;
	Info[] lines = AudioSystem.getMixerInfo();
	ByteArrayOutputStream out = new ByteArrayOutputStream();

	int sampleRate = 44100;
    int sampleSizeInBits = 16;
    int channels = 1;
    int bufferSize = 2048;
	int bytesRead = 0;
    boolean signed = true;
    boolean bigEndian = false;
    AudioFormat format;// =  new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    IStreamCoder audOutStreamCoder;
	byte[] buffer = new byte[bufferSize];
	
	static long now = System.nanoTime();
    static long firstTimeStamp = 0; 
    static long timeStamp = 0;
	
	public EncodeAudio(IStreamCoder audOutStreamCoder){
		this.audOutStreamCoder = audOutStreamCoder;
		//Mixer.Info[] mixers = AudioSystem.getMixerInfo();
		//for (Mixer.Info mixerInfo : mixers){
		//    System.out.println(mixerInfo + " " + mixers);
		//}

	}
	
	@Override
	public void run() {
		
		format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100.0F, 16, 1, 2, 44100.0F, false);//new AudioFormat((float) sampleRate, sampleSizeInBits, channels, signed, bigEndian);
		DataLine.Info inInfo = new DataLine.Info(TargetDataLine.class, format);
        
    	try {
    		inputLine = (TargetDataLine)AudioSystem.getMixer(lines[3]).getLine(inInfo);
    		inputLine.open(format);
    		
    	} catch (LineUnavailableException e2) {
    		System.exit(0);
    	} 
    	
    	
    	inputLine.start();
    	while(true){
    		
    		IPacket audpacket = IPacket.make();
    		
    		bytesRead = inputLine.read(buffer, 0, bufferSize);
    		//out.write(buffer, 0 , bytesRead);
    		
    		

            IBuffer iBuf = IBuffer.make(null, buffer, 0, bytesRead);
            
            IAudioSamples samples = IAudioSamples.make(iBuf, channels, Format.FMT_S16);
            long numSamples = (bytesRead);
            //System.out.println(numSamples);

            
                 
            //if (firstTimeStamp == 0)
            //	firstTimeStamp = now;
            
            timeStamp = (now - System.nanoTime());
            samples.setComplete(true, numSamples,(int) format.getSampleRate(), format.getChannels(),IAudioSamples.Format.FMT_S16, timeStamp/1000);
            //samples.setComplete(true, numSamples, sampleRate, channels, Format.FMT_S16, timeStamp);
            samples.put(buffer, 0, 0, bytesRead);            
            audOutStreamCoder.encodeAudio(audpacket, samples, 0);
            if (audpacket.isComplete()){
            	//if(CreateStreamContainer.containerInUse == false){
        			CreateStreamContainer.addPacket(audpacket);
        		//}
            }
            
            //inputLine.stop();
    	}
	}
	
	


}
