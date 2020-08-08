package utils;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

public class GifSequenceWriter {
	
	  protected ImageWriter gifWriter;
	  protected ImageWriteParam imageWriteParam;
	  protected IIOMetadata imageMetaData;
	  
	  public GifSequenceWriter(
		ImageOutputStream outputStream, int imageType, int timeBetweenFramesMS, boolean loopContinuously) throws IIOException, IOException {
	  	gifWriter = getWriter(); 
  		imageWriteParam = gifWriter.getDefaultWriteParam();
  		ImageTypeSpecifier imageTypeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(imageType);
  		imageMetaData = gifWriter.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);

  		String metaFormatName = imageMetaData.getNativeMetadataFormatName();
  		IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);
  		IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

	    graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
	    graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
	    graphicsControlExtensionNode.setAttribute("transparentColorFlag","FALSE");
	    graphicsControlExtensionNode.setAttribute("delayTime",Integer.toString(timeBetweenFramesMS / 10));
	    graphicsControlExtensionNode.setAttribute("transparentColorIndex","0");

	    IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
	    commentsNode.setAttribute("CommentExtension", "Created by MAH");
	    IIOMetadataNode appEntensionsNode = getNode(root,"ApplicationExtensions");
	    IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

	    child.setAttribute("applicationID", "NETSCAPE");
	    child.setAttribute("authenticationCode", "2.0");

	    int loop = loopContinuously ? 0 : 1;
	    byte[] by = (new byte[]{(byte) (0x1), (byte) (loop & 0xFF), (byte)((loop >> 8) & 0xFF)});
	    child.setUserObject(by);
	    appEntensionsNode.appendChild(child);

	    imageMetaData.setFromTree(metaFormatName, root);
	    gifWriter.setOutput(outputStream);
	    gifWriter.prepareWriteSequence(null);
	  }
	  
	  public void writeToSequence(RenderedImage img) throws IOException {
	    gifWriter.writeToSequence(new IIOImage(img,null,imageMetaData),imageWriteParam);
	  }
	  
	  /**
	   * Close this GifSequenceWriter object. This does not close the underlying
	   * stream, just finishes off the GIF.
	   */
	  public void close() throws IOException {
	    gifWriter.endWriteSequence();    
	  }

	  private static ImageWriter getWriter() throws IIOException {
	    Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
	    if(!iter.hasNext()) {
	      throw new IIOException("No GIF Image Writers Exist");
	    } else {
	      return iter.next();
	    }
	  }

	  private static IIOMetadataNode getNode(IIOMetadataNode rootNode,String nodeName) {
	    int nNodes = rootNode.getLength();
	    for (int i = 0; i < nNodes; i++) {
	      if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
	        return((IIOMetadataNode) rootNode.item(i));
	      }
	    }
	    IIOMetadataNode node = new IIOMetadataNode(nodeName);
	    rootNode.appendChild(node);
	    return(node);
	  }
	  
	  public static void generateGif(String path, String[] files, String gifname, int timeBetweenFramesMS) throws Exception {
	      BufferedImage firstImage = ImageIO.read(new File(path + files[0]));
	      ImageOutputStream output = new FileImageOutputStream(new File(path + gifname));
	      int imageType = BufferedImage.TYPE_INT_ARGB;
	      GifSequenceWriter writer = new GifSequenceWriter(output, imageType, timeBetweenFramesMS, false);
	      writer.writeToSequence(firstImage);
	      for(int i=1; i<files.length; i++) {
	    	  BufferedImage nextImage = null;
	    	  try {
	    		  nextImage = ImageIO.read(new File(path + files[i]));
	    	  } catch(Exception ex) {
	    		  // Do nothing
	    	  }
	    	  if(nextImage != null) {
	    		  writer.writeToSequence(nextImage);
	    		  deleteFile(new File(path + files[i]));
	    	  }
	      }
	      deleteFile(new File(path + files[0]));
	      writer.close();
	      output.close();
	  }
	  
	  static void deleteFile(File element) {
		  if (element.isDirectory()) {
			  for (File sub : element.listFiles()) {
				  deleteFile(sub);
			  }
		  }
		  element.delete();
	  }
  }