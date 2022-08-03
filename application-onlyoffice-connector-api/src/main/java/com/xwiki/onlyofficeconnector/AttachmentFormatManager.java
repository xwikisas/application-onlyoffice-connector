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
package com.xwiki.onlyofficeconnector;

import org.xwiki.component.annotation.Role;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.stability.Unstable;

/**
 * Provides methods to convert attachments.
 *
 * @version $Id$
 * @since 2.0
 */
@Role
@Unstable
public interface AttachmentFormatManager
{
    /**
     * Convert an attachment to the desired format.
     *
     * @param attachmentReference the reference of the attachments that needs conversion
     * @param format the format to which the attachment will be converted to
     * @return a byte array representing the contents of the converted file
     * @throws AttachmentConversionException if there were errors in locating or converting the attachment
     */
    byte[] convertAttachment(AttachmentReference attachmentReference, String format)
        throws AttachmentConversionException;

    /**
     * Converts a file to a desired format and, if the conversion is successful, saves it to the document the reference
     * points to.
     *
     * @param attachmentReference the reference of the attachment pointing the location and name of the file that
     * will be converted
     * @param format the format to which the file will be converted to
     * @param file the contents of the file that needs conversion
     * @throws AttachmentConversionException if there were errors in converting the file
     */
    void convertAndSave(AttachmentReference attachmentReference, String format, byte[] file)
        throws AttachmentConversionException;

}
