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

package com.xwiki.onlyofficeconnector.script;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.primeframework.jwt.domain.JWT;
import org.xwiki.component.annotation.Component;
import org.xwiki.script.service.ScriptService;
import org.xwiki.stability.Unstable;

import com.xwiki.onlyofficeconnector.OnlyOfficeManager;
import com.xwiki.onlyofficeconnector.configuration.OnlyOfficeConfiguration;

/**
 * Smth.
 *
 * @version $Id$
 * @since 2.2.0
 */
@Component
@Named("onlyoffice")
@Singleton
public class OnlyOfficeScriptService implements ScriptService
{
    @Inject
    private OnlyOfficeManager onlyOfficeManager;

    @Inject
    private OnlyOfficeConfiguration configuration;

    /**
     * Create JWT token having a set of claims.
     *
     * @param payloadClaims a map of properties that will be added as claims to the JWT.
     * @return the serialized JWT.
     */
    public String createToken(final Map<String, Object> payloadClaims)
    {
        return onlyOfficeManager.createToken(payloadClaims);
    }

    /**
     * Transform a received token into a readable item.
     *
     * @param token serialized token.
     * @return a JWT object or null if the token could not be parsed.
     */
    public JWT readToken(final String token)
    {
        return onlyOfficeManager.readToken(token);
    }

    /**
     * Get the document creation settings from the only office configuration.
     *
     * @return the document creation settings
     * @since 2.5.0
     */
    @Unstable
    public boolean isCreationEnabled()
    {
        return configuration.isCreationEnabled();
    }

    /**
     * Get the conversion settings from the only office configuration.
     *
     * @return the conversion settings
     * @since 2.5.0
     */
    @Unstable
    public String getConversionOption()
    {
        return configuration.getConversion();
    }

    /**
     * Get the authorization header from the only office configuration.
     *
     * @return the authorization header
     * @since 2.5.0
     */
    @Unstable
    public String getAuthorizationHeader()
    {
        return configuration.getAuthorizationHeader();
    }

    /**
     * Get the realtime editing settings from the only office configuration.
     *
     * @return the realtime editing
     * @since 2.5.0
     */
    @Unstable
    public boolean isRealtimeEnabled()
    {
        return configuration.isRealtimeEnabled();
    }

    /**
     * Get the only office server URL from the only office configuration.
     *
     * @return the only office server URL
     * @since 2.5.0
     */
    @Unstable
    public String getOnlyOfficeServer()
    {
        return configuration.getOnlyOfficeServer();
    }
}
