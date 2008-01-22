package org.apache.maven.doxia.module.xwiki.parser;

import org.apache.maven.doxia.module.xwiki.blocks.Block;
import org.apache.maven.doxia.module.xwiki.blocks.FigureBlock;
import org.apache.maven.doxia.module.xwiki.blocks.MacroBlock;
import org.apache.maven.doxia.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class was written with performance in mind and thus is lacking clarity. Since this code is
 * in the rendering path it's important it be very fast.
 * <p/>
 * TODO: Compare speed with using regex. For example, something like this:
 * <pre><code>
 *         Pattern p = Pattern.compile("(?:(.*)(?::)?)?(.*)?(?:/)}(?:(.*)\\{(?:\\/)\1})?");
 * <p/>
 *         Matcher m = p.matcher(macro);
 *         m.find();
 *         Map parameters = new HashMap();
 *         if (m.group(2) != null && m.group(2).length() > 0) {
 *             StringTokenizer st = new StringTokenizer(m.group(2), "|");
 *             while (st.hasMoreTokens()) {
 *                 String param = st.nextToken();
 *                 StringTokenizer st2 = new StringTokenizer(param, "=");
 *                 parameters.put(st2.nextToken(), st2.nextToken());
 *             }
 *          }
 * </code></pre>
 */
public class MacroParser
{
    private int STATE_NAME = 0;

    private int STATE_PARAM_NAME = 1;

    private int STATE_PARAM_VALUE = 2;

    private int STATE_CONTENT = 3;

    private int STATE_END = 4;

    private boolean isInCompatibilityMode = false;

    private List multilineMacros;

    public class MacroParserResult
    {
        public int position;

        public Block block;
    }

    public MacroParser()
    {
        this.multilineMacros = new ArrayList();
        this.multilineMacros.add( "code" );
        this.multilineMacros.add( "style" );
        this.multilineMacros.add( "pre" );
    }

    public void setCompatibilityMode( boolean isInCompatibilityMode )
    {
        this.isInCompatibilityMode = isInCompatibilityMode;
    }

