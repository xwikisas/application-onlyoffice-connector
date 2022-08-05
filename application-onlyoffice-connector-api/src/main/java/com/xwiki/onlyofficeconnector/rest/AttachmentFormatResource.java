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

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.xwiki.rest.XWikiRestException;
import org.xwiki.stability.Unstable;

/**
 * Provides the functionality of converting attachments to different formats.
 *
 * @version $Id$
 * @since 2.0
 */
@Unstable
@Path("/wikis/{wikiName}/spaces/{spaceName: .+}/pages/{pageName}/attachments/{attachmentName}/formats/{format}")
public interface AttachmentFormatResource
{
    /**
     * Converts the attachment identified by the URL and returns the path to it.
     *
     * @param wikiName The Wiki where the attachment is located.
     * @param spaceName The spaces of the document that holds the attachment.
     * @param pageName The name of the page that holds the attachment.
     * @param attachmentName The name of the attachment.
     * @param format The format to which the attachment will be converted.
     * @param fallBack If true, return the unconverted attachment in case of failure.
     * @return The path to the converted version of the file.
     * @throws XWikiRestException when the file could not be found or converted.
     */
    @GET
    Response convertAttachment(
        @PathParam("wikiName") String wikiName,
        @PathParam("spaceName") @Encoded String spaceName,
        @PathParam("pageName") String pageName,
        @PathParam("attachmentName") String attachmentName,
        @PathParam("format") String format,
        @QueryParam("fallback") @DefaultValue("false") Boolean fallBack
    ) throws XWikiRestException;

    /**
     * Converts a file sent through the body of the request and saves it to the document identified by the URL.
     *
     * @param wikiName The Wiki where the attachment will be located.
     * @param spaceName The spaces of the document that will hold the attachment.
     * @param pageName The name of the page that will hold the attachment.
     * @param attachmentName The name of the attachment.
     * @param format The format to which the attachment will be converted.
     * @param file The file that will be converted.
     * @return an HTTP response indicating the status of the conversion.
     */
    @PUT
    Response convertAndSave(
        @PathParam("wikiName") String wikiName,
        @PathParam("spaceName") @Encoded String spaceName,
        @PathParam("pageName") String pageName,
        @PathParam("attachmentName") String attachmentName,
        @PathParam("format") String format,
        byte[] file
    ) throws XWikiRestException;
}
