/*
 * Jaudiotagger Copyright (C)2004,2005
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public  License as published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not,
 * you can get a copy from http://www.opensource.org/licenses/lgpl-license.php or write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jaudiotagger;

import junit.framework.TestCase;

import java.io.*;

/**
 *
 */
public abstract class AbstractTestCase extends TestCase
{
    /**
     * Copy a File
     *
     * @param fromFile The existing File
     * @param toFile   The new File
     * @return <code>true</code> if and only if the renaming
     *         succeeded;
     *         <code>false</code> otherwise
     */
    private static boolean copy(File fromFile, File toFile)
    {
        try
        {
            FileInputStream in = new FileInputStream(fromFile);
            FileOutputStream out = new FileOutputStream(toFile);
            BufferedInputStream inBuffer = new BufferedInputStream(in);
            BufferedOutputStream outBuffer = new BufferedOutputStream(out);

            int theByte;

            while ((theByte = inBuffer.read()) > -1)
            {
                outBuffer.write(theByte);
            }

            outBuffer.close();
            inBuffer.close();
            out.close();
            in.close();

            // cleanupif files are not the same length
            if (fromFile.length() != toFile.length())
            {
                toFile.delete();

                return false;
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

    }

    private static boolean append(File fromFile1, File fromFile2, File toFile)
    {
        try
        {
            FileInputStream in = new FileInputStream(fromFile1);
            FileInputStream in2 = new FileInputStream(fromFile2);
            FileOutputStream out = new FileOutputStream(toFile);
            BufferedInputStream inBuffer = new BufferedInputStream(in);
            BufferedInputStream inBuffer2 = new BufferedInputStream(in2);
            BufferedOutputStream outBuffer = new BufferedOutputStream(out);

            int theByte;

            while ((theByte = inBuffer.read()) > -1)
            {
                outBuffer.write(theByte);
            }

            while ((theByte = inBuffer2.read()) > -1)
            {
                outBuffer.write(theByte);
            }

            outBuffer.close();
            inBuffer.close();
            inBuffer2.close();
            out.close();
            in.close();
            in2.close();

            // cleanupif files are not the same length
            if ((fromFile1.length() + fromFile2.length()) != toFile.length())
            {
                toFile.delete();

                return false;
            }

            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Copy audiofile to processing dir ready for use in test
     *
     * @param fileName
     * @return
     */
    public static File copyAudioToTmp(String fileName)
    {
        File inputFile = new File("testdata", fileName);
        File outputFile = new File("testdatatmp", fileName);
        if (!outputFile.getParentFile().exists())
        {
            outputFile.getParentFile().mkdirs();
        }
        boolean result = copy(inputFile, outputFile);
        assertTrue(result);
        return outputFile;
    }

    /**
     * Copy audiofile to processing dir ready for use in test, use this if using same file
     * in multiple tests because with junit multithreading can have problemsa otherwise
     *
     * @param fileName
     * @return
     */
    public static File copyAudioToTmp(String fileName, File newFileName)
    {
        File inputFile = new File("testdata", fileName);
        File outputFile = new File("testdatatmp", newFileName.getName());
        if (!outputFile.getParentFile().exists())
        {
            outputFile.getParentFile().mkdirs();
        }
        boolean result = copy(inputFile, outputFile);
        assertTrue(result);
        return outputFile;
    }

    /**
     * Prepends file with tag file in order to create an mp3 with a valid
     * id3
     *
     * @param tagfile
     * @param fileName
     * @return
     */
    public static File copyAudioToTmp(String tagfile, String fileName)
    {
        File inputTagFile = new File("testtagdata", tagfile);
        File inputFile = new File("testdata", fileName);
        File outputFile = new File("testdatatmp", fileName);
        if (!outputFile.getParentFile().exists())
        {
            outputFile.getParentFile().mkdirs();
        }
        boolean result = append(inputTagFile, inputFile, outputFile);
        assertTrue(result);
        return outputFile;
    }
}
