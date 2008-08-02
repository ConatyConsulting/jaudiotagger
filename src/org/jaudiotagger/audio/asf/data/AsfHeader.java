/*
 * Entagged Audio Tag library
 * Copyright (c) 2004-2005 Christian Laireiter <liree@web.de>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *  
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package org.jaudiotagger.audio.asf.data;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Hashtable;

/**
 * Each asf file starts with a so called header. <br>
 * This header contains other chunks. Each chunk starts with a 16 byte GUID
 * followed by the length (in bytes) of the chunk (including GUID). The length
 * number takes 8 bytes and is unsigned. Finally the chunk's data appears. <br>
 *
 * @author Christian Laireiter
 */
public class AsfHeader extends Chunk
{

    /**
     * An ASF header contains multiple chunks. <br>
     * The count of those is stored here.
     */
    private final long chunkCount;

    /**
     * Stores the {@link Chunk} objects to their {@link GUID}.
     */
    private final Hashtable<GUID, Chunk> chunkTable;

    /**
     * Stores the encoding chunk.
     */
    private EncodingChunk encodingChunk;

    /**
     * Stores the encoding chunk.
     */
    private EncryptionChunk encryptionChunk;

    /**
     * This array stores all found stream chunks.
     */
    private StreamChunk[] streamChunks;

    /**
     * This field stores all chunks which aren't specified and not represented
     * by a wrapper. <br>
     * However during write operations this position and size of those chunks is
     * useful.
     */
    private Chunk[] unspecifiedChunks;

    /**
     * Creates an instance.
     *
     * @param pos      see {@link Chunk#position}
     * @param chunkLen see {@link Chunk#chunkLength}
     * @param chunkCnt
     */
    public AsfHeader(long pos, BigInteger chunkLen, long chunkCnt)
    {
        super(GUID.GUID_HEADER, pos, chunkLen);
        this.chunkCount = chunkCnt;
        this.streamChunks = new StreamChunk[0];
        this.unspecifiedChunks = new Chunk[0];
        this.chunkTable = new Hashtable<GUID, Chunk>();
    }

    /**
     * @param chunk
     */
    public void addChunk(Chunk chunk)
    {
        this.chunkTable.put(chunk.getGuid(), chunk);
    }

    /**
     * This method appends a StreamChunk to the header. <br>
     *
     * @param toAdd Chunk to add.
     */
    public void addStreamChunk(StreamChunk toAdd)
    {
        if (toAdd == null)
        {
            throw new IllegalArgumentException("Argument must not be null.");
        }
        if (!Arrays.asList(this.streamChunks).contains(toAdd))
        {
            StreamChunk[] tmp = new StreamChunk[this.streamChunks.length + 1];
            System.arraycopy(this.streamChunks, 0, tmp, 0, this.streamChunks.length);
            tmp[tmp.length - 1] = toAdd;
            this.streamChunks = tmp;
        }
    }

    /**
     * This method appends the given chunk to the
     * {@linkplain #unspecifiedChunks unspecified}list. <br>
     *
     * @param toAppend The chunk whose use is unknown or of no interest.
     */
    public void addUnspecifiedChunk(Chunk toAppend)
    {
        if (toAppend == null)
        {
            throw new IllegalArgumentException("Argument must not be null.");
        }
        if (!Arrays.asList(unspecifiedChunks).contains(toAppend))
        {
            Chunk[] tmp = new Chunk[unspecifiedChunks.length + 1];
            System.arraycopy(unspecifiedChunks, 0, tmp, 0, unspecifiedChunks.length);
            tmp[tmp.length - 1] = toAppend;
            unspecifiedChunks = tmp;
        }
    }

    /**
     * This method returns the first audio stream chunk found in the asf file or
     * stream.
     *
     * @return Returns the audioStreamChunk.
     */
    public AudioStreamChunk getAudioStreamChunk()
    {
        AudioStreamChunk result = null;
        for (int i = 0; i < getStreamChunkCount() && result == null; i++)
        {
            StreamChunk tmp = getStreamChunk(i);
            if (tmp instanceof AudioStreamChunk)
            {
                result = (AudioStreamChunk) tmp;
            }
        }
        return result;
    }

    /**
     * @return Returns the chunkCount.
     */
    public long getChunkCount()
    {
        return chunkCount;
    }

    /**
     * @return Returns the contentDescription.
     */
    public ContentDescription getContentDescription()
    {
        return (ContentDescription) this.chunkTable.get(GUID.GUID_CONTENTDESCRIPTION);
    }

