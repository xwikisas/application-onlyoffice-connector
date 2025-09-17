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
package com.xwiki.onlyofficeconnector.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestComponent;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

/**
 * Provides necessary endpoints for the only office application.
 *
 * @version $Id$
 * @since 2.5.0
 */
@Unstable
@Path("/onlyoffice/attachment")
public interface OnlyOfficeResource extends XWikiRestComponent
{
    /**
     * Return the XWiki attachment content with code 200. Return code 401 if the authorization token is invalid.
     *
     * @param attachRef the reference of the attachment for which the content is to be retrieved.
     * @return the attachment content
     * @throws XWikiRestException if an internal error occurs.
     */
    @GET
    @Path("/content")
    Response getContent(@QueryParam("attach_ref") String attachRef) throws XWikiRestException;
}
