package org.jaudiotagger.audio.mp3;

import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

/**
 * An alternative to MP3File that doesn't require the existence of an actual file, but rather is based
 * off a memory buffer.
 * Only part of the file API's functionality is supported.
 */
public class MP3Buffer
{

    private ID3v11Tag v1Tag;
    private AbstractID3v2Tag v2Tag;

    private ByteBuffer audioData;

    private MP3AudioHeader header;


    /**
     * Same as wrapping the array in a ByteBuffer and calling the other constructor.
     */
    public MP3Buffer(byte[] dataBytes)
        throws IOException, TagException, InvalidAudioFrameException
    {
        this(ByteBuffer.wrap(dataBytes));
    }


    /**
     * Creates a new MP3Buffer from a byte buffer containing the entire MP3.
     *
     * @param dataBuffer The mp3 data.  Must be _all_ the data, not just a fragment.
     * @throws IOException if there are IO problems reading the data
     * @throws TagException if there are exceptions reading the tags
     * @throws InvalidAudioFrameException if the audio data doesn't start with a valid frame
     */

    public MP3Buffer(ByteBuffer dataBuffer)
        throws IOException, TagException, InvalidAudioFrameException
    {
        v1Tag = ID3v11Tag.carveID3v11Tag(dataBuffer);

        dataBuffer.rewind();

        AbstractID3v2Tag.TagHeaderInfo headerInfo = AbstractID3v2Tag.getV2HeaderInfo(dataBuffer);

        if(headerInfo != null){

            dataBuffer.position(headerInfo.tagSize);

            audioData = dataBuffer.slice();

            dataBuffer.position(0);

            switch(headerInfo.majorVersion){
                case ID3v22Tag.MAJOR_VERSION:
                    v2Tag = new ID3v22Tag(dataBuffer, "MP3 file from byte array");
                    break;
                case ID3v23Tag.MAJOR_VERSION:
                    v2Tag = new ID3v23Tag(dataBuffer, "MP3 file from byte array");
                    break;
                case ID3v24Tag.MAJOR_VERSION:
                    v2Tag = new ID3v24Tag(dataBuffer, "MP3 file from byte array");
                    break;
                default:
                    throw new TagException("Unknown ID3v2 tag major version: " + String.valueOf(headerInfo.majorVersion));
            }
        } else {
            // No v2 Tag at all
            audioData = dataBuffer;
            audioData.rewind();
        }

        header = new MP3AudioHeader(audioData, "MP3 file from byte array");
    }


    public ID3v11Tag getV1Tag(){
        return v1Tag;
    }

    public void setV1Tag(ID3v11Tag tag){
        v1Tag = tag;
    }

    public AbstractID3v2Tag getV2Tag(){
        return v2Tag;
    }

    public void setV2Tag(AbstractID3v2Tag tag){
        v2Tag = tag;
    }


    public void write(WritableByteChannel out)
        throws IOException
    {
        // v2 tag, data, v1 tag

        if(v2Tag != null){
            v2Tag.write(out);
        }

        audioData.rewind();
        out.write(audioData);

        if(v1Tag != null){
            out.write(ByteBuffer.wrap(v1Tag.generateTagBytes()));
        }

    }

    public MP3AudioHeader getHeader(){
        return header;
    }

}


