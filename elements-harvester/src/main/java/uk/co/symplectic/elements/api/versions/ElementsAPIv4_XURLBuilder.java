/*******************************************************************************
 * Copyright (c) 2012 Symplectic Ltd. All rights reserved.
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 ******************************************************************************/
package uk.co.symplectic.elements.api.versions;

import org.apache.commons.lang.StringUtils;
import uk.co.symplectic.elements.api.ElementsAPIURLBuilder;
import uk.co.symplectic.elements.api.queries.ElementsAPIFeedGroupQuery;
import uk.co.symplectic.elements.api.queries.ElementsAPIFeedObjectQuery;
import uk.co.symplectic.elements.api.queries.ElementsAPIFeedRelationshipQuery;
import uk.co.symplectic.utils.URLBuilder;

public class ElementsAPIv4_XURLBuilder extends ElementsAPIURLBuilder.GenericBase {
    @Override

    public String buildObjectFeedQuery(String endpointUrl, ElementsAPIFeedObjectQuery feedQuery) {
        URLBuilder queryUrl = new URLBuilder(endpointUrl);

        if(feedQuery.getQueryDeletedObjects()){
            queryUrl.appendPath("deleted");
        }

        if (feedQuery.getCategory() != null) {
            queryUrl.appendPath(feedQuery.getCategory().getPlural());
        } else {
            queryUrl.appendPath("objects");
        }

        if (feedQuery.getGroups().size() != 0) {
            queryUrl.addParam("groups", convertIntegerArrayToQueryString(feedQuery.getGroups()));

            if (feedQuery.getExplicitMembersOnly()) {
                queryUrl.addParam("group-membership", "explicit");
            }
        }

        if (feedQuery.getApprovedObjectsOnly()) {
            queryUrl.addParam("ever-approved", "true");
        }

        if (feedQuery.getFullDetails()) {
            queryUrl.addParam("detail", "full");
        }

        if (feedQuery.getPerPage() > 0) {
            int maxPerPageAllowed = feedQuery.getFullDetails() ? 25 : 1000;
            int requestedPerPage = Math.min(feedQuery.getPerPage(), maxPerPageAllowed);
            queryUrl.addParam("per-page", Integer.toString(requestedPerPage));  //v4.6 introduced a new maximum per page of 25 for full detail
        }

        if (!StringUtils.isEmpty(feedQuery.getModifiedSince())) {
            if(feedQuery.getQueryDeletedObjects())
                queryUrl.addParam("deleted-since", feedQuery.getModifiedSince());
            else
                queryUrl.addParam("modified-since", feedQuery.getModifiedSince());
        }

        //hack in a page for testing
        //queryUrl.addParam("page", Integer.toString(feedQuery.page));

        return queryUrl.toString();
    }

    @Override
    public String buildRelationshipFeedQuery(String endpointUrl, ElementsAPIFeedRelationshipQuery feedQuery) {
        URLBuilder queryUrl = new URLBuilder(endpointUrl);

        queryUrl.appendPath("relationships");

        if (feedQuery.getFullDetails()) {
            queryUrl.addParam("detail", "full");
        }

        if (feedQuery.getPerPage() > 0) {
            queryUrl.addParam("per-page", Integer.toString(feedQuery.getPerPage(), feedQuery.getFullDetails() ? 25 : 100));  //v4.6 introduced a new maximum per page of 25 for full detail
        }

        //hack in a page for testing
        //queryUrl.addParam("types", Integer.toString(83));

        return queryUrl.toString();
    }

    public String buildGroupQuery(String endpointUrl, ElementsAPIFeedGroupQuery feedQuery){
        URLBuilder queryUrl = new URLBuilder(endpointUrl);
        queryUrl.appendPath("groups");
        return queryUrl.toString();
    }
}
