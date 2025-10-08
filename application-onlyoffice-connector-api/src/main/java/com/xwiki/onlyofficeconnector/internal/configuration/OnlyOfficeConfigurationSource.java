/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xwiki.onlyofficeconnector.internal.configuration;

import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.internal.AbstractWikisConfigurationSource;
import org.xwiki.model.reference.LocalDocumentReference;

/**
 * Only office configuration source.
 *
 * @version $Id$
 */
@Component
@Named(OnlyOfficeConfigurationSource.HINT)
@Singleton
public class OnlyOfficeConfigurationSource extends AbstractWikisConfigurationSource
{
    /**
     * The hint for this component.
     */
    public static final String HINT = "onlyoffice.configuration.current";

    private static final LocalDocumentReference CONFIG_REFERENCE =
        new LocalDocumentReference("XWikiOnlyOfficeCode", "ConfigurationClass");

    private static final LocalDocumentReference CONFIG_CLASS_REFERENCE = CONFIG_REFERENCE;

    @Override
    protected LocalDocumentReference getLocalDocumentReference()
    {
        return CONFIG_REFERENCE;
    }

    @Override
    protected LocalDocumentReference getClassReference()
    {
        return CONFIG_CLASS_REFERENCE;
    }

    @Override
    protected String getCacheId()
    {
        return HINT;
    }
}
