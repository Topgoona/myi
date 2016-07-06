package myi.Stream;

import java.awt.image.BufferedImage;
import java.io.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.xuggle.xuggler.Global;
import com.xuggle.xuggler.IAudioSamples;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.IVideoResampler;
import com.xuggle.xuggler.Utils;
import com.xuggle.xuggler.demos.VideoImage;

public class DecodeAndPlayStream implements Runnable{
	
	static int videoStreamId = -1;
	static int audioStreamId = -1;
	static IStreamCoder videoCoder = null;
	static IStreamCoder audioCoder = null;
	static IStream vidstream = null;
	static IStream audstream = null;
	
	static int i = 0;
	
	static SourceDataLine sdLine;
	static VideoImage mScreen = null;


	public DecodeAndPlayStream(InputStream in) {
		
		IContainer inputCont = IContainer.make();
		IContainerFormat informat = IContainerFormat.make();
		informat.setInputFormat("flv, null, null");
		inputCont.open( in, informat, true, false);


		//Check number of streams in container
		int numStreams = inputCont.getNumStreams();
		System.out.println(inputCont.getNumStreams());
		
		//Check for video streams
		for(int i = 0; i < numStreams; i++){
			// Find the stream object
			vidstream = inputCont.getStream(i);
		  
			// Get the pre-configured decoder that can decode this stream;
			IStreamCoder vcoder = vidstream.getStreamCoder();

			if (vcoder.getCodecType() == ICodec.Type.CODEC_TYPE_VIDEO){
				videoStreamId = i;
				videoCoder = vcoder;
				System.out.println("Found video stream");
				System.out.println("Stream id is " + videoStreamId + " " + videoCoder);
				break;
			}
		}
		//If no video streams are present throw exception
		if (videoStreamId == -1)
		  throw new RuntimeException("There were no video streams. ");
		
		
		//Check for audio streams
		//for(int i = 0; i < numStreams; i++){
			// Find the stream object
		//	audstream = inputCont.getStream(i);
			
			// Get the pre-configured decoder that can decode this stream;
		//	IStreamCoder acoder = audstream.getStreamCoder();
        
		//	if (acoder.getCodecType() == ICodec.Type.CODEC_TYPE_AUDIO){
		//		audioStreamId = i;
		//		audioCoder = acoder;
		//		System.out.println("Found audio stream");
		//		System.out.println("Stream id is " + audioStreamId + " " + audioCoder);
		//		break;
		//	}
		//}
		//If no audio streams are present throw exception
		//if (audioStreamId == -1)
		//	throw new RuntimeException("There were no audio streams.");
		
		if (videoCoder.open() < 0)
		    throw new RuntimeException("Sorry, I could not open the video decoder for received stream");
		//if (audioCoder.open() < 0)
		//	throw new RuntimeException("Sorry, Icould not open the audio decoder for received stream");

		
		//Check file video format is BGR24, if not, convert using VideoResampler
		IVideoResampler resampler = null;
		if (videoCoder.getPixelType() != IPixelFormat.Type.BGR24){
			resampler = IVideoResampler.make(videoCoder.getWidth(), 
			videoCoder.getHeight(), IPixelFormat.Type.BGR24,
			videoCoder.getWidth(), videoCoder.getHeight(), videoCoder.getPixelType());
			if (resampler == null)
				throw new RuntimeException("Could not create color space resampler.");
		}
		
		openJavaWindow();
		//openJavaSound(audioCoder);
		
		IPacket packet = IPacket.make();
		long firstTimestampInStream = Global.NO_PTS;
		long startTime = 0;
		
		while(inputCont.readNextPacket(packet) >= 0){
		     
			if (packet.getStreamIndex() == videoStreamId){
				IVideoPicture picture = IVideoPicture.make(videoCoder.getPixelType(),
		        videoCoder.getWidth(), videoCoder.getHeight());

				int offset = 0;
				while(offset < packet.getSize()){
					int bytesDecoded = videoCoder.decodeVideo(picture, packet, offset);
					if (bytesDecoded < 0)
						throw new RuntimeException("Error decoding video from stream");
		      
					offset += bytesDecoded;

					if (picture.isComplete()){
						IVideoPicture newPic = picture;
						if (resampler != null){
							newPic = IVideoPicture.make(resampler.getOutputPixelFormat(),
									picture.getWidth(), picture.getHeight());
							if (resampler.resample(newPic, picture) < 0)
								throw new RuntimeException("Sorry, I could not resample the video from stream");
						}
						if (newPic.getPixelType() != IPixelFormat.Type.BGR24)
							throw new RuntimeException("Sorry, I could not decode the video as BGR 24 bit data.");

						if (firstTimestampInStream == Global.NO_PTS){
							// This is our first time through
							firstTimestampInStream = picture.getTimeStamp();
							// get the starting clock time so we can hold up frames
							// until the right time.
							startTime = System.currentTimeMillis();
						} 
						else {
							long CurrentTime = System.currentTimeMillis();
							long TimeSinceStartofVideo = CurrentTime - startTime;
							long StreamTimeSinceStartOfVideo = (picture.getTimeStamp() - firstTimestampInStream)/1000;
							final long millisecondsTolerance = 50; // and we give ourselves 50 ms of tolerance
							final long millisecondsToSleep = (StreamTimeSinceStartOfVideo - (TimeSinceStartofVideo + millisecondsTolerance));
		        		
							if (millisecondsToSleep > 0){
								try{
		        				Thread.sleep(millisecondsToSleep);
								}
								catch (InterruptedException e){
									// we might get this when the user closes the dialog box, so
									// just return from the method.
									return;
								}
							}
						}

						// And finally, convert the BGR24 to an Java buffered image
						@SuppressWarnings("deprecation")
						BufferedImage javaImage = Utils.videoPictureToImage(newPic);

						// and display it on the Java Swing window
						updateJavaWindow(javaImage);
					}
				}
			}
			//else if (packet.getStreamIndex() == audioStreamId){
				
			//	IAudioSamples samples = IAudioSamples.make(1024, audioCoder.getChannels());
			
			//	int offset = 0;
			//	while(offset < packet.getSize()){
			//		int bytesDecoded = audioCoder.decodeAudio(samples, packet, offset);
		  
			//		if (bytesDecoded < 0)
			//			throw new RuntimeException("got error decoding audio ");
					
			//		offset += bytesDecoded;
					
					//Check for full set of samples
			//		if (samples.isComplete()){
			//			playJavaSound(samples);
						//System.out.println(samples);
			//		}
			//	}
			//}
		}
	}
	
