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
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.rest.XWikiRestException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwiki.onlyofficeconnector.OnlyOfficeManager;
import com.xwiki.onlyofficeconnector.configuration.OnlyOfficeConfiguration;
import com.xwiki.onlyofficeconnector.rest.OnlyOfficeResource;

/**
 * Default implementation of {@link OnlyOfficeResource}.
 *
 * @version $Id$
 * @since 2.5.0
 */
@Component
@Named("com.xwiki.onlyofficeconnector.internal.rest.DefaultOnlyOfficeResource")
@Singleton
public class DefaultOnlyOfficeResource implements OnlyOfficeResource
{
    @Inject
    private OnlyOfficeManager onlyOfficeManager;

    @Inject
    private OnlyOfficeConfiguration onlyOfficeConfiguration;

    @Inject
    private Provider<XWikiContext> wikiContextProvider;

    @Inject
    @Named("current")
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Inject
    private Logger logger;

    @Override
    public Response getContent(String attachRef) throws XWikiRestException
    {
        try {
            logger.debug("Request to get content initiated for attachment: [{}].", attachRef);
            onlyOfficeManager.checkAuthorizationToken();
            XWikiContext xcontext = wikiContextProvider.get();
            AttachmentReference attachmentReference = this.attachmentReferenceResolver.resolve(attachRef);
            DocumentReference documentReference = attachmentReference.getDocumentReference();
            XWikiDocument doc = xcontext.getWiki().getDocument(documentReference, xcontext);
            XWikiAttachment attachment = doc.getAttachment(attachmentReference.getName());

            return Response.ok().entity(attachment.getContentInputStream(xcontext))
                .type(attachment.getMimeType()).build();
        } catch (SecurityException ex) {
            logger.warn("There was a security issue while attempting to get the content of [{}]: [{}]", attachRef,
                ex.getMessage());
            return Response.status(Response.Status.UNAUTHORIZED).build();
        } catch (Exception e) {
            logger.warn("Failed to get content of file [{}]. Root cause: [{}]", attachRef,
                ExceptionUtils.getRootCauseMessage(e));
            throw new XWikiRestException(e);
        }
    }
}
