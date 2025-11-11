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

import org.apache.commons.lang.exception.ExceptionUtils;
import org.primeframework.jwt.Signer;
import org.primeframework.jwt.Verifier;
import org.primeframework.jwt.domain.JWT;
import org.primeframework.jwt.hmac.HMACSigner;
import org.primeframework.jwt.hmac.HMACVerifier;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.AttachmentReferenceResolver;
import org.xwiki.security.authorization.AccessDeniedException;
import org.xwiki.security.authorization.ContextualAuthorizationManager;
import org.xwiki.security.authorization.Right;

import com.xpn.xwiki.XWikiContext;
import com.xwiki.onlyofficeconnector.OnlyOfficeManager;
import com.xwiki.onlyofficeconnector.configuration.OnlyOfficeConfiguration;

/**
 * The default {@link OnlyOfficeManager} implementation that retrieves the JWT secret from the configuration file.
 *
 * @version $Id$
 * @since 2.2.0
 */
@Component
@Singleton
public class DefaultOnlyOfficeManager implements OnlyOfficeManager
{
    private static final String BEARER_KEY = "Bearer ";

    @Inject
    private Logger logger;

    @Inject
    private OnlyOfficeConfiguration onlyOfficeConfiguration;

    @Inject
    private ContextualAuthorizationManager authorizationManager;

    @Inject
    private AttachmentReferenceResolver<String> attachmentReferenceResolver;

    @Inject
    private Provider<XWikiContext> wikiContextProvider;

    @Override
    public String createToken(final Map<String, Object> payloadClaims)
    {
        try {
            String token = "";
            if (hasRights(payloadClaims)) {
                // build a HMAC signer using a SHA-256 hash
                Signer signer = HMACSigner.newSHA256Signer(onlyOfficeConfiguration.getSecret());
                JWT jwt = new JWT();
                for (String key : payloadClaims.keySet()) {
                    jwt.addClaim(key, payloadClaims.get(key));
                }
                token = JWT.getEncoder().encode(jwt, signer);
            }
            return token;
        } catch (Exception e) {
            logger.error("There was an error while attempting to encode the token.", e);
            return "";
        }
    }

    @Override
    public JWT readToken(final String token)
    {
        try {
            // build a HMAC verifier using the token secret
            Verifier verifier = HMACVerifier.newVerifier(onlyOfficeConfiguration.getSecret());

            // verify and decode the encoded string JWT to a rich object
            return JWT.getDecoder().decode(token, verifier);
        } catch (Exception exception) {
            logger.error("There was an error while attempting to decode the token.", exception);
            return null;
        }
    }

    @Override
    public void checkAuthorizationToken() throws SecurityException
    {
        logger.debug("Initiating authorization token validation.");
        XWikiContext xcontext = this.wikiContextProvider.get();
        String authHeaderName = onlyOfficeConfiguration.getAuthorizationHeader();
        logger.debug("Authorization header name: [{}]", authHeaderName);
        String authHeader = xcontext.getRequest().getHeader(authHeaderName);
        if (authHeader == null) {
            logger.debug("No authorization header found in the request.");
            throw new SecurityException("Missing authorization header.");
        }
        if (!authHeader.startsWith(BEARER_KEY)) {
            logger.debug("Authorization header does not start with Bearer [{}] prefix. Header: [{}]", BEARER_KEY,
                authHeader);
            throw new SecurityException("Invalid authorization header format.");
        }

        String token = authHeader.substring(BEARER_KEY.length());
        logger.debug("Bearer token found. Token length: {}", token.length());
        JWT decoded = readToken(token);
        if (decoded == null) {
            throw new SecurityException("Failed to decode JWT.");
        }
        logger.debug("Authorization token successfully validated.");
    }

    private boolean hasRights(Map<String, Object> payloadClaims)
    {
        try {
            Map<String, Object> documentMap = (Map<String, Object>) payloadClaims.get("document");
            AttachmentReference attachRef = attachmentReferenceResolver.resolve((String) documentMap.get("attachRef"));
            authorizationManager.checkAccess(Right.VIEW, attachRef);
            Map<String, Boolean> permissions = (Map<String, Boolean>) documentMap.get("permissions");
            if (permissions.getOrDefault("edit", false)) {
                authorizationManager.checkAccess(Right.EDIT, attachRef);
            }
            return true;
        } catch (AccessDeniedException e) {
            logger.error("Token creation unauthorized", e);
        } catch (Exception e) {
            logger.error("There was an error while attempting to check the rights for the token creation. "
                + "Root cause is: [{}]", ExceptionUtils.getRootCauseMessage(e));
        }
        return false;
    }
}
