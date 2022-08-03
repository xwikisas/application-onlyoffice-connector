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
package com.xwiki.onlyofficeconnector.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.officeimporter.converter.OfficeConverter;
import org.xwiki.officeimporter.converter.OfficeConverterException;
import org.xwiki.officeimporter.converter.OfficeConverterResult;
import org.xwiki.officeimporter.server.OfficeServer;
import org.xwiki.officeimporter.server.OfficeServerException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xwiki.onlyofficeconnector.AttachmentConversionException;
import com.xwiki.onlyofficeconnector.AttachmentFormatManager;

/**
 * Default implementation of {@link AttachmentFormatManager}.
 *
 * @version $Id$
 * @since 2.0
 */
@Component
@Singleton
public class DefaultAttachmentFormatManager implements AttachmentFormatManager
{
    private static final String FAILED_CONVERSION_EXCEPTION_MESSAGE =
        "Failed to convert attachment [%s] to format [%s].";

    @Inject
    private OfficeServer officeServer;

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Override
    public byte[] convertAttachment(AttachmentReference attachmentReference, String format)
        throws AttachmentConversionException
    {
        checkLibreOfficeServer();

        XWikiContext context = contextProvider.get();
        DocumentReference docRef = attachmentReference.getDocumentReference();

        try {
            XWikiDocument document = context.getWiki().getDocument(docRef, context);
            String attachmentName = attachmentReference.getName();
            XWikiAttachment attachment = document.getAttachment(attachmentName);
            if (attachment == null) {
                throw new AttachmentConversionException(
                    String.format("Attachment [%s] could not be found", attachmentReference));
            }
            OfficeConverter converter = officeServer.getConverter();
            try (OfficeConverterResult result = converter.convertDocument(
                Collections.singletonMap(attachmentName, attachment.getContentInputStream(context)),
                attachmentName, changeExtensionAndGet(attachmentName, format)))
            {
                return FileUtils.readFileToByteArray(result.getOutputFile());
            }
        } catch (IOException | OfficeConverterException | XWikiException e) {
            throw new AttachmentConversionException(
                String.format(FAILED_CONVERSION_EXCEPTION_MESSAGE, attachmentReference, format), e);
        }
    }

    @Override
    public void convertAndSave(AttachmentReference attachmentReference, String format, byte[] file)
        throws AttachmentConversionException
    {
        checkLibreOfficeServer();

        DocumentReference docRef = attachmentReference.getDocumentReference();
        String attachmentName = attachmentReference.getName();
        XWikiContext context = contextProvider.get();

        try {
            XWikiDocument document = context.getWiki().getDocument(docRef, context);
            InputStream inputStream = new ByteArrayInputStream(file);
            String receivedFileName = changeExtensionAndGet(attachmentName, format);

            OfficeConverterResult result =
                officeServer.getConverter().convertDocument(Collections.singletonMap(receivedFileName, inputStream),
                    receivedFileName, attachmentName);

            document.setAttachment(attachmentName, FileUtils.openInputStream(result.getOutputFile()), context);

            document.setAuthorReference(context.getUserReference());
            context.getWiki().saveDocument(document, context);
            result.close();
        } catch (OfficeConverterException | IOException | XWikiException e) {
            throw new AttachmentConversionException(
                String.format(FAILED_CONVERSION_EXCEPTION_MESSAGE, attachmentReference, format), e);
        }
    }

    private void checkLibreOfficeServer() throws AttachmentConversionException
    {
        if (!officeServer.getState().equals(OfficeServer.ServerState.CONNECTED)) {
            try {
                officeServer.start();
            } catch (OfficeServerException e) {
                throw new AttachmentConversionException("Document Conversion server could not be contacted!");
            }
        }
    }

    private String changeExtensionAndGet(String file, String extension)
    {
        return file.substring(0, file.lastIndexOf('.') + 1) + extension;
    }
}
