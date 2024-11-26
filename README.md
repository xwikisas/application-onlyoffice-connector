# OnlyOffice Connector Application

* Project Lead: [Teo Caras](https://github.com/trrenty) 
* [Issue Tracker](https://github.com/xwikisas/application-onlyoffice-connector/issues)
* [Development Practices](http://dev.xwiki.org/xwiki/bin/view/Community/DevelopmentPractices)
* Minimal XWiki version supported: XWiki 14.10
* License: LGPL 2.1
* Translations: N/A
* Sonar Dashboard: N/A
* Continuous Integration Status: [![Build Status](http://ci.xwikisas.com/view/All/job/xwikisas/job/application-onlyoffice-connector/job/master/badge/icon)](http://ci.xwikisas.com/view/All/job/xwikisas/job/application-onlyoffice-connector/job/master/)

# Description

Edit attachments with OnlyOffice directly in XWiki. Supports doc, docx, xls, xlsx, pps, ppsx. It was converted to a paid application from [xwiki-labs/xoo](https://git.xwikisas.com/xwiki-labs/xoo).

# OnlyOffice CORS

In order to use this connector, OnlyOffice must be setup using CORS (cross-origin-resource-sharing). You can enable this by putting a proxy in front of OnlyOffice or you can change the OnlyOffice nginx configuration file: /etc/nginx/includes/onlyoffice-documentserver-docservice.conf to the content of the supporting file by the same name.