	public static byte[] removeEcho(byte[] buffer){
	    byte[] outbuffer = new byte[buffer.length];
	    
	    
	    System.out.println("Removing echo");
	

	    return outbuffer;
	}

	//Method to update video window
	private static void updateJavaWindow(BufferedImage javaImage){
		mScreen.setImage(javaImage);
		mScreen.setBounds(10, 84, 647, 504);
    	mScreen.setTitle("MyI");
	}

	//Method to create video window
	private static void openJavaWindow(){
		mScreen = new VideoImage();
	}

	//Method ro close video window
	@SuppressWarnings("unused")
	private static void closeJavaWindow(){
		System.exit(0);
	}
	
	//Method to open java sound and create audio line
	private static void openJavaSound(IStreamCoder aAudioCoder){
		  
		AudioFormat audioFormat = new AudioFormat(aAudioCoder.getSampleRate(),
		(int)IAudioSamples.findSampleBitDepth(aAudioCoder.getSampleFormat()),
	    aAudioCoder.getChannels(),true, false);
	    
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
		try{
			sdLine = (SourceDataLine) AudioSystem.getLine(info);
			sdLine.open(audioFormat);
			sdLine.start();
		}
		catch (LineUnavailableException e){
			throw new RuntimeException("Sorry, I could not open an audio line for stream audio");
		}  
	}
	  
	private static void playJavaSound(IAudioSamples aSamples){
		byte[] rawBytes = aSamples.getData().getByteArray(0, aSamples.getSize());
		sdLine.write(rawBytes, 0, aSamples.getSize());
	}

	@SuppressWarnings("unused")
	private static void closeJavaSound(){
		if (sdLine != null){
		  sdLine.drain();
		  sdLine.close();
		  sdLine=null;
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}

