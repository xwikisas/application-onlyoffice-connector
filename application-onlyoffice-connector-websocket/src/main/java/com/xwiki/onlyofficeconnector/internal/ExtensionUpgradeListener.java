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

import java.util.Collections;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.ComponentAnnotationLoader;
import org.xwiki.component.annotation.ComponentDeclaration;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;
import org.xwiki.observation.AbstractEventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.script.service.ScriptService;
import org.xwiki.websocket.script.WebSocketScriptService;

/**
 * Listener that will register the platform websocket support into the component manager. To be removed after several
 * versions.
 *
 * @version $Id$
 * @since 2.3.4
 */
@Component
@Singleton
@Named(ExtensionUpgradeListener.ROLE_HINT)
public class ExtensionUpgradeListener extends AbstractEventListener implements Initializable
{
    /**
     * Hint of this component.
     */
    public static final String ROLE_HINT = "com.xwiki.onlyofficeconnector.internal.ExtensionUpgradeListener";

    private static final String WEBSOCKET_ROLE_HINT = "websocket";

    @Inject
    private ComponentManager componentManager;

    /**
     * Default constructor.
     */
    public ExtensionUpgradeListener()
    {
        // "com.xwiki.onlyoffice-connector:application-onlyoffice-connector-ui"
        super(ROLE_HINT, Collections.emptyList());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {

    }

    @Override
    public void initialize() throws InitializationException
    {
        try {
            if (componentManager.getInstance(ScriptService.class, WEBSOCKET_ROLE_HINT).getClass().getName()
                .contains("contrib"))
            {
                registerWebSocketScriptService();
            }
        } catch (ComponentLookupException e) {
            registerWebSocketScriptService();
        } catch (Exception ignored) {
        }
    }

    private void registerWebSocketScriptService()
    {
        ComponentAnnotationLoader annotationLoader = new ComponentAnnotationLoader();
        annotationLoader.register(componentManager, this.getClass().getClassLoader(),
            Collections.singletonList(new ComponentDeclaration(WebSocketScriptService.class.getName())));
    }
}
