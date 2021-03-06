package org.apache.maven.jxr.java.src.symtab;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.log4j.Logger;

import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Definition of a variable in a source file.
 * This can be member data in class,
 * a local variable or a method parameter.
 *
 * @version $Id$
 */
public class VariableDef
    extends Definition
    implements TypedDef, Externalizable
{

    /** Logger for this class  */
    private static final Logger log = Logger.getLogger( VariableDef.class );

    // ==========================================================================
    // ==  Class Variables
    // ==========================================================================

    /** The type of the variable */
    private ClassDef type = null;

    // ==========================================================================
    // ==  Methods
    // ==========================================================================

    /**
     * Default constructor needs to be public for deserialization.
     */
    public VariableDef()
    {
    }

    /**
     * Constructor to create a new variable symbol
     *
     * @param name
     * @param occ
     * @param type
     * @param parentScope
     */
    VariableDef( String name, // the variable's name
                 Occurrence occ, // where it was defined
                 ClassDef type, // the type of the variable
                 ScopedDef parentScope )
    { // which scope owns it

        super( name, occ, parentScope );

        this.type = type;
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.TypedDef#getType()
     */
    public Definition getType()
    {
        return type;
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.Definition#generateTags(org.apache.maven.jxr.java.src.symtab.HTMLTagContainer)
     */
    public void generateTags( HTMLTagContainer tagList )
    {

        String nameString = "<a class=\"varDef\" name=\"" + getClassScopeName() + "\" href=\"" + getRefName() + "#"
            + getClassScopeName() + "\">" + getName() + "</a>";

        // generate tag for this method
        if ( getOccurrence() == null )
        {
            return;
        }

        HTMLTag t = new HTMLTag( getOccurrence(), getName(), nameString );

        tagList.addElement( t );
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.Definition#getOccurrenceTag(org.apache.maven.jxr.java.src.symtab.Occurrence)
     */
    public HTMLTag getOccurrenceTag( Occurrence occ )
    {

        if ( log.isDebugEnabled() )
        {
            log.debug( "getOccurrenceTag(Occurrence) - Occurrence occ=" + occ );
        }

        String linkString;
        String linkFileName;

        linkFileName = getRelativePath( occ ) + getSourceName();
        linkString = "<a class=\"varRef\" title=\"" + getType().getName() + "\" " + "href=\"" + linkFileName + "#"
            + getClassScopeName() + "\">" + getName() + "</a>";

        HTMLTag t = new HTMLTag( occ, getName(), linkString );

        return t;
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.Definition#resolveTypes(org.apache.maven.jxr.java.src.symtab.SymbolTable)
     */
    void resolveTypes( SymbolTable symbolTable )
    {

        if ( ( type != null ) && ( type instanceof DummyClass ) )
        {

            // resolve the type of the variable
            ClassDef newType = symbolTable.lookupDummy( type );

            if ( newType != null )
            {
                newType.addReference( type.getOccurrence() );

                type = newType;
            }
        }

        super.resolveTypes( symbolTable );
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.Definition#getClassScopeName()
     */
    String getClassScopeName()
    {

        String result;
        Definition parentScope = getParentScope();
        Definition grandParentScope = parentScope.getParentScope();

        if ( parentScope instanceof MethodDef ) // method variable
        {
            Definition greatGrandParentScope = grandParentScope.getParentScope();

            if ( greatGrandParentScope instanceof ClassDef ) // inner class
            {
                result = greatGrandParentScope.getName() + "." + grandParentScope.getName();
            }
            else
            {
                result = grandParentScope.getName();
            }

            result += "." + parentScope.getName();
        }
        else
        // class variable
        {
            if ( grandParentScope instanceof ClassDef ) // inner class
            {
                result = grandParentScope.getName() + "." + parentScope.getName();
            }
            else
            {
                result = parentScope.getName();
            }
        }

        result += "." + getName();

        return result;
    }

    /**
     * @see java.io.Externalizable#writeExternal(java.io.ObjectOutput)
     */
    public void writeExternal( ObjectOutput out )
        throws java.io.IOException
    {

        if ( log.isDebugEnabled() )
        {
            log.debug( "writeExternal(ObjectOutput) - persisting VariableDef " + getQualifiedName() );
        }

        out.writeObject( getName() );

        Definition parentScopeOut = getParentScope();

        if ( !( parentScopeOut instanceof MethodDef ) )
        {
            parentScopeOut = new ClassDefProxy( (ClassDef) parentScopeOut );
        }

        out.writeObject( parentScopeOut );
        out.writeObject( getOccurrence() );

        ClassDef typeOut;

        if ( type instanceof DummyClass )
        {
            typeOut = type;
        }
        else
        {
            typeOut = new ClassDefProxy( type );
        }

        out.writeObject( typeOut );
    }

    /**
     * @see java.io.Externalizable#readExternal(java.io.ObjectInput)
     */
    public void readExternal( ObjectInput in )
        throws java.io.IOException, ClassNotFoundException
    {

        setName( (String) in.readObject() );
        SymbolTable.startReadExternal( "VariableDef " + getName() );
        setParentScope( (ScopedDef) in.readObject() );
        setOccurrence( (Occurrence) in.readObject() );

        type = (ClassDef) in.readObject();

        setupFileNames(); // TBD: still need this?
        SymbolTable.endReadExternal();
    }

    /**
     * @see org.apache.maven.jxr.java.src.symtab.Definition#accept(org.apache.maven.jxr.java.src.symtab.Visitor)
     */
    public void accept( Visitor visitor )
    {
        visitor.visit( this );
    }
}
