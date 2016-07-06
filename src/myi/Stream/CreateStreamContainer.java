package myi.Stream;

import java.io.OutputStream;

import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainerFormat;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IRational;
import com.xuggle.xuggler.IStream;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IPixelFormat;

public class CreateStreamContainer{

	public static boolean containerInUse = false;
	public static IContainer streamCont = null;
    private IStream vidOutStream, audOutStream;
    private ICodec vcodec = null;
	private ICodec acodec = null;
	private IStreamCoder vidOutStreamCoder = null;
	private IStreamCoder audOutStreamCoder = null;
    
	public  CreateStreamContainer(final OutputStream out){
        
		//Create container object
    	streamCont = IContainer.make();

    	//Create format for container
    	final IContainerFormat writeformat = IContainerFormat.make();		
    	writeformat.setOutputFormat("avi", null, null);
		
    	//Open output stream container and check for errors
    	if (streamCont.open(out, writeformat, true, false) < 0){
    		throw new RuntimeException("Could not open output stream");
    	}
  
    	//Configure audio and video streams
    	configureVideoCodec();
		configureAudioCodec();

		//Write header and check if successful
		if (streamCont.writeHeader() < 0) 
			throw new RuntimeException("Failed to write header"); 
		
		//Open stream coders
		vidOutStreamCoder.open();
		audOutStreamCoder.open();
		
		//Start threads
		(new Thread(new EncodeVideo(vidOutStreamCoder))).start();
		(new Thread(new EncodeAudio(audOutStreamCoder))).start();
    }
	
	static synchronized void addPacket(IPacket packet){
		//CreateStreamContainer.streamCont.acquire();
    	CreateStreamContainer.streamCont.writePacket(packet);
    	//CreateStreamContainer.streamCont.release();
	}
	
	//Configure video stream
	private void configureVideoCodec(){
		int width = 640;
		int height = 480;
		int picGroup = 100;
		int bitRate = 2500;
		int bitTolerance = 900;
		int noOfFrames = 100;
		
		vidOutStream = streamCont.addNewStream(0);
		vidOutStreamCoder = vidOutStream.getStreamCoder();
		vcodec = ICodec.guessEncodingCodec(null, null, "stream.flv", null, ICodec.Type.CODEC_TYPE_VIDEO);
		vidOutStreamCoder.setNumPicturesInGroupOfPictures(picGroup); 
		vidOutStreamCoder.setCodec(vcodec); 
		vidOutStreamCoder.setBitRate(bitRate); 
		vidOutStreamCoder.setBitRateTolerance(bitTolerance); 
		vidOutStreamCoder.setPixelType(IPixelFormat.Type.YUV420P); 
		vidOutStreamCoder.setHeight(height); 
		vidOutStreamCoder.setWidth(width); 
		vidOutStreamCoder.setFlag(IStreamCoder.Flags.FLAG_QSCALE, true); 
		vidOutStreamCoder.setGlobalQuality(0); 
		IRational frameRate = IRational.make(noOfFrames,1); 
		vidOutStreamCoder.setFrameRate(frameRate); 
		vidOutStreamCoder.setTimeBase(IRational.make(frameRate.getDenominator(), frameRate.getNumerator())); 
	}
	
	//Configure audio stream
	private void configureAudioCodec(){
		int sampleRate = 44100;
		int channels = 1;
		int bitRate = 16;
		int bitTolerance = 90;
		
		audOutStream = streamCont.addNewStream(1);
		audOutStreamCoder = audOutStream.getStreamCoder();
		acodec = ICodec.guessEncodingCodec(null, null, "stream.flv", null, ICodec.Type.CODEC_TYPE_AUDIO);	
		audOutStreamCoder.setCodec(acodec);
		audOutStreamCoder.setSampleRate(sampleRate);
		audOutStreamCoder.setChannels(channels);
		audOutStreamCoder.setGlobalQuality(0);
		//audOutStreamCoder.setBitRate(bitRate);
		//audOutStreamCoder.setBitRateTolerance(bitTolerance);
	}
}
