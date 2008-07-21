package org.jaudiotagger.tag.id3;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.TagFieldKey;
import org.jaudiotagger.tag.id3.framebody.*;

import java.io.File;

/**
 *
 */
public class ID3v22TagTest extends TestCase
{
    /**
     * Constructor
     *
     * @param arg0
     */
    public ID3v22TagTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Command line entrance.
     *
     * @param args
     */
    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(ID3v22TagTest.suite());
    }

    /////////////////////////////////////////////////////////////////////////
    // TestCase classes to override
    /////////////////////////////////////////////////////////////////////////

    /**
     *
     */
    protected void setUp()
    {
    }

    /**
     *
     */
    protected void tearDown()
    {
    }

    /**
     *
     */
//    protected void runTest()
//    {
//    }

    /**
     * Builds the Test Suite.
     *
     * @return the Test Suite.
     */
    public static Test suite()
    {
        return new TestSuite(ID3v22TagTest.class);
    }

    /////////////////////////////////////////////////////////////////////////
    // Tests
    /////////////////////////////////////////////////////////////////////////


    public void testCreateIDv22Tag()
    {
        ID3v22Tag v2Tag = new ID3v22Tag();
        assertEquals((byte) 2, v2Tag.getRelease());
        assertEquals((byte) 2, v2Tag.getMajorVersion());
        assertEquals((byte) 0, v2Tag.getRevision());
    }

    public void testCreateID3v22FromID3v11()
    {
        ID3v11Tag v11Tag = ID3v11TagTest.getInitialisedTag();
        ID3v22Tag v2Tag = new ID3v22Tag(v11Tag);
        assertNotNull(v11Tag);
        assertNotNull(v2Tag);
        assertEquals(ID3v11TagTest.ARTIST, ((FrameBodyTPE1) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_ARTIST)).getBody()).getText());
        assertEquals(ID3v11TagTest.ALBUM, ((FrameBodyTALB) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_ALBUM)).getBody()).getText());
        assertEquals(ID3v11TagTest.COMMENT, ((FrameBodyCOMM) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_COMMENT)).getBody()).getText());
        assertEquals(ID3v11TagTest.TITLE, ((FrameBodyTIT2) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TITLE)).getBody()).getText());
        assertEquals(ID3v11TagTest.TRACK_VALUE, ((FrameBodyTRCK) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TRACK)).getBody()).getText());
        assertTrue(((FrameBodyTCON) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_GENRE)).getBody()).getText().endsWith(ID3v11TagTest.GENRE_VAL));

        //TODO:Note confusingly V22 YEAR Frame shave v2 identifier but use TDRC behind the scenes, is confusing
        assertEquals(ID3v11TagTest.YEAR, ((FrameBodyTDRC) ((ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TYER)).getBody()).getText());

        assertEquals((byte) 2, v2Tag.getRelease());
        assertEquals((byte) 2, v2Tag.getMajorVersion());
        assertEquals((byte) 0, v2Tag.getRevision());
    }

    public void testCreateIDv22TagAndSave()
    {
        Exception exception = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("testV1.mp3");
            MP3File mp3File = new MP3File(testFile);
            ID3v22Tag v2Tag = new ID3v22Tag();
            v2Tag.setTitle("fred");
            v2Tag.setArtist("artist");
            v2Tag.setAlbum("album");

            assertEquals((byte) 2, v2Tag.getRelease());
            assertEquals((byte) 2, v2Tag.getMajorVersion());
            assertEquals((byte) 0, v2Tag.getRevision());
            mp3File.setID3v2Tag(v2Tag);
            mp3File.save();

            //Read using new Interface
            AudioFile v22File = AudioFileIO.read(testFile);
            assertEquals("fred", v22File.getTag().getFirstTitle());
            assertEquals("artist", v22File.getTag().getFirstArtist());
            assertEquals("album", v22File.getTag().getFirstAlbum());

            //Read using old Interface
            mp3File = new MP3File(testFile);
            v2Tag = (ID3v22Tag) mp3File.getID3v2Tag();
            ID3v22Frame frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TITLE);
            assertEquals("fred", ((AbstractFrameBodyTextInfo) frame.getBody()).getText());

        }
        catch (Exception e)
        {
            exception = e;
        }
        assertNull(exception);
    }

    public void testv22TagWithUnnneccessaryTrailingNulls()
    {
        File orig = new File("testdata", "test24.mp3");
        if (!orig.isFile())
        {
            return;
        }

        Exception exception = null;
        try
        {
            File testFile = AbstractTestCase.copyAudioToTmp("test24.mp3");
            AudioFile af = AudioFileIO.read(testFile);
            MP3File m = (MP3File) af;

            //Read using new Interface getFirst method with key
            assertEquals("Listen to images:", af.getTag().getFirst(TagFieldKey.TITLE) + ":");
            assertEquals("Clean:", af.getTag().getFirst(TagFieldKey.ALBUM) + ":");
            assertEquals("Cosmo Vitelli:", af.getTag().getFirst(TagFieldKey.ARTIST) + ":");
            assertEquals("Electronica/Dance:", af.getTag().getFirst(TagFieldKey.GENRE) + ":");
            assertEquals("2003:", af.getTag().getFirst(TagFieldKey.YEAR) + ":");
            assertEquals("1/11:", af.getTag().getFirst(TagFieldKey.TRACK) + ":");

            //Read using new Interface getFirst method with String
            assertEquals("Listen to images:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_TITLE) + ":");
            assertEquals("Clean:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_ALBUM) + ":");
            assertEquals("Cosmo Vitelli:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_ARTIST) + ":");
            assertEquals("Electronica/Dance:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_GENRE) + ":");
            assertEquals("2003:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_TYER) + ":");
            assertEquals("1/11:", af.getTag().getFirst(ID3v22Frames.FRAME_ID_V2_TRACK) + ":");

            //Read using new Interface getFirst methods for common fields
            assertEquals("Listen to images:", af.getTag().getFirstTitle() + ":");
            assertEquals("Cosmo Vitelli:", af.getTag().getFirstArtist() + ":");
            assertEquals("Clean:", af.getTag().getFirstAlbum() + ":");
            assertEquals("Electronica/Dance:", af.getTag().getFirstGenre() + ":");
            assertEquals("2003:", af.getTag().getFirstYear() + ":");
            assertEquals("1/11:", af.getTag().getFirstTrack() + ":");

            //Read using old Interface
            ID3v22Tag v2Tag = (ID3v22Tag) m.getID3v2Tag();
            ID3v22Frame frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TITLE);
            assertEquals("Listen to images\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");
            frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_ARTIST);
            assertEquals("Cosmo Vitelli\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");
            frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_ALBUM);
            assertEquals("Clean\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");
            frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_GENRE);
            assertEquals("Electronica/Dance\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");
            frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TYER);
            assertEquals("2003\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");
            frame = (ID3v22Frame) v2Tag.getFrame(ID3v22Frames.FRAME_ID_V2_TRACK);
            assertEquals("1/11\0:", ((AbstractFrameBodyTextInfo) frame.getBody()).getText() + ":");

        }
        catch (Exception e)
        {
            exception = e;
        }
        assertNull(exception);
    }
}
