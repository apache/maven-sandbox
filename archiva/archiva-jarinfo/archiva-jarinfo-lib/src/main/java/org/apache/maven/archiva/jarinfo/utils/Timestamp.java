package org.apache.maven.archiva.jarinfo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * UTC Timestamps
 */
public class Timestamp
{
    public static final TimeZone UTC = TimeZone.getTimeZone( "UTC" );
    
    public static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss z";

    /**
     * Get the {@link Calendar} for now, in {@link #UTC} {@link TimeZone}.
     * 
     * @return the {@link Calendar} for now in {@link #UTC} {@link TimeZone}.
     */
    public static Calendar now()
    {
        return Calendar.getInstance( UTC );
    }
    
    /**
     * Convert calendar to text.
     * 
     * @param cal the calendar to convert
     * @return the text form of the Calendar.
     */
    public static String convert( Calendar cal )
    {
        SimpleDateFormat formatter = new SimpleDateFormat( TIMESTAMP_FORMAT );
        formatter.setTimeZone( UTC );
        cal.setTimeZone( UTC );
        return formatter.format( cal.getTime() );
    }
    
    /**
     * Convert text to Calendar.
     * 
     * @param text the text to convert.
     * @return the Calendar represented by the text, or null of text cannot be parsed.
     */
    public static Calendar convert( String text )
    {
        SimpleDateFormat format = new SimpleDateFormat( TIMESTAMP_FORMAT );
        format.setTimeZone( UTC );
        try
        {
            Date date = format.parse( text );
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone( UTC );
            cal.setTime( date );
            return cal;
        }
        catch ( ParseException e )
        {
            return null;
        }
    }
}
