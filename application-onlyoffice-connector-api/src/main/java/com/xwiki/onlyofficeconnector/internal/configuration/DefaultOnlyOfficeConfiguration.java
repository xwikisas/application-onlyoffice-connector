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

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;

import com.xwiki.onlyofficeconnector.configuration.OnlyOfficeConfiguration;

/**
 * Default implementation of {@link OnlyOfficeConfiguration}.
 *
 * @version $Id$
 * @since 2.5.0
 */
@Component
@Singleton
public class DefaultOnlyOfficeConfiguration implements OnlyOfficeConfiguration
{
    private static final String SERVER_SECRET_KEY = "serverSecret";

    private static final String AUTHORIZATION_HEADER_KEY = "authorizationHeader";

    private static final String ONLY_OFFICE_SERVER_KEY = "onlyOfficeServer";

    private static final String ENABLE_REALTIME_KEY = "enableRealtime";

    private static final String ENABLE_CREATION_KEY = "enableCreation";

    private static final String CONVERSION_KEY = "conversion";

    private static final String DEFAULT_AUTHORIZATION_HEADER = "Authorization";

    @Inject
    @Named(OnlyOfficeConfigurationSource.HINT)
    private ConfigurationSource configuration;

    @Override
    public String getSecret()
    {
        return this.configuration.getProperty(SERVER_SECRET_KEY, "");
    }

    @Override
    public String getOnlyOfficeServer()
    {
        return this.configuration.getProperty(ONLY_OFFICE_SERVER_KEY, "");
    }

    @Override
    public boolean isRealtimeEnabled()
    {
        return this.configuration.getProperty(ENABLE_REALTIME_KEY, true);
    }

    @Override
    public boolean isCreationEnabled()
    {
        return this.configuration.getProperty(ENABLE_CREATION_KEY, true);
    }

    @Override
    public String getConversion()
    {
        return this.configuration.getProperty(CONVERSION_KEY, "");
    }

    @Override
    public String getAuthorizationHeader()
    {
        return this.configuration.getProperty(AUTHORIZATION_HEADER_KEY, DEFAULT_AUTHORIZATION_HEADER);
    }
}
