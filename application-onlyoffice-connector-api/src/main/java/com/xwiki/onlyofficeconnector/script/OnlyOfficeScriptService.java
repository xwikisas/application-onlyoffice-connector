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

import com.xwiki.onlyofficeconnector.OnlyOfficeManager;

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
}
