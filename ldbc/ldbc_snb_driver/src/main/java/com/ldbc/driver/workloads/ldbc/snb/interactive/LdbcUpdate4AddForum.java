package com.ldbc.driver.workloads.ldbc.snb.interactive;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.ldbc.driver.Operation;
import com.ldbc.driver.SerializingMarshallingException;
import com.ldbc.driver.util.ListUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class LdbcUpdate4AddForum extends Operation<LdbcNoResult>
{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final int TYPE = 1004;
    public static final String FORUM_ID = "forumId";
    public static final String FORUM_TITLE = "forumTitle";
    public static final String CREATION_DATE = "creationDate";
    public static final String MODERATOR_PERSON_ID = "moderatorPersonId";
    public static final String TAG_IDS = "tagIds";

    private final long forumId;
    private final String forumTitle;
    private final Date creationDate;
    private final long moderatorPersonId;
    private final List<Long> tagIds;

    public LdbcUpdate4AddForum( long forumId, String forumTitle, Date creationDate, long moderatorPersonId,
            List<Long> tagIds )
    {
        this.forumId = forumId;
        this.forumTitle = forumTitle;
        this.creationDate = creationDate;
        this.moderatorPersonId = moderatorPersonId;
        this.tagIds = tagIds;
    }

    public long forumId()
    {
        return forumId;
    }

    public String forumTitle()
    {
        return forumTitle;
    }

    public Date creationDate()
    {
        return creationDate;
    }

    public long moderatorPersonId()
    {
        return moderatorPersonId;
    }

    public List<Long> tagIds()
    {
        return tagIds;
    }

    @Override
    public Map<String, Object> parameterMap() {
        return ImmutableMap.<String, Object>builder()
                .put(FORUM_ID, forumId)
                .put(FORUM_TITLE, forumTitle)
                .put(CREATION_DATE, creationDate)
                .put(MODERATOR_PERSON_ID, moderatorPersonId)
                .put(TAG_IDS, tagIds)
                .build();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        { return true; }
        if ( o == null || getClass() != o.getClass() )
        { return false; }

        LdbcUpdate4AddForum that = (LdbcUpdate4AddForum) o;

        if ( forumId != that.forumId )
        { return false; }
        if ( moderatorPersonId != that.moderatorPersonId )
        { return false; }
        if ( creationDate != null ? !creationDate.equals( that.creationDate ) : that.creationDate != null )
        { return false; }
        if ( forumTitle != null ? !forumTitle.equals( that.forumTitle ) : that.forumTitle != null )
        { return false; }
        if ( tagIds != null ? !ListUtils.listsEqual( sort( tagIds ), sort( that.tagIds ) ) : that.tagIds != null )
        { return false; }

        return true;
    }

    private <T extends Comparable> List<T> sort( List<T> list )
    {
        Collections.sort( list );
        return list;
    }

    @Override
    public int hashCode()
    {
        int result = (int) (forumId ^ (forumId >>> 32));
        result = 31 * result + (forumTitle != null ? forumTitle.hashCode() : 0);
        result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
        result = 31 * result + (int) (moderatorPersonId ^ (moderatorPersonId >>> 32));
        result = 31 * result + (tagIds != null ? tagIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString()
    {
        return "LdbcUpdate4AddForum{" +
               "forumId=" + forumId +
               ", forumTitle='" + forumTitle + '\'' +
               ", creationDate=" + creationDate +
               ", moderatorPersonId=" + moderatorPersonId +
               ", tagIds=" + tagIds +
               '}';
    }

    @Override
    public LdbcNoResult marshalResult( String serializedOperationResult )
    {
        return LdbcNoResult.INSTANCE;
    }

    @Override
    public String serializeResult( Object operationResultInstance ) throws SerializingMarshallingException
    {
        try
        {
            return objectMapper.writeValueAsString(
                    LdbcSnbInteractiveWorkloadConfiguration.WRITE_OPERATION_NO_RESULT_DEFAULT_RESULT );
        }
        catch ( IOException e )
        {
            throw new SerializingMarshallingException( format( "Error while trying to serialize result\n%s",
                    operationResultInstance ), e );
        }
    }

    @Override
    public int type()
    {
        return TYPE;
    }
}
