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

import org.jaudiotagger.audio.asf.io.WriteableChunk;
import org.jaudiotagger.audio.asf.tag.AsfFieldKey;
import org.jaudiotagger.audio.asf.util.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * This class represents the data of a chunk which contains title, author,
 * copyright, description and the rating of the file. <br>
 * It is optional within ASF files. But if, exists only once.
 *
 * @author Christian Laireiter
 */
public class ContentDescription extends Chunk implements WriteableChunk
{
    /**
     * File artist.
     */
    private String author = null;

    /**
     * File copyright.
     */
    private String copyRight = null;

    /**
     * File comment.
     */
    private String description = null;

    /**
     * File rating.
     */
    private String rating = null;

    /**
     * File title.
     */
    private String title = null;

    /**
     * Creates an instance. <br>
     */
    public ContentDescription()
    {
        this(BigInteger.valueOf(0));
    }

    /**
     * Creates an instance.
     *
     * @param pos      Position of content description within file or stream
     * @param chunkLen Length of content description.
     */
    public ContentDescription(BigInteger chunkLen)
    {
        super(GUID.GUID_CONTENTDESCRIPTION, chunkLen);
    }

    /**
     * @return Returns the author.
     */
    public String getAuthor()
    {
        if (author == null)
        {
            return "";
        }
        return author;
    }

    /**
     * @return Returns the comment.
     */
    public String getComment()
    {
        if (description == null)
        {
            return "";
        }
        return description;
    }

    /**
     * @return Returns the copyRight.
     */
    public String getCopyRight()
    {
        if (copyRight == null)
        {
            return "";
        }
        return copyRight;
    }

    /**
     * {@inheritDoc}
     */
    public long getCurrentAsfChunkSize()
    {
        long result = 44; // GUID + UINT64 for size + 5 times string length (each
        // 2 bytes) + 5 times zero term char (2 bytes each).
        result += getAuthor().length() * 2; // UTF-16LE
        result += getComment().length() * 2;
        result += getRating().length() * 2;
        result += getTitle().length() * 2;
        result += getCopyRight().length() * 2;
        return result;
    }

    /**
     * @return returns the rating.
     */
    public String getRating()
    {
        if (rating == null)
        {
            return "";
        }
        return rating;
    }

    /**
     * @return Returns the title.
     */
    public String getTitle()
    {
        if (title == null)
        {
            return "";
        }
        return title;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEmpty()
    {
        return Utils.isBlank(author) && Utils.isBlank(copyRight) && Utils.isBlank(description) && Utils.isBlank(rating) && Utils
                        .isBlank(title);
    }

    /**
     * (overridden)
     *
     * @see org.jaudiotagger.audio.asf.data.Chunk#prettyPrint()
     */
    public String prettyPrint()
    {
        StringBuffer result = new StringBuffer(super.prettyPrint());
        result.insert(0, Utils.LINE_SEPARATOR + "Content Description:" + Utils.LINE_SEPARATOR);
        result.append("   Title      : " + getTitle() + Utils.LINE_SEPARATOR);
        result.append("   Author     : " + getAuthor() + Utils.LINE_SEPARATOR);
        result.append("   Copyright  : " + getCopyRight() + Utils.LINE_SEPARATOR);
        result.append("   Description: " + getComment() + Utils.LINE_SEPARATOR);
        result.append("   Rating     :" + getRating() + Utils.LINE_SEPARATOR);
        return result.toString();
    }

    /**
     * @param fileAuthor The author to set.
     * @throws IllegalArgumentException If "UTF-16LE"-byte-representation would take more than 65535
     *                                  bytes.
     */
    public void setAuthor(String fileAuthor) throws IllegalArgumentException
    {
        Utils.checkStringLengthNullSafe(fileAuthor);
        this.author = fileAuthor;
    }

    /**
     * @param tagComment The comment to set.
     * @throws IllegalArgumentException If "UTF-16LE"-byte-representation would take more than 65535
     *                                  bytes.
     */
    public void setComment(String tagComment) throws IllegalArgumentException
    {
        Utils.checkStringLengthNullSafe(tagComment);
        this.description = tagComment;
    }

    /**
     * @param cpright The copyRight to set.
     * @throws IllegalArgumentException If "UTF-16LE"-byte-representation would take more than 65535
     *                                  bytes.
     */
    public void setCopyRight(String cpright) throws IllegalArgumentException
    {
        Utils.checkStringLengthNullSafe(cpright);
        this.copyRight = cpright;
    }

    /**
     * @param ratingText The rating to be set.
     * @throws IllegalArgumentException If "UTF-16LE"-byte-representation would take more than 65535
     *                                  bytes.
     */
    public void setRating(String ratingText) throws IllegalArgumentException
    {
        Utils.checkStringLengthNullSafe(ratingText);
        this.rating = ratingText;
    }

    /**
     * @param songTitle The title to set.
     * @throws IllegalArgumentException If "UTF-16LE"-byte-representation would take more than 65535
     *                                  bytes.
     */
    public void setTitle(String songTitle) throws IllegalArgumentException
    {
        Utils.checkStringLengthNullSafe(songTitle);
        this.title = songTitle;
    }

    /**
     * {@inheritDoc}
     */
    public long writeInto(OutputStream out) throws IOException
    {
        long chunkSize = getCurrentAsfChunkSize();

        out.write(this.getGuid().getBytes());
        Utils.writeUINT64(getCurrentAsfChunkSize(), out);
        // write the sizes of the string representations plus 2 bytes zero term
        // character
        Utils.writeUINT16(getTitle().length() * 2 + 2, out);
        Utils.writeUINT16(getAuthor().length() * 2 + 2, out);
        Utils.writeUINT16(getCopyRight().length() * 2 + 2, out);
        Utils.writeUINT16(getComment().length() * 2 + 2, out);
        Utils.writeUINT16(getRating().length() * 2 + 2, out);
        // write the Strings
        out.write(Utils.getBytes(getTitle(),AsfHeader.ASF_CHARSET));
        out.write(AsfHeader.ZERO_TERM);
        out.write(Utils.getBytes(getAuthor(),AsfHeader.ASF_CHARSET));
        out.write(AsfHeader.ZERO_TERM);
        out.write(Utils.getBytes(getCopyRight(),AsfHeader.ASF_CHARSET));
        out.write(AsfHeader.ZERO_TERM);
        out.write(Utils.getBytes(getComment(),AsfHeader.ASF_CHARSET));
        out.write(AsfHeader.ZERO_TERM);
        out.write(Utils.getBytes(getRating(),AsfHeader.ASF_CHARSET));
        out.write(AsfHeader.ZERO_TERM);
        return chunkSize;
    }
}