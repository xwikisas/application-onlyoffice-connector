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

package com.xwiki.onlyofficeconnector.internal.rest;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.officeimporter.converter.OfficeConverter;
import org.xwiki.officeimporter.converter.OfficeDocumentFormat;
import org.xwiki.officeimporter.server.OfficeServer;
import org.xwiki.rest.XWikiRestException;
import org.xwiki.rest.internal.resources.pages.ModifiablePageResource;
import org.xwiki.security.authorization.ContextualAuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwiki.onlyofficeconnector.AttachmentConversionException;
import com.xwiki.onlyofficeconnector.AttachmentFormatManager;
import com.xwiki.onlyofficeconnector.OnlyOfficeManager;
import com.xwiki.onlyofficeconnector.configuration.OnlyOfficeConfiguration;
import com.xwiki.onlyofficeconnector.rest.AttachmentFormatResource;

/**
 * Default implementation of {@link AttachmentFormatResource}.
 *
 * @version $Id$
 * @since 2.0
 */
@Component
@Named("com.xwiki.onlyofficeconnector.internal.rest.DefaultAttachmentFormatResource")
@Singleton
public class DefaultAttachmentFormatResource extends ModifiablePageResource implements AttachmentFormatResource
{
    private static final String BEARER_KEY = "Bearer ";

    @Inject
    private OfficeServer officeServer;

    @Inject
    private OnlyOfficeConfiguration onlyOfficeConfiguration;

    @Inject
    private AttachmentFormatManager attachmentFormatManager;

    @Inject
    private ContextualAuthorizationManager authorizationManager;

    @Inject
    private OnlyOfficeManager onlyOfficeManager;

    @Inject
    private Logger logger;

    @Override
    public Response convertAttachment(String wikiName, String spaces, String pageName, String attachmentName,
        String format, Boolean fallBack) throws XWikiRestException
    {
        logger.debug("Request to convert attachment [{}] to format [{}] initiated.", attachmentName, format);
        try {
            onlyOfficeManager.checkAuthorizationToken();
        } catch (SecurityException exc) {
            logger.warn("There was a security issue while attempting to to convert attachment [{}] to format [{}: [{}]",
                attachmentName, format, exc.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }
        XWikiAttachment attachment = getAttachment(wikiName, spaces, pageName, attachmentName);

        OfficeConverter converter = officeServer.getConverter();
        OfficeDocumentFormat inputFormat = converter.getDocumentFormat(attachmentName);
        OfficeDocumentFormat outputFormat = converter.getDocumentFormat("fileName." + format);
        if (!converter.isConversionSupported(inputFormat.getMediaType(), outputFormat.getMediaType())
            && !Boolean.TRUE.equals(fallBack))
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("The conversion is not supported.").build();
        }
        try {
            byte[] result = attachmentFormatManager.convertAttachment(attachment.getReference(), format);
            return Response
                .status(Response.Status.OK)
                .type(inputFormat.getMediaType())
                .entity(result)
                .build();
        } catch (AttachmentConversionException e) {
            String errorMessage =
                String.format("Failed to convert attachment [%s] to format [%s].", attachmentName, format);
            logger.warn(errorMessage, e);
            if (Boolean.TRUE.equals(fallBack)) {
                logger.debug("Attempting conversion fallback.");
                try {
                    XWikiContext wikiContext = getXWikiContext();
                    return Response.ok().type(attachment.getMimeType())
                        .entity(attachment.getContentInputStream(wikiContext).readAllBytes()).build();
                } catch (Exception ex) {
                    logger.warn("Failed attachment conversion fallback", ex);
                    throw new XWikiRestException(ex);
                }
            }
            throw new XWikiRestException(e);
        }
    }

    @Override
    public Response convertAndSave(String wikiName, String spaces, String pageName, String attachmentName,
        String format, byte[] file) throws XWikiRestException
    {

        DocumentReference docRef = new DocumentReference(pageName, getSpaceReference(spaces, wikiName));

        AttachmentReference attachmentReference = new AttachmentReference(attachmentName, docRef);
        if (!authorizationManager.hasAccess(Right.EDIT, attachmentReference)) {
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
        }
        try {
            attachmentFormatManager.convertAndSave(attachmentReference, format, file);
            return Response.ok().build();
        } catch (AttachmentConversionException e) {
            throw new XWikiRestException(e);
        }
    }

    private XWikiAttachment getAttachment(String wikiName, String spaces, String pageName, String attachmentName)
        throws XWikiRestException
    {
        try {
            XWikiContext wikiContext = getXWikiContext();
            XWikiDocument doc = wikiContext.getWiki()
                .getDocument(new DocumentReference(wikiName, parseSpaceSegments(spaces), pageName), wikiContext);
            XWikiAttachment attachment = doc.getAttachment(attachmentName);
            if (attachment == null) {
                throw new WebApplicationException(Response.Status.NOT_FOUND);
            }
            return attachment;
        } catch (XWikiException e) {
            throw new XWikiRestException(e);
        }
    }
}
