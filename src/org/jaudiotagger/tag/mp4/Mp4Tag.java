/*
 * Entagged Audio Tag library
 * Copyright (c) 2003-2005 Raphael Slinckx <raphael@slinckx.net>
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
package org.jaudiotagger.tag.mp4;

import org.jaudiotagger.audio.generic.AbstractTag;
import org.jaudiotagger.audio.mp4.atom.Mp4BoxHeader;
import org.jaudiotagger.logging.ErrorMessage;
import org.jaudiotagger.tag.*;
import org.jaudiotagger.tag.datatype.Artwork;
import static org.jaudiotagger.tag.mp4.Mp4FieldKey.*;
import org.jaudiotagger.tag.mp4.field.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * A Logical representation of Mp4Tag, i.e the meta information stored in an Mp4 file underneath the
 * moov.udt.meta.ilst atom.
 */
public class Mp4Tag extends AbstractTag
{

    private static final EnumMap<FieldKey, Mp4FieldKey> tagFieldToMp4Field = new EnumMap<FieldKey, Mp4FieldKey>(FieldKey.class);

    //Mapping from generic key to mp4 key
    static
    {
        tagFieldToMp4Field.put(FieldKey.ARTIST, Mp4FieldKey.ARTIST);
        tagFieldToMp4Field.put(FieldKey.ALBUM, Mp4FieldKey.ALBUM);
        tagFieldToMp4Field.put(FieldKey.TITLE, Mp4FieldKey.TITLE);
        tagFieldToMp4Field.put(FieldKey.TRACK, Mp4FieldKey.TRACK);
        tagFieldToMp4Field.put(FieldKey.YEAR, Mp4FieldKey.DAY);
        tagFieldToMp4Field.put(FieldKey.GENRE, Mp4FieldKey.GENRE);
        tagFieldToMp4Field.put(FieldKey.COMMENT, Mp4FieldKey.COMMENT);
        tagFieldToMp4Field.put(FieldKey.ALBUM_ARTIST, Mp4FieldKey.ALBUM_ARTIST);
        tagFieldToMp4Field.put(FieldKey.COMPOSER, Mp4FieldKey.COMPOSER);
        tagFieldToMp4Field.put(FieldKey.GROUPING, Mp4FieldKey.GROUPING);
        tagFieldToMp4Field.put(FieldKey.DISC_NO, Mp4FieldKey.DISCNUMBER);
        tagFieldToMp4Field.put(FieldKey.BPM, Mp4FieldKey.BPM);
        tagFieldToMp4Field.put(FieldKey.ENCODER, Mp4FieldKey.ENCODER);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_ARTISTID, Mp4FieldKey.MUSICBRAINZ_ARTISTID);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_RELEASEID, Mp4FieldKey.MUSICBRAINZ_ALBUMID);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_RELEASEARTISTID, Mp4FieldKey.MUSICBRAINZ_ALBUMARTISTID);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_TRACK_ID, Mp4FieldKey.MUSICBRAINZ_TRACKID);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_DISC_ID, Mp4FieldKey.MUSICBRAINZ_DISCID);
        tagFieldToMp4Field.put(FieldKey.MUSICIP_ID, Mp4FieldKey.MUSICIP_PUID);
        tagFieldToMp4Field.put(FieldKey.AMAZON_ID, Mp4FieldKey.ASIN);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_RELEASE_STATUS, Mp4FieldKey.MUSICBRAINZ_ALBUM_STATUS);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_RELEASE_TYPE, Mp4FieldKey.MUSICBRAINZ_ALBUM_TYPE);
        tagFieldToMp4Field.put(FieldKey.MUSICBRAINZ_RELEASE_COUNTRY, Mp4FieldKey.RELEASECOUNTRY);
        tagFieldToMp4Field.put(FieldKey.LYRICS, Mp4FieldKey.LYRICS);
        tagFieldToMp4Field.put(FieldKey.IS_COMPILATION, Mp4FieldKey.COMPILATION);
        tagFieldToMp4Field.put(FieldKey.ARTIST_SORT, Mp4FieldKey.ARTIST_SORT);
        tagFieldToMp4Field.put(FieldKey.ALBUM_ARTIST_SORT, Mp4FieldKey.ALBUM_ARTIST_SORT);
        tagFieldToMp4Field.put(FieldKey.ALBUM_SORT, Mp4FieldKey.ALBUM_SORT);
        tagFieldToMp4Field.put(FieldKey.TITLE_SORT, Mp4FieldKey.TITLE_SORT);
        tagFieldToMp4Field.put(FieldKey.COMPOSER_SORT, Mp4FieldKey.COMPOSER_SORT);
        tagFieldToMp4Field.put(FieldKey.COVER_ART, Mp4FieldKey.ARTWORK);
        tagFieldToMp4Field.put(FieldKey.ISRC, Mp4FieldKey.ISRC);
        tagFieldToMp4Field.put(FieldKey.CATALOG_NO, Mp4FieldKey.CATALOGNO);
        tagFieldToMp4Field.put(FieldKey.BARCODE, Mp4FieldKey.BARCODE);
        tagFieldToMp4Field.put(FieldKey.RECORD_LABEL, Mp4FieldKey.LABEL);
        tagFieldToMp4Field.put(FieldKey.LYRICIST, Mp4FieldKey.LYRICIST);
        tagFieldToMp4Field.put(FieldKey.CONDUCTOR, Mp4FieldKey.CONDUCTOR);
        tagFieldToMp4Field.put(FieldKey.REMIXER, Mp4FieldKey.REMIXER);
        tagFieldToMp4Field.put(FieldKey.MOOD, Mp4FieldKey.MOOD);
        tagFieldToMp4Field.put(FieldKey.MEDIA, Mp4FieldKey.MEDIA);
        tagFieldToMp4Field.put(FieldKey.URL_OFFICIAL_RELEASE_SITE, Mp4FieldKey.URL_OFFICIAL_RELEASE_SITE);
        tagFieldToMp4Field.put(FieldKey.URL_DISCOGS_RELEASE_SITE, Mp4FieldKey.URL_DISCOGS_RELEASE_SITE);
        tagFieldToMp4Field.put(FieldKey.URL_WIKIPEDIA_RELEASE_SITE, Mp4FieldKey.URL_WIKIPEDIA_RELEASE_SITE);
        tagFieldToMp4Field.put(FieldKey.URL_OFFICIAL_ARTIST_SITE, Mp4FieldKey.URL_OFFICIAL_ARTIST_SITE);
        tagFieldToMp4Field.put(FieldKey.URL_DISCOGS_ARTIST_SITE, Mp4FieldKey.URL_DISCOGS_ARTIST_SITE);
        tagFieldToMp4Field.put(FieldKey.URL_WIKIPEDIA_ARTIST_SITE, Mp4FieldKey.URL_WIKIPEDIA_ARTIST_SITE);
        tagFieldToMp4Field.put(FieldKey.LANGUAGE, Mp4FieldKey.LANGUAGE);
        tagFieldToMp4Field.put(FieldKey.KEY, Mp4FieldKey.KEY);
        tagFieldToMp4Field.put(FieldKey.URL_LYRICS_SITE, Mp4FieldKey.URL_LYRICS_SITE);
        tagFieldToMp4Field.put(FieldKey.TRACK_TOTAL, Mp4FieldKey.TRACK);
        tagFieldToMp4Field.put(FieldKey.DISC_TOTAL, Mp4FieldKey.DISCNUMBER);
    }

    /**
     * Create genre field
     * <p/>
     * <p>If the content can be parsed to one of the known values use the genre field otherwise
     * use the custom field.
     *
     * @param content
     * @return
     */
    @SuppressWarnings({"JavaDoc"})
    private TagField createGenreField(String content)
    {
        if (content == null)
        {
            throw new IllegalArgumentException(ErrorMessage.GENERAL_INVALID_NULL_ARGUMENT.getMsg());
        }
        if (Mp4GenreField.isValidGenre(content))
        {
            return new Mp4GenreField(content);
        }
        else
        {
            return new Mp4TagTextField(GENRE_CUSTOM.getFieldName(), content);
        }
    }

    protected boolean isAllowedEncoding(String enc)
    {
        return enc.equals(Mp4BoxHeader.CHARSET_UTF_8);
    }

    public String toString()
    {
        return "Mpeg4 " + super.toString();
    }


    /**
     * Maps the generic key to the mp4 key and return the list of values for this field
     *
     * @param genericKey
     */
    @SuppressWarnings({"JavaDoc"})
    @Override
    public List<TagField> getFields(FieldKey genericKey) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }
        return super.get(tagFieldToMp4Field.get(genericKey).getFieldName());
    }


    /**
     * Retrieve the  values that exists for this mp4keyId (this is the internalid actually used)
     * <p/>
     *
     * @param mp4FieldKey
     * @throws org.jaudiotagger.tag.KeyNotFoundException
     * @return
     */
    public List<TagField> get(Mp4FieldKey mp4FieldKey) throws KeyNotFoundException
    {
        if (mp4FieldKey == null)
        {
            throw new KeyNotFoundException();
        }
        return super.get(mp4FieldKey.getFieldName());
    }

    /**
     * Retrieve the first value that exists for this generic key
     *
     * @param genericKey
     * @return
     */
    public String getFirst(FieldKey genericKey) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }

        if(genericKey== FieldKey.GENRE)
        {
            List<TagField> genres = get(GENRE.getFieldName());
            if (genres.size() == 0)
            {
                genres = get(GENRE_CUSTOM.getFieldName());
            }
            if(genres.size()>0)
            {
                return ((TagTextField)genres.get(0)).getContent();
            }
            else
            {
                return "";
            }

        }
        else if(genericKey== FieldKey.TRACK)
        {
            List<TagField> list = get(tagFieldToMp4Field.get(genericKey));
            if(list.size()>0)
            {
                Mp4TrackField trackField = (Mp4TrackField)list.get(0);
                if(trackField.getTrackNo()>0)
                {
                    return String.valueOf(trackField.getTrackNo());
                }
            }
        }
        else if(genericKey== FieldKey.TRACK_TOTAL)
        {
            List<TagField> list = get(tagFieldToMp4Field.get(genericKey));
            if(list.size()>0)
            {
                Mp4TrackField trackField = (Mp4TrackField)list.get(0);
                if(trackField.getTrackTotal()>0)
                {
                    return String.valueOf(trackField.getTrackTotal());
                }
            }
        }
        else if(genericKey== FieldKey.DISC_NO)
        {
            List<TagField> list = get(tagFieldToMp4Field.get(genericKey));
            if(list.size()>0)
            {
                Mp4DiscNoField discField = (Mp4DiscNoField)list.get(0);
                if(discField.getDiscNo()>0)
                {
                     return String.valueOf(discField.getDiscNo());
                }

            }
        }
        else if(genericKey== FieldKey.DISC_TOTAL)
        {
            List<TagField> list = get(tagFieldToMp4Field.get(genericKey));
            if(list.size()>0)
            {
                Mp4DiscNoField discField = (Mp4DiscNoField)list.get(0);
                if(discField.getDiscTotal()>0)
                {
                     return String.valueOf(discField.getDiscTotal());
                }
            }
        }
        else
        {
            return super.getFirst(tagFieldToMp4Field.get(genericKey).getFieldName());
        }
        return "";
    }

    /**
     * Retrieve the first value that exists for this mp4key
     *
     * @param mp4Key
     * @return
     * @throws org.jaudiotagger.tag.KeyNotFoundException
     */
    public String getFirst(Mp4FieldKey mp4Key) throws KeyNotFoundException
    {
        if (mp4Key == null)
        {
            throw new KeyNotFoundException();
        }
        return super.getFirst(mp4Key.getFieldName());
    }

    public Mp4TagField getFirstField(FieldKey genericKey) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }
        return (Mp4TagField)super.getFirstField(tagFieldToMp4Field.get(genericKey).getFieldName());
    }

    public Mp4TagField getFirstField(Mp4FieldKey mp4Key) throws KeyNotFoundException
    {
        if (mp4Key == null)
        {
            throw new KeyNotFoundException();
        }
        return (Mp4TagField) super.getFirstField(mp4Key.getFieldName());
    }

    /**
     * Delete fields with this generic key
     *
     * @param genericKey
     */
    public void deleteField(FieldKey genericKey) throws KeyNotFoundException
    {
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }
        super.deleteField(tagFieldToMp4Field.get(genericKey).getFieldName());
    }

    /**
     * Delete fields with this mp4key
     *
     * @param mp4Key
     * @throws org.jaudiotagger.tag.KeyNotFoundException
     */
    public void deleteTagField(Mp4FieldKey mp4Key) throws KeyNotFoundException
    {
        if (mp4Key == null)
        {
            throw new KeyNotFoundException();
        }
        super.deleteField(mp4Key.getFieldName());
    }

    /**
     * Create discno field
     *
     * @param content can be any of the following
     *                1
     *                1/10
     * @return
     * @throws org.jaudiotagger.tag.FieldDataInvalidException
     */
    public TagField createDiscNoField(String content) throws FieldDataInvalidException
    {
        return new Mp4DiscNoField(content);
    }

    /**
     * Create artwork field
     *
     * @param data raw image data
     * @return
     * @throws org.jaudiotagger.tag.FieldDataInvalidException
     */
    public TagField createArtworkField(byte[] data)
    {
        return new Mp4TagCoverField(data);
    }

     /**
     * Create artwork field
     *    
     * @return
     */
    public TagField createField(Artwork artwork) throws FieldDataInvalidException
    {
        return new Mp4TagCoverField(artwork.getBinaryData());
    }

    /**
     * Create Tag Field using generic key
     * <p/>
     * This should use the correct subclass for the key
     *
     * @param genericKey
     * @param value
     * @return
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    @Override
    public TagField createField(FieldKey genericKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        if (value == null)
        {
            throw new IllegalArgumentException(ErrorMessage.GENERAL_INVALID_NULL_ARGUMENT.getMsg());
        }
        if (genericKey == null)
        {
            throw new KeyNotFoundException();
        }

        if(genericKey== FieldKey.TRACK)
        {
            return new Mp4TrackField(Integer.parseInt(value));
        }
        else if(genericKey== FieldKey.TRACK_TOTAL)
        {
            return new Mp4TrackField(0,Integer.parseInt(value));
        }
        else if(genericKey== FieldKey.DISC_NO)
        {
            return new Mp4DiscNoField(Integer.parseInt(value));
        }
        else if(genericKey== FieldKey.DISC_TOTAL)
        {
            return new Mp4DiscNoField(0,Integer.parseInt(value));
        }
        else
        {
            return createTagField(tagFieldToMp4Field.get(genericKey), value);
        }
    }

    /**
     * Set field, special handling for track and disc because they hold two fields
     * 
     * @param field
     */
    @Override
    public void setField(TagField field)
    {
        if (field == null)
        {
            return;
        }

        if(field.getId().equals(TRACK.getFieldName()))
        {
            List<TagField> list = fields.get(field.getId());
            if(list==null||list.size()==0)
            {
                 super.setField(field);
            }
            else
            {
                Mp4TrackField existingTrackField = (Mp4TrackField)list.get(0);
                Mp4TrackField newTrackField      = (Mp4TrackField)field;
                Short trackNo    = existingTrackField.getTrackNo();
                Short trackTotal = existingTrackField.getTrackTotal();
                if(newTrackField.getTrackNo()>0 )
                {
                    trackNo = newTrackField.getTrackNo();
                }
                if(newTrackField.getTrackTotal()>0 )
                {
                    trackTotal = newTrackField.getTrackTotal();
                }

                Mp4TrackField mergedTrackField = new Mp4TrackField(trackNo,trackTotal);
                super.setField(mergedTrackField);
            }
        }
        else if(field.getId().equals(DISCNUMBER.getFieldName()))
        {
            List<TagField> list = fields.get(field.getId());
            if(list==null||list.size()==0)
            {
                 super.setField(field);
            }
            else
            {
                Mp4DiscNoField existingDiscNoField = (Mp4DiscNoField)list.get(0);
                Mp4DiscNoField newDiscNoField      = (Mp4DiscNoField)field;
                Short discNo    = existingDiscNoField.getDiscNo();
                Short discTotal = existingDiscNoField.getDiscTotal();
                if(newDiscNoField.getDiscNo()>0 )
                {
                    discNo = newDiscNoField.getDiscNo();
                }
                if(newDiscNoField.getDiscTotal()>0 )
                {
                    discTotal = newDiscNoField.getDiscTotal();
                }

                Mp4DiscNoField mergedDiscNoField = new Mp4DiscNoField(discNo,discTotal);
                super.setField(mergedDiscNoField);
            }
        }
        else
        {
            super.setField(field);
        }
    }

    /**
     * Create Tag Field using mp4 key
     * <p/>
     * Uses the correct subclass for the key
     *
     * @param mp4FieldKey
     * @param value
     * @return
     * @throws KeyNotFoundException
     * @throws FieldDataInvalidException
     */
    public TagField createTagField(Mp4FieldKey mp4FieldKey, String value) throws KeyNotFoundException, FieldDataInvalidException
    {
        if (value == null)
        {
            throw new IllegalArgumentException(ErrorMessage.GENERAL_INVALID_NULL_ARGUMENT.getMsg());
        }
        if (mp4FieldKey == null)
        {
            throw new KeyNotFoundException();
        }
        switch (mp4FieldKey)
        {
            //This is boolean stored as 1, but calling program might setField as 'true' so we handle this
            //case internally
            case COMPILATION:
                if(value.equals("true"))
                {
                    value= Mp4TagByteField.TRUE_VALUE;
                }
                return new Mp4TagByteField(mp4FieldKey, value, mp4FieldKey.getFieldLength());

            case RATING:
            case BPM:
            case CONTENT_TYPE:
            case TV_SEASON:
            case TV_EPISODE:
            case TOOL:
                return new Mp4TagByteField(mp4FieldKey, value, mp4FieldKey.getFieldLength());

            case GENRE:
                return createGenreField(value);

            case PODCAST_URL:
            case EPISODE_GLOBAL_ID:
                return new Mp4TagTextNumberField(mp4FieldKey.getFieldName(), value);

            case DISCNUMBER:
                return new Mp4DiscNoField(value);

            case TRACK:
                return new Mp4TrackField(value);

            case MUSICBRAINZ_TRACKID:
            case MUSICBRAINZ_ARTISTID:
            case MUSICBRAINZ_ALBUMID:
            case MUSICBRAINZ_ALBUMARTISTID:
            case MUSICBRAINZ_DISCID:
            case MUSICIP_PUID:
            case ASIN:
            case MUSICBRAINZ_ALBUM_STATUS:
            case MUSICBRAINZ_ALBUM_TYPE:
            case RELEASECOUNTRY:
            case PART_OF_GAPLESS_ALBUM:
            case ITUNES_SMPB:
            case ITUNES_NORM:
            case CDDB_1:
            case CDDB_TRACKNUMBER:
            case CDDB_IDS:
            case LYRICIST:
            case CONDUCTOR:
            case REMIXER:
            case ENGINEER:
            case PRODUCER:
            case DJMIXER:
            case MIXER:
            case MOOD:
            case ISRC:
            case MEDIA:
            case LABEL:
            case CATALOGNO:
            case BARCODE:
            case URL_OFFICIAL_RELEASE_SITE:
            case URL_DISCOGS_RELEASE_SITE:
            case URL_WIKIPEDIA_RELEASE_SITE:
            case URL_OFFICIAL_ARTIST_SITE:
            case URL_DISCOGS_ARTIST_SITE:
            case URL_WIKIPEDIA_ARTIST_SITE:
            case LANGUAGE:
            case KEY: 
            case URL_LYRICS_SITE:
                return new Mp4TagReverseDnsField(mp4FieldKey, value);

            case ARTWORK:
                throw new UnsupportedOperationException(ErrorMessage.ARTWORK_CANNOT_BE_CREATED_WITH_THIS_METHOD.getMsg());
                 
            default:
                return new Mp4TagTextField(mp4FieldKey.getFieldName(), value);
        }
    }

    public List<Artwork> getArtworkList()
    {
        List<TagField> coverartList = get(Mp4FieldKey.ARTWORK);
        List<Artwork> artworkList = new ArrayList<Artwork>(coverartList.size());

        for(TagField next:coverartList)
        {
            Mp4TagCoverField mp4CoverArt = (Mp4TagCoverField)next;
            Artwork artwork = new Artwork();
            artwork.setBinaryData(mp4CoverArt.getData());
            artwork.setMimeType(Mp4TagCoverField.getMimeTypeForImageType(mp4CoverArt.getFieldType()));
            artworkList.add(artwork);
        }
        return artworkList;
    }

}
