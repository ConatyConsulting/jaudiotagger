package org.jaudiotagger.issues;

import org.jaudiotagger.AbstractTestCase;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;

import java.io.File;

/**
 * Unable to write, offsets do not match
 *
 * TODO Unable to reproduce at the moment
 */
public class Issue291Test extends AbstractTestCase
{
    public void testSavingFile()
    {
        File orig = new File("testdata", "test60.m4p");
        if (!orig.isFile())
        {
            System.err.println("Unable to test file - not available");
            return;
        }



        File testFile = null;
        Exception exceptionCaught = null;
        try
        {
            testFile = AbstractTestCase.copyAudioToTmp("test60.m4p");
            AudioFile af = AudioFileIO.read(testFile);
            System.out.println("Tag is"+af.getTag().toString());
            af.getTag().setField(af.getTag().createField(FieldKey.ARTIST,"fredqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"));
            af.getTag().setField(af.getTag().createField(FieldKey.AMAZON_ID,"fredqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq"));

            af.commit();

            af = AudioFileIO.read(testFile);
            assertEquals("fredqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqqq",af.getTag().getFirst(FieldKey.ARTIST));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            exceptionCaught = e;
        }

        assertNull(exceptionCaught);
    }


}
