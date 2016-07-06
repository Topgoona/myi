package myi.Stream;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.List;

import javax.swing.JOptionPane;

import myi.Main.CreateGui;

import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.awt.AWTImageConverter;
import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IPacket;
import com.xuggle.xuggler.IPixelFormat;
import com.xuggle.xuggler.IStreamCoder;
import com.xuggle.xuggler.IVideoPicture;
import com.xuggle.xuggler.video.ConverterFactory;
import com.xuggle.xuggler.video.IConverter;

public class EncodeVideo implements Runnable{
	
	CaptureSystemFactory factory = null;
	static CaptureStream captureStream = null;
	public static BufferedImage bgrImg;
    
    static long now = 0;
    static long firstTimeStamp = 0; 
    static long timeStamp = 0;
    IStreamCoder vidOutStreamCoder;
    
    public static boolean capturingVideo = false;
    
    public EncodeVideo(final IStreamCoder vidOutStreamCoder){
    	this.vidOutStreamCoder = vidOutStreamCoder;
    }
    
	@Override
	public void run() {  
    	factory = DefaultCaptureSystemFactorySingleton.instance();
        CaptureSystem system;

        try {
			system = factory.createCaptureSystem();
			system.init();
			List list = system.getCaptureDeviceInfoList();
			if(list.size() == 0){
				System.exit(1);
			}else{
				CaptureDeviceInfo info = (CaptureDeviceInfo) list.get(1);
				captureStream = system.openCaptureDeviceStream(info.getDeviceID());
				
				final IPacket videopacket = IPacket.make();

		    	captureStream.setObserver(new CaptureObserver(){
		        	public void onNewImage(CaptureStream sender, com.lti.civil.Image image) {
	
		            	now = System.currentTimeMillis(); 
		            	if (firstTimeStamp == 0)
		            		firstTimeStamp = now;
		            			
		            	timeStamp = (now - firstTimeStamp)*1000;// convert to microseconds for Xuggler
		        			
		            	BufferedImage capture = AWTImageConverter.toBufferedImage(image);  //must keep this line
		            	bgrImg = ConverterFactory.convertToType(capture, BufferedImage.TYPE_3BYTE_BGR);
		        			
		            	bgrImg.setAccelerationPriority(0);
		            	bgrImg.getGraphics();

		            	Graphics text = bgrImg.getGraphics();
		            	text.setColor(java.awt.Color.RED); 
		            	text.setFont(new Font("Serif", Font.BOLD, 30)); 
		                String s = CreateGui.msg; 
		                FontMetrics fm = text.getFontMetrics(); 
		                int x = (bgrImg.getWidth() - fm.stringWidth(s)) / 2; 
		                int y = 400; 
		                text.drawString(s, x, y); 
		                text.dispose(); 

		            	IConverter converter = ConverterFactory.createConverter(bgrImg, IPixelFormat.Type.YUV420P); 
		            	IVideoPicture outFrame = converter.toPicture(bgrImg, timeStamp); 
		            	outFrame.setQuality(0);
		            	vidOutStreamCoder.encodeVideo(videopacket, outFrame, 0);

		            	if(videopacket.isComplete()){
		            		//if(CreateStreamContainer.containerInUse == false){
		            			CreateStreamContainer.addPacket(videopacket);
		            		//}
		            	}
		            	
		        	}

		            public void onError(CaptureStream arg0, CaptureException arg1) {
		            	JOptionPane.showMessageDialog(null, "Camera Not supported");
		            }
		            
		            
		        });
		        	
		        try {
					captureStream.start();
				} catch (CaptureException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		} catch (CaptureException e) {

		}  
    }
	
	public void close() {
		try {
			captureStream.stop();
		} catch (CaptureException e) {
			//System.out.println("Unable to close video stream");
			e.printStackTrace();
		}	
	}

}