    /**
     * @return Returns the encodingChunk.
     */
    public EncodingChunk getEncodingChunk()
    {
        return encodingChunk;
    }

    /**
     * @return Returns the encodingChunk.
     */
    public EncryptionChunk getEncryptionChunk()
    {
        return encryptionChunk;
    }

    /**
     * @return Returns the tagHeader.
     */
    public ExtendedContentDescription getExtendedContentDescription()
    {
        return (ExtendedContentDescription) this.chunkTable.get(GUID.GUID_EXTENDED_CONTENT_DESCRIPTION);
    }

    /**
     * @return Returns the fileHeader.
     */
    public FileHeader getFileHeader()
    {
        return (FileHeader) this.chunkTable.get(GUID.GUID_FILE);
    }

    /**
     * @return Returns the streamBitratePropertiesChunk.
     */
    public StreamBitratePropertiesChunk getStreamBitratePropertiesChunk()
    {
        return (StreamBitratePropertiesChunk) this.chunkTable.get(GUID.GUID_STREAM_BITRATE_PROPERTIES);
    }

    /**
     * This method returns the StreamChunk at given index. <br>
     *
     * @param index index of the wanted chunk
     * @return StreamChunk at given index.
     */
    public StreamChunk getStreamChunk(int index)
    {
        return this.streamChunks[index];
    }

    /**
     * This method returns the amount of StreamChunks in this header. <br>
     *
     * @return Number of inserted StreamChunks.
     */
    public int getStreamChunkCount()
    {
        return this.streamChunks.length;
    }

    /**
     * This method returns the unspecified chunk at given position. <br>
     *
     * @param index Index of the wanted chunk
     * @return The chunk at given index.
     */
    public Chunk getUnspecifiedChunk(int index)
    {
        return this.unspecifiedChunks[index];
    }

    /**
     * This method returns the number of {@link Chunk}objects which where
     * inserted using {@link #addUnspecifiedChunk(Chunk)}.
     *
     * @return Number of unspecified chunks.
     */
    public int getUnspecifiedChunkCount()
    {
        return this.unspecifiedChunks.length;
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.asf.data.Chunk#prettyPrint()
     */
    public String prettyPrint()
    {
        StringBuffer result = new StringBuffer(super.prettyPrint());
        result.insert(0, "\nASF Chunk\n");
        result.append("   Contains: \"" + getChunkCount() + "\" chunks\n");
        result.append(getFileHeader());
        result.append(getExtendedContentDescription());
        result.append(getEncodingChunk());
        result.append(getContentDescription());
        result.append(getStreamBitratePropertiesChunk());
        for (int i = 0; i < getStreamChunkCount(); i++)
        {
            result.append(getStreamChunk(i));
        }
        return result.toString();
    }

    /**
     * @param contentDesc sets the contentDescription. <code>null</code> deletes the
     *                    chunk.
     */
    public void setContentDescription(ContentDescription contentDesc)
    {
        this.chunkTable.put(GUID.GUID_CONTENTDESCRIPTION, contentDesc);
    }

    /**
     * @param encChunk The encodingChunk to set.
     */
    public void setEncodingChunk(EncodingChunk encChunk)
    {
        if (encChunk == null)
        {
            throw new IllegalArgumentException("Argument must not be null.");
        }
        this.encodingChunk = encChunk;
    }

    /**
     * @param encryptionChunk *            The encodingChunk to set.
     */
    public void setEncryptionChunk(EncryptionChunk encChunk)
    {
        if (encChunk == null) throw new IllegalArgumentException("Argument must not be null.");
        this.encryptionChunk = encChunk;
    }

    /**
     * @param th sets the extendedContentDescription. <code>null</code>
     *           delete the chunk.
     */
    public void setExtendedContentDescription(ExtendedContentDescription th)
    {
        this.chunkTable.put(GUID.GUID_EXTENDED_CONTENT_DESCRIPTION, th);
    }

    /**
     * @param fh
     */
    public void setFileHeader(FileHeader fh)
    {
        if (fh == null)
        {
            throw new IllegalArgumentException("Argument must not be null.");
        }
        this.chunkTable.put(GUID.GUID_FILE, fh);
    }

    /**
     * @param streamBitratePropertiesChunk The streamBitratePropertiesChunk to set.
     */
    public void setStreamBitratePropertiesChunk(StreamBitratePropertiesChunk streamBitratePropertiesChunk1)
    {
        this.chunkTable.put(GUID.GUID_STREAM_BITRATE_PROPERTIES, streamBitratePropertiesChunk1);
    }
}