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

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.primeframework.jwt.Signer;
import org.primeframework.jwt.Verifier;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;
import org.primeframework.jwt.hmac.HMACVerifier;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.LocalDocumentReference;
import org.xwiki.model.reference.WikiReference;
import org.xwiki.wiki.descriptor.WikiDescriptorManager;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;
import com.xwiki.onlyofficeconnector.OnlyOfficeManager;

/**
 * The default {@link OnlyOfficeManager} implementation that retrieves the JWT secret from the configuration file.
 * @since 2.2.0
 * @version $Id$
 */
@Component
@Singleton
public class DefaultOnlyOfficeManager implements OnlyOfficeManager
{
    private static final LocalDocumentReference CONFIG_REFERENCE = new LocalDocumentReference("XWikiOnlyOfficeCode",
        "ConfigurationClass");

    private static final LocalDocumentReference CONFIG_CLASS_REFERENCE = CONFIG_REFERENCE;

    private static final String SERVER_SECRET_KEY = "serverSecret";

    private static final String AUTHORIZATION_HEADER_KEY = "authorizationHeader";

    private static final String DEFAULT_AUTHORIZATION_HEADER = "Authorization";

    private static final String GLOBAL_CONFIG_KEY = "useGlobalConfig";

    @Inject
    private Provider<XWikiContext> contextProvider;

    @Inject
    private Logger logger;

    @Inject
    private WikiDescriptorManager wikiDescriptorManager;

    @Override
    public String createToken(final Map<String, Object> payloadClaims)
    {
        try {
            // build a HMAC signer using a SHA-256 hash
            Signer signer = HMACSigner.newSHA256Signer(getSecret());
            JWT jwt = new JWT();
            for (String key : payloadClaims.keySet()) {
                jwt.addClaim(key, payloadClaims.get(key));
            }
            return JWT.getEncoder().encode(jwt, signer);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public JWT readToken(final String token)
    {
        try {
            // build a HMAC verifier using the token secret
            Verifier verifier = HMACVerifier.newVerifier(getSecret());

            // verify and decode the encoded string JWT to a rich object
            return JWT.getDecoder().decode(token, verifier);
        } catch (Exception exception) {
            return null;
        }
    }

    @Override
    public String getAuthorizationHeader()
    {
        try {
            XWikiContext context = contextProvider.get();
            XWikiDocument document = context.getWiki().getDocument(CONFIG_REFERENCE, context);
            BaseObject baseObject = document.getXObject(CONFIG_CLASS_REFERENCE);
            String authorizationHeader;
            if (baseObject.getStringValue(GLOBAL_CONFIG_KEY).equals("0")) {
                authorizationHeader = baseObject.getStringValue(AUTHORIZATION_HEADER_KEY);
            } else {
                WikiReference wikiReference = wikiDescriptorManager.getMainWikiDescriptor().getReference();
                DocumentReference mainWikiConfigRef = new DocumentReference(CONFIG_REFERENCE, wikiReference);
                XWikiDocument mainWikiDoc = context.getWiki().getDocument(mainWikiConfigRef, context);
                BaseObject mainWikiBaseObject = mainWikiDoc.getXObject(CONFIG_CLASS_REFERENCE);
                authorizationHeader = mainWikiBaseObject.getStringValue(AUTHORIZATION_HEADER_KEY);
            }
            if (authorizationHeader == null || authorizationHeader.isEmpty()) {
                authorizationHeader = DEFAULT_AUTHORIZATION_HEADER;
            }
            return authorizationHeader;
        } catch (Exception e) {
            logger.warn("Failed to retrieve the authorization header from the only office configuration.");
            return "";
        }
    }

    private String getSecret()
    {
        XWikiContext context = contextProvider.get();

        try {
            XWikiDocument document = context.getWiki().getDocument(CONFIG_REFERENCE, context);
            BaseObject baseObject = document.getXObject(CONFIG_CLASS_REFERENCE);
            if (baseObject.getStringValue(GLOBAL_CONFIG_KEY).equals("0")) {
                return baseObject.getStringValue(SERVER_SECRET_KEY);
            } else {
                WikiReference wikiReference = wikiDescriptorManager.getMainWikiDescriptor().getReference();
                DocumentReference mainWikiConfigRef = new DocumentReference(CONFIG_REFERENCE, wikiReference);
                XWikiDocument mainWikiDoc = context.getWiki().getDocument(mainWikiConfigRef, context);
                BaseObject mainWikiBaseObject = mainWikiDoc.getXObject(CONFIG_CLASS_REFERENCE);
                return mainWikiBaseObject.getStringValue(SERVER_SECRET_KEY);
            }
        } catch (Exception e) {
            logger.warn("Failed to retrieve the secret for generating/verifying a JWT.");
            return "";
        }
    }
}
