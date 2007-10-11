package org.jaudiotagger.tag.vorbiscomment;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.tag.vorbiscomment.util.Base64Coder;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioFile;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.RandomAccessFile;
import java.io.ByteArrayInputStream;
import java.awt.image.BufferedImage;

/**
 */
public class VorbisImageTest extends AbstractTestCase
{
    /**
     * Test can read file with base64 encoded image from ogg
     *
     * Works
     */
    public void testReadFileWithSmallImageTag()
    {
        Exception exceptionCaught = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("testsmallimage.ogg");
            AudioFile f = AudioFileIO.read(testFile);
            String mimeType  = ((VorbisCommentTag)f.getTag()).getFirst(VorbisCommentFieldKey.COVERARTMIME);
            assertEquals("image/jpeg",mimeType);
            if(mimeType!=null&mimeType.length()>0)
            {
                String imageRawData = ((VorbisCommentTag)f.getTag()).getFirst(VorbisCommentFieldKey.COVERART);
                assertEquals(22972,imageRawData.length());
            }
        }
        catch(Exception e)
        {
             exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Test can read file with base64 encoded image thats spans multiple ogg pages
     *
     * Fails:Doesnt give error but doesnt read image
     */
    public void testReadFileWithLargeImageTag()
    {
        Exception exceptionCaught = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("testlargeimage.ogg");
            AudioFile f = AudioFileIO.read(testFile);
            String mimeType  = ((VorbisCommentTag)f.getTag()).getFirst(VorbisCommentFieldKey.COVERARTMIME);
            assertEquals("image/jpeg",mimeType);
            if(mimeType!=null&mimeType.length()>0)
            {
                String imageRawData = ((VorbisCommentTag)f.getTag()).getFirst(VorbisCommentFieldKey.COVERART);
                assertEquals(1013576,imageRawData.length());
            }
        }
        catch(Exception e)
        {
             e.printStackTrace();
             exceptionCaught = e;
        }
        assertNull(exceptionCaught);
    }

    /**
     * Write and read image using lowest level methods
     */
    public void testWriteImage1()
    {
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testWriteImage1.ogg"));
            AudioFile f = AudioFileIO.read(testFile);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new image, requires two fields in oggVorbis with data in  base64 encoded form
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"),"r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            tag.set(tag.createTagField(VorbisCommentFieldKey.COVERART,base64image));
            tag.set(tag.createTagField(VorbisCommentFieldKey.COVERARTMIME,"image/png"));
            f.commit();

            f   = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //VorbisImage base64 image, and reconstruct
            assertEquals(base64image,tag.getFirst(VorbisCommentFieldKey.COVERART));
            assertEquals("image/png",tag.getFirst(VorbisCommentFieldKey.COVERARTMIME));
            BufferedImage bi = ImageIO.read(ImageIO
                        .createImageInputStream(new ByteArrayInputStream(Base64Coder.
                            decode(tag.getFirst(VorbisCommentFieldKey.COVERART).toCharArray()))));
            assertNotNull(bi);
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
    }

     /**
     * Write Image using new method, read using lowlevel
     */
    public void testWriteImage2()
    {
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testWriteImage2.ogg"));
            AudioFile f = AudioFileIO.read(testFile);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new image using purpose built method
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"),"r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);

            tag.setArtworkField(imagedata,"image/png");
            f.commit();

            f   = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //VorbisImage base64 image, and reconstruct
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            assertEquals(base64image,tag.getFirst(VorbisCommentFieldKey.COVERART));
            assertEquals("image/png",tag.getFirst(VorbisCommentFieldKey.COVERARTMIME));
            BufferedImage bi = ImageIO.read(ImageIO
                        .createImageInputStream(new ByteArrayInputStream(Base64Coder.
                            decode(tag.getFirst(VorbisCommentFieldKey.COVERART).toCharArray()))));
            assertNotNull(bi);
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
    }

    /**
     * Write Image using lowlevel , read using new method
     */
    public void testWriteImage3()
    {
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test.ogg", new File("testWriteImage3.ogg"));
            AudioFile f = AudioFileIO.read(testFile);
            VorbisCommentTag tag = (VorbisCommentTag)f.getTag();

            //Add new image, requires two fields in oggVorbis with data in  base64 encoded form
            RandomAccessFile imageFile = new RandomAccessFile(new File("testdata", "coverart.png"),"r");
            byte[] imagedata = new byte[(int)imageFile.length()];
            imageFile.read(imagedata);
            char[] testdata = Base64Coder.encode(imagedata);
            String base64image = new String(testdata);
            tag.set(tag.createTagField(VorbisCommentFieldKey.COVERART,base64image));
            tag.set(tag.createTagField(VorbisCommentFieldKey.COVERARTMIME,"image/png"));
            f.commit();

            f   = AudioFileIO.read(testFile);
            tag = (VorbisCommentTag)f.getTag();

            //VorbisImage base64 image, and reconstruct
            assertEquals("image/png",tag.getArtworkMimeType());
            byte[] newImageData = tag.getArtworkBinaryData();
            BufferedImage bi = ImageIO.read(ImageIO
                        .createImageInputStream(new ByteArrayInputStream(newImageData)));
            assertNotNull(bi);
        }
        catch(Exception e)
        {
             e.printStackTrace();
        }
    }

}
