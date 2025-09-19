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
package com.xwiki.onlyofficeconnector.configuration;

import org.xwiki.component.annotation.Role;
import org.xwiki.stability.Unstable;

/**
 * Only office configurations.
 *
 * @version $Id$
 * @since 2.5.0
 */
@Role
@Unstable
public interface OnlyOfficeConfiguration
{
    /**
     * Get the only office server secret from the only office configuration.
     *
     * @return the only office server secret
     */
    String getSecret();

    /**
     * Get the only office server URL from the only office configuration.
     *
     * @return the only office server URL
     */
    String getOnlyOfficeServer();

    /**
     * Get the realtime editing settings from the only office configuration.
     *
     * @return the realtime editing
     */
    boolean isRealtimeEnabled();

    /**
     * Get the document creation settings from the only office configuration.
     *
     * @return the document creation settings
     */
    boolean isCreationEnabled();

    /**
     * Get the conversion settings from the only office configuration.
     *
     * @return the conversion settings
     */
    String getConversion();

    /**
     * Get the authorization header from the only office configuration.
     *
     * @return the authorization header
     */
    String getAuthorizationHeader();
}