    public MacroParserResult parse( String input, int position )
        throws ParseException
    {
        MacroParserResult result = new MacroParserResult();
        String macroName = null;
        Map parameters = new HashMap();
        String parameterName = null;
        String content = "";

        int state = STATE_NAME;
        StringBuffer text = new StringBuffer();

        int i = position;
        while ( i < input.length() && state != STATE_END )
        {
            char c = input.charAt( i );

            switch ( c )
            {
                // {macroname:...}
                case ':':
                    if ( state == STATE_NAME )
                    {
                        macroName = text.toString();
                        state = STATE_PARAM_NAME;
                        text = new StringBuffer();
                    }
                    else if ( state != STATE_PARAM_VALUE )
                    {
                        throw new ParseException( "Invalid position for character ':' in Macro" );
                    }
                    else
                    {
                        // We only allow ':' characters in parameter values (and of course after
                        // the macro name).
                        text.append( c );
                    }
                    break;
                    // {macroname:... /}. Contraction of {macroname:...}{/macroname}
                case '/':
                    if ( state == STATE_PARAM_VALUE || state == STATE_NAME )
                    {
                        if ( charAt( input, i ) == '}' )
                        {
                            i++;
                            if ( state == STATE_PARAM_VALUE )
                            {
                                parameters.put( parameterName, text.toString() );
                            }
                            else
                            {
                                macroName = text.toString();
                            }
                            state = STATE_END;
                        }
                        else
                        {
                            // We allow the '/' character in parameter values.
                            text.append( c );
                        }
                    }
                    else if ( state == STATE_CONTENT )
                    {
                        text.append( c );
                    }
                    else if ( isInCompatibilityMode && state == STATE_PARAM_NAME )
                    {
                        if ( charAt( input, i ) == '}' )
                        {
                            // In compatibility mode, allow a parameter without value
                            i++;
                            parameters.put( "default", text.toString() );
                            state = STATE_END;
                        }
                        else
                        {
                            throw new ParseException( "Character '/' is not valid in parameter names" );
                        }
                    }
                    else
                    {
                        throw new ParseException( "Invalid position for character '/' in Macro" );
                    }
                    break;
                    // {macro:...} or {macro:...}...{macro} or {macro:...}...{/macro}
                case '}':
                    // Here are the use cases to take into account:
                    // * {newmacro..}...{/newmacro}
                    // * {oldsinglelinemacro:...}
                    // * {oldmultilinemacro:...}...{oldmultilinemacro}
                    if ( state == STATE_PARAM_NAME )
                    {
                        if ( isInCompatibilityMode )
                        {
                            parameters.put( "default", text.toString() );
                            text = new StringBuffer();

                            // Since we can't guess if a macro is a multiline one or a single line one we rely on
                            // a static list of known multiline macros...
                            if ( multilineMacros.contains( macroName ) )
                            {
                                state = STATE_CONTENT;
                            }
                            else
                            {
                                state = STATE_END;
                            }

                        }
                        else
                        {
                            throw new ParseException(
                                "A value must be specified for a macro parameter (e.g. param=value)" );
                        }
                    }
                    else if ( state == STATE_PARAM_VALUE )
                    {
                        parameters.put( parameterName, text.toString() );
                        text = new StringBuffer();

                        // {macro:...}
                        if ( isInCompatibilityMode )
                        {
                            // Since we can't guess if a macro is a multiline one or a single line one we rely on
                            // a static list of known multiline macros...
                            if ( multilineMacros.contains( macroName ) )
                            {
                                state = STATE_CONTENT;
                            }
                            else
                            {
                                state = STATE_END;
                            }
                        }
                        else
                        {
                            state = STATE_CONTENT;
                        }
                    }
                    else if ( state == STATE_NAME )
                    {
                        macroName = text.toString();
                        text = new StringBuffer();

                        // {macro:...}
                        if ( isInCompatibilityMode )
                        {
                            // Since we can't guess if a macro is a multiline one or a single line one we rely on
                            // a static list of known multiline macros...
                            if ( multilineMacros.contains( macroName ) )
                            {
                                state = STATE_CONTENT;
                            }
                            else
                            {
                                state = STATE_END;
                            }
                        }
                        else
                        {
                            state = STATE_CONTENT;
                        }
                    }
                    else if ( state == STATE_CONTENT )
                    {
                        // ...{macro}
                        if ( isInCompatibilityMode )
                        {
                            state = STATE_END;
                        }
                        else
                        {
                            // We allow the '}' character in macro content.
                            text.append( c );
                        }
                    }
                    break;
                    // {macroname:...}...{/macroname}
                case '{':
                    // When this parsing method is called it's assumed the first { character
                    // representing the macro has already been consumed so if there's another such
                    // character it means it's either part of the macro content (signifying a
                    // nested macro), part of a macro parameter or simply the closing part of the
                    // macro.
                    if ( charAt( input, i ) == '/' )
                    {
                        // TODO: We should probably verify here that the name of the closed macro
                        // corresponds to the current macro being parsed. For now we just assume it
                        // is.
                        content = text.toString();
                        i++;
                        char cc;
                        do
                        {
                            i++;
                            cc = input.charAt( i );
                        }
                        while ( cc != '}' && i < input.length() );
                        state = STATE_END;
                    }
                    else
                    {
                        if ( state == STATE_PARAM_VALUE )
                        {
                            // We allow the '{' character in macro parameter values
                            text.append( c );
                        }
                        else if ( state == STATE_CONTENT )
                        {
                            if ( isInCompatibilityMode )
                            {
                                // Allow closing macros without using the '/' character
                                // TODO: Add special support for code macros nested in other code macros and for
                                // style macros nested in other style macros.

                                // Verify that the following characters are the name of the macro and thus that this
                                // '}' is the beginning of the macro closing.
                                int pos = input.indexOf( macroName + "}", i + 1 );
                                if ( pos == i + 1 )
                                {
                                    state = STATE_END;
                                    i += macroName.length() + 1;
                                    content = text.toString();
                                }
                                else
                                {
                                    text.append( c );
                                }
                            }
                            else
                            {
                                // TODO: We have a '{' character inside the macro content. Let's consider
                                // it's a nested macro and let's parse it.
                                // For now just ignore it
                                text.append( c );
                            }
                        }
                    }
                    break;
                case '|':
                    // TODO: In the future allow quoted param values so that this character can be
                    // supported too in param values.
                    if ( state == STATE_PARAM_VALUE )
                    {
                        parameters.put( parameterName, text.toString() );
                        text = new StringBuffer();
                        state = STATE_PARAM_NAME;
                    }
                    else if ( state == STATE_CONTENT )
                    {
                        text.append( c );
                    }
                    else if ( isInCompatibilityMode && state == STATE_PARAM_NAME )
                    {
                        parameters.put( "default", text.toString() );
                        text = new StringBuffer();
                        state = STATE_PARAM_NAME;

                    }
                    else
                    {
                        throw new ParseException( "Invalid position for character '|' in Macro" );
                    }
                    break;
                case '=':
                    if ( state == STATE_PARAM_NAME )
                    {
                        parameterName = text.toString();
                        text = new StringBuffer();
                        state = STATE_PARAM_VALUE;
                    }
                    else if ( state == STATE_PARAM_VALUE || state == STATE_CONTENT )
                    {
                        text.append( c );
                    }
                    else
                    {
                        throw new ParseException( "Invalid position for character '=' in Macro" );
                    }
                    break;
                default:
                    // Any non alphanumeric character found when parsing the macro name should stop the parsing since
                    // it means the text being parsed is not a macro.
                    if ( ( state == STATE_NAME ) && ( ( c < 'a' || c > 'z' ) && ( c < 'A' || c > 'Z' ) ) )
                    {
                        // Invalid macro, exit
                        state = STATE_END;
                    }
                    else
                    {
                        text.append( c );
                    }
            }

            i++;
        }

        if ( state != STATE_END || macroName == null )
        {
            // This is not a valid macro. We have two choices here:
            // 1) decide that the code is not a macro and reset the cursor position at the beginning
            // 2) throw a parsing exception
            // For the moment we consider that the code is not a macro (option 1)).
            result.position = position;
        }
        else
        {
            result.block = createAppropriateBlock( macroName, parameters, content );
            result.position = i;
        }

        return result;
    }

    private Block createAppropriateBlock( String macroName, Map parameters, String content )
    {
        Block result;
        if ( macroName.equals( "image" ) )
        {
            String caption = (String) parameters.get( "alt" );
            String location = (String) parameters.get( "default" );
            if ( location == null )
            {
                location = (String) parameters.get( "file" );
            }

            if ( caption == null )
            {
                result = new FigureBlock( location );
            }
            else
            {
                result = new FigureBlock( location, caption );
            }
        }
        else
        {
            result = new MacroBlock( macroName, parameters, content );
        }

        return result;
    }

    private static char charAt( String input, int i )
    {
        return input.length() > i + 1 ? input.charAt( i + 1 ) : '\0';
    }
}
